package com.google.cloud.solutions.mobilepushnotification;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Holder of the configuration used by this sample. Some of them need to be changed by developers
 * that want to use this sample.
 *
 */
public class Configuration {
  private Configuration() {

  }
  /**
   * The name of p12 certificate file. The certificate file needs to be deployed with other
   * binaries. Eclipse does it automatically if the file is placed under the src folder
   */
  private static final String CERTTIFICATE_FILE_NAME = "<!!! INSERT THE FILE NAME HERE.p12 !!!>";
  static final String CERTIFICATE_PASSWORD = "<!!! INSERT THE CERTIFICATE PASSWORD HERE !!!>";
  private static byte[] certificateBytes = null;
  static final boolean USE_PRODUCTION_APNS_SERVICE = false;

  private static InputStream getCertificateStream() {
    return Configuration.class.getClassLoader().getResourceAsStream(CERTTIFICATE_FILE_NAME);
  }

  protected static byte[] getCertificateBytes() {
    if (certificateBytes == null) {
      InputStream certifacteStream = getCertificateStream();
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

      byte[] buffer = new byte[4096];

      try {
        int bytesRead = 0;
        while ((bytesRead = certifacteStream.read(buffer, 0, buffer.length)) != -1) {
          outputStream.write(buffer, 0, bytesRead);
        }
      } catch (IOException e) {
        Logger.getLogger(Configuration.class.getCanonicalName())
            .log(Level.SEVERE, "Error reading the certificate", e);
      }

      certificateBytes = outputStream.toByteArray();
    }
    return certificateBytes;
  }
}
