package com.islandsoftware.storefronhq.shopify.sync.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NgrocResponse
{
   @JsonProperty("tunnels")
   private List<NgrocTunnel> tunnels;

   public List<NgrocTunnel> getTunnels()
   {
      return tunnels;
   }
}
