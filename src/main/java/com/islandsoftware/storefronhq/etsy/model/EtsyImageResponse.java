package com.islandsoftware.storefronhq.etsy.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EtsyImageResponse
{
   @JsonProperty("count")
   private Integer count;
   @JsonProperty("results")
   private EtsyImage[] results;

   public Integer getCount()
   {
      return count;
   }

   public void setCount(Integer count)
   {
      this.count = count;
   }

   public EtsyImage[] getResults()
   {
      return results;
   }

   public void setResults(EtsyImage[] results)
   {
      this.results = results;
   }
}
