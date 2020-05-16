package com.islandsoftware.storefronhq.etsy.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Variation
{
   private VariationOption[] options;

   public VariationOption[] getOptions()
   {
      return options;
   }

   public void setOptions(VariationOption[] options)
   {
      this.options = options;
   }
}
