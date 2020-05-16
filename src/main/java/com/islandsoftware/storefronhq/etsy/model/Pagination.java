package com.islandsoftware.storefronhq.etsy.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Pagination
{
   @JsonProperty("next_offset")
   private Integer nextOffset;

   public Integer getNextOffset()
   {
      return nextOffset;
   }

   public void setNextOffset(Integer nextOffset)
   {
      this.nextOffset = nextOffset;
   }
}
