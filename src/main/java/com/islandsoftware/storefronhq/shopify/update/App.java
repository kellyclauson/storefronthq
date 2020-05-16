package com.islandsoftware.storefronhq.shopify.update;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.islandsoftware.storefronhq.OAuthFilter;
import com.islandsoftware.storefronhq.shopify.sync.model.*;
import com.islandsoftware.storefronhq.tools.Keyword;
import com.islandsoftware.storefronhq.tools.Utils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.rs.security.oauth.client.OAuthClientUtils;
import org.apache.cxf.rs.security.oauth.provider.OAuthServiceException;
import org.apache.cxf.transport.http.HTTPConduit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.islandsoftware.storefronhq.tools.Utils.generatePerm;

public class App
{
   private final static int LIMIT = 250;

   private WebClient client;

   private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

   public static void main( String[] args )
   {
      try
      {
         /*
         Set<String> keywords = new TreeSet<>();
         List<String> lines = Files.readAllLines(Paths.get("C:\\tmp\\keywords.csv"));
         for (String line : lines)
         {
            String keyword = line.trim();
            if (keyword.startsWith("afr"))
            {
               LOGGER.info("[{}]", keyword);
            }
            keywords.add(line.trim());
         }
         for (String keyword : keywords)
         {
            LOGGER.info("from the set {}", keyword);
         }
         */

         List<Keyword> list = new ArrayList<>();
         list.add(new Keyword("one", 100));
         list.add(new Keyword("two", 50));
         list.add(new Keyword("three", 10));
         List<List<Keyword>> lists = generatePerm(list);
         for (List<Keyword> strings : lists)
         {
            LOGGER.info("{}", strings);
         }
      }
      catch (Exception e)
      {
         LOGGER.error("App", e);

      }
   }

   private static void write(String filename, Set<String> set)
   {
      StringBuilder sb = new StringBuilder();
      for (String s : set)
      {
         sb.append(s).append("\n");
      }
      Utils.write(filename, sb.toString());
   }

   private static Set<String> readFile(String filename) throws Exception
   {
      //LOGGER.info("readFile: {}", filename);
      Set<String> names = new TreeSet<>();
      List<String> lines = Files.readAllLines(Paths.get(filename));
      for (String line : lines)
      {
         names.add(line);
      }
      return names;
   }

   private static File[] getFilesMatchingPattern(String dir, String pattern)
   {
      File directory = new File(dir);
      FilenameFilter filter = new FilenameFilter()
      {
         @Override
         public boolean accept(File dir, String name)
         {
            if (name.contains(pattern))
            {
               return true;
            }
            return false;
         }
      };
      File[] files = directory.listFiles(filter);
      return files;
   }

   private static List<QuickenTransaction> readTransactions(String filename) throws Exception
   {
      SimpleDateFormat formatter = new SimpleDateFormat("M/d/yyyy");
      List<QuickenTransaction> transactions = new ArrayList<>();
      List<String> lines = Files.readAllLines(Paths.get(filename));
      for (String line : lines)
      {
         String[] split = line.split(",");
         Date  date = formatter.parse(pad(split[0].trim()));
         String description = split[1].trim();
         QuickenTransaction transaction = new QuickenTransaction();
         transaction.setDate(date);
         transaction.setDescription(description);
         transactions.add(transaction);
      }
      return transactions;
   }

   private static String pad(String date)
   {
      StringBuilder sb = new StringBuilder();
      char[] chars = date.toCharArray();
      for (char c : chars)
      {
         if (c != '\uFEFF')
         {
            sb.append(c);
         }
      }
      return sb.toString();
   }

   private TreeSet<String> getScheduleUrls(String filename) throws Exception
   {
      TreeSet<String> urls = new TreeSet<String>();

      FileInputStream fis = new FileInputStream(filename);
      BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
      String line = reader.readLine();
      int i = 0;
      while(line != null)
      {
         i++;
         line = reader.readLine();
         if (line != null)
         {
            urls.add(line);
         }
      }
      System.out.println("number of lines read: " + i);
      reader.close();
      fis.close();
      return urls;
   }

   public App()
   {
      client = createClient();
   }

   private void update(String filename, boolean includeSku) throws Exception
   {
      System.out.println("begin: getting updates from " + filename);
      Map<String, Product> productUpdates = getProductUpdates(filename, includeSku);

      TreeMap<String, String> id2Handle = new TreeMap<String, String>();
      List<Product> products = getProducts("Fabric", "0");
      System.out.println("found " + products.size() + " products");
      for (Product product : products)
      {
         id2Handle.put(String.valueOf(product.getId()), product.getHandle());
      }
      while (products.size() == LIMIT)
      {
         sleep(2000L);
         String lastProductId = getLastProductId(id2Handle);
         System.out.println("lastProductId=" + lastProductId);
         products = getProducts("Fabric", lastProductId);
         System.out.println("found " + products.size() + " products");
         for (Product product : products)
         {
            id2Handle.put(String.valueOf(product.getId()), product.getHandle());
         }
      }
      System.out.println("found " + id2Handle.size() + " total products");
      doUpdates(id2Handle, productUpdates);
   }

