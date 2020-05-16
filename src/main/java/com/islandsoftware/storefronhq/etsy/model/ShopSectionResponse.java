package com.islandsoftware.storefronhq.etsy.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ShopSectionResponse
{
   @JsonProperty("count")
   private Integer count;
   @JsonProperty("results")
   private ShopSection[] results;

   public Integer getCount()
   {
      return count;
   }

   public void setCount(Integer count)
   {
      this.count = count;
   }

   public ShopSection[] getResults()
   {
      return results;
   }

   public void setResults(ShopSection[] results)
   {
      this.results = results;
   }
}
