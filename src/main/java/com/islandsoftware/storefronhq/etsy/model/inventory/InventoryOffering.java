package com.islandsoftware.storefronhq.etsy.model.inventory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class InventoryOffering
{
   @JsonProperty("offering_id")
   private Long offeringId;
   @JsonProperty("price")
   private InventoryOfferingPrice inventoryOfferingPrice;
   @JsonProperty("quantity")
   private Integer quantity;
   @JsonProperty("is_enabled")
   private int enabled = 1;
   @JsonProperty("is_deleted")
   private int deleted = 0;

   public Long getOfferingId()
   {
      return offeringId;
   }

   public void setOfferingId(Long offeringId)
   {
      this.offeringId = offeringId;
   }

   public InventoryOfferingPrice getInventoryOfferingPrice()
   {
      return inventoryOfferingPrice;
   }

   public void setInventoryOfferingPrice(InventoryOfferingPrice inventoryOfferingPrice)
   {
      this.inventoryOfferingPrice = inventoryOfferingPrice;
   }

   public Integer getQuantity()
   {
      return quantity;
   }

   public void setQuantity(Integer quantity)
   {
      this.quantity = quantity;
   }

   public int getEnabled()
   {
      return enabled;
   }

   public void setEnabled(int enabled)
   {
      this.enabled = enabled;
   }

   public int getDeleted()
   {
      return deleted;
   }

   public void setDeleted(int deleted)
   {
      this.deleted = deleted;
   }

   /*
   {
      "offering_id":2081469655,
         "price":{
      "amount":712,
            "divisor":100,
            "currency_code":"USD",
            "currency_formatted_short":"$7.12",
            "currency_formatted_long":"$7.12 USD",
            "currency_formatted_raw":"7.12"
   },
      "quantity":6
   }
   */
}
