package com.islandsoftware.storefronhq.etsy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImaginationFabricClient extends EtsyClient
{

   // this user id comes from the url when viewing the user's profile in the etsy store

   // this user id is returned from the etsy api when requesting the user id for a listing
   private final static long USER_ID =165381432;

   private final static String KEY = "";
   private final static String SECRET = "";
   private final static String TOKEN = "";
   private final static String TOKEN_SECRET = "";
   private final static long STORE_ID = 18259728;


   private static final Logger LOGGER = LoggerFactory.getLogger(ImaginationFabricClient.class);

   public ImaginationFabricClient()
   {
   }

   public ImaginationFabricClient(String state)
   {
      super(state);
   }

   public String getStoreName()
   {
      return "ImaginationFabric";
   }

   protected long getStoreId()
   {
      return STORE_ID;
   }
   protected String getKey()
   {
      return KEY;
   }
   protected String getSecret()
   {
      return SECRET;
   }
   protected String getToken()
   {
      return TOKEN;
   }
   protected String getTokenSecret()
   {
      return TOKEN_SECRET;
   }
   public long getUserId()
   {
      return USER_ID;
   }
   public long getShippingTemplateId()
   {
      return 60586542337L;
   }
   protected Logger getLogger()
   {
      return LOGGER;
   }
}
