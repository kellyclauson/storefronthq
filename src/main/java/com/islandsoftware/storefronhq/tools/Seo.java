package com.islandsoftware.storefronhq.tools;

import com.islandsoftware.storefronhq.shopify.sync.model.TitleTag;

import java.util.ArrayList;
import java.util.List;

public class Seo
{
   private String etsyTitle;
   private String shopifyTitle;
   private TitleTag titleTag;
   private String h1;
   private String h2;
   private String description;
   private List<String> keywords = new ArrayList<>();

   @Override
   public String toString()
   {
      return "Seo{" +
            "etsyTitle='" + etsyTitle + '\'' +
            ", shopifyTitle='" + shopifyTitle + '\'' +
            ", titleTag='" + titleTag.getTitleTag() + '\'' +
            ", h1='" + h1 + '\'' +
            ", h2='" + h2 + '\'' +
            ", description='" + description + '\'' +
            ", keywords=" + Utils.keywordsToString(keywords) +
            '}';
   }

   public String getEtsyTitle()
   {
      return etsyTitle;
   }

   public String getShopifyTitle()
   {
      return shopifyTitle;
   }

   public void setShopifyTitle(String shopifyTitle)
   {
      this.shopifyTitle = shopifyTitle;
   }

   public void setEtsyTitle(String etsyTitle)
   {
      this.etsyTitle = etsyTitle;
   }

   public TitleTag getTitleTag()
   {
      return titleTag;
   }

   public void setTitleTag(TitleTag titleTag)
   {
      this.titleTag = titleTag;
   }

   public String getH1()
   {
      return h1;
   }

   public void setH1(String h1)
   {
      this.h1 = h1;
   }

   public String getH2()
   {
      return h2;
   }

   public void setH2(String h2)
   {
      this.h2 = h2;
   }

   public String getDescription()
   {
      return description;
   }

   public void setDescription(String description)
   {
      this.description = description;
   }

   public List<String> getKeywords()
   {
      return keywords;
   }

   public void setKeywords(List<String> keywords)
   {
      this.keywords = keywords;
   }
}
