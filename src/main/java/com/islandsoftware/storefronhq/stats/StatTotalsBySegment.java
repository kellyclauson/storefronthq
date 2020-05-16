package com.islandsoftware.storefronhq.stats;

public class StatTotalsBySegment
{
   private StatTotals summary;
   private String averageOrders;
   private String averageRevenue;
   private String averageProfit;

   public StatTotals getSummary()
   {
      return summary;
   }

   public void setSummary(StatTotals summary)
   {
      this.summary = summary;
   }

   public String getAverageOrders()
   {
      return averageOrders;
   }

   public void setAverageOrders(String averageOrders)
   {
      this.averageOrders = averageOrders;
   }

   public String getAverageRevenue()
   {
      return averageRevenue;
   }

   public void setAverageRevenue(String averageRevenue)
   {
      this.averageRevenue = averageRevenue;
   }

   public String getAverageProfit()
   {
      return averageProfit;
   }

   public void setAverageProfit(String averageProfit)
   {
      this.averageProfit = averageProfit;
   }
}
