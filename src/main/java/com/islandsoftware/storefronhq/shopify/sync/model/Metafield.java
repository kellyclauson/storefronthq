package com.islandsoftware.storefronhq.shopify.sync.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Metafield
{
   @JsonProperty("namespace")
   private String namespace;
   @JsonProperty("key")
   private String key;
   @JsonProperty("value")
   private String value;
   @JsonProperty("value_type")
   private String valueType;

   public String getNamespace()
   {
      return namespace;
   }

   public void setNamespace(String namespace)
   {
      this.namespace = namespace;
   }

   public String getKey()
   {
      return key;
   }

   public void setKey(String key)
   {
      this.key = key;
   }

   public String getValue()
   {
      return value;
   }

   public void setValue(String value)
   {
      this.value = value;
   }

   public String getValueType()
   {
      return valueType;
   }

   public void setValueType(String valueType)
   {
      this.valueType = valueType;
   }

   @Override
   public String toString()
   {
      return "Metafield{" +
            "namespace='" + namespace + '\'' +
            ", key='" + key + '\'' +
            ", value='" + value + '\'' +
            ", valueType='" + valueType + '\'' +
            '}';
   }
}
