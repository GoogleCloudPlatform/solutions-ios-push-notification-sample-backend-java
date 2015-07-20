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

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * An entity for iOS device information.
 *
 * @author ggogol@gogole.com
 */
@Entity
public class DeviceRegistration {
  /*
   * Push notification device token.
   */
  @Id
  private String deviceToken;

  /*
   * Timestamp when this device registered last time with the backend.
   */
  private Date timestamp;

  public String getDeviceToken() {
    return deviceToken;
  }

  public void setDeviceToken(String deviceToken) {
    this.deviceToken = deviceToken;
  }

  public Date getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Date date) {
    this.timestamp = date;
  }
}
