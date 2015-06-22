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

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Holder of the configuration used by this sample. Some of them need to be changed by developers
 * that want to use this sample.
 *
 */
public class Configuration {
  /**
   * @constructor Private constructor as this is a utility class
   */
  private Configuration() {}

  /**
   * Unauthenticated calls are disabled by default to prevent random clients from calling endpoints
   * exposed by this sample and triggering push notification. Developers may want to temporarily
   * allow unauthenticated calls during controlled tests if they don't have CLIENT_ID configured yet
   *
   */
  public static final boolean ALLOW_UNAUTHENTICATED_CALLS = false;
  public static final String CLIENT_ID = "<!!! INSERT YOUR CLIENT ID FOR IOS HERE !!!>";

  private static final String TASKQUEUE_NAME_HEADER = "X-AppEngine-QueueName";

  /**
   * All push task queues used in this sample are configured to be available to admin only. This
   * prevents random users from interfering with task queue request handlers. For additional
   * protection, e.g, if a developer accidently changes the security configuration, all task queue
   * request handlers in this sample are additionally checking for a presence of a header that is
   * guaranteed to indicate that the request came from a Task Queue.
   *
   * @throws IOException
   */
  static boolean isRequestFromTaskQueue(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    String queueName = request.getHeader(TASKQUEUE_NAME_HEADER);
    if (queueName == null || queueName.isEmpty()) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
      return false;
    }
    return true;
  }
}
