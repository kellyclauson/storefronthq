package com.islandsoftware.storefronhq.tools;

public class SearchAnalyticsAndVolume
{
   private String query;
   private int clicks;
   private int impressions;
   private double ctr;
   private double avgPosition;
   private int monthlySearches;
   private double competition;
   private double suggestedBid;

   public String getQuery()
   {
      return query;
   }

   public void setQuery(String query)
   {
      this.query = query;
   }

   public int getClicks()
   {
      return clicks;
   }

   public void setClicks(int clicks)
   {
      this.clicks = clicks;
   }

   public int getImpressions()
   {
      return impressions;
   }

   public void setImpressions(int impressions)
   {
      this.impressions = impressions;
   }

   public double getCtr()
   {
      return ctr;
   }

   public void setCtr(double ctr)
   {
      this.ctr = ctr;
   }

   public double getAvgPosition()
   {
      return avgPosition;
   }

   public void setAvgPosition(double avgPosition)
   {
      this.avgPosition = avgPosition;
   }

   public int getMonthlySearches()
   {
      return monthlySearches;
   }

   public void setMonthlySearches(int monthlySearches)
   {
      this.monthlySearches = monthlySearches;
   }

   public double getCompetition()
   {
      return competition;
   }

   public void setCompetition(double competition)
   {
      this.competition = competition;
   }

   public double getSuggestedBid()
   {
      return suggestedBid;
   }

   public void setSuggestedBid(double suggestedBid)
   {
      this.suggestedBid = suggestedBid;
   }
}
