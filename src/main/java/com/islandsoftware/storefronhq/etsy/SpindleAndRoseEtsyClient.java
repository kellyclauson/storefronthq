package com.islandsoftware.storefronhq.etsy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpindleAndRoseEtsyClient extends EtsyClient
{
   private final static String KEY = "mwnokgdpwfqx5069esfb4v21";
   private final static String SECRET = "pnrakqlviv";
   //private final static String TOKEN = "c475948a5effc0e5928cd448e15b6f";
   //private final static String TOKEN_SECRET = "2111299970";
   private final static String TOKEN = "54199bb97d5ebebcbc5189e0b91e89";
   private final static String TOKEN_SECRET = "f9674a88cf";

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
