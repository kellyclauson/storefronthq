package com.islandsoftware.storefronhq.etsy.model.inventory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class InventoryResults
{
   @JsonProperty("products")
   private InventoryProduct[] products;

   public InventoryProduct[] getProducts()
   {
      return products;
   }

   public void setProducts(InventoryProduct[] products)
   {
      this.products = products;
   }

   /*
   "products":[
   {
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
   }
   ]
   */
}
