# CloudPushSample for iOS - Java Backend

## Copyright
Copyright 2013 Google Inc. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

## Disclaimer
This sample application is not an official Google product.

## Supported Platform and Versions
This sample source code and project is designed to work with Eclipse. It was tested with Eclipse 3.8.


The sample uses [JavaPNS library](https://code.google.com/p/javapns/). As of June 2013, JavaPNS makes write() and flush() calls on the socket for every notification that is sent. This significantly reduces the notification throughput for this sample on Google App Engine to a few hundred notifications per second per backend instance. If you need to achieve higher throughput, you need to buffer notifications, either by modifying JavaPNS or using a different library. If you use JavaPNS library, you should download the librariy and its dependency and put them under cloudpush-worker/lib/ directory.

## Overview
This sample shows how Google App Engine can be used to orchestrate push notifications to iOS devices.

### Prerequisite
1. Latest [Apache Maven](http://maven.apache.org/download.cgi). Please refer to the [Google App Engine Maven Document](https://cloud.google.com/appengine/docs/java/tools/maven) for details.

2. When deploying this sample to App Engine, the billing needs to be enabled for the target app id. When you are done with evaluating the sample,
you should stop the 'worker' backend to reduce the billable usage.

## Download Instruction
The download contains the Java backend. After the download finishes, unzip the files and import the project into Eclipse.

## Developer Guide using Eclipse - modyfying and deploying the backend
1. Open war/WEB-INF/appengine-web.xml and enter your app id within the application XML element.

2. Add your APNS certificate p12 file to the src folder.

3. Edit src/com/google/solutions/mobilepushnotification/Consts.java and update CERTTIFICATE_FILE_NAME and CERTIFICATE_PASSWORD with the file name and password for your certificate.

4. In the same file update CLIENT_ID with your Client Id for iOS devices. For information how to obtain the Client Id see [documentation](https://developers.google.com/console/help/#installed_applications).

5. Select the project and from Google context menu select Deploy to App Engine.

## Developer Guide using XCode - registering for push notification from your iOS client application
1. Follow [documentation](https://developers.google.com/appengine/docs/java/endpoints/consume_ios) to (1) compile the client library generator and generate client library for the deployed backend using deviceregistration-v1-rpc.discovery and gameEndpoint-v1-rpc.discovery files, (2) add required files to your iOS project, (3) create the service object and (4) add a sign-in dialog to your iOS Client

2. Your iOS application needs to register for push notifications by calling registerForRemoteNotificationTypes().

3. Your iOS application needs to implement didRegisterForRemoteNotificationsWithDeviceToken() and pass the retrieved device token to the backend by calling deviceregistration registerDevice method. For testing you can instead use your web browser and navigate to https://url_of_your_deployed_backend/admin/test?registerdevicetoken=device_token_passed_to_didRegisterForRemoteNotificationsWithDeviceToken.

4. It may help with testing the notifications if you change the alert style for your iOS app in iOS Settings->Notifications to Alert instead of Banner.

5. To test sending push notification you can use use your web browser and navigate to https://url_of_your_deployed_backend/admin/test?alert=HelloWorld. This should send push notification to all registered devices.

6. A very simple iOS app that registers for push notifications, passes the device token to the deployed backend and displays a text box when it receives notification is also published on [GitHub](https://github.com/GoogleCloudPlatform/solutions-ios-push-notification-sample-ios-client).





