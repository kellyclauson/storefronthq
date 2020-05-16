package com.islandsoftware.storefronhq.etsy.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionVariation
{
   @JsonProperty("formatted_value")
   private String formattedValue;

   public String getFormattedValue()
   {
      return formattedValue;
   }

   public void setFormattedValue(String formattedValue)
   {
      this.formattedValue = formattedValue;
   }
}
