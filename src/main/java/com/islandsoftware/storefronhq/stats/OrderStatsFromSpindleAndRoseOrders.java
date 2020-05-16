package com.islandsoftware.storefronhq.stats;

import com.islandsoftware.storefronhq.orderprocessing.OrdersProcessor;
import com.islandsoftware.storefronhq.orderprocessing.SpindleAndRoseOrder;
import com.islandsoftware.storefronhq.tools.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class OrderStatsFromSpindleAndRoseOrders
{
   private static final Logger LOGGER = LoggerFactory.getLogger(OrderStatsFromSpindleAndRoseOrders.class);

   public static void main(String[] args)
   {
      try
      {
         List<SpindleAndRoseOrder> orders = OrdersProcessor.getOrders();
         List<OrderStats> stats = Utils.toOrderStats(orders);
         LOGGER.info("orderStatsFromSpindleAndRose: numberOfStats={}", stats.size());
         stats.sort(new OrderStatsComparator());
         StatsCollector statsCollector = new StatsCollector("C:/tmp/astats.csv", "C:/tmp/astatsorderitems.csv");
         for (OrderStats stat : stats)
         {
            statsCollector.collect(stat);
         }
      }
      catch (Exception e)
      {
         LOGGER.error("orderStatsFromSpindleAndRoseOrders", e);
      }
   }
}
