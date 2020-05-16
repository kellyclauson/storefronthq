package com.islandsoftware.storefronhq.stats;

import java.util.Comparator;

public class StatTotalsByOrdersComparator implements Comparator<StatTotalsBySegment>
{
   private String orderBy;

   public StatTotalsByOrdersComparator(String orderBy)
   {
      this.orderBy = orderBy;
   }

   @Override
   public int compare(StatTotalsBySegment firstStatTotals, StatTotalsBySegment secondStatTotals)
   {
      // reverse the order to sort from smallest to largest
      if (orderBy.startsWith("desc"))
      {
         return secondStatTotals.getSummary().getOrders() - firstStatTotals.getSummary().getOrders();
      }
      return firstStatTotals.getSummary().getOrders() - secondStatTotals.getSummary().getOrders();
   }
}
