package com.islandsoftware.storefronhq.tools;

public class Keyword implements Comparable<Keyword>
{
   private String query;
   private int volume;
   private int titleTagCount;
   private int h1Count;
   private int h2Count;

   public Keyword()
   {
   }

   public Keyword(String query, int volume)
   {
      this.query = query;
      this.volume = volume;
   }

   @Override
   public String toString()
   {
      return "Keyword{" +
         "query='" + query + '\'' +
         ", volume=" + volume +
         ", titleTagCount=" + titleTagCount +
         ", h1Count=" + h1Count +
         ", h2Count=" + h2Count +
         '}';
   }

   @Override
   public int compareTo(Keyword otherKeyword)
   {
      return this.getQuery().compareTo(otherKeyword.getQuery());
   }

   @Override
   public boolean equals(Object o)
   {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Keyword keyword = (Keyword) o;

      return query.equals(keyword.query);

   }

   @Override
   public int hashCode()
   {
      return query.hashCode();
   }

   public String getQuery()
   {
      return query;
   }

   public void setQuery(String query)
   {
      this.query = query;
   }

   public int getVolume()
   {
      return volume;
   }

   public void setVolume(int volume)
   {
      this.volume = volume;
   }

   public int getTitleTagCount()
   {
      return titleTagCount;
   }

   public void setTitleTagCount(int titleTagCount)
   {
      this.titleTagCount = titleTagCount;
   }

   public int getH1Count()
   {
      return h1Count;
   }

   public void setH1Count(int h1Count)
   {
      this.h1Count = h1Count;
   }

   public int getH2Count()
   {
      return h2Count;
   }

   public void setH2Count(int h2Count)
   {
      this.h2Count = h2Count;
   }

   public int getTotalCount()
   {
      return titleTagCount + h1Count + h2Count;
   }
}
