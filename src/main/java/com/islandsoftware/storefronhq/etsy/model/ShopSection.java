package com.islandsoftware.storefronhq.etsy.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ShopSection
{
   @JsonProperty("shop_section_id")
   private Long id;
   @JsonProperty("title")
   private String title;
   @JsonProperty("rank")
   private Integer rank;
   @JsonProperty("active_listing_count")
   private Integer activeListingCount;
   @JsonProperty("user_id")
   private Long userId;

   public Long getId()
   {
      return id;
   }

   public void setId(Long id)
   {
      this.id = id;
   }

   public String getTitle()
   {
      return title;
   }

   public void setTitle(String title)
   {
      this.title = title;
   }

   public Integer getRank()
   {
      return rank;
   }

   public void setRank(Integer rank)
   {
      this.rank = rank;
   }

   public Integer getActiveListingCount()
   {
      return activeListingCount;
   }

   public void setActiveListingCount(Integer activeListingCount)
   {
      this.activeListingCount = activeListingCount;
   }

   public Long getUserId()
   {
      return userId;
   }

   public void setUserId(Long userId)
   {
      this.userId = userId;
   }
}
