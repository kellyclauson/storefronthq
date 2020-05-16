package com.islandsoftware.storefronhq.stats;

import java.time.DayOfWeek;
import java.util.Comparator;

public class StatTotalsByDayOfWeekComparator implements Comparator<StatTotalsBySegment>
{
   private String orderBy;

   public StatTotalsByDayOfWeekComparator(String orderBy)
   {
      this.orderBy = orderBy;
   }

   @Override
   public int compare(StatTotalsBySegment firstStatTotals, StatTotalsBySegment secondStatTotals)
   {
      DayOfWeek dayOfWeekOne = DayOfWeek.valueOf(firstStatTotals.getSummary().getTimePeriod().toUpperCase());
      DayOfWeek dayOfWeekTwo = DayOfWeek.valueOf(secondStatTotals.getSummary().getTimePeriod().toUpperCase());
      if (orderBy.startsWith("desc"))
      {
         return dayOfWeekTwo.getValue() - dayOfWeekOne.getValue();
      }
      return dayOfWeekOne.getValue() - dayOfWeekTwo.getValue();
   }
}
