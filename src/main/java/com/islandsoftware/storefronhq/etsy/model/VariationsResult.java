package com.islandsoftware.storefronhq.etsy.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VariationsResult
{
   @JsonProperty("property_id")
   private int propertyId;
   @JsonProperty("formatted_name")
   private String formattedName;
   private List<VariationOption> options;

   public int getPropertyId()
   {
      return propertyId;
   }

   public void setPropertyId(int propertyId)
   {
      this.propertyId = propertyId;
   }

   public String getFormattedName()
   {
      return formattedName;
   }

   public void setFormattedName(String formattedName)
   {
      this.formattedName = formattedName;
   }

   public List<VariationOption> getOptions()
   {
      return options;
   }

   public void setOptions(List<VariationOption> options)
   {
      this.options = options;
   }
}
