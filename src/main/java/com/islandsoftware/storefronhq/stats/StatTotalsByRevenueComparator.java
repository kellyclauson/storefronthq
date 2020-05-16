package com.islandsoftware.storefronhq.stats;

import com.islandsoftware.storefronhq.tools.Utils;

import java.util.Comparator;

public class StatTotalsByRevenueComparator implements Comparator<StatTotalsBySegment>
{
   private String orderBy;

   public StatTotalsByRevenueComparator(String orderBy)
   {
      this.orderBy = orderBy;
   }

   @Override
   public int compare(StatTotalsBySegment firstStatTotals, StatTotalsBySegment secondStatTotals)
   {
      int firstRevenue =  (int)(Utils.fromCurrencyFormat(firstStatTotals.getSummary().getRevenue()) * 100);
      int secondRevenue =  (int)(Utils.fromCurrencyFormat(secondStatTotals.getSummary().getRevenue()) * 100);
      if (orderBy.startsWith("desc"))
      {
         return secondRevenue - firstRevenue;
      }
      return firstRevenue - secondRevenue;
   }
}
