package com.islandsoftware.storefronhq.etsy.model.inventory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InventoryProduct
{
   @JsonProperty("product_id")
   private Long productId;
   @JsonProperty("property_values")
   private InventoryPropertyValue[] propertyValues;
   @JsonProperty("offerings")
   private InventoryOffering[] inventoryOfferings;
   @JsonProperty("sku")
   private String sku = "";
   @JsonProperty("is_deleted")
   private int deleted = 0;

   public Long getProductId()
   {
      return productId;
   }

   public void setProductId(Long productId)
   {
      this.productId = productId;
   }

   public InventoryPropertyValue[] getPropertyValues()
   {
      return propertyValues;
   }

   public void setPropertyValues(InventoryPropertyValue[] propertyValues)
   {
      this.propertyValues = propertyValues;
   }

   public InventoryOffering[] getInventoryOfferings()
   {
      return inventoryOfferings;
   }

   public void setInventoryOfferings(InventoryOffering[] inventoryOfferings)
   {
      this.inventoryOfferings = inventoryOfferings;
   }

   public String getSku()
   {
      return sku;
   }

   public void setSku(String sku)
   {
      this.sku = sku;
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
   "product_id":2226187166,
         "property_values":[
   {
      "property_id":506,
         "property_name":"Length",
         "scale_id":null,
         "scale_name":null,
         "value_ids":[
      3535129615
      ],
      "values":[
      "1\/2 Yard"
      ]
   }
   ],
         "offerings":[
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
   ]
   */
}