   private void doUpdates(TreeMap<String, String> id2Handle, Map<String, Product> productUpdates)
   {
      for (String handle : productUpdates.keySet())
      {
         for (Map.Entry<String, String> entry : id2Handle.entrySet())
         {
            if (handle.equalsIgnoreCase(entry.getValue()))
            {
               Product updates = productUpdates.get(handle);
               String id = entry.getKey();
               Product product = getProduct(id);
               /*
               String body = product.getBodyHtml();
               int i = body.indexOf("*Care Instructions");
               if (i > 2)
               {
                  System.out.println("removing extra lines of description");
                  product.setBodyHtml(body.substring(0, i - 2));
               }
               */

               product.setTitle(updates.getTitle());
               System.out.println("set title to " + product.getTitle());
               product.setVendor(updates.getVendor());
               System.out.println("set vendor to " + product.getVendor());
               product.setTags(updates.getTags());
               System.out.println("set tags to " + product.getTags());
               List<Variant> variants = product.getVariants();
               Double price = updates.getPrice();
               Double weight = updates.getWeight();
               String sku = updates.getSku();
               Integer inventory = updates.getInventory();
               for (Variant variant : variants)
               {
                  String variantTitle = variant.getTitle();
                  if (variantTitle.startsWith("1 Yard"))
                  {
                     variant.setSku(sku);
                     variant.setPrice(String.valueOf(format(price)));
                     variant.setWeight(String.valueOf(weight));
                     variant.setInventoryQuantity(inventory);
                     variant.setInventoryManagement("shopify");
                     System.out.println("set sku=" + variant.getSku() + ", price=" + variant.getPrice() + ", inventory=" + variant.getInventoryQuantity() + ", and weight=" + variant.getWeight() + " for 1 Yard variant");
                  }
                  else if (variantTitle.startsWith("3/4 Yard"))
                  {
                     variant.setSku(sku == null ? null : sku + "-75");
                     variant.setPrice(String.valueOf(format(price * .75)));
                     variant.setWeight(String.valueOf(weight * .75));
                     variant.setInventoryQuantity(inventory);
                     variant.setInventoryManagement("shopify");
                     System.out.println("set sku=" + variant.getSku() + ", price=" + variant.getPrice() + ", inventory=" + variant.getInventoryQuantity() + ", and weight=" + variant.getWeight() + " for 3/4 Yard variant");
                  }
                  else if (variantTitle.startsWith("1/2 Yard"))
                  {
                     variant.setSku(sku == null ? null : sku + "-50");
                     variant.setPrice(String.valueOf(format(price / 2)));
                     variant.setWeight(String.valueOf(weight / 2));
                     variant.setInventoryQuantity(inventory);
                     variant.setInventoryManagement("shopify");
                     System.out.println("set sku=" + variant.getSku() + ", price=" + variant.getPrice() + ", inventory=" + variant.getInventoryQuantity() + ", and weight=" + variant.getWeight() + " for 1/2 Yard variant");
                  }
                  else if (variantTitle.startsWith("1/4 Yard"))
                  {
                     variant.setSku(sku == null ? null : sku + "-25");
                     variant.setPrice(String.valueOf(format(price / 4)));
                     variant.setWeight(String.valueOf(weight / 4));
                     variant.setInventoryQuantity(inventory);
                     variant.setInventoryManagement("shopify");
                     System.out.println("set sku=" + variant.getSku() + ", price=" + variant.getPrice() + ", inventory=" + variant.getInventoryQuantity() + ", and weight=" + variant.getWeight() + " for 1/4 Yard variant");
                  }
                  else if (variantTitle.startsWith("Fat Quarter"))
                  {
                     variant.setSku(sku == null ? null : sku + "-FQ");
                     variant.setPrice(String.valueOf(format((price / 4) * 1.1)));
                     variant.setWeight(String.valueOf(weight / 4));
                     variant.setInventoryQuantity(inventory);
                     variant.setInventoryManagement("shopify");
                     System.out.println("set sku=" + variant.getSku() + ", price=" + variant.getPrice() + ", inventory=" + variant.getInventoryQuantity() + ", and weight=" + variant.getWeight() + " for FQ variant");
                  }
                  else
                  {
                     System.out.println("Unknown variant title: " + variantTitle);
                  }
               }

               System.out.println("updating product " + product.getTitle());
               updateProduct(product);
               sleep(1000L);
            }
         }
      }
   }

