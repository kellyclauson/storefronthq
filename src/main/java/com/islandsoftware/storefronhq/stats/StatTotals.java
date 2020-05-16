package com.islandsoftware.storefronhq.stats;

public class StatTotals
{
   private String timePeriod;
   private int orders;
   private String revenue;
   private String profit;
   private String cogs;
   private String profitRatio;
   private String profitPerOrder;
   private String revenuePerOrder;
   private double yardsSold;

   @Override
   public String toString()
   {
      return "StatTotals{" +
         "timePeriod='" + timePeriod + '\'' +
         ", orders=" + orders +
         ", revenue='" + revenue + '\'' +
         ", profit='" + profit + '\'' +
         ", cogs='" + cogs + '\'' +
         ", profitRatio='" + profitRatio + '\'' +
         ", profitPerOrder='" + profitPerOrder + '\'' +
         ", revenuePerOrder='" + revenuePerOrder + '\'' +
         ", yardsSold='" + yardsSold + '\'' +
         '}';
   }

   public String getTimePeriod()
   {
      return timePeriod;
   }

   public void setTimePeriod(String timePeriod)
   {
      this.timePeriod = timePeriod;
   }

   public int getOrders()
   {
      return orders;
   }

   public void setOrders(int orders)
   {
      this.orders = orders;
   }

   public String getRevenue()
   {
      return revenue;
   }

   public void setRevenue(String revenue)
   {
      this.revenue = revenue;
   }

   public String getProfit()
   {
      return profit;
   }

   public void setProfit(String profit)
   {
      this.profit = profit;
   }

   public String getCogs()
   {
      return cogs;
   }

   public void setCogs(String cogs)
   {
      this.cogs = cogs;
   }

   public String getProfitRatio()
   {
      return profitRatio;
   }

   public void setProfitRatio(String profitRatio)
   {
      this.profitRatio = profitRatio;
   }

   public String getProfitPerOrder()
   {
      return profitPerOrder;
   }

   public void setProfitPerOrder(String profitPerOrder)
   {
      this.profitPerOrder = profitPerOrder;
   }

   public String getRevenuePerOrder()
   {
      return revenuePerOrder;
   }

   public void setRevenuePerOrder(String revenuePerOrder)
   {
      this.revenuePerOrder = revenuePerOrder;
   }

   public double getYardsSold()
   {
      return yardsSold;
   }

   public void setYardsSold(double yardsSold)
   {
      this.yardsSold = yardsSold;
   }
}
