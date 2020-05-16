package com.islandsoftware.storefronhq.stats;

import java.util.Comparator;

public class OrderStatsComparator implements Comparator<OrderStats>
{
   @Override
   public int compare(OrderStats firstOrderStats, OrderStats secondOrderStats)
   {
      long diff = firstOrderStats.getTime() - secondOrderStats.getTime();
      if (diff < 0)
      {
         return -1;
      }
      if (diff > 0)
      {
         return 1;
      }
      return 0;
   }
}