   private Map<String, Product> getProductUpdates(String filename, boolean includeSku) throws Exception
   {
      Map<String, Product> updates = new HashMap<String, Product>();

      FileInputStream fis = new FileInputStream(filename);
      BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
      String line = reader.readLine();
      int i = 0;
      while(line != null)
      {
         if (i++ == 0)
         {
            // skip header row
         }
         else
         {
            System.out.println("reading: " + line);
            final String[] split = line.split(",");
            Product p = new Product();
            p.setHandle(split[0]);
            p.setPrice(Double.valueOf(split[1]));
            p.setTitle(split[2]);
            p.setWeight(Double.valueOf(split[3]));
            p.setVendor(split[4]);
            p.setTags(split[5].replaceAll("\\|", ","));
            p.setInventory(Integer.parseInt(split[6]));
            if (includeSku)
            {
               p.setSku(split[7]);
            }
            updates.put(p.getHandle(), p);
         }
         line = reader.readLine();
      }
      reader.close();
      fis.close();
      return updates;
   }

   private void updateProduct(Product product)
   {
      ProductPut pp = new ProductPut();
      pp.setProduct(product);

      client.back(true);
      client.resetQuery();
      client.path("products");
      client.path(product.getId() + ".json");
      System.out.println("PUT " + client.getCurrentURI());
      client.type(MediaType.APPLICATION_JSON_TYPE);
      client.accept(MediaType.APPLICATION_JSON_TYPE);
      client.put(pp);
   }

   private String getLastProductId(TreeMap<String, String> id2Handle)
   {
      Set<String> ids = id2Handle.keySet();
      return ids.toArray(new String[0])[ids.size() - 1];
   }

   private List<Product> getProducts(String type, String sinceId)
   {
      client.back(true);
      client.resetQuery();
      client.path("products.json");
      client.query("fields", "id,handle");
      client.query("limit", String.valueOf(LIMIT));
      client.query("product_type", type);
      client.query("since_id", sinceId);
      System.out.println("GET " + client.getCurrentURI());
      client.accept(MediaType.APPLICATION_JSON_TYPE);
      ProductsResponse p = client.get(ProductsResponse.class);
      return p.getProducts();
   }

   private void sleep(long millis)
   {
      try
      {
         Thread.sleep(millis);
      }
      catch (InterruptedException e)
      {
         e.printStackTrace();
      }
   }

   private Product getProduct(String productId)
   {
      client.back(true);
      client.resetQuery();
      client.path("products");
      client.path(productId + ".json");
      client.query("fields", "id,title,body_html,vendor,handle,tags,variants");
      System.out.println("GET " + client.getCurrentURI());
      client.accept(MediaType.APPLICATION_JSON_TYPE);
      ProductResponse p = client.get(ProductResponse.class);
      return p.getProduct();
   }

   private WebClient createClient()
   {
      String url = "https://spindleandrose.myshopify.com/admin";
      List<?> providers = Arrays.asList(new JacksonJaxbJsonProvider());
      WebClient client = WebClient.create(url, providers);
      HTTPConduit conduit = WebClient.getConfig(client).getHttpConduit();
      conduit.getAuthorization().setUserName("");
      conduit.getAuthorization().setPassword("");
      return client;
   }

   private static String format(double amount)
   {
      String s = String.valueOf(amount);
      final BigDecimal rounded = round(new BigDecimal(s), new BigDecimal("0.05"), RoundingMode.DOWN);
      return rounded.toString();
   }

   private static BigDecimal round(BigDecimal value, BigDecimal increment, RoundingMode roundingMode)
   {
      if (increment.signum() == 0)
      {
         // 0 increment does not make much sense, but prevent division by 0
         return value;
      }
      else
      {
         BigDecimal divided = value.divide(increment, 0, roundingMode);
         return divided.multiply(increment);
      }
   }

   private static String toJson(Object o)
   {
      try
      {
         return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(o);
      }
      catch (IOException e)
      {
         e.printStackTrace();
         return "error writing product to json";
      }
   }

   /*
   private void mapper()
   {
      final ObjectMapper mapper = new ObjectMapper();

      try
      {
         ReceiptsResponse response = mapper.readValue(json, ReceiptsResponse.class);
         System.out.println("count=" + response.getQuantity());
         System.out.println("limit=" + response.getParams().getLimit());
         System.out.println("");
         ReceiptResult[] results = response.getResults();
         for (ReceiptResult result : results)
         {
            System.out.println("orderId=" + result.getOrderId());
            Transaction[] transactions = result.getTransactions();
            if (transactions != null)
            {
               for (Transaction transaction : transactions)
               {
                  System.out.println("transactionId=" + transaction.getTransactionId());
                  System.out.println("title=" + transaction.getEtsyTitle());
                  System.out.println("quantity=" + transaction.getQuantity());
                  TransactionVariation[] variations = transaction.getVariations();
                  for (TransactionVariation variation : variations)
                  {
                     System.out.println("formattedValue=" + variation.getFormattedValue());
                  }
               }
            }
            System.out.println("");
         }
      }
      catch (final Exception e)
      {
         e.printStackTrace();
      }
   }
   */

