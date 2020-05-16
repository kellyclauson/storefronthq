package com.islandsoftware.storefronhq.shopify.sync.model;

import com.islandsoftware.storefronhq.tools.Keyword;

import java.util.ArrayList;
import java.util.List;

public class TitleTag
{
   private String titleTag;
   private List<Keyword> keywords = new ArrayList<>();
   private int totalVolume;
   private int priority;

   @Override
   public boolean equals(Object o)
   {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      TitleTag titleTag1 = (TitleTag) o;

      return titleTag.equals(titleTag1.titleTag);
   }

   @Override
   public int hashCode()
   {
      return titleTag.hashCode();
   }

   public TitleTag()
   {
   }

   public TitleTag(String titleTag)
   {
      this.titleTag = titleTag;
   }

   @Override
   public String toString()
   {
      return "TitleTag{" +
         "titleTag='" + titleTag + '\'' +
         '}';
   }

   public String getTitleTag()
   {
      return titleTag;
   }

   public void setTitleTag(String titleTag)
   {
      this.titleTag = titleTag;
   }

   public List<Keyword> getKeywords()
   {
      return keywords;
   }

   public int getTotalVolume()
   {
      return totalVolume;
   }

   public void setTotalVolume(int totalVolume)
   {
      this.totalVolume = totalVolume;
   }

   public int getPriority()
   {
      return priority;
   }

   public void setPriority(int priority)
   {
      this.priority = priority;
   }
}
