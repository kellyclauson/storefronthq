package com.islandsoftware.storefronhq.stats;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatsResponse
{
   private String channel;
   private StatTotals summary;
   private List<FormattedOrderStats> orders;

   public String getChannel()
   {
      return channel;
   }

   public void setChannel(String channel)
   {
      this.channel = channel;
   }

   public StatTotals getSummary()
   {
      return summary;
   }

   public void setSummary(StatTotals summary)
   {
      this.summary = summary;
   }

   public List<FormattedOrderStats> getOrders()
   {
      return orders;
   }

   public void setOrders(List<FormattedOrderStats> orders)
   {
      this.orders = orders;
   }
}
