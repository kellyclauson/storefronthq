package com.islandsoftware.storefronhq.etsy.model.inventory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class InventoryParams
{
   @JsonProperty("listing_id")
   private String listingId;
   @JsonProperty("write_missing_inventory")
   private  Boolean writeMissingInventory;

   public String getListingId()
   {
      return listingId;
   }

   public void setListingId(String listingId)
   {
      this.listingId = listingId;
   }

   public Boolean isWriteMissingInventory()
   {
      return writeMissingInventory;
   }

   public void setWriteMissingInventory(Boolean writeMissingInventory)
   {
      this.writeMissingInventory = writeMissingInventory;
   }

/*
   "params":{
   "listing_id":"463449440",
         "write_missing_inventory":false
   }
   */
}
