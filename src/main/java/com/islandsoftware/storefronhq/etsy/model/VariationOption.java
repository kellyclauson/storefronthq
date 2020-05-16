package com.islandsoftware.storefronhq.etsy.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VariationOption
{
   private String value;
   @JsonProperty("formatted_value")
   private String formattedValue;
   private double price;

   public String getValue()
   {
      return value;
   }

   public void setValue(String value)
   {
      this.value = value;
   }

   public String getFormattedValue()
   {
      return formattedValue;
   }

   public void setFormattedValue(String formattedValue)
   {
      this.formattedValue = formattedValue;
   }

   public double getPrice()
   {
      return price;
   }

   public void setPrice(double price)
   {
      this.price = price;
   }
}
