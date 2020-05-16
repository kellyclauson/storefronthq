package com.islandsoftware.storefronhq.stats;

import com.islandsoftware.storefronhq.tools.Utils;

import java.util.Comparator;

public class StatTotalsByProfitComparator implements Comparator<StatTotalsBySegment>
{
   private String orderBy;

   public StatTotalsByProfitComparator(String orderBy)
   {
      this.orderBy = orderBy;
   }

   @Override
   public int compare(StatTotalsBySegment firstStatTotals, StatTotalsBySegment secondStatTotals)
   {
      int firstProfit =  (int)(Utils.fromCurrencyFormat(firstStatTotals.getSummary().getProfit()) * 100);
      int secondProfit =  (int)(Utils.fromCurrencyFormat(secondStatTotals.getSummary().getProfit()) * 100);
      if (orderBy.startsWith("desc"))
      {
         return secondProfit - firstProfit;
      }
      return firstProfit - secondProfit;
   }
}
