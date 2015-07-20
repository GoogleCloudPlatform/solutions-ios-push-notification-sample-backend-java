/*
 * Copyright (c) 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.cloud.solutions.mobilepushnotification;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.datanucleus.query.JPACursorHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * HttpServlet for preprocessing push notification requests. In particular it takes a request for
 * sending push notification to all registered devices, and uses a cursor to process it in batches
 * with as much transactional consistency as possible.
 *
 * It is intended to be called by Push Task Queue, so the request is retried if it fails.
 *
 */
public class PushPreProcessingServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;

  /*
   * BATCH_SIZE indicates how many device tokens will be included in a payload of a single task.
   * Typically the larger the batch, the higher the throughput as fewer RPC calls need to be made.
   * However, the payload of a single task has a size limit. Also, it may be impractical to track
   * the notifications from a single task on per device token basis. When an instance
   * is restarted, it is given a finite amount of time to complete processing (e.g., 30 seconds) and
   * the batch size should be small enough to guarantee that the whole batch will be processed
   * within the allotted time to avoid duplicated notifications while still guaranteeing that the
   * notification is sent to each device in a batch.
   */
  private static final int BATCH_SIZE = 250;

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    if (!Configuration.isRequestFromTaskQueue(req, resp)) {
      return;
    }
    String alertMessage = req.getParameter("alert");
    String queryType = req.getParameter("queryType");
    if (queryType.equalsIgnoreCase("allDevices")) {
      String cursorString = req.getParameter("cursorString");
      preprocessBatchOfDevices(alertMessage, cursorString);
    }
  }

  private void preprocessBatchOfDevices(String alertMessage, String cursorString) {
    EntityManager mgr = null;
    Cursor cursor = null;

    try {
      mgr = getEntityManager();

      // Retrieve entities (and not just deviceToken property) in order to paginate using cursor
      Query query = mgr.createQuery("select from DeviceRegistration as DeviceRegistration");
      if (cursorString != null && cursorString != "") {
        cursor = Cursor.fromWebSafeString(cursorString);
        query.setHint(JPACursorHelper.CURSOR_HINT, cursor);
      }

      query.setMaxResults(BATCH_SIZE);

      @SuppressWarnings("unchecked")
      List<DeviceRegistration> deviceRegistrations = query.getResultList();
      cursor = JPACursorHelper.getCursor(deviceRegistrations);
      if (cursor != null) {
        cursorString = cursor.toWebSafeString();
      }

      List<String> deviceTokens = new ArrayList<String>();
      for (DeviceRegistration deviceRegistartion : deviceRegistrations) {
        deviceTokens.add(deviceRegistartion.getDeviceToken());
      }

      if (deviceTokens.isEmpty()) {
        // no more matching device tokens matching this query.
        return;
      }

      mgr.getTransaction().begin();

      try {
        PushNotificationUtility.enqueuePushAlert(alertMessage, deviceTokens);

        if (deviceRegistrations.size() == BATCH_SIZE) {
          PushNotificationUtility.continueEnqueueingPushAlertToAllDevices(
              alertMessage, cursorString);
        }

        mgr.getTransaction().commit();
      } catch (RuntimeException e) {
        if (mgr.getTransaction().isActive()) {
          mgr.getTransaction().rollback();
        }
        throw e;
      }

    } finally {
      mgr.close();
    }
  }

  private static EntityManager getEntityManager() {
    return EMF.get().createEntityManager();
  }
}
