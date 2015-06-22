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

import com.google.api.server.spi.ServiceException;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.users.User;

import java.util.Date;

import javax.persistence.EntityManager;

/**
 * Endpoint allowing iOS client app to register device information for push notification
 *
 */
@Api(name = "deviceregistration", clientIds = {Configuration.CLIENT_ID})
public class DeviceRegistrationEndpoint {
  /**
   * Inserts a new entity into App Engine datastore or updates existing entity.It uses HTTP POST
   * method.
   *
   * @param device the entity to be inserted/updated.
   * @return The inserted/updated entity.
   * @throws ServiceException when the call is unauthenticated and the backend is configured not to
   *         allow them
   */
  public DeviceRegistration registerDevice(DeviceRegistration device, User user)
      throws ServiceException {

    if (user == null && !Configuration.ALLOW_UNAUTHENTICATED_CALLS) {
      throw new UnauthorizedException("Only authenticated calls are allowed");
    }

    EntityManager mgr = getEntityManager();
    try {
      device.setTimestamp(new Date());
      mgr.persist(device);
    } finally {
      mgr.close();
    }
    return device;
  }

  private static EntityManager getEntityManager() {
    return EMF.get().createEntityManager();
  }
}
