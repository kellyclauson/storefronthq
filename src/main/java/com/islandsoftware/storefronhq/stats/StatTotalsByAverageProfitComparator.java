package com.islandsoftware.storefronhq.stats;

import com.islandsoftware.storefronhq.tools.Utils;

import java.util.Comparator;

public class StatTotalsByAverageProfitComparator implements Comparator<StatTotalsBySegment>
{
   private String orderBy;

   public StatTotalsByAverageProfitComparator(String orderBy)
   {
      this.orderBy = orderBy;
   }

   @Override
   public int compare(StatTotalsBySegment firstStatTotals, StatTotalsBySegment secondStatTotals)
   {
      int firstAverage =  (int)(Utils.fromCurrencyFormat(firstStatTotals.getAverageProfit()) * 100);
      int secondAverage =  (int)(Utils.fromCurrencyFormat(secondStatTotals.getAverageProfit()) * 100);
      if (orderBy.startsWith("desc"))
      {
         return secondAverage - firstAverage;
      }
      return firstAverage - secondAverage;
   }
}
