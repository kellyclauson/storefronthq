package com.islandsoftware.storefronhq.stats;

import java.util.List;

public class StatsForDaysOfWeekResponse
{
   private String channel;
   private String sortBy;
   private String orderBy;
   private List<StatTotalsBySegment> stats;

   public String getChannel()
   {
      return channel;
   }

   public void setChannel(String channel)
   {
      this.channel = channel;
   }

   public String getSortBy()
   {
      return sortBy;
   }

   public void setSortBy(String sortBy)
   {
      this.sortBy = sortBy;
   }

   public String getOrderBy()
   {
      return orderBy;
   }

   public void setOrderBy(String orderBy)
   {
      this.orderBy = orderBy;
   }

   public List<StatTotalsBySegment> getStats()
   {
      return stats;
   }

   public void setStats(List<StatTotalsBySegment> stats)
   {
      this.stats = stats;
   }
}
