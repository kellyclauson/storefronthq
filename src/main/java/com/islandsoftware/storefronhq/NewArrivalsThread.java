package com.islandsoftware.storefronhq;

import com.islandsoftware.storefronhq.tools.NewArrivals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NewArrivalsThread implements Runnable
{
   private static final Logger LOGGER = LoggerFactory.getLogger(NewArrivalsThread.class);

   public void run()
   {
      try
      {
         LOGGER.info("run: begin New Arrivals Processing");
         NewArrivals.remove();
         LOGGER.info("run: end New Arrivals Processing");
      }
      catch (Exception e)
      {
         String msg = "Error processing New Arrivals";
         LOGGER.error(msg, e);
         SMSClient.alertAdmin(msg);
      }
   }
}
