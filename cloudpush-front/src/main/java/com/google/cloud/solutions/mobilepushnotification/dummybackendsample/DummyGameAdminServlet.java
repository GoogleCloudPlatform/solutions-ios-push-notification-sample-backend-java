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
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.cloud.solutions.mobilepushnotification.DeviceRegistration;
import com.google.cloud.solutions.mobilepushnotification.DeviceRegistrationEndpoint;
import com.google.cloud.solutions.mobilepushnotification.PushNotificationUtility;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * HttpServlet for testing push notifications.
 * To register a new iOS device token: /admin/test?registerDeviceToken=<deviceToken>
 * To send a push alert to all registered devices: /admin/test?alert=<alertMessage>
 * To process an achievement, which sends push alert to some devices:
 *                /admin/test?achievement=<achievementText>
 *
 */
public class DummyGameAdminServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger
            .getLogger(DummyGameAdminServlet.class.getName());

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        PrintWriter out = res.getWriter();
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();

        String deviceToken = req.getParameter("registerdevicetoken");
        if (deviceToken != null) {
            // This code simulates an iOS client calling an API
            // to register device token for push notification
            DeviceRegistration device = new DeviceRegistration();
            device.setDeviceToken(deviceToken);
            try {
              new DeviceRegistrationEndpoint().registerDevice(device, user);
            } catch (ServiceException e) {
              out.println("Device NOT registered: " + e.getMessage());
              return;
            }
            log.info("Admin registered a new device token");
            out.println("Device registered successfully");
        }

        String alert = req.getParameter("alert");
        if (alert != null) {
            // This code simulate an action of an admin that wants to send a
            // push notification to all devices
            PushNotificationUtility.enqueuePushAlertToAllDevices(alert);
            log.info("Admin enqueud push alert to all devices");
            out.println("Push alert sent to all devices");
        }

        String achievement = req.getParameter("achievement");
        if (achievement != null) {
            // This code simulates an iOS client calling an API that
            // among other things sends push notification
            try {
                new DummyGameEndpoint().achievementUnlocked(achievement, user);
                log.info("Admin called achievementUnlocked");
                out.println("Achievement unlocked processed successfully");
            } catch (ServiceException e) {
              out.println("Achievement request NOT processed: " + e.getMessage());
              return;
            }
        }
    }
}
