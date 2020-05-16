package com.islandsoftware.storefronhq;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import com.google.api.services.sheets.v4.Sheets;
import com.islandsoftware.storefronhq.etsy.model.CustomListing;
import com.islandsoftware.storefronhq.orderprocessing.ProductInfo;
import com.islandsoftware.storefronhq.shopify.sync.model.TitleTag;
import com.islandsoftware.storefronhq.tools.Keyword;
import com.islandsoftware.storefronhq.tools.Seo;
import com.islandsoftware.storefronhq.tools.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class GoogleSheets
{
   private static final Logger LOGGER = LoggerFactory.getLogger(GoogleSheets.class);

   /** Application name. */
   private static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";

   /** Directory to store user credentials for this application. */
   //private static final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"), ".credentials/sheets.googleapis.com-java-quickstart");
   private static final java.io.File DATA_STORE_DIR = new java.io.File("/home/pi/spindleandrose/sync/conf/.credentials/googleapis.com");

   /** Global instance of the {@link FileDataStoreFactory}. */
   private static FileDataStoreFactory DATA_STORE_FACTORY;

   /** Global instance of the JSON factory. */
   private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

   /** Global instance of the HTTP transport. */
   private static HttpTransport HTTP_TRANSPORT;

   private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("M/d/yyyy");

   /** Global instance of the scopes required by this quickstart.
    *
    * If modifying these scopes, delete your previously saved credentials
    * at ~/.credentials/sheets.googleapis.com-java-quickstart
    */
   private static final List<String> SCOPES = Arrays.asList(SheetsScopes.SPREADSHEETS_READONLY);

   static
   {
      try
      {
         LOGGER.info("initializing static block");
         HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
         DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
      }
      catch (Throwable t)
      {
         LOGGER.error("Could not initialize Google Sheets Client" ,t);
         //t.printStackTrace();
         //System.exit(1);
      }
   }

   /**
    * Creates an authorized Credential object.
    * @return an authorized Credential object.
    * @throws IOException
    */
   private static Credential authorize() throws IOException {
      LOGGER.info("authorize");
      // Load client secrets.
      InputStream in = GoogleSheets.class.getResourceAsStream("/client_secret.json");
      GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

      // Build flow and trigger user authorization request.
      GoogleAuthorizationCodeFlow flow =
         new GoogleAuthorizationCodeFlow.Builder(
            HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
            .setDataStoreFactory(DATA_STORE_FACTORY)
            .setAccessType("offline")
            .build();
      Credential credential = new AuthorizationCodeInstalledApp(
         flow, new LocalServerReceiver()).authorize("user");
      LOGGER.info("Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
      return credential;
   }

   /**
    * Build and return an authorized Sheets API client service.
    * @return an authorized Sheets API client service
    * @throws IOException
    */
   private static Sheets getSheetsService() throws IOException {
      LOGGER.info("getSheetsService");
      Credential credential = authorize();
      return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
         .setApplicationName(APPLICATION_NAME)
         .build();
   }

   public static Map<String, ProductInfo> readProductInfo() throws Exception
   {
      return readProductInfo(true);
   }

   public static Map<String, CustomListing> getCustomListingMap() throws Exception
   {
      LOGGER.info("getCustomListingMap");
      Map<String, CustomListing> customListingMap = new HashMap<>();
      Sheets service = getSheetsService();
      String spreadsheetId = "1YWeQK2bKSKuIf_LMuZKXP2gqVw8pQCsh2D7j5Hobw-w";
      String range = "Custom-Listing!A2:D";
      ValueRange response = service.spreadsheets().values().get(spreadsheetId, range).execute();
      List<List<Object>> values = response.getValues();
      if (values == null || values.size() == 0)
      {
         throw new Exception("No data found in Google Custom Listing Sheet");
      }
      LOGGER.info("getCustomListingMap: rows found in master product sheet={}", values.size());
      for (List row : values)
      {
         String entry = row.get(0).toString();
         if (entry == null || entry.equals(""))
         {
            LOGGER.info("getCustomListingMap: attempting to read empty row...");
            continue;
         }
         String etsyTitle = row.get(0).toString();
         Double ounces = Double.valueOf(row.get(1).toString());
         Double ourCost = Double.valueOf(row.get(2).toString());
         Double costToShipToUs = Double.valueOf(row.get(3).toString());
         CustomListing customListing = new CustomListing();
         customListing.setTitle(etsyTitle);
         customListing.setWeight(ounces);
         customListing.setOurCost(ourCost);
         customListing.setCostToShipToUs(costToShipToUs);
         customListingMap.put(etsyTitle, customListing);
      }
      LOGGER.info("getCustomListingMap: complete, numberOfListingInMap={}", customListingMap.size());
      return customListingMap;
   }

   public static Map<String, ProductInfo> readProductInfo(boolean createVariations) throws Exception
   {
      LOGGER.info("readProductInfo");
      Map<String, ProductInfo> products = new HashMap<>();
      Sheets service = getSheetsService();
      String spreadsheetId = "1YWeQK2bKSKuIf_LMuZKXP2gqVw8pQCsh2D7j5Hobw-w";
      String range = "SpindleAndRose-MasterProductList!A2:L";
      ValueRange response = service.spreadsheets().values().get(spreadsheetId, range).execute();
      List<List<Object>> values = response.getValues();
      if (values == null || values.size() == 0)
      {
         throw new Exception("No data found in Google Master Product Sheet");
      }
      LOGGER.info("readProductInfo: rows found in master product sheet={}", values.size());
      for (List row : values)
      {
         String entry = row.get(0).toString();
         if (entry == null || entry.equals(""))
         {
            LOGGER.info("readProductInfo: attempting to read empty row...");
            continue;
         }
         //LOGGER.info("{},{},{},{},{},{},{},{}", row.get(0), row.get(1), row.get(2), row.get(3), row.get(4), row.get(5), row.get(6), row.get(7));
         ProductInfo productInfo = new ProductInfo();
         productInfo.setEtsyTitle(row.get(1).toString());
         try
         {
            productInfo.setDateAdded(DATE_FORMAT.parse(row.get(0).toString()));
         }
         catch (ParseException e)
         {
            LOGGER.error("readProductInfo: parse exception reading date for etsy title {}", productInfo.getEtsyTitle());
            LOGGER.info("readProductInfo: setting date added field to current date");
            productInfo.setDateAdded(new Date());
         }
         //productInfo.setShopifyTitle(row.get(2).toString());
         productInfo.setVariation(row.get(2).toString());
         productInfo.setCost(Double.valueOf(row.get(3).toString()));
         productInfo.setShippingCost(Double.valueOf(row.get(4).toString()));
         productInfo.setPriceOverride(Double.valueOf(row.get(5).toString()));
         productInfo.setVendor(row.get(6).toString());
         //productInfo.setTags(row.get(8) == null ? "na" : row.get(8).toString());
         //productInfo.setImageAltText(row.get(9) == null ? "na" : row.get(9).toString());
         //productInfo.setMetaDescription(row.get(10) == null ? "na" : row.get(10).toString());
         //productInfo.setSku(row.get(12).toString());
         ProductInfo info = products.put(Utils.createKey(productInfo.getEtsyTitle(), productInfo.getVariation()), productInfo);
         if (info != null)
         {
            LOGGER.error("duplicate title={}", productInfo.getEtsyTitle());
         }
         /*
         if (productInfo.getShopifyTitle() == null)
         {
            LOGGER.error("null shopify title for etsy title={}", productInfo.getEtsyTitle());
         }
         */
         if (createVariations)
         {
            Map<String, ProductInfo> variations = Utils.createProductInfoForVariations(productInfo);
            if (variations != null)
            {
               products.putAll(variations);
            }
         }
      }
      LOGGER.info("readProductInfo: complete, numberOfProductsInMap={}", products.size());
      return products;
   }

   public static Map<String, Keyword> readQueryVolume() throws Exception
   {
      LOGGER.info("readQueryVolume");
      Map<String, Keyword> keywords = new HashMap<>();
      Sheets service = getSheetsService();
      String spreadsheetId = "1tc6uGAzWx7_HHNT0EMGa9r2CApUUQtE7FygJDx-Jg-A";
      String range = "QueryVolume!A2:C";
      ValueRange response = service.spreadsheets().values().get(spreadsheetId, range).execute();
      List<List<Object>> values = response.getValues();
      if (values == null || values.size() == 0)
      {
         throw new Exception("No data found in SpindleAndRose SEO Volume Sheet");
      }
      for (List row : values)
      {
         Keyword keyword = new Keyword();
         keyword.setQuery(row.get(1).toString());
         keyword.setVolume(Integer.parseInt(row.get(2).toString()));
         keywords.put(row.get(0).toString(), keyword);
      }
      LOGGER.info("readQueryVolume: complete, numberOfKeywords={}", keywords.size());
      return keywords;
   }

   public static Map<String, Seo> getSeoMap() throws Exception
   {
      LOGGER.info("getSeoMap");
      Map<String, Seo> seoMap = new HashMap<>();
      Sheets service = getSheetsService();
      String spreadsheetId = "1tc6uGAzWx7_HHNT0EMGa9r2CApUUQtE7FygJDx-Jg-A";
      String range = "MetaTitleAndDescription!A2:F";
      ValueRange response = service.spreadsheets().values().get(spreadsheetId, range).execute();
      List<List<Object>> values = response.getValues();
      if (values == null || values.size() == 0)
      {
         throw new Exception("No data found in SpindleAndRose SEO MetaTitleAndDescription Sheet");
      }
      for (List row : values)
      {
         Seo seo = new Seo();
         seo.setEtsyTitle(row.get(0).toString());
         seo.setH1(row.get(1).toString());
         seo.setH2(row.get(2).toString());
         String titleTag = row.get(3).toString();
         seo.setTitleTag(new TitleTag(titleTag));
         String[] keywords = row.get(4).toString().split("|");
         for (String keyword : keywords)
         {
            seo.getKeywords().add(keyword);
         }
         seo.setDescription(row.get(5).toString());
         seoMap.put(seo.getEtsyTitle().trim().replaceAll(" ", ""), seo);
      }
      LOGGER.info("getSeoMap: complete, numberOfSeoObjects={}", seoMap.size());
      return seoMap;
   }

   public static void main(String[] args)
   {
      try
      {
         Map<String, ProductInfo> products = readProductInfo(false);
         for (ProductInfo productInfo : products.values())
         {
            LOGGER.info("{}", productInfo);
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

}
