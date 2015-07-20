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
package com.google.cloud.solutions.mobilepushnotification.dummybackendsample;

import com.google.api.server.spi.ServiceException;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.users.User;
import com.google.cloud.solutions.mobilepushnotification.Configuration;
import com.google.cloud.solutions.mobilepushnotification.EMF;
import com.google.cloud.solutions.mobilepushnotification.PushNotificationUtility;

import java.util.List;
import java.util.logging.Logger;

import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * A dummy Cloud Endpoint that can be called from the iOS client. The main role is to just test that
 * the push notification works.
 *
 */
@Api(name = "gameEndpoint", clientIds = {Configuration.CLIENT_ID})
public class DummyGameEndpoint {
  private static final Logger log = Logger.getLogger(DummyGameEndpoint.class.getName());

  /**
   * Called when the player unlocked an achievement.
   *
   * @param achievement the achievement unlocked by the player.
   * @throws ServiceException
   */
  public void achievementUnlocked(@Named("message") String achievement, User user)
      throws ServiceException {

    if (user == null && !Configuration.ALLOW_UNAUTHENTICATED_CALLS) {
      throw new UnauthorizedException("Only authenticated calls are allowed");
    }

    // The actual game backend would probably validate the achievement first
    // and then store it in Datastore. Then it might send push notification
    // to user's friends.
    // In this sample only the send part is implemented and instead of
    // retrieving the list of user's friends, the code just retrieves
    // a few most recently registered devices and sends an alert to them

    String playerName =
        (user == null || user.getEmail() == null) ? "Anonymous User" : user.getEmail();

    String alert = String.format("%1$s unlocked achievement '%2$s'", playerName, achievement);

    EntityManager mgr = null;
    try {
      mgr = getEntityManager();
      Query query =
          mgr.createQuery("select deviceToken from DeviceRegistration d order by timestamp");

      query.setFirstResult(0);
      query.setMaxResults(5);

      @SuppressWarnings("unchecked")
      List<String> deviceTokensOfFriends = query.getResultList();

      log.info("Sent achievement unlocked push notification to " + deviceTokensOfFriends.size()
          + " friend(s) of " + playerName);
      PushNotificationUtility.enqueuePushAlert(alert, deviceTokensOfFriends);
    } finally {
      mgr.close();
    }
  }

  private static EntityManager getEntityManager() {
    return EMF.get().createEntityManager();
  }
}
