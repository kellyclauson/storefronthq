package com.islandsoftware.storefronhq.etsy.model.inventory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class InventoryPropertyValue
{
   @JsonProperty("property_id")
   private Long propertyId;
   @JsonProperty("property_name")
   private String propertyName;
   @JsonProperty("scale_id")
   private Long scaleId;
   @JsonProperty("scale_name")
   private String scaleName;
   @JsonProperty("value_ids")
   private Long[] valueIds;
   @JsonProperty("values")
   private String[] values;

   public Long getPropertyId()
   {
      return propertyId;
   }

   public void setPropertyId(Long propertyId)
   {
      this.propertyId = propertyId;
   }

   public String getPropertyName()
   {
      return propertyName;
   }

   public void setPropertyName(String propertyName)
   {
      this.propertyName = propertyName;
   }

   public Long getScaleId()
   {
      return scaleId;
   }

   public void setScaleId(Long scaleId)
   {
      this.scaleId = scaleId;
   }

   public String getScaleName()
   {
      return scaleName;
   }

   public void setScaleName(String scaleName)
   {
      this.scaleName = scaleName;
   }

   public Long[] getValueIds()
   {
      return valueIds;
   }

   public void setValueIds(Long[] valueIds)
   {
      this.valueIds = valueIds;
   }

   public String[] getValues()
   {
      return values;
   }

   public void setValues(String[] values)
   {
      this.values = values;
   }

   /*
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
   */
}
