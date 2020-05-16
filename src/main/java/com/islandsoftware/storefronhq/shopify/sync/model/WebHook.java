package com.islandsoftware.storefronhq.shopify.sync.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties (ignoreUnknown = true)
public class WebHook
{
   private long id;
   private String topic;
   private String address;
   private String format;

   public long getId()
   {
      return id;
   }

   public void setId(long id)
   {
      this.id = id;
   }

   public String getTopic()
   {
      return topic;
   }

   public void setTopic(String topic)
   {
      this.topic = topic;
   }

   public String getAddress()
   {
      return address;
   }

   public void setAddress(String address)
   {
      this.address = address;
   }

   public String getFormat()
   {
      return format;
   }

   public void setFormat(String format)
   {
      this.format = format;
   }

   @Override
   public String toString()
   {
      return "WebHook{" +
            "id=" + id +
            ", topic='" + topic + '\'' +
            ", address='" + address + '\'' +
            ", format='" + format + '\'' +
            '}';
   }
}
