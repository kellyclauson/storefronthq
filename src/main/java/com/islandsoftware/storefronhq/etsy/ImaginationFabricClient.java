package com.islandsoftware.storefronhq.etsy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImaginationFabricClient extends EtsyClient
{

   // this user id comes from the url when viewing the user's profile in the etsy store
   //private final static String USER_ID = "wcip6vuh";

   // this user id is returned from the etsy api when requesting the user id for a listing
   private final static long USER_ID =165381432;

   private final static String APP_NAME = "ImaginationFabricListingManager";
   private final static String KEY = "vgk2jk9v57ji9znnnpyf32hs";
   private final static String SECRET = "irjgl0ylax";
   //oauth_token=3b412b2e118b37c1ee624584c85066&oauth_token_secret=543a331ea4
   //private final static String TOKEN = "3b412b2e118b37c1ee624584c85066";
   //private final static String TOKEN_SECRET = "543a331ea4";
   private final static String TOKEN = "c23993e7ea71e5019a9acf9b51b165";
   private final static String TOKEN_SECRET = "e69a68e7ad";
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
