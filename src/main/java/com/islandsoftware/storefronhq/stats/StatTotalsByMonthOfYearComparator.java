package com.islandsoftware.storefronhq.stats;

import java.time.Month;
import java.util.Comparator;

public class StatTotalsByMonthOfYearComparator implements Comparator<StatTotalsBySegment>
{
   private String orderBy;

   public StatTotalsByMonthOfYearComparator(String orderBy)
   {
      this.orderBy = orderBy;
   }

   @Override
   public int compare(StatTotalsBySegment firstStatTotals, StatTotalsBySegment secondStatTotals)
   {
      Month monthOne = Month.valueOf(firstStatTotals.getSummary().getTimePeriod().toUpperCase());
      Month monthTwo = Month.valueOf(secondStatTotals.getSummary().getTimePeriod().toUpperCase());
      if (orderBy.startsWith("desc"))
      {
         return monthTwo.getValue() - monthOne.getValue();
      }
      return monthOne.getValue() - monthTwo.getValue();
   }
}