   private static void getAccessToken()
   {
      WebClient access = WebClient.create("https://openapi.etsy.com/v2/oauth/access_token");
      access.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE);
      try
      {
         String key = "";
         String secret = "";
         String tokenKey = "";
         String tokenSecret = "";
         OAuthClientUtils.Consumer consumer = new OAuthClientUtils.Consumer(key, secret);
         OAuthClientUtils.Token token = new OAuthClientUtils.Token(tokenKey, tokenSecret);
         String verifier = "9f5c7dee";
         OAuthClientUtils.Token accessToken = OAuthClientUtils.getAccessToken(access, consumer, token, verifier);
         String accessTokenKey = accessToken.getToken();
         String accessTokenSecret = accessToken.getSecret();
         System.out.println("token=" + accessTokenKey);
         System.out.println("secret=" + accessTokenSecret);

      }
      catch (OAuthServiceException e)
      {
         Throwable cause = e.getCause();
         System.out.println(cause.getMessage());
         System.out.println("Status: " + access.getResponse().getStatus());
         System.out.println("Body: " + Utils.getStringFromInputStream((InputStream)access.getResponse().getEntity()));
         MultivaluedMap<String, Object> headers = access.getResponse().getHeaders();
         for (Map.Entry<String, List<Object>> entry : headers.entrySet())
         {
            System.out.println(entry.getKey() + "=" + entry.getValue());
         }
      }
      catch (Exception e)
      {
         System.out.println(e.getMessage());
         System.out.println("Status: " + access.getResponse().getStatus());
         System.out.println("Body: " + Utils.getStringFromInputStream((InputStream)access.getResponse().getEntity()));
      }
   }

   public static void listings() throws IOException
   {
      String url = "https://openapi.etsy.com/v2/shops/13195966/listings/active";
      String key = "";
      String secret = "";
      String tokenKey = "";
      String tokenSecret = "";
      List<Object> providers = new ArrayList<Object>(2);
      providers.add(new JacksonJaxbJsonProvider());
      providers.add(new OAuthFilter(key, secret, tokenKey, tokenSecret));
      WebClient client = WebClient.create(url, providers).type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);
      try
      {
         String json = client.get(String.class);
         System.out.println("json: " + json);

      }
      catch (Exception e)
      {
         System.out.println(e.getMessage());
         System.out.println("Status: " + client.getResponse().getStatus());
         System.out.println("Body: " + Utils.getStringFromInputStream((InputStream)client.getResponse().getEntity()));
      }
   }

   public static void transactions() throws IOException
   {
      String url = "https://openapi.etsy.com/v2/shops/13195966/transactions?fields=transaction_id,quantity,variations";
      String key = "";
      String secret = "";
      String tokenKey = "";
      String tokenSecret = "";
      List<Object> providers = new ArrayList<Object>(2);
      providers.add(new JacksonJaxbJsonProvider());
      providers.add(new OAuthFilter(key, secret, tokenKey, tokenSecret));
      WebClient client = WebClient.create(url, providers).type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);
      try
      {
         String json = client.get(String.class);
         System.out.println("json: " + json);

      }
      catch (Exception e)
      {
         System.out.println(e.getMessage());
         System.out.println("Status: " + client.getResponse().getStatus());
         System.out.println("Body: " + Utils.getStringFromInputStream((InputStream)client.getResponse().getEntity()));
      }
   }

   public static void receipts() throws IOException
   {
      String url = "https://openapi.etsy.com/v2/shops/13195966/receipts?min_created=1482592731";
      String key = "";
      String secret = "";
      String tokenKey = "";
      String tokenSecret = "";
      List<Object> providers = new ArrayList<Object>(2);
      providers.add(new JacksonJaxbJsonProvider());
      providers.add(new OAuthFilter(key, secret, tokenKey, tokenSecret));
      WebClient client = WebClient.create(url, providers).type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);
      try
      {
         String json = client.get(String.class);
         System.out.println("json: " + json);

      }
      catch (Exception e)
      {
         System.out.println(e.getMessage());
         System.out.println("Status: " + client.getResponse().getStatus());
         System.out.println("Body: " + Utils.getStringFromInputStream((InputStream)client.getResponse().getEntity()));
      }
   }
}
