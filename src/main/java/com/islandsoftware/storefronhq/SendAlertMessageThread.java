package com.islandsoftware.storefronhq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SendAlertMessageThread implements Runnable
{
   private static final Logger LOGGER = LoggerFactory.getLogger(SendAlertMessageThread.class);

   public void run()
   {
      try
      {
         LOGGER.debug("run: begin send alert messages");
         SMSClient.sendMessages();
         LOGGER.debug("run: end send alert messages");
      }
      catch (Throwable e)
      {
         String msg = "Error in SendAlertMessages " + e.getMessage();
         LOGGER.error(msg, e);
      }
   }
}
