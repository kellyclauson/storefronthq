package com.islandsoftware.storefronhq.shopify.sync.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PostWebHookResponse
{
   @JsonProperty("webhook")
   private WebHook webHook;

   public WebHook getWebHook()
   {
      return webHook;
   }

   public void setWebHook(WebHook webHook)
   {
      this.webHook = webHook;
   }
}
