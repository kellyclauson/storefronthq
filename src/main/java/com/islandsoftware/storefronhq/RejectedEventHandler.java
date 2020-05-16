package com.islandsoftware.storefronhq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public class RejectedEventHandler implements RejectedExecutionHandler
{
   private static final Logger LOGGER = LoggerFactory.getLogger(RejectedEventHandler.class);

   public void rejectedExecution(Runnable r, ThreadPoolExecutor executor)
   {
      ProcessShopifyOrderThread e = (ProcessShopifyOrderThread) r;
      LOGGER.warn("Event was rejected: {}", e.toString());
   }
}
