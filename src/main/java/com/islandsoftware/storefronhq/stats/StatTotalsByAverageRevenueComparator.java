package com.islandsoftware.storefronhq.stats;

import com.islandsoftware.storefronhq.tools.Utils;

import java.util.Comparator;

public class StatTotalsByAverageRevenueComparator implements Comparator<StatTotalsBySegment>
{
   private String orderBy;

   public StatTotalsByAverageRevenueComparator(String orderBy)
   {
      this.orderBy = orderBy;
   }

   @Override
   public int compare(StatTotalsBySegment firstStatTotals, StatTotalsBySegment secondStatTotals)
   {
      int firstAverage =  (int)(Utils.fromCurrencyFormat(firstStatTotals.getAverageRevenue()) * 100);
      int secondAverage =  (int)(Utils.fromCurrencyFormat(secondStatTotals.getAverageRevenue()) * 100);
      if (orderBy.startsWith("desc"))
      {
         return secondAverage - firstAverage;
      }
      return firstAverage - secondAverage;
   }
}
