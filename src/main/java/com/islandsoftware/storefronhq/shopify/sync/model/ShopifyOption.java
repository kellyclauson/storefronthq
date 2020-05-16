package com.islandsoftware.storefronhq.shopify.sync.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShopifyOption
{
   private String name;
   private String[] values;

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public String[] getValues()
   {
      return values;
   }

   public void setValues(String[] values)
   {
      this.values = values;
   }
}
