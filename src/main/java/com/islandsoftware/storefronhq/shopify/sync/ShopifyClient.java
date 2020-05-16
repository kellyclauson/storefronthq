package com.islandsoftware.storefronhq.shopify.sync;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.islandsoftware.storefronhq.SMSClient;
import com.islandsoftware.storefronhq.shopify.sync.model.*;
import com.islandsoftware.storefronhq.tools.Utils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.transport.http.HTTPConduit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.*;

public class ShopifyClient
{
   WebClient client;
   private TreeMap<Long, String> id2Title;
   private TreeMap<String, Long> title2Id;

   private final static int LIMIT = 250;
   private static final String USER_NAME = "a09fe28c9e42fdd6133b6810fe023297";
   private static final String PASSWORD = "f167d69ee987e652b77998df2d2caf41";

   private static final Logger LOGGER = LoggerFactory.getLogger(ShopifyClient.class);

   public ShopifyClient()
   {
      this(30000L);
   }

   public ShopifyClient(long receiveTimeout)
   {
      String url = "https://spindleandrose.myshopify.com/admin";
      //List<?> providers = Arrays.asList(new JacksonJaxbJsonProvider());
      List providers = new ArrayList<>();
      providers.add(new JacksonJaxbJsonProvider());
      providers.add(new TimeoutExceptionHandler());
      client = WebClient.create(url, providers);
      HTTPConduit conduit = WebClient.getConfig(client).getHttpConduit();
      conduit.getAuthorization().setUserName(USER_NAME);
      conduit.getAuthorization().setPassword(PASSWORD);
      conduit.getClient().setReceiveTimeout(receiveTimeout);

      buildMaps();
   }

   public Map<Long, String> getId2Title() { return id2Title; }
   public Map<String, Long> getTitle2Id() { return title2Id; }

   public void update()
   {
      buildMaps();
   }

   public void createWebHooks(String publicUrl)
   {
      List<WebHook> webHooks = getWebHooks();
      LOGGER.info("on startup: webhooks=" + webHooks);
      for (WebHook webHook : webHooks)
      {
         removeWebHook(webHook.getId());
      }
      webHooks = getWebHooks();
      LOGGER.info("after removing prior: webhooks=" + webHooks);

      // add webhooks
      WebHook productUpdate = new WebHook();
      productUpdate.setAddress(publicUrl + "/spindleandrose/product/update");
      productUpdate.setTopic("products/update");
      productUpdate.setFormat("json");
      createWebHook(productUpdate);

      WebHook orderPaid = new WebHook();
      orderPaid.setAddress(publicUrl + "/spindleandrose/order/paid");
      orderPaid.setTopic("orders/paid");
      orderPaid.setFormat("json");
      createWebHook(orderPaid);

      webHooks = getWebHooks();
      LOGGER.info("after create: webhooks=" + webHooks);
   }

   public void shutdown()
   {
      LOGGER.info("shutdown, removing webhooks...");
      List<WebHook> webHooks = getWebHooks();
      for (WebHook webHook : webHooks)
      {
         removeWebHook(webHook.getId());
      }
   }

   public synchronized List<WebHook> getWebHooks()
   {
      client.back(true);
      client.resetQuery();
      client.path("webhooks.json");
      client.accept(MediaType.APPLICATION_JSON_TYPE);
      LOGGER.debug("GET " + client.getCurrentURI());
      GetWebHooksResponse response = client.get(GetWebHooksResponse.class);
      return response.getWebHooks();
   }

   public synchronized void removeWebHook(long id)
   {
      client.back(true);
      client.resetQuery();
      client.path("webhooks");
      client.path(id + ".json");
      LOGGER.info("DELETE " + client.getCurrentURI());
      client.delete();
   }

