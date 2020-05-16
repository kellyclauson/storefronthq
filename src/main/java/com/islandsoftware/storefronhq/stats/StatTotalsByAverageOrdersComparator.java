package com.islandsoftware.storefronhq.stats;

import com.islandsoftware.storefronhq.tools.Utils;

import java.util.Comparator;

public class StatTotalsByAverageOrdersComparator implements Comparator<StatTotalsBySegment>
{
   private String orderBy;

   public StatTotalsByAverageOrdersComparator(String orderBy)
   {
      this.orderBy = orderBy;
   }

   @Override
   public int compare(StatTotalsBySegment firstStatTotals, StatTotalsBySegment secondStatTotals)
   {
      int firstAverage =  (int)(Utils.fromNumberFormat(firstStatTotals.getAverageOrders()) * 100);
      int secondAverage =  (int)(Utils.fromNumberFormat(secondStatTotals.getAverageOrders()) * 100);
      if (orderBy.startsWith("desc"))
      {
         return secondAverage - firstAverage;
      }
      return firstAverage - secondAverage;
   }
}
