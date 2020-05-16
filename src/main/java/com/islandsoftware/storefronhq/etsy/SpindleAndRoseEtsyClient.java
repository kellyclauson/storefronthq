package com.islandsoftware.storefronhq.etsy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpindleAndRoseEtsyClient extends EtsyClient
{
   private final static String KEY = "";
   private final static String SECRET = "";
   private final static String TOKEN = "";
   private final static String TOKEN_SECRET = "";

   private final static long STORE_ID = 13195966;

   private static final long SHIPPING_TEMPLATE = 28131275311L;
   private static final long SHIPPING_TEMPLATE_FREE_SHIPPING_US = 59853488996L;


   // this user id is returned from the etsy api when requesting the user id for a listing
   private final static long USER_ID = 88614744;

   private static final Logger LOGGER = LoggerFactory.getLogger(SpindleAndRoseEtsyClient.class);

   public SpindleAndRoseEtsyClient()
   {
   }

   public SpindleAndRoseEtsyClient(String state)
   {
      super(state);
   }

   public String getStoreName()
   {
      return "Spindle+Rose";
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
      return SHIPPING_TEMPLATE;
   }
   protected Logger getLogger()
   {
      return LOGGER;
   }
}
