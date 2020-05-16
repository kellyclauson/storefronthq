package com.islandsoftware.storefronhq;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.islandsoftware.storefronhq.etsy.EtsyClient;
import com.islandsoftware.storefronhq.etsy.SpindleAndRoseEtsyClient;
import com.islandsoftware.storefronhq.shopify.sync.NgrocClient;
import com.islandsoftware.storefronhq.shopify.sync.SpindleAndRoseRestService;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.apache.cxf.jaxrs.swagger.Swagger2Feature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpindleAndRoseServer
{
   private static final Logger LOGGER = LoggerFactory.getLogger(SpindleAndRoseServer.class);

   public static void main(String[] args)
   {
      try
      {
         new SpindleAndRoseServer();

      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
   }

   public SpindleAndRoseServer() throws Exception
   {
      LOGGER.info("SpindleAndRose Server Starting...");
      //final ShopifyClient shopifyClient = new ShopifyClient();
      EtsyClient spindleEtsyClient = new SpindleAndRoseEtsyClient();
      //EtsyClient imagineEtsyClient = new ImaginationFabricClient();

      final StoreSync storeSync = new StoreSync();
      storeSync.initialize(null, spindleEtsyClient);
      storeSync.startSchedulers();

      // query ngroc to find our public url and tcp info
      NgrocClient ngrocClient = new NgrocClient();
      String publicUrl = ngrocClient.getPublicUrl();
      LOGGER.info("Public URL: " + publicUrl);
      String tcpInfo = ngrocClient.getTcpInfo();
      LOGGER.info("TCP info: {}", tcpInfo);
      SMSClient.alertAdmin("publicUrl=" + publicUrl.replace("https://", "").replace(".ngrok.io", ""));
      SMSClient.alertAdmin("tcpInfo=" + tcpInfo.replace("tcp://0.tcp.ngrok.io:", ""));

      // start listening for notifications from shopify
      startRestService(storeSync, ngrocClient);
      LOGGER.info("SpindleAndRose Rest Service started");

      // initialize shopify webhooks
      //shopifyClient.createWebHooks(publicUrl);
      LOGGER.info("SpindleAndRose Server Started");

      Runtime.getRuntime().addShutdownHook(new Thread(new Runnable()
      {
         public void run()
         {
            LOGGER.info("Shutdown signal received");
            //shopifyClient.shutdown();
            storeSync.shutdown();
            LOGGER.info("shut down complete");
         }
      }, "SpindleAndRose Server Shutdown"));
   }

   private void startRestService(StoreSync storeSync, NgrocClient ngrocClient) throws Exception
   {
      Swagger2Feature feature = new Swagger2Feature();
      feature.setBasePath("/spindleandrose");

      JAXRSServerFactoryBean factory = new JAXRSServerFactoryBean();
      factory.setResourceClasses(SpindleAndRoseRestService.class);
      factory.setResourceProvider(SpindleAndRoseRestService.class, new SingletonResourceProvider(new SpindleAndRoseRestService(storeSync, ngrocClient)));
      factory.setProvider(new JacksonJsonProvider());
      factory.getFeatures().add(feature);

      String port = System.getProperty("rest.service.port");
      if (port == null)
      {
         throw new Exception("System property rest.service.port must be set");
      }
      String address = "http://localhost:" + port;
      LOGGER.info("SpindleAndRose: address: " + address);
      factory.setAddress(address);
      factory.create();
   }
}
