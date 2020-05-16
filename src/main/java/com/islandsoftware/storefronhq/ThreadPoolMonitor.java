package com.islandsoftware.storefronhq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadPoolExecutor;

public class ThreadPoolMonitor implements Runnable
{
   private ThreadPoolExecutor executor;
   private long interval;
   private boolean run = true;

   private static final Logger LOGGER = LoggerFactory.getLogger(ThreadPoolMonitor.class);

   public ThreadPoolMonitor(ThreadPoolExecutor executor, long interval)
   {
      LOGGER.info("EventHandlerThreadPoolMonitor<init> interval={}", interval);
      this.executor = executor;
      this.interval = interval;
   }

   public void shutdown()
   {
      while (executor.getQueue().size() > 0)
      {
         LOGGER.info("waiting for events in queue to process...");
         try
         {
            Thread.sleep(5000);
         }
         catch (InterruptedException e)
         {
            LOGGER.warn("interrupted", e);
         }
      }
      this.run = false;
      LOGGER.info(toString());
      LOGGER.info("Event Handler Thread Pool Monitor shutdown complete");
   }

   @Override
   public String toString()
   {
      return String.format("[ThreadPoolMonitor] [poolSize=%d, corePoolSize=%d, activeCount=%d," +
                  " completedTaskCount=%d, taskCount=%d, queueSize=%d, isShutdown=%s, isTerminated=%s]",
            this.executor.getPoolSize(),
            this.executor.getCorePoolSize(),
            this.executor.getActiveCount(),
            this.executor.getCompletedTaskCount(),
            this.executor.getTaskCount(),
            this.executor.getQueue().size(),
            this.executor.isShutdown(),
            this.executor.isTerminated());
   }

   public void run()
   {
      while (run)
      {
         if (LOGGER.isDebugEnabled())
         {
            LOGGER.debug(toString());
         }
         try
         {
            Thread.sleep(interval);
         }
         catch (InterruptedException e)
         {
            LOGGER.warn("interrupted", e);
         }
      }
   }
}
