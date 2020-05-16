package com.islandsoftware.storefronhq;

import com.islandsoftware.storefronhq.tools.CreateShopifyProducts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateProductsThread implements Runnable
{
   StoreSync storeSync;

   private static final Logger LOGGER = LoggerFactory.getLogger(CreateProductsThread.class);

   public CreateProductsThread(StoreSync storeSync)
   {
      this.storeSync = storeSync;
   }

   public void run()
   {
      try
      {
         LOGGER.info("run: begin update maps");
         storeSync.update();
         LOGGER.info("run: end update maps");
         LOGGER.info("run: begin reload Master Product Map");
         storeSync.loadMasterProductMap();
         LOGGER.info("run: end reload Master Product Map");
         LOGGER.info("run: begin create products");
         CreateShopifyProducts.createProducts(storeSync);
         LOGGER.info("run: end create products");
      }
      catch (Throwable e)
      {
         String msg = "Error in Create Products " + e.getMessage();
         LOGGER.error(msg, e);
         SMSClient.alertAdmin(msg);
      }
   }
}
