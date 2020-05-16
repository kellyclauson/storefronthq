package com.islandsoftware.storefronhq.shopify.sync.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class GetWebHooksResponse
{
   @JsonProperty ("webhooks")
   private List<WebHook> webHooks;

   public List<WebHook> getWebHooks()
   {
      return webHooks;
   }

   public void setWebHooks(List<WebHook> webHooks)
   {
      this.webHooks = webHooks;
   }
}
