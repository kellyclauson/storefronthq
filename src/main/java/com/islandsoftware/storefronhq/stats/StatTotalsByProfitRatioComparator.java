package com.islandsoftware.storefronhq.stats;

import com.islandsoftware.storefronhq.tools.Utils;

import java.util.Comparator;

public class StatTotalsByProfitRatioComparator implements Comparator<StatTotalsBySegment>
{
   private String orderBy;

   public StatTotalsByProfitRatioComparator(String orderBy)
   {
      this.orderBy = orderBy;
   }

   @Override
   public int compare(StatTotalsBySegment firstStatTotals, StatTotalsBySegment secondStatTotals)
   {
      int firstProfitRatio =  (int)(Utils.fromPercentageFormat(firstStatTotals.getSummary().getProfitRatio()) * 100);
      int secondProfitRatio =  (int)(Utils.fromPercentageFormat(secondStatTotals.getSummary().getProfitRatio()) * 100);
      if (orderBy.startsWith("desc"))
      {
         return secondProfitRatio - firstProfitRatio;
      }
      return firstProfitRatio - secondProfitRatio;
   }
}
