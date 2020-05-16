package com.islandsoftware.storefronhq.etsy.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReceiptsParams
{
   private int limit;

   public int getLimit()
   {
      return limit;
   }

   public void setLimit(int limit)
   {
      this.limit = limit;
   }
}
