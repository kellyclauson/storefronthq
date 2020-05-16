package com.islandsoftware.storefronhq.shopify.sync.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NgrocTunnel
{
   @JsonProperty ("proto")
   private String proto;
   @JsonProperty ("public_url")
   private String publicUrl;

   public String getProto()
   {
      return proto;
   }

   public void setProto(String proto)
   {
      this.proto = proto;
   }

   public String getPublicUrl()
   {
      return publicUrl;
   }

   public void setPublicUrl(String publicUrl)
   {
      this.publicUrl = publicUrl;
   }

   @Override
   public String toString()
   {
      return "NgrocTunnel{" +
            "proto='" + proto + '\'' +
            ", publicUrl='" + publicUrl + '\'' +
            '}';
   }
}
