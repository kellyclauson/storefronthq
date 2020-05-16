package com.islandsoftware.storefronhq.etsy;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.islandsoftware.storefronhq.etsy.model.*;
import com.islandsoftware.storefronhq.etsy.model.inventory.InventoryResponse;
import com.islandsoftware.storefronhq.OAuthFilter;
import com.islandsoftware.storefronhq.SMSClient;
import com.islandsoftware.storefronhq.etsy.model.inventory.InventoryProduct;
import com.islandsoftware.storefronhq.tools.Utils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.ContentDisposition;
import org.slf4j.Logger;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public abstract class EtsyClient
{
   /*
   private final static String SPINDLE_AND_ROSE_KEY = "mwnokgdpwfqx5069esfb4v21";
   private final static String SPINDLE_AND_ROSE_SECRET = "pnrakqlviv";
   private final static String SPINDLE_AND_ROSE_TOKEN = "c475948a5effc0e5928cd448e15b6f";
   private final static String SPINDLE_AND_ROSE_TOKEN_SECRET = "2111299970";
   private final static long SPINDLE_AND_ROSE_STORE_ID = 13195966;

   private final static String IMAGINATION_FABRIC_APP_NAME = "ImaginationFabricListingManager";
   private final static String IMAGINATION_FABRIC_KEY_STRING = "vgk2jk9v57ji9znnnpyf32hs";
   private final static String IMAGINATION_FABRIC_SHARED_SECRET = "irjgl0ylax";
   //oauth_token=3b412b2e118b37c1ee624584c85066&oauth_token_secret=543a331ea4
   private final static String IMAGINATION_FABRIC_TOKEN = "3b412b2e118b37c1ee624584c85066";
   private final static String IMAGINATION_FABRIC_TOKEN_SECRET = "543a331ea4";
   private final static long IMAGINATION_FABRIC_STORE_ID = 18259728;
   */

   private Map<Long, String> id2Title;
   private Map<String, Long> title2Id;
   private Map<Integer, Country> countryId2Country;
   private WebClient client;

   private final static String DEFAULT_FIELDS = "title,tags,quantity,listing_id,item_weight,category_id,materials,processing_min,processing_max,taxonomy_id,description";

   public abstract  String getStoreName();
   public abstract  long getShippingTemplateId();
   protected abstract  long getStoreId();
   protected abstract  String getKey();
   protected abstract  String getSecret();
   protected abstract  String getToken();
   protected abstract  String getTokenSecret();
   public abstract  long getUserId();
   protected abstract  Logger getLogger();

   public EtsyClient()
   {
      client = createClient();
      buildMaps();
   }

   public EtsyClient(String state)
   {
      client = createClient();
      buildMaps(state);
   }

   public Map<Long, String> getId2Title() { return id2Title; }
   public Map<String, Long> getTitle2Id() { return title2Id; }

   public Map<Integer, Country> getCountryId2Country()
   {
      return countryId2Country;
   }

   public void update()
   {
      buildMaps();
   }

   private WebClient createClient()
   {
      getLogger().info("createClient: creating connection to etsy");
      String url = "https://openapi.etsy.com/v2";
      List<Object> providers = new ArrayList<Object>(2);
      providers.add(new JacksonJaxbJsonProvider());
      providers.add(new OAuthFilter(getKey(), getSecret(), getToken(), getTokenSecret()));
      return WebClient.create(url, providers).type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);
   }

   public void shutdown()
   {
      getLogger().info("shutdown: closing connection to etsy");
      if (client != null)
      {
         client.close();
      }
   }

   public synchronized ReceiptsResponse receipts(long minCreated)
   {
      //String url = "https://openapi.etsy.com/v2/shops/13195966/receipts?min_created=1482592731";
      try
      {
         client.back(true);
         client.resetQuery();
         client.path("shops");
         client.path(getStoreId());
         client.path("receipts");
         client.query("min_created", minCreated);
         client.query("includes", "Transactions");
         getLogger().debug("GET: {}", client.getCurrentURI());
         return client.get(ReceiptsResponse.class);
      }
      catch (Exception e)
      {
         if (client.getResponse() == null)
         {
            getLogger().error("response is null, msg={}", e.getMessage());
         }
         else
         {
            getLogger().error("status={}, msg={}, body={}", client.getResponse().getStatus(), e.getMessage(), Utils.getStringFromInputStream((InputStream)client.getResponse().getEntity()));
         }
         return null;
      }
   }

   public synchronized ListingsResponse getListing(Long listingId, String includes)
   {
      getLogger().info("getListing: begin, listingId={} includes={}", listingId, includes);
      // https://openapi.etsy.com/v2/listings/484719707?includes=Images,Variations
      try
      {
         client.back(true);
         client.resetQuery();
         client.path("listings");
         client.path(listingId);
         if (includes != null)
         {
            client.query("includes", includes);
         }
         getLogger().info("GET: {}", client.getCurrentURI());
         getLogger().info("getListing: complete - success");
         //String s = client.get(String.class);
         //getLogger().info("RESPONSE:\n{}", s);
         //return null;
         ListingsResponse listingsResponse = client.get(ListingsResponse.class);
         return listingsResponse;
      }
      catch (Exception e)
      {
         if (client.getResponse() != null)
         {
            String body = Utils.getStringFromInputStream((InputStream)client.getResponse().getEntity());
            getLogger().error("status={}, msg={}, body={}", client.getResponse().getStatus(), e.getMessage(), body);
         }
         getLogger().error("getListing: complete - fail", e);
         return null;
      }
      finally
      {
         getLogger().info("getListing: end, listingId={} includes={}", listingId, includes);
      }
   }

   public synchronized String getOffering(Long listingId, Long productId, Long offeringId)
   {
      getLogger().info("getOffering: begin, listingId={}, productId={}, offeringId={}", listingId, productId, offeringId);
      try
      {
         client.back(true);
         client.resetQuery();
         client.path("listings");
         client.path(listingId);
         client.path("products");
         client.path(productId);
         client.path("offerings");
         client.path(offeringId);
         getLogger().info("GET: {}", client.getCurrentURI());
         //return client.get(InventoryResponse.class);
         return client.get(String.class);
      }
      catch (Exception e)
      {
         if (client.getResponse() == null)
         {
            getLogger().error("response is null, msg={}", e.getMessage());
         }
         else
         {
            getLogger().error("status={}, msg={}, body={}", client.getResponse().getStatus(), e.getMessage(), Utils.getStringFromInputStream((InputStream)client.getResponse().getEntity()));
         }
         return null;
      }
      finally
      {
         getLogger().info("getOffering: end, listingId={}", listingId);
      }
   }

   public synchronized InventoryResponse getInventory(Long listingId) throws Exception
   {
      getLogger().info("getInventory: begin, listingId={}", listingId);
      if (listingId == null)
      {
         return null;
      }
      try
      {
         client.back(true);
         client.resetQuery();
         client.path("listings");
         client.path(listingId);
         client.path("inventory");
         getLogger().info("GET: {}", client.getCurrentURI());
         InventoryResponse response = client.get(InventoryResponse.class);
         getLogger().info("getInventory: end, listingId={}", listingId);
         return response;
         //String r = client.get(String.class);
         //getLogger().info("RESPONSE=\n{}", r);
         //return null;
      }
      catch (Exception e)
      {
         if (client.getResponse() == null)
         {
            throw new Exception("response is null", e);
         }
         else
         {
            String msg = "status=" + client.getResponse().getStatus() + " body=" + Utils.getStringFromInputStream((InputStream)client.getResponse().getEntity());
            throw new Exception(msg, e);
         }
      }
   }

   public int updateInventory(Long listingId, InventoryProduct[] products)
   {
      String updatedProductJson = Utils.toJson(products);
      return updateInventory(listingId, updatedProductJson);
   }

   public synchronized int updateInventory(Long listingId, String products)
   {
      getLogger().info("updateInventory: listingId={}", listingId);
      try
      {
         client.back(true);
         client.resetQuery();
         client.path("listings");
         client.path(listingId);
         client.path("inventory");
         client.query("products", products);
         client.query("price_on_property", 506);
         client.query("quantity_on_property", 506);
         //client.query("sku_on_property", 506);
         //client.header("Content-Length", products.length());
         getLogger().info("PUT: {}", client.getCurrentURI());
         client.put("products=" + products);
         int status = client.getResponse().getStatus();
         getLogger().info("updatedInventory: status {}", status);
         if (status > 200)
         {
            getLogger().error("updatedInventory: status={}, body={}", client.getResponse().getStatus(), Utils.getStringFromInputStream((InputStream)client.getResponse().getEntity()));
            //SMSClient.alertAdmin("updateInventory: failed, status=" + status);
         }
         return status;
      }
      catch (Exception e)
      {
         SMSClient.alertAdmin("updateInventory: " + e.getMessage());
         if (client.getResponse() == null)
         {
            getLogger().error("updatedInventory: response is null, msg={}", e.getMessage());
         }
         else
         {
            getLogger().error("updatedInventory: status={}, msg={}, body={}", client.getResponse().getStatus(), e.getMessage(), Utils.getStringFromInputStream((InputStream)client.getResponse().getEntity()));
         }
         return 500;
      }
   }

   public synchronized VariationsResponse getVariations(Long listingId)
   {
      try
      {
         client.back(true);
         client.resetQuery();
         client.path("listings");
         client.path(listingId);
         client.path("variations");
         getLogger().info("GET: {}", client.getCurrentURI());
         return client.get(VariationsResponse.class);
      }
      catch (Exception e)
      {
         if (client.getResponse() == null)
         {
            getLogger().error("response is null, msg={}", e.getMessage());
         }
         else
         {
            getLogger().error("status={}, msg={}, body={}", client.getResponse().getStatus(), e.getMessage(), Utils.getStringFromInputStream((InputStream)client.getResponse().getEntity()));
         }
         return null;
      }
   }

   public synchronized void updateVariation(Long listingId, Long propertyId, String value, double price)
   {
      try
      {
         client.back(true);
         client.resetQuery();
         client.path("listings");
         client.path(listingId);
         client.path("variations");
         client.path(propertyId);
         client.query("value", value);
         client.query("is_available", true);
         client.query("price", price);
         getLogger().info("POST: {}", client.getCurrentURI());
         client.post(null);
         int status = client.getResponse().getStatus();
         getLogger().info("status {}", status);
         if (status > 201)
         {
            getLogger().error("Body: " + Utils.getStringFromInputStream((InputStream)client.getResponse().getEntity()));
            MultivaluedMap<String, Object> headers = client.getResponse().getHeaders();
            for (Entry<String, List<Object>> entry : headers.entrySet())
            {
               getLogger().error(entry.getKey() + ": " + entry.getValue());
            }
         }
      }
      catch (Exception e)
      {
         getLogger().error("status={}, msg={}, body={}", client.getResponse().getStatus(), e.getMessage(), Utils.getStringFromInputStream((InputStream)client.getResponse().getEntity()));
      }
   }

   public synchronized void updateVariation(Long listingId, String variations)
   {
      try
      {
         client.back(true);
         client.resetQuery();
         client.path("listings");
         client.path(listingId);
         client.path("variations");
         client.query("variations", variations);
         client.query("length_scale", "352");
         getLogger().info("PUT: {}", client.getCurrentURI());
         client.put(null);
         int status = client.getResponse().getStatus();
         getLogger().info("status {}", status);
         if (status > 201)
         {
            getLogger().error("Body: " + Utils.getStringFromInputStream((InputStream)client.getResponse().getEntity()));
            MultivaluedMap<String, Object> headers = client.getResponse().getHeaders();
            for (Entry<String, List<Object>> entry : headers.entrySet())
            {
               getLogger().error(entry.getKey() + ": " + entry.getValue());
            }
         }
      }
      catch (Exception e)
      {
         getLogger().error("status={}, msg={}, body={}", client.getResponse().getStatus(), e.getMessage(), Utils.getStringFromInputStream((InputStream)client.getResponse().getEntity()));
      }
   }

   public synchronized int updateShippingTemplateId(Long listingId, long id)
   {
      try
      {
         client.back(true);
         client.resetQuery();
         client.path("listings");
         client.path(listingId);
         client.query("shipping_template_id", id);
         getLogger().info("PUT: {}", client.getCurrentURI());
         client.put("shipping_template_id=" + id);
         int status = client.getResponse().getStatus();
         getLogger().info("updateShippingTemplateId: {} status {}", getStoreName(), status);
         if (status > 201)
         {
            getLogger().error("Body: " + Utils.getStringFromInputStream((InputStream)client.getResponse().getEntity()));
            MultivaluedMap<String, Object> headers = client.getResponse().getHeaders();
            for (Entry<String, List<Object>> entry : headers.entrySet())
            {
               getLogger().error(entry.getKey() + ": " + entry.getValue());
            }
         }
         return status;
      }
      catch (Exception e)
      {
         getLogger().error("status={}, msg={}, body={}", client.getResponse().getStatus(), e.getMessage(), Utils.getStringFromInputStream((InputStream)client.getResponse().getEntity()));
         return 500;
      }

   }

   public synchronized int updateListingState(Long listingId, String state)
   {
      try
      {
         client.back(true);
         client.resetQuery();
         client.path("listings");
         client.path(listingId);
         client.query("state", state);
         getLogger().info("PUT: {}", client.getCurrentURI());
         client.put("state=" + state);
         int status = client.getResponse().getStatus();
         getLogger().info("updateListingState: {} status {}", getStoreName(), status);
         if (status > 201)
         {
            getLogger().error("Body: " + Utils.getStringFromInputStream((InputStream)client.getResponse().getEntity()));
            MultivaluedMap<String, Object> headers = client.getResponse().getHeaders();
            for (Entry<String, List<Object>> entry : headers.entrySet())
            {
               getLogger().error(entry.getKey() + ": " + entry.getValue());
            }
         }
         return status;
      }
      catch (Exception e)
      {
         getLogger().error("status={}, msg={}, body={}", client.getResponse().getStatus(), e.getMessage(), Utils.getStringFromInputStream((InputStream)client.getResponse().getEntity()));
         return 500;
      }

   }

   public synchronized int updateListing(Long listingId, Map<String, String> params)
   {
      try
      {
         client.back(true);
         client.resetQuery();
         client.path("listings");
         client.path(listingId);
         for (Entry<String, String> entry : params.entrySet())
         {
            client.query(entry.getKey(), entry.getValue());
         }
         getLogger().info("PUT: {}", client.getCurrentURI());
         client.put("title=" + params.get("title"));
         int status = client.getResponse().getStatus();
         getLogger().info("updateListing: {} status {}", getStoreName(), status);
         if (status > 201)
         {
            getLogger().error("Body: " + Utils.getStringFromInputStream((InputStream)client.getResponse().getEntity()));
            MultivaluedMap<String, Object> headers = client.getResponse().getHeaders();
            for (Entry<String, List<Object>> entry : headers.entrySet())
            {
               getLogger().error(entry.getKey() + ": " + entry.getValue());
            }
         }
         return status;
      }
      catch (Exception e)
      {
         getLogger().error("status={}, msg={}, body={}", client.getResponse().getStatus(), e.getMessage(), Utils.getStringFromInputStream((InputStream)client.getResponse().getEntity()));
         return 500;
      }
   }

   public Map<String, ListingsResult> listings()
   {
      getLogger().info("listings");
      return listings("active", null);
   }

   public Map<String, ListingsResult> listings(String additionalFields)
   {
      getLogger().info("listings");
      return listings("active", additionalFields);
   }

   public Map<String, ListingsResult> listings(String state, String additionalFields)
   {
      getLogger().info("listings: begin, additionalFields={}", additionalFields);
      Map<String, ListingsResult> title2Quantity = new HashMap<String, ListingsResult>();
      ListingsResponse response = listings(0, state, additionalFields);
      if (response != null)
      {
         int count = response.getCount();
         getLogger().info("found {} total listings", count);
         ListingsResult[] results = response.getResults();
         for (ListingsResult result : results)
         {
            ListingsResult existing = title2Quantity.put(result.getTitle().trim(), result);
            if (existing != null)
            {
               getLogger().warn("Duplicate title {}", result.getTitle());
            }
         }
         Integer nextOffset = response.getPagination().getNextOffset();
         getLogger().debug("nextOffset={}", nextOffset);
         while (nextOffset != null)
         {
            response = listings(nextOffset, state, additionalFields);
            results = response.getResults();
            for (ListingsResult result : results)
            {
               ListingsResult existing = title2Quantity.put(result.getTitle().trim(), result);
               if (existing != null)
               {
                  getLogger().warn("Duplicate title {}", result.getTitle());
               }
            }
            nextOffset = response.getPagination().getNextOffset();
            getLogger().debug("nextOffset={}", nextOffset);
         }
      }
      getLogger().info("listings: end, size={} additionalFields={}", title2Quantity.size(), additionalFields);
      return title2Quantity;
   }

   private synchronized ListingsResponse listings(int offset, String state, String additionalFields)
   {
      getLogger().info("listings: begin, offset={} state={} additionalFields={}", offset, state, additionalFields);
      //String url = "https://openapi.etsy.com/v2/shops/13195966/listings/active?fields=title,quantity&limit=100&offset=0";
      try
      {
         client.back(true);
         client.resetQuery();
         client.path("shops");
         client.path(getStoreId());
         client.path("listings");
         client.path(state);
         if (additionalFields == null)
         {
            client.query("fields", DEFAULT_FIELDS);
         }
         else
         {
            client.query("fields", DEFAULT_FIELDS + "," + additionalFields);
         }
         client.query("limit", "100");
         client.query("offset", String.valueOf(offset));
         getLogger().info("GET: {}", client.getCurrentURI());
         return client.get(ListingsResponse.class);
      }
      catch (Exception e)
      {
         getLogger().error("status={}, msg={}, body={}", client.getResponse().getStatus(), e.getMessage(), Utils.getStringFromInputStream((InputStream)client.getResponse().getEntity()));
         return null;
      }
      finally
      {
         getLogger().info("listings: end, offset={} state={} additionalFields={}", offset, state, additionalFields);
      }
   }

   public boolean isListingActive(Long listingId)
   {
      getLogger().info("isListingActive: begin, listingId={}", listingId);
      ListingsResponse listingsResponse = getListing(listingId, "state");
      ListingsResult result = listingsResponse.getResults()[0];
      String state = result.getState();
      getLogger().info("isListingActive: state={} listingId={}", state, listingId);
      return state.equals("active");
   }

   public synchronized void uploadImage(Long listingId, Integer rank, String filename, String extension) throws Exception
   {
      getLogger().info("uploadImage: begin, listingId={} rank={} filename={} extension={}", listingId, rank, filename, extension);
      InputStream is = new FileInputStream(filename);
      client.back(true);
      client.resetQuery();
      client.type(MediaType.MULTIPART_FORM_DATA_TYPE);
      client.path("listings");
      client.path(listingId);
      client.path("images");
      client.query("rank", rank);

      String s = "attachment;name=image;filename=image" + rank + "." + extension + ";rank=" + rank;
      ContentDisposition cd = new ContentDisposition(s);
      Attachment att = new Attachment("root", is, cd);
      getLogger().info("POST: {}", client.getCurrentURI());
      client.post(att);
      int status = client.getResponse().getStatus();
      getLogger().info("status {}", status);
      if (status > 201)
      {
         getLogger().error("Body: " + Utils.getStringFromInputStream((InputStream)client.getResponse().getEntity()));
         MultivaluedMap<String, Object> headers = client.getResponse().getHeaders();
         for (Map.Entry<String, List<Object>> entry : headers.entrySet())
         {
            getLogger().error(entry.getKey() + ": " + entry.getValue());
         }
         throw new Exception("uploadImage: status=" + status);
      }
   }

   public synchronized void createShippingTemplate()
   {
      try
      {
         client.back(true);
         client.resetQuery();
         client.path("shipping");
         client.path("templates");
         client.query("title", "MyShippingTemplate");
         client.query("origin_country_id", 209 );
         client.query("primary_cost", 1.00);
         client.query("secondary_cost", 1.00);
         getLogger().info("POST: {}", client.getCurrentURI());
         client.post(null);
         getLogger().info("createShippingTemplate: status={}", client.getResponse().getStatus());
      }
      catch (Exception e)
      {
         getLogger().error("createShippingTemplate: status={}, msg={}, body={}", client.getResponse().getStatus(), e.getMessage(), Utils.getStringFromInputStream((InputStream)client.getResponse().getEntity()));
      }

   }

   public synchronized void getShippingTemplates()
   {
      try
      {
         client.back(true);
         client.resetQuery();
         client.path("users");
         client.path(getUserId());
         client.path("shipping");
         client.path("templates");
         getLogger().info("GET: {}", client.getCurrentURI());
         String s = client.get(String.class);
         getLogger().info("getShippingTemplates: status={}", client.getResponse().getStatus());
         getLogger().info("getShippingTemplates: response={}", s);
      }
      catch (Exception e)
      {
         getLogger().error("getShippingTemplates: status={}, msg={}, body={}", client.getResponse().getStatus(), e.getMessage(), Utils.getStringFromInputStream((InputStream)client.getResponse().getEntity()));
      }

   }

   public synchronized void createListing(int quantity, String title, String tags, String description, double price, String materials, long sectionId, long categoryId, long taxonomyId) throws Exception
   {
      getLogger().debug("createListing: begin, quantity={} title={} tags={} description={} price={} materials={} sectionId={} categoryId={} taxonomyId={}",
         quantity, title, tags, description, price, materials, sectionId, categoryId, taxonomyId);

      try
      {
         client.back(true);
         client.resetQuery();
         client.path("listings");
         client.query("quantity", quantity);
         client.query("title", title);
         client.query("tags", tags);
         client.query("description", description);
         client.query("price", price);
         client.query("who_made", "someone_else");
         client.query("is_supply", true);
         client.query("is_customizable", true);
         client.query("when_made", "2010_2018");
         client.query("state", "draft");
         client.query("shipping_template_id", getShippingTemplateId());
         client.query("processing_min", 1);
         client.query("processing_max", 3);
         client.query("category_id", categoryId);
         client.query("taxonomy_id", taxonomyId);
         client.query("materials", materials);
         client.query("shop_section_id", sectionId);
         client.query("should_auto_renew", true);
         getLogger().info("POST: {}", client.getCurrentURI());
         client.post(null);
         int status = client.getResponse().getStatus();
         getLogger().info("createListing: status={}", status);
         getLogger().info("createListing: msg={}", Utils.getStringFromInputStream((InputStream)client.getResponse().getEntity()));
         if (status > 201)
         {
            throw new Exception("createListing: status=" + status + " title=" + title);
         }
      }
      /*
      catch (Exception e)
      {
         getLogger().error("createListing", e);
         getLogger().error("createListing: msg={}", Utils.getStringFromInputStream((InputStream)client.getResponse().getEntity()));
         //getLogger().error("createListing: status={}, msg={}, body={}", client.getResponse().getStatus(), e.getMessage(), Utils.getStringFromInputStream((InputStream)client.getResponse().getEntity()));
      }
      */
      finally
      {
         getLogger().debug("createListing: end, quantity={} title={} description={} price={}", quantity, title, description, price);
      }
   }

   public synchronized Map<String, Long> getShopSectionMap()
   {
      Map<String, Long> map = new HashMap<>();
      getLogger().info("getShopSectionMap: begin");
      try
      {
         client.back(true);
         client.resetQuery();
         client.path("shops");
         client.path(getStoreId());
         client.path("sections");
         getLogger().info("GET: {}", client.getCurrentURI());
         ShopSectionResponse response = client.get(ShopSectionResponse.class);
         ShopSection[] sections = response.getResults();
         for (ShopSection section : sections)
         {
            map.put(section.getTitle().trim(), section.getId());
         }
         getLogger().info("getShopSectionMap: status={}", client.getResponse().getStatus());
      }
      catch (Exception e)
      {
         getLogger().error("getShopSectionMap: status={}, msg={}, body={}", client.getResponse().getStatus(), e.getMessage(), Utils.getStringFromInputStream((InputStream)client.getResponse().getEntity()));
      }
      finally
      {
         getLogger().info("getShopSectionMap: end");
         return map;
      }
   }

   public synchronized EtsyImageResponse getImages(Long listingId)
   {
      getLogger().info("getImages: begin listingId={}", listingId);
      try
      {
         client.back(true);
         client.resetQuery();
         client.path("listings");
         client.path(listingId);
         client.path("images");
         getLogger().info("GET: {}", client.getCurrentURI());
         EtsyImageResponse response = client.get(EtsyImageResponse.class);
         getLogger().info("getImages: status={}", client.getResponse().getStatus());
         return response;
      }
      catch (Exception e)
      {
         getLogger().error("getImages: status={}, msg={}, body={}", client.getResponse().getStatus(), e.getMessage(), Utils.getStringFromInputStream((InputStream)client.getResponse().getEntity()));
         return null;
      }
      finally
      {
         getLogger().info("getImages: end");
      }
   }

   private synchronized CountriesResponse getCountries()
   {
      getLogger().info("getCountries: begin");
      try
      {
         client.back(true);
         client.resetQuery();
         client.path("countries");
         getLogger().info("GET: {}", client.getCurrentURI());
         CountriesResponse response = client.get(CountriesResponse.class);
         getLogger().info("getCountries: status={}", client.getResponse().getStatus());
         return response;
      }
      catch (Exception e)
      {
         getLogger().error("getCountries: status={}, msg={}, body={}", client.getResponse().getStatus(), e.getMessage(), Utils.getStringFromInputStream((InputStream)client.getResponse().getEntity()));
         return null;
      }
      finally
      {
         getLogger().info("getCountries: end");
      }
   }

   public synchronized void deleteListing(Long listingId)
   {
      getLogger().info("deleteListing: begin listingId={}", listingId);
      client.back(true);
      client.resetQuery();
      client.path("listings");
      client.path(listingId);
      getLogger().info("DELETE: {}", client.getCurrentURI());
      client.delete();
      getLogger().info("deleteListing: status={}", client.getResponse().getStatus());
      getLogger().info("deleteListing: msg={}", Utils.getStringFromInputStream((InputStream)client.getResponse().getEntity()));
      getLogger().info("deleteListing: end");
   }

   private void buildMaps()
   {
      buildMaps("active");
   }

   private void buildMaps(String state)
   {
      getLogger().info("update: begin");
      title2Id = new HashMap<>();
      id2Title = new HashMap<>();
      countryId2Country = new HashMap<>();
      Map<String, ListingsResult> listings = listings(state, null);
      for (ListingsResult listing : listings.values())
      {
         title2Id.put(listing.getTitle().trim(), listing.getListingId());
         id2Title.put(listing.getListingId(), listing.getTitle().trim());
      }
      CountriesResponse response = getCountries();
      Country[] countries = response.getCountries();
      for (Country country : countries)
      {
         countryId2Country.put(country.getCountryId(), country);
      }

      getLogger().info("buildMaps: end, size={}", title2Id.size());
   }
}