   public synchronized void createWebHook(WebHook webHook)
   {
      client.back(true);
      client.resetQuery();
      client.path("webhooks.json");
      client.accept(MediaType.APPLICATION_JSON_TYPE);
      client.type(MediaType.APPLICATION_JSON_TYPE);
      LOGGER.info("POST " + client.getCurrentURI());
      WebHookPost webHookPost = new WebHookPost();
      webHookPost.setWebHook(webHook);
      PostWebHookResponse response = client.post(webHookPost, PostWebHookResponse.class);
      LOGGER.info("Status: " + client.getResponse().getStatus());
      LOGGER.info("created webhook: " + response.getWebHook());
   }

   public synchronized Product getProduct(long productId)
   {
      return getProduct(productId, null);
   }

   public synchronized Product getProduct(long productId, String fields)
   {
      LOGGER.debug("getProduct: productId=" + productId);
      client.back(true);
      client.resetQuery();
      client.path("products");
      client.path(productId + ".json");
      if (fields == null)
      {
         client.query("fields", "id,title,product_type,vendor,tags,handle,variants");
      }
      else
      {
         client.query("fields", fields);
      }
      client.accept(MediaType.APPLICATION_JSON_TYPE);
      LOGGER.info("GET " + client.getCurrentURI());
      try
      {
         ProductResponse response = client.get(ProductResponse.class);
         int status = client.getResponse().getStatus();
         LOGGER.info("Status: " + client.getResponse().getStatus());
         if (status == Response.Status.REQUEST_TIMEOUT.getStatusCode())
         {
            LOGGER.error("getProduct: productId={} TIMED OUT", productId);
            return null;
         }
         if (status == Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
         {
            LOGGER.error("getProduct: productId={} SHOPIFY API ERROR", productId);
            return null;
         }
         return response.getProduct();
      }
      catch (Exception e)
      {
         Response response = client.getResponse();
         if (response != null)
         {
            Object entity = response.getEntity();
            if (entity instanceof ProductResponse)
            {
               LOGGER.error("Status: " + client.getResponse().getStatus());
            }
            else
            {
               String s = Utils.getStringFromInputStream((InputStream) client.getResponse().getEntity());
               LOGGER.error("getProduct: status={}, msg={}, body={}", client.getResponse().getStatus(), e.getMessage(), s);
            }
         }
         LOGGER.error("getProduct", e);
         return null;
      }
   }

   public synchronized void setCollectionTitleTag(Long collectionId, String titleTag)
   {
      LOGGER.debug("setCollectionTitleTag: collectionId={} titleTag={}", collectionId, titleTag);
      client.back(true);
      client.resetQuery();
      client.path("collections");
      client.path(collectionId);
      client.path("metafields.json");
      Metafield metafield = new Metafield();
      metafield.setNamespace("global");
      metafield.setKey("title_tag");
      metafield.setValue(titleTag);
      metafield.setValueType("string");
      MetafieldPost post = new MetafieldPost();
      post.setMetafield(metafield);
      String s = Utils.toJson(post);
      LOGGER.debug("titleTag Json={}", s);
      client.type(MediaType.APPLICATION_JSON_TYPE);

      try
      {
         client.post(post);
         LOGGER.info("Status: " + client.getResponse().getStatus());
      }
      catch (Exception e)
      {
         LOGGER.error("setCollectionTitleTag: status={}, msg={}, body={}", client.getResponse().getStatus(), e.getMessage(), Utils.getStringFromInputStream((InputStream)client.getResponse().getEntity()));
      }
   }

   public synchronized void setCollectionDescriptionTag(Long collectionId, String description)
   {
      LOGGER.debug("setCollectionDescriptionTag: collectionId={} description={}", collectionId, description);
      client.back(true);
      client.resetQuery();
      client.path("collections");
      client.path(collectionId);
      client.path("metafields.json");
      Metafield metafield = new Metafield();
      metafield.setNamespace("global");
      metafield.setKey("description_tag");
      metafield.setValue(description);
      metafield.setValueType("string");
      MetafieldPost post = new MetafieldPost();
      post.setMetafield(metafield);
      String s = Utils.toJson(post);
      LOGGER.debug("descriptionTag Json={}", s);
      client.type(MediaType.APPLICATION_JSON_TYPE);

      try
      {
         client.post(post);
         LOGGER.info("Status: " + client.getResponse().getStatus());
      }
      catch (Exception e)
      {
         LOGGER.error("setCollectionDescriptionTag: status={}, msg={}, body={}", client.getResponse().getStatus(), e.getMessage(), Utils.getStringFromInputStream((InputStream)client.getResponse().getEntity()));
      }
   }

   public synchronized void setProductTitleTag(Long productId, String titleTag)
   {
      LOGGER.debug("setProductTitleTag: productId={} titleTag={}", productId, titleTag);
      client.back(true);
      client.resetQuery();
      client.path("products");
      client.path(productId);
      client.path("metafields.json");
      Metafield metafield = new Metafield();
      metafield.setNamespace("global");
      metafield.setKey("title_tag");
      metafield.setValue(titleTag);
      metafield.setValueType("string");
      MetafieldPost post = new MetafieldPost();
      post.setMetafield(metafield);
      String s = Utils.toJson(post);
      LOGGER.debug("titleTag Json={}", s);
      client.type(MediaType.APPLICATION_JSON_TYPE);
      LOGGER.info("POST " + client.getCurrentURI());
      try
      {
         client.post(post);
         LOGGER.info("Status: " + client.getResponse().getStatus());
      }
      catch (Exception e)
      {
         LOGGER.error("setProductTitleTag: status={}, msg={}, body={}", client.getResponse().getStatus(), e.getMessage(), Utils.getStringFromInputStream((InputStream)client.getResponse().getEntity()));
      }
   }

   public synchronized void setProductDescriptionTag(Long productId, String description)
   {
      LOGGER.debug("setProductDescriptionTag: productId={} description={}", productId, description);
      client.back(true);
      client.resetQuery();
      client.path("products");
      client.path(productId);
      client.path("metafields.json");
      Metafield metafield = new Metafield();
      metafield.setNamespace("global");
      metafield.setKey("description_tag");
      metafield.setValue(description);
      metafield.setValueType("string");
      MetafieldPost post = new MetafieldPost();
      post.setMetafield(metafield);
      String s = Utils.toJson(post);
      LOGGER.debug("descriptionTag Json={}", s);
      client.type(MediaType.APPLICATION_JSON_TYPE);
      LOGGER.info("POST " + client.getCurrentURI());
      try
      {
         client.post(post);
         LOGGER.info("Status: " + client.getResponse().getStatus());
      }
      catch (Exception e)
      {
         LOGGER.error("setProductDescriptionTag: status={}, msg={}, body={}", client.getResponse().getStatus(), e.getMessage(), Utils.getStringFromInputStream((InputStream)client.getResponse().getEntity()));
      }
   }

   public synchronized void setKeywords(Long id, String templateName, String keywords)
   {
      LOGGER.debug("setKeywords: id={} templateName={} keywords={}",id, templateName, keywords);
      client.back(true);
      client.resetQuery();
      client.path(templateName);
      client.path(id);
      client.path("metafields.json");
      Metafield metafield = new Metafield();
      metafield.setNamespace("spindleandrose");
      metafield.setKey("keywords");
      metafield.setValue(keywords);
      metafield.setValueType("string");
      MetafieldPost post = new MetafieldPost();
      post.setMetafield(metafield);
      String s = Utils.toJson(post);
      LOGGER.debug("keywords Json={}", s);
      client.type(MediaType.APPLICATION_JSON_TYPE);
      LOGGER.info("POST " + client.getCurrentURI());
      try
      {
         client.post(post);
         LOGGER.info("Status: " + client.getResponse().getStatus());
      }
      catch (Exception e)
      {
         LOGGER.error("setKeywords: status={}, msg={}, body={}", client.getResponse().getStatus(), e.getMessage(), Utils.getStringFromInputStream((InputStream)client.getResponse().getEntity()));
      }
   }

   public synchronized void removeProductMetafield(Long productId, long metafieldId)
   {
      LOGGER.info("removeProductMetafield: productId={} metafieldId={}", productId, metafieldId);
      client.back(true);
      client.resetQuery();
      client.path("products");
      client.path(productId);
      client.path("metafields");
      client.path(metafieldId + ".json");
      LOGGER.info("DELETE " + client.getCurrentURI());
      try
      {
         client.delete();
         LOGGER.info("Status: " + client.getResponse().getStatus());
      }
      catch (Exception e)
      {
         LOGGER.error("removeProductMetafield: status={}, msg={}, body={}", client.getResponse().getStatus(), e.getMessage(), Utils.getStringFromInputStream((InputStream)client.getResponse().getEntity()));
      }
   }

   public synchronized MetafieldList getImageMetafields(long imageId)
   {
      /*
      owner_id is imageId
      GET https://spindleandrose.myshopify.com/admin/metafields.json?metafield[owner_id]=19023234947&metafield[owner_resource]=product_image
      Response:
     [Metafield{namespace='tags', key='alt', value='Honeycomb Bumble Bee material from Art Gallery Fabrics', valueType='string'}]
       */

      LOGGER.debug("getImageMetafields: imageId=" + imageId);
      client.back(true);
      client.resetQuery();
      client.path("metafields.json");
      client.query("metafield[owner_id]", imageId);
      client.query("metafield[owner_resource]", "product_image");
      client.accept(MediaType.APPLICATION_JSON_TYPE);
      LOGGER.info("GET " + client.getCurrentURI());
      try
      {
         String json = client.get(String.class);
         LOGGER.info("Status: " + client.getResponse().getStatus());
         LOGGER.debug("Json: {}", json);
         return new ObjectMapper().readValue(json, MetafieldList.class);
      }
      catch (Exception e)
      {
         LOGGER.error("getImageMetafields: status={}, msg={}, body={}", client.getResponse().getStatus(), e.getMessage(), Utils.getStringFromInputStream((InputStream)client.getResponse().getEntity()));
         return null;
      }
   }

   public synchronized List<Redirect> getRedirects()
   {
      List<Redirect> allRedirects = new ArrayList<>();
      List<Redirect> redirects = getRedirects("0");
      allRedirects.addAll(redirects);
      while (redirects.size() == LIMIT)
      {
         Utils.sleep(2000L);
         Long lastId = getLastId(allRedirects);
         redirects = getRedirects(String.valueOf(lastId));
         allRedirects.addAll(redirects);
      }
      return allRedirects;
   }

   private Long getLastId(List<Redirect> allRedirects)
   {
      long lastId = 0;
      for (Redirect redirect : allRedirects)
      {
         if (redirect.getId() > lastId)
         {
            lastId = redirect.getId();
         }
      }
      return lastId;
   }

   private synchronized List<Redirect> getRedirects(String sinceId)
   {
      client.back(true);
      client.resetQuery();
      client.path("redirects.json");
      client.query("limit", String.valueOf(LIMIT));
      client.query("since_id", sinceId);
      client.accept(MediaType.APPLICATION_JSON_TYPE);
      LOGGER.info("GET " + client.getCurrentURI());
      try
      {
         RedirectList redirectList = client.get(RedirectList.class);
         LOGGER.info("Status: " + client.getResponse().getStatus());
         return redirectList.getRedirects();
      }
      catch (Exception e)
      {
         LOGGER.error("getRedirects: status={}, msg={}, body={}", client.getResponse().getStatus(), e.getMessage(), Utils.getStringFromInputStream((InputStream)client.getResponse().getEntity()));
         return null;
      }
   }

   public synchronized void updateRedirect(Redirect redirect)
   {
      client.back(true);
      client.resetQuery();
      client.path("redirects");
      client.path(String.valueOf(redirect.getId()) + ".json");
      LOGGER.info("PUT " + client.getCurrentURI());
      client.type(MediaType.APPLICATION_JSON_TYPE);
      try
      {
         RedirectPost put = new RedirectPost();
         put.setRedirect(redirect);
         client.put(put);
         LOGGER.info("Status: " + client.getResponse().getStatus());
      }
      catch (Exception e)
      {
         LOGGER.error("updateRedirect: status={}, msg={}, body={}", client.getResponse().getStatus(), e.getMessage(), Utils.getStringFromInputStream((InputStream)client.getResponse().getEntity()));
      }
   }

   public synchronized void removeRedirect(long id)
   {
      client.back(true);
      client.resetQuery();
      client.path("redirects");
      client.path(String.valueOf(id) + ".json");
      LOGGER.info("DELETE " + client.getCurrentURI());
      try
      {
         client.delete();
         LOGGER.info("Status: " + client.getResponse().getStatus());
      }
      catch (Exception e)
      {
         LOGGER.error("removeRedirect: status={}, msg={}, body={}", client.getResponse().getStatus(), e.getMessage(), Utils.getStringFromInputStream((InputStream)client.getResponse().getEntity()));
      }
   }

   public Map<String, Product> getTitleToProductMap()
   {
      LOGGER.info("getTitleToProductMap: begin");
      TreeMap<String, Product> title2Product = new TreeMap<>();
      List<Product> products = getProducts(null, "0", "id,title,tags");
      LOGGER.debug("found " + products.size() + " products");
      for (Product product : products)
      {
         title2Product.put(product.getTitle(), product);
      }
      while (products.size() == LIMIT)
      {
         Utils.sleep(2000L);
         Long lastProductId = lastProductId(title2Product);
         LOGGER.debug("lastProductId=" + lastProductId);
         products = getProducts(null, String.valueOf(lastProductId), "id,title,tags");
         LOGGER.debug("found " + products.size() + " products");
         for (Product product : products)
         {
            title2Product.put(product.getTitle(), product);
         }
      }
      LOGGER.info("found " + title2Product.size() + " total products");
      LOGGER.info("getTitleToProductMap: end");
      return title2Product;
   }

   public Set<String> getTitleTagSet()
   {
      LOGGER.info("getTitleTagSet: begin");
      Set<String> titleTags = new HashSet<>();
      int count = 0;
      for (Long id : id2Title.keySet())
      {
         MetafieldList productSeoData = getProductSeoData(id);
         List<Metafield> metafields = productSeoData.getMetafields();
         for (Metafield metafield : metafields)
         {
            if ("title_tag".equals(metafield.getKey()))
            {
               String titleTag = metafield.getValue().trim();
               LOGGER.info("getTitleTagSet: adding {} of {} {}", ++count, id2Title.size(), titleTag);
               titleTags.add(titleTag);
            }
         }
         Utils.sleep(1000L);
      }
      int numProducts = id2Title.size();
      int numTitleTags = titleTags.size();
      LOGGER.info("Number of titleTags={}", numTitleTags);
      if (numProducts != numTitleTags)
      {
         LOGGER.error("Number of products={} titleTags={}", numProducts, numTitleTags);
      }
      LOGGER.info("getTitleTagSet: end");
      return titleTags;
   }

   private List<Product> getProducts(String type, String sinceId, String fields)
   {
      client.back(true);
      client.resetQuery();
      client.path("products.json");
      client.query("fields", fields);
      client.query("limit", String.valueOf(LIMIT));
      if (type != null)
      {
         client.query("product_type", type);
      }
      client.query("since_id", sinceId);
      LOGGER.debug("GET " + client.getCurrentURI());
      client.accept(MediaType.APPLICATION_JSON_TYPE);
      ProductsResponse p = client.get(ProductsResponse.class);
      return p.getProducts();
   }


   public synchronized void addRedirect(Redirect redirect) throws Exception
   {
      client.back(true);
      client.resetQuery();
      client.path("redirects.json");
      client.accept(MediaType.APPLICATION_JSON_TYPE);
      client.type(MediaType.APPLICATION_JSON_TYPE);
      LOGGER.info("POST " + client.getCurrentURI());
      try
      {
         RedirectPost post = new RedirectPost();
         post.setRedirect(redirect);
         client.post(post);
         int status = client.getResponse().getStatus();
         LOGGER.info("Status: {}", status);
         if (status > 201)
         {
            String msg= Utils.getStringFromInputStream((InputStream) client.getResponse().getEntity());
            throw new Exception(msg);
         }
      }
      catch (Exception e)
      {
         String msg = "getRedirects: status=" + client.getResponse().getStatus() + ", msg=" + e.getMessage() + ", body=" + Utils.getStringFromInputStream((InputStream)client.getResponse().getEntity());
         throw new Exception(msg);
      }
   }

   public synchronized List<Metafield> getCollectionSeoData(long collectionId)
   {
      client.back(true);
      client.resetQuery();
      client.path("collections");
      client.path(collectionId);
      client.path("metafields.json");
      client.accept(MediaType.APPLICATION_JSON_TYPE);
      LOGGER.info("GET " + client.getCurrentURI());
      try
      {
         MetafieldList list = client.get(MetafieldList.class);
         LOGGER.info("Status: " + client.getResponse().getStatus());
         return list.getMetafields();
      }
      catch (Exception e)
      {
         LOGGER.error("getCollectionSeoData: status={}, msg={}, body={}", client.getResponse().getStatus(), e.getMessage(), Utils.getStringFromInputStream((InputStream)client.getResponse().getEntity()));
         return null;
      }
   }

   public synchronized MetafieldList getProductSeoData(long productId)
   {
      LOGGER.debug("getProduct: productId=" + productId);
      client.back(true);
      client.resetQuery();
      client.path("products");
      client.path(productId);
      client.path("metafields.json");
      client.accept(MediaType.APPLICATION_JSON_TYPE);
      LOGGER.info("GET " + client.getCurrentURI());
      try
      {
         String json = client.get(String.class);
         LOGGER.info("Status: " + client.getResponse().getStatus());
         LOGGER.debug("Json: {}", json);
         return new ObjectMapper().readValue(json, MetafieldList.class);
      }
      catch (Exception e)
      {
         LOGGER.error("getProductSeoData: status={}, msg={}, body={}", client.getResponse().getStatus(), e.getMessage(), Utils.getStringFromInputStream((InputStream)client.getResponse().getEntity()));
         return null;
      }
   }

   public synchronized void updateImage(long productId, ShopifyImage image)
   {
      LOGGER.debug("updateImage: productId={} image={}", productId, image);
      ShopifyImagePut put = new ShopifyImagePut();
      put.setImage(image);
      //String s = Utils.toJson(put);
      //LOGGER.info("putting: {}", s);

      client.back(true);
      client.resetQuery();
      client.path("products");
      client.path(productId);
      client.path("images");
      client.path(image.getId() + ".json");
      LOGGER.info("PUT " + client.getCurrentURI());
      client.type(MediaType.APPLICATION_JSON_TYPE);
      client.accept(MediaType.APPLICATION_JSON_TYPE);
      client.put(put);
      int status = client.getResponse().getStatus();
      LOGGER.info("updateImage: status={}", status);
   }

   public synchronized void updateProduct(Product product)
   {
      LOGGER.info("updateProduct: productTitle=" + product.getTitle());
      ProductPut pp = new ProductPut();
      pp.setProduct(product);
      client.back(true);
      client.resetQuery();
      client.path("products");
      client.path(product.getId() + ".json");
      LOGGER.info("PUT " + client.getCurrentURI());
      client.type(MediaType.APPLICATION_JSON_TYPE);
      client.accept(MediaType.APPLICATION_JSON_TYPE);
      client.put(pp);
      int status = client.getResponse().getStatus();
      LOGGER.info("updateProduct: status={}", status);
   }

   public synchronized void removeProduct(Long productId)
   {
      LOGGER.info("removeProduct: productId=" + productId);
      client.back(true);
      client.resetQuery();
      client.path("products");
      client.path(productId + ".json");
      LOGGER.info("DELETE " + client.getCurrentURI());
      client.delete();
      int status = client.getResponse().getStatus();
      LOGGER.info("remoeProduct: status={}", status);
   }

   public synchronized long createProduct(Product product)
   {
      LOGGER.info("createProduct: title=" + product.getTitle());
      ProductPut pp = new ProductPut();
      pp.setProduct(product);
      client.back(true);
      client.resetQuery();
      client.path("products.json");
      LOGGER.info("POST " + client.getCurrentURI());
      client.type(MediaType.APPLICATION_JSON_TYPE);
      client.accept(MediaType.APPLICATION_JSON_TYPE);
      client.post(pp);

      try
      {
         String json = Utils.getStringFromInputStream((InputStream)client.getResponse().getEntity());
         LOGGER.info("createProduct: json response from Shopify={}", json);
         ObjectMapper mapper = new ObjectMapper();
         ProductResponse response = mapper.readValue(json, ProductResponse.class);
         return response.getProduct().getId();
      }
      catch (Exception e)
      {
         LOGGER.error("createProduct: error reading Product response object for new product {}", product.getTitle(), e);
         SMSClient.alertAdmin("error reading Product response object, cannot obtain productId for " + product.getTitle());
         return 0;
      }
   }

   public List<SmartCollection> getSmartCollections()
   {
      return getSmartCollections(0L);
   }

   public List<SmartCollection> getSmartCollections(long sinceId)
   {
      try
      {
         client.back(true);
         client.resetQuery();
         //client.path("custom_collections.json");
         client.path("smart_collections.json");
         if (sinceId > 0)
         {
            client.query("since_id", sinceId);
         }
         client.query("limit", 200);
         LOGGER.debug("GET " + client.getCurrentURI());
         client.accept(MediaType.APPLICATION_JSON_TYPE);
         SmartCollectionList smartCollectionList = client.get(SmartCollectionList.class);
         return smartCollectionList.getCollections();
      }
      catch (Exception e)
      {
         LOGGER.error("getSmartCollections: status={}, msg={}, body={}", client.getResponse().getStatus(), e.getMessage(), Utils.getStringFromInputStream((InputStream)client.getResponse().getEntity()));
         return null;
      }
   }

   private Long getLastProductId(TreeMap<Long, String> id2Handle)
   {
      Set<Long> ids = id2Handle.keySet();
      return ids.toArray(new Long[0])[ids.size() - 1];
   }

   private Long lastProductId(TreeMap<String, Product> title2Product)
   {
      Long last = 0L;
      for (Product product : title2Product.values()) {
         if (product.getId() > last)
         {
            last = product.getId();
         }
      }
      return last;
   }

   private void buildMaps()
   {
      LOGGER.info("buildMaps: begin");
      id2Title = new TreeMap<>();
      title2Id = new TreeMap<>();
      List<Product> products = getProducts(null, "0", "id,title");
      LOGGER.debug("found " + products.size() + " products");
      for (Product product : products)
      {
         id2Title.put(product.getId(), product.getTitle().trim());
         title2Id.put(product.getTitle().trim(), product.getId());
      }
      while (products.size() == LIMIT)
      {
         Utils.sleep(2000L);
         Long lastProductId = getLastProductId(id2Title);
         LOGGER.debug("lastProductId=" + lastProductId);
         products = getProducts(null, String.valueOf(lastProductId), "id,title");
         LOGGER.debug("found " + products.size() + " products");
         for (Product product : products)
         {
            id2Title.put(product.getId(), product.getTitle().trim());
            title2Id.put(product.getTitle().trim(), product.getId());
         }
      }
      LOGGER.info("found " + id2Title.size() + " total products");
      LOGGER.info("buildMaps: end");
   }

}
