package com.islandsoftware.storefronhq.etsy.model.inventory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class InventoryResponse
{
   @JsonProperty("count")
   private Integer count;
   @JsonProperty("results")
   private InventoryResults inventoryResults;
   @JsonProperty("params")
   private InventoryParams inventoryParams;
   @JsonProperty("pagination")
   private InventoryPagination inventoryPagination;
   @JsonProperty("type")
   private String type;

   public Integer getCount()
   {
      return count;
   }

   public void setCount(Integer count)
   {
      this.count = count;
   }

   public InventoryResults getInventoryResults()
   {
      return inventoryResults;
   }

   public void setInventoryResults(InventoryResults inventoryResults)
   {
      this.inventoryResults = inventoryResults;
   }

   public InventoryParams getInventoryParams()
   {
      return inventoryParams;
   }

   public void setInventoryParams(InventoryParams inventoryParams)
   {
      this.inventoryParams = inventoryParams;
   }

   public InventoryPagination getInventoryPagination()
   {
      return inventoryPagination;
   }

   public void setInventoryPagination(InventoryPagination inventoryPagination)
   {
      this.inventoryPagination = inventoryPagination;
   }

   public String getType()
   {
      return type;
   }

   public void setType(String type)
   {
      this.type = type;
   }

   /*
   {
   "count":1,
   "results":{
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
         },
         {
            "product_id":2226187168,
            "property_values":[
               {
                  "property_id":506,
                  "property_name":"Length",
                  "scale_id":null,
                  "scale_name":null,
                  "value_ids":[
                     2974720998
                  ],
                  "values":[
                     "1 Yard"
                  ]
               }
            ],
            "offerings":[
               {
                  "offering_id":2081469657,
                  "price":{
                     "amount":1424,
                     "divisor":100,
                     "currency_code":"USD",
                     "currency_formatted_short":"$14.24",
                     "currency_formatted_long":"$14.24 USD",
                     "currency_formatted_raw":"14.24"
                  },
                  "quantity":3
               }
            ]
         }
      ]
   },
   "params":{
      "listing_id":"463449440",
      "write_missing_inventory":false
   },
   "type":"ListingInventory",
   "pagination":{

   }
}
    */
}
