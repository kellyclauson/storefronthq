package com.islandsoftware.storefronhq.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.islandsoftware.storefronhq.FabricInventory;
import com.islandsoftware.storefronhq.SMSClient;
import com.islandsoftware.storefronhq.etsy.model.VariationOption;
import com.islandsoftware.storefronhq.etsy.model.inventory.*;
import com.islandsoftware.storefronhq.orderprocessing.ProductInfo;
import com.islandsoftware.storefronhq.orderprocessing.SpindleAndRoseOrder;
import com.islandsoftware.storefronhq.shopify.sync.model.Product;
import com.islandsoftware.storefronhq.shopify.sync.model.Variant;
import com.islandsoftware.storefronhq.stats.FormattedOrderStats;
import com.islandsoftware.storefronhq.stats.OrderItem;
import com.islandsoftware.storefronhq.stats.OrderStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Utils
{
   private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
   private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss");
   private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance();
   private static final NumberFormat PERCENT_FORMAT = NumberFormat.getPercentInstance();
   private static final NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance();

   private static final String US = "US";
   private static final String UNITED_STATES = "United States";
   private static final String CA = "CA";
   private static final String CANADA = "Canada";
   private static final String SHIPPING_INFO_FILE = "/home/pi/spindleandrose/sync/conf/shippinginfo";
   private static final String SHIPPING_INFO_FILE_CANADA = "/home/pi/spindleandrose/sync/conf/shippinginfo-canada";
   private static final String SHIPPING_INFO_FILE_INTERNATIONAL = "/home/pi/spindleandrose/sync/conf/shippinginfo-international";
   private static final Double PACKAGE_WEIGHT = 2.0;
   private static final Double OUNCES_PER_GRAM = 0.035274;
   private static final Double OTHER = 15.25;

   // printer paper
   private static final double PRICE_PER_CARTON = 35.13; // with tax
   private static final double NUMBER_OF_SHEETS_IN_CARTON = 5000.0;
   private static final double LABELS_PER_SHEET = 2.0;
   private static final double LABEL_COST_PER_ORDER = PRICE_PER_CARTON / NUMBER_OF_SHEETS_IN_CARTON / LABELS_PER_SHEET;

   // ink
   private static final double PRICE_PER_INK_CARTON = 44.72; // with tax
   private static final double NUMBER_OF_CARTRIDGES_IN_CARTON = 2.0;
   private static final double NUMBER_OF_ORDERS_PER_CARTRIDGE = 160.0;
   private static final double INK_COST_PER_ORDER = PRICE_PER_INK_CARTON / NUMBER_OF_CARTRIDGES_IN_CARTON / NUMBER_OF_ORDERS_PER_CARTRIDGE;

   // international packaging
   private static final double PRICE_INTERNATIONAL_PACKAGING_CARTON = 11.49; // with tax
   private static final double NUMBER_OF_INTERNATIONAL_PACKAGES_IN_CARTON = 100.0;
   private static final double INTERNATIONAL_PACKAGE_COST_PER_ORDER = PRICE_INTERNATIONAL_PACKAGING_CARTON / NUMBER_OF_INTERNATIONAL_PACKAGES_IN_CARTON;

   // domestic packaging
   private static final double PRICE_DOMESTIC_PACKAGING_CARTON = 229.99; // with tax
   private static final double NUMBER_OF_DOMESTIC_PACKAGES_IN_CARTON = 1500.0;
   private static final double PRICE_PER_DOMESTIC_PACKAGE = PRICE_DOMESTIC_PACKAGING_CARTON / NUMBER_OF_DOMESTIC_PACKAGES_IN_CARTON;
   private static final double PERCENTAGE_OF_DOMESTIC_ORDERS_NEEDING_PACKAGE = 0.7;
   private static final double DOMESTIC_PACKAGE_COST_PER_ORDER = PRICE_PER_DOMESTIC_PACKAGE * PERCENTAGE_OF_DOMESTIC_ORDERS_NEEDING_PACKAGE;

   // stickers
   private static final double PRICE_STICKERS_CARTON = 9.99; // with tax
   private static final double NUMBER_OF_STICKERS_IN_CARTON = 2000.0;
   private static final double STICKER_COST_PER_ORDER = PRICE_STICKERS_CARTON / NUMBER_OF_STICKERS_IN_CARTON;

   // tissue paper
   private static final double PRICE_TISSUE_PAPER_CARTON = 5.96; // with tax
   private static final double NUMBER_OF_TISSUES_IN_CARTON = 100.0;
   private static final double NUMBER_OF_ORDERS_PER_TISSUE = 4.0;
   private static final double TISSUE_COST_PER_ORDER = PRICE_TISSUE_PAPER_CARTON / NUMBER_OF_TISSUES_IN_CARTON / NUMBER_OF_ORDERS_PER_TISSUE;

   private static final double SHIPPING_SUPPLIES_COST_PER_DOMESTIC_ORDER =
      LABEL_COST_PER_ORDER +
         INK_COST_PER_ORDER +
         DOMESTIC_PACKAGE_COST_PER_ORDER +
         STICKER_COST_PER_ORDER +
         TISSUE_COST_PER_ORDER;

   private static final double SHIPPING_SUPPLIES_COST_PER_INTERNATIONAL_ORDER =
      LABEL_COST_PER_ORDER +
         INK_COST_PER_ORDER +
         INTERNATIONAL_PACKAGE_COST_PER_ORDER +
         STICKER_COST_PER_ORDER +
         TISSUE_COST_PER_ORDER;

   private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

   static
   {
      NUMBER_FORMAT.setMaximumFractionDigits(2);
   }

   private Utils()
   {}


   public static long dateStringToMillis(String date) throws Exception
   {
      return DATE_FORMAT.parse(date).getTime();
   }

   public static String toDayOfWeek(long date) throws Exception
   {
     return ZonedDateTime.ofInstant(Instant.ofEpochMilli(date), ZoneId.of("US/Eastern")).getDayOfWeek().name();
   }

   public static String toMonthOfYear(long date) throws Exception
   {
      return ZonedDateTime.ofInstant(Instant.ofEpochMilli(date), ZoneId.of("US/Eastern")).getMonth().name();
   }

   public static int toMonthOfYearValue(long date) throws Exception
   {
      return ZonedDateTime.ofInstant(Instant.ofEpochMilli(date), ZoneId.of("US/Eastern")).getMonthValue();
   }

   public static int toDayOfYear(long date) throws Exception
   {
      return ZonedDateTime.ofInstant(Instant.ofEpochMilli(date), ZoneId.of("US/Eastern")).getDayOfYear();
   }

   public static int toYear(long date) throws Exception
   {
      return ZonedDateTime.ofInstant(Instant.ofEpochMilli(date), ZoneId.of("US/Eastern")).getYear();
   }

   public static String millisToDateString(long time)
   {
      return DATE_FORMAT.format(new Date(time));
   }

   public static String currencyFormat(double value)
   {
      return CURRENCY_FORMAT.format(value);
   }

   public static String numberFormat(double value)
   {
      return NUMBER_FORMAT.format(value);
   }

   public static double fromNumberFormat(String value)
   {
      return Double.valueOf(value);
   }

   public static double fromCurrencyFormat(String value)
   {
      return Double.valueOf(value.replaceAll("\\$", "").replaceAll(",", ""));
   }

   public static String percentageFormat(double value)
   {
      return PERCENT_FORMAT.format(value);
   }

   public static double fromPercentageFormat(String value)
   {
      return Double.valueOf(value.replaceAll("%", ""));
   }

   // called by SyncShopifyInventoryWithEtsy tool to update shopify's inventory to sync with etsy
   // and by EtsyReceiptPoller to update shopify inventory on etsy transaction
   public static boolean updateInventory(Product product, int newBaseQuantity, boolean isBaseQuantityHalfYard) throws Exception
   {
      LOGGER.info("updateInventory: newBaseQuantity={} isBaseQuantityHalfYard={} for [{}]", newBaseQuantity, isBaseQuantityHalfYard, product.getTitle());
      String productType = product.getProductType();
      LOGGER.debug("updateInventory: productType={}", productType);
      boolean updated = false;
      if ("Gift Certificate".equalsIgnoreCase(productType))
      {
         LOGGER.info("updateInventory: Skipping Gift Certificate");
      }
      else
      {
         List<Variant> variants = product.getVariants();
         if (variants != null && variants.size() > 0)
         {
            LOGGER.debug("updateInventory: updating variant inventory");
            for (Variant variant : variants)
            {
               LOGGER.info("updateInventory: current quantity={} for variant=[{}]", variant.getInventoryQuantity(), variant.getTitle());
               double updatedQuantity;
               if (variant.getTitle().startsWith("Fat Quarter") || variant.getTitle().startsWith("1/4"))
               {
                  if (isBaseQuantityHalfYard)
                  {
                     updatedQuantity = Math.floor((double)newBaseQuantity * 2.0);
                  }
                  else
                  {
                     updatedQuantity = Math.floor((double)newBaseQuantity * 4.0);
                  }
               }
               else if (variant.getTitle().startsWith("1/2"))
               {
                  if (isBaseQuantityHalfYard)
                  {
                     updatedQuantity = newBaseQuantity;
                  }
                  else
                  {
                     updatedQuantity = Math.floor((double)newBaseQuantity * 2.0);
                  }
               }
               else if (variant.getTitle().startsWith("3/4"))
               {
                  if (newBaseQuantity == 0)
                  {
                     updatedQuantity = 0;
                  }
                  else
                  {
                     if (isBaseQuantityHalfYard)
                     {
                        double oneYardQuantity = (double)newBaseQuantity / 2.0;
                        updatedQuantity = Math.floor(oneYardQuantity / 0.75);
                     }
                     else
                     {
                        updatedQuantity = Math.floor((double)newBaseQuantity / 0.75);
                     }
                  }
               }
               else if (variant.getTitle().startsWith("1 Yard") || variant.getTitle().startsWith("1 Panel") || variant.getTitle().startsWith("Default Title"))
               {
                  if (isBaseQuantityHalfYard)
                  {
                     updatedQuantity = Math.floor((double)newBaseQuantity / 2.0);
                  }
                  else
                  {
                     updatedQuantity = newBaseQuantity;
                  }
               }
               else
               {
                  throw new Exception("Unsupported variant title: " + variant.getTitle());
               }

               Integer currentQuantity = variant.getInventoryQuantity();
               if (currentQuantity != (int)updatedQuantity)
               {
                  variant.setInventoryQuantity((int)updatedQuantity);
                  LOGGER.info("updateInventory: set quantity={} for variant=[{}]", variant.getInventoryQuantity(), variant.getTitle());
                  updated = true;
               }
               else
               {
                  LOGGER.info("updateInventory: quantity of {} for variant=[{}] already correct", variant.getInventoryQuantity(), variant.getTitle());
               }
            }
         }
         else
         {
            LOGGER.error("updateInventory: No variants for product [{}], not updating variant inventory", product.getTitle());
         }
      }
      return updated;
   }

   // called by ProcessShopifyOrderThread to adjust shopify variant inventory on shopify sale
   public static boolean updateInventory(Product product, long variantId, String purchasedVariantTitle, int purchasedQuantity) throws Exception
   {
      LOGGER.info("updateInventory: purchased variant={} purchasedQuantity={}", purchasedVariantTitle, purchasedQuantity);
      String productType = product.getProductType();
      LOGGER.debug("updateInventory: productType={}", productType);
      if ("Gift Certificate".equalsIgnoreCase(product.getProductType()))
      {
         LOGGER.info("updateInventory: ignoring Gift Certificate");
         return false;
      }
      List<Variant> variants = product.getVariants();
      if (variants != null && variants.size() > 0)
      {
         for (Variant variant : variants)
         {
            if (Utils.isValidVariationTitle(variant.getTitle()))
            {
               if (variant.getId() != variantId)
               {
                  int variantQuantitySold;
                  double unitsSold = Utils.calculateNumberOfBaseUnits(purchasedVariantTitle, purchasedQuantity);
                  LOGGER.info("updateInventory: baseQuantitySold={}", unitsSold);
                  if (variant.getTitle().startsWith("Fat Quarter") || variant.getTitle().startsWith("1/4"))
                  {
                     variantQuantitySold = (int)Math.ceil(unitsSold * 4.0);
                  }
                  else if (variant.getTitle().startsWith("1/2"))
                  {
                     variantQuantitySold = (int)Math.ceil(unitsSold * 2.0);
                  }
                  else if (variant.getTitle().startsWith("3/4"))
                  {
                     variantQuantitySold = (int)Math.ceil(unitsSold / 0.75);
                  }
                  else
                  {
                     variantQuantitySold = (int)Math.ceil(unitsSold);
                  }
                  LOGGER.info("updateInventory: variant=[{}]", variant.getTitle());
                  LOGGER.info("updateInventory: variantQuantitySold={}", variantQuantitySold);
                  int currentVariantQuantity = variant.getInventoryQuantity();
                  LOGGER.info("updateInventory: currentVariantQuantity={}", currentVariantQuantity);
                  int updatedQuantity = currentVariantQuantity - variantQuantitySold;
                  LOGGER.info("updateInventory: updatedVariantQuantity={}", updatedQuantity);
                  if (updatedQuantity < 0)
                  {
                     LOGGER.warn("updateInventory: updatedVariantQuantity={} IS NEGATIVE, setting to 0", updatedQuantity);
                     updatedQuantity = 0;
                  }
                  variant.setInventoryQuantity(updatedQuantity);
               }
            }
            else
            {
               throw new Exception("updateInventory: unsupported variation " + variant.getTitle() + " for product [" + product.getTitle() + "]");
            }
         }
         return true;
      }
      else
      {
         throw new Exception("updateInventory: no variants for product [" + product.getTitle() + "]");
      }
   }
   public static boolean isValidVariationTitle(String title)
   {
      if (title.startsWith("Fat Quarter") ||
         title.startsWith("1/4") ||
         title.startsWith("1/2") ||
         title.startsWith("3/4") ||
         title.startsWith("1 Yard") ||
         title.startsWith("1 Panel") ||
         title.startsWith("Default Title") ||
         title.startsWith("Ten Dollars") ||
         title.startsWith("Twenty Dollars") ||
         title.startsWith("Fifty Dollars") ||
         title.startsWith("Seventy-Five") ||
         title.startsWith("One Hundred"))
      {
         return true;
      }
      return false;
   }

   public static boolean isPartialYard(String title)
   {
      if (title.startsWith("Fat Quarter") ||
         title.startsWith("1/4") ||
         title.startsWith("1/2") ||
         title.startsWith("3/4"))
      {
         return true;
      }
      return false;
   }

   public static double calculateWeight(String variantTitle, double weight, String productTitle)
   {
      String title = variantTitle.trim();
      if (title.startsWith("Fat Quarter") || title.startsWith("1/4"))
      {
         return weight / 4.0;
      }
      if (title.startsWith("1/2"))
      {
         return weight / 2.0;
      }
      if (title.startsWith("3/4"))
      {
         return weight * 0.75;
      }
      if (title.startsWith("1 Yard"))
      {
         return weight;
      }
      if (title.startsWith("1 Panel"))
      {
         return weight;
      }
      if (title.startsWith("Default"))
      {
         return weight;
      }
      if (title.equals("Ten Dollars") || title.equals("Twenty Dollars") || title.equals("Fifty Dollars") || title.equals("Seventy-Five") || title.equals("One Hundred"))
      {
         return weight;
      }
      LOGGER.error("Unsupported variant title [{}] for product [{}]", title, productTitle);
      return 0;
   }

   public static double adjustWeight(double initialWeight)
   {
      if (initialWeight <= 1.0)
      {
         return initialWeight;
      }
      double weight = initialWeight + 1.0;
      return weight;
   }

   public static void adjustWeight(Product product)
   {
      Double weight = product.getWeight();
      if (weight > 1.0)
      {
         weight += 1.0;
         product.setWeight(weight);
      }
   }

   public static List<Variant> reorder(List<Variant> variants)
   {
      LOGGER.debug("reorder: before={}", variants);
      TreeMap<Integer, Variant> map = new TreeMap<Integer, Variant>();
      for (Variant variant : variants)
      {
         map.put(Integer.parseInt(variant.getPosition()), variant);
      }
      List<Variant> reordered = new ArrayList<Variant>(variants.size());
      for (Integer position : map.keySet())
      {
         reordered.add(map.get(position));
      }
      LOGGER.debug("reorder: after={}", reordered);
      return reordered;
   }

   public static void sleep(long millis)
   {
      try
      {
         Thread.sleep(millis);
      }
      catch (Exception e)
      {
         LOGGER.error("sleep", e);
      }
   }

   public static Integer calculateInventory(String variantTitle, Integer inventory, String productTitle)
   {
      String title = variantTitle.trim();
      if (title.startsWith("Fat Quarter") || title.startsWith("1/4"))
      {
         return inventory * 4;
      }
      if (title.startsWith("1/2"))
      {
         return inventory * 2;
      }
      if (title.startsWith("3/4"))
      {
         return (int)(inventory.doubleValue() / 0.75);
      }
      if (title.startsWith("1 Yard"))
      {
         return inventory;
      }
      if (title.startsWith("1 Panel"))
      {
         return inventory;
      }
      if (title.startsWith("Default"))
      {
         return inventory;
      }
      if (title.equals("Ten Dollars") || title.equals("Twenty Dollars") || title.equals("Fifty Dollars") || title.equals("Seventy-Five") || title.equals("One Hundred"))
      {
         return 100;
      }
      LOGGER.error("Unsupported variant title [{}] for product [{}]", title, productTitle);
      return 0;
   }

   public static String findPosition(String value)
   {
      if (value.startsWith("Fat Quarter"))
      {
         return "5";
      }
      if (value.startsWith("1/4"))
      {
         return "4";
      }
      if (value.startsWith("1/2"))
      {
         return "3";
      }
      if (value.startsWith("3/4"))
      {
         return "2";
      }
      else
      {
         return "1";
      }
   }

   public static String getStringFromInputStream(InputStream is) {

      BufferedReader br = null;
      StringBuilder sb = new StringBuilder();
      String line;
      try
      {
         br = new BufferedReader(new InputStreamReader(is));
         while ((line = br.readLine()) != null)
         {
            sb.append(line);
         }
      }
      catch (IOException e)
      {
         LOGGER.error("getStringFromInputStream: error reading input steam", e);
         sb.append("Could Not Read Input Stream");
      }
      finally
      {
         if (br != null)
         {
            try
            {
               br.close();
            }
            catch (IOException e)
            {
               LOGGER.error("getStringFromInputStream: error closing buffered reader", e);
            }
         }
      }
      return sb.toString();
   }

   public static double calculateNumberOfBaseUnits(String variation, int quantity)
   {
      if (variation.startsWith("1/4"))
      {
         return quantity * 0.25;
      }
      if (variation.startsWith("1/2") || variation.startsWith("Fat Quarter"))
      {
         return quantity * 0.5;
      }
      if (variation.startsWith("3/4"))
      {
         return quantity * 0.75;
      }
      return quantity;
   }

   public static int getBaseQuantity(Product product) throws Exception
   {
      List<Variant> variants = product.getVariants();
      if (variants == null)
      {
         throw new Exception("No variants for product " + product.getTitle());
      }
      for (Variant variant : variants)
      {
         String title = variant.getTitle();
         if (title.startsWith("Default Title") || title.startsWith("1 Panel") || title.startsWith("1 Yard"))
         {
            return variant.getInventoryQuantity();
         }
      }
      throw new Exception("could not find base variant for [" + product.getTitle() + "] variants are " + variants);
   }

   public static boolean isHalfAndWholeYardOnly(InventoryResponse inventory)
   {
      LOGGER.debug("isHalfAndWholeYardOnly");
      boolean halfAndWholeYard = false;
      if (inventory != null)
      {
         InventoryResults inventoryResults = inventory.getInventoryResults();
         if (inventoryResults != null)
         {
            InventoryProduct[] products = inventoryResults.getProducts();
            if (products != null && products.length == 2)
            {
               boolean foundHalf = false;
               boolean foundWhole = false;
               for (InventoryProduct product : products)
               {
                  InventoryPropertyValue[] propertyValues = product.getPropertyValues();
                  if (propertyValues != null && propertyValues.length > 0)
                  {
                     String[] values = propertyValues[0].getValues();
                     if (values != null)
                     {
                        for (String value : values)
                        {
                           if (value.startsWith("1/2"))
                           {
                              foundHalf = true;
                           }
                           if (value.startsWith("1 Yard"))
                           {
                              foundWhole = true;
                           }
                        }
                     }
                  }
               }
               if (foundHalf && foundWhole)
               {
                  halfAndWholeYard = true;
               }
            }
         }
      }
      return halfAndWholeYard;
   }

   public static boolean isHalfYardBased(VariationOption[] options)
   {
      LOGGER.debug("isHalfYardBased");
      boolean halfYardBased = false;
      if (options != null)
      {
         LOGGER.debug("isHalfYardBased: examining option values {}", Arrays.asList(options));
         if (options.length == 1 && options[0].getValue().startsWith("1/2"))
         {
            halfYardBased = true;
         }
      }
      return halfYardBased;
   }

   public static Collection<? extends Variant> createShopifyVariantsFromOneYardEtsyProduct(String title, double oneYardWeight, InventoryProduct oneYard)
   {
      int oneYardQuantity = oneYard.getInventoryOfferings()[0].getQuantity();
      LOGGER.info("createShopiyfVariantsFromOneYardEtsyOption: 1 Yard inventory is {} for {}", oneYardQuantity, title);
      // product weight is for 1 yard so no change
      LOGGER.info("createShopiyfVariantsFromOneYardEtsyOption: 1 Yard weight is {}", oneYardWeight);
      Integer amount = oneYard.getInventoryOfferings()[0].getInventoryOfferingPrice().getAmount();
      double oneYardPrice = (double)amount / 100.0;
      LOGGER.info("createShopiyfVariantsFromOneYardEtsyOption: 1 Yard price is {}", oneYardPrice);

      List<Variant> variants = new ArrayList<>();
      Variant quarterYard = createVariant("1/4 Yard", format(calculateVariantPriceFromOneYardPrice("1/4 Yard", oneYardPrice)), oneYardWeight, title, oneYardQuantity);
      variants.add(quarterYard);
      LOGGER.info("createShopiyfVariantsFromOneYardEtsyOption: created variant 1/4 Yard with inventory={} weight={} price={}", quarterYard.getInventoryQuantity(), quarterYard.getWeight(), quarterYard.getPrice());
      Variant fatQuarter = createVariant("Fat Quarter", format(calculateVariantPriceFromOneYardPrice("Fat Quarter", oneYardPrice)), oneYardWeight, title, oneYardQuantity);
      variants.add(fatQuarter);
      LOGGER.info("createShopiyfVariantsFromOneYardEtsyOption: created variant Fat Quarter with inventory={} weight={} price={}", fatQuarter.getInventoryQuantity(), fatQuarter.getWeight(), fatQuarter.getPrice());
      Variant halfYard = createVariant("1/2 Yard", format(calculateVariantPriceFromOneYardPrice("1/2 Yard", oneYardPrice)), oneYardWeight, title, oneYardQuantity);
      variants.add(halfYard);
      LOGGER.info("createShopiyfVariantsFromOneYardEtsyOption: created variant 1/2 Yard with inventory={} weight={} price={}", halfYard.getInventoryQuantity(), halfYard.getWeight(), halfYard.getPrice());
      Variant threeQuarters = createVariant("3/4 Yard", format(calculateVariantPriceFromOneYardPrice("3/4 Yard", oneYardPrice)), oneYardWeight, title, oneYardQuantity);
      variants.add(threeQuarters);
      LOGGER.info("createShopiyfVariantsFromOneYardEtsyOption: created variant 3/4 Yard with inventory={} weight={} price={}", threeQuarters.getInventoryQuantity(), threeQuarters.getWeight(), threeQuarters.getPrice());
      Variant yard = createVariant("1 Yard", format(oneYardPrice), oneYardWeight, title, oneYardQuantity);
      variants.add(yard);
      LOGGER.info("createShopiyfVariantsFromOneYardEtsyOption: created variant 1 Yard with inventory={} weight={} price={}", yard.getInventoryQuantity(), yard.getWeight(), yard.getPrice());
      return variants;
   }

   private static Variant createVariantWithAdjustedWeightAndQuantity(String title, String price, Double weight, String productTitle, Integer quantity)
   {
      Variant variant = new Variant();
      variant.setTitle(title);
      variant.setPrice(price);
      variant.setOption1(title);
      variant.setPosition(Utils.findPosition(title));
      variant.setWeight(String.valueOf(weight));
      variant.setInventoryManagement("shopify");
      variant.setInventoryQuantity(quantity);
      return variant;
   }

   private static Variant createVariant(String title, String price, Double weight, String productTitle, Integer productInventory)
   {
      Variant variant = new Variant();
      variant.setTitle(title);
      variant.setPrice(price);
      variant.setOption1(title);
      variant.setPosition(Utils.findPosition(title));
      variant.setWeight(String.valueOf(Utils.calculateWeight(title, weight, productTitle)));
      variant.setInventoryManagement("shopify");
      variant.setInventoryQuantity(Utils.calculateInventory(title, productInventory, productTitle));
      return variant;
   }

   private static double calculateVariantPriceFromHalfYardPrice(String variantTitle, double halfYardPrice)
   {
      if (variantTitle.equals("Fat Quarter"))
      {
         return (halfYardPrice / 2.0) * 1.1;
      }
      if (variantTitle.equals("1/4 Yard"))
      {
         return halfYardPrice / 2.0;
      }
      if (variantTitle.equals("3/4 Yard"))
      {
         return (halfYardPrice * 2.0) * 0.75;
      }
      return halfYardPrice * 2.0;
   }

   private static double calculateVariantPriceFromOneYardPrice(String variantTitle, double oneYardPrice)
   {
      if (variantTitle.equals("Fat Quarter"))
      {
         return (oneYardPrice / 4.0) * 1.1;
      }
      if (variantTitle.equals("1/4 Yard"))
      {
         return oneYardPrice / 4.0;
      }
      if (variantTitle.equals("1/2 Yard"))
      {
         return oneYardPrice / 2.0;
      }
      if (variantTitle.equals("3/4 Yard"))
      {
         return (oneYardPrice * 0.75);
      }
      return oneYardPrice;
   }

   public static String format(double d)
   {
      String s = Double.toString(d);
      String[] split = s.split("\\.");
      if (split[1].length() == 2)
      {
         return s;
      }
      if (split[1].length() == 1)
      {
         return split[0] + "." + split[1] + "0";
      }
      return split[0] + "." + split[1].substring(0, split[1].length() == 1 ? 1 : 2);
   }

   public static void setPriceOnProductVariations(Product product, double yardPrice)
   {
      List<Variant> variants = product.getVariants();
      for (Variant variant : variants)
      {
         if (variant.getTitle().startsWith("Fat Quarter"))
         {
            String price = format((yardPrice / 4.0 * 1.1));
            LOGGER.info("setting Fat Quarter price from {} to {} for product {}", variant.getPrice(), price, product.getTitle());
            variant.setPrice(price);
         }
         else if (variant.getTitle().startsWith("1/4 Yard"))
         {
            String price = format(yardPrice / 4.0);
            LOGGER.info("setting 1/4 Yard price from {} to {} for product {}", variant.getPrice(), price, product.getTitle());
            variant.setPrice(price);
         }
         else if (variant.getTitle().startsWith("1/2 Yard"))
         {
            String price = format(yardPrice / 2.0);
            LOGGER.info("setting 1/2 Yard price from {} to {} for product {}", variant.getPrice(), price, product.getTitle());
            variant.setPrice(price);
         }
         else if (variant.getTitle().startsWith("3/4 Yard"))
         {
            String price = format(yardPrice * 0.75);
            LOGGER.info("setting 3/4 Yard price from {} to {} for product {}", variant.getPrice(), price, product.getTitle());
            variant.setPrice(price);
         }
         else if (variant.getTitle().startsWith("1 Yard"))
         {
            String price = format(yardPrice);
            LOGGER.info("setting 1 Yard price from {} to {} for product {}", variant.getPrice(), price, product.getTitle());
            variant.setPrice(price);
         }
         else if (variant.getTitle().startsWith("1 Panel"))
         {
            String price = format(yardPrice);
            LOGGER.info("setting 1 Panel price from {} to {} for product {}", variant.getPrice(), price, product.getTitle());
            variant.setPrice(price);
         }
         else if (variant.getTitle().startsWith("Default Title"))
         {
            String price = format(yardPrice);
            LOGGER.info("setting Default Title price from {} to {} for product {}", variant.getPrice(), price, product.getTitle());
            variant.setPrice(price);
         }
         else
         {
            LOGGER.error("UNKNOWN product variant name {} for product {}", variant.getTitle(), product.getTitle());
         }
      }
   }

   public static VariationOption getOneYardVariationOption(VariationOption[] options)
   {
      for (VariationOption option : options)
      {
         if (option.getValue().startsWith("1 Yard"))
         {
            return option;
         }
      }
      return null;
   }

   public static String toJson(Object o)
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

   public static InventoryProduct[] addOneYard(InventoryProduct[] products)
   {
      InventoryProduct halfYard = products[0];
      InventoryOffering halfYardInventoryOffering = halfYard.getInventoryOfferings()[0];
      Integer halfYardAmount = halfYardInventoryOffering.getInventoryOfferingPrice().getAmount();
      Integer halfYardQuantity = halfYardInventoryOffering.getQuantity();

      InventoryProduct oneYard = new InventoryProduct();
      InventoryOffering inventoryOffering = new InventoryOffering();
      inventoryOffering.setQuantity(halfYardQuantity / 2);
      InventoryOfferingPrice inventoryOfferingPrice = new InventoryOfferingPrice();
      inventoryOfferingPrice.setAmount(halfYardAmount * 2);
      inventoryOfferingPrice.setDivisor(100);
      inventoryOfferingPrice.setCurrencyCode("USD");
      String formattedPrice = format((double)inventoryOfferingPrice.getAmount() / 100.0);
      inventoryOfferingPrice.setCurrencyFormattedRaw(formattedPrice);
      inventoryOfferingPrice.setCurrencyFormattedShort("$" + formattedPrice);
      inventoryOfferingPrice.setCurrencyFormattedLong("$" + formattedPrice + " USD");
      inventoryOffering.setInventoryOfferingPrice(inventoryOfferingPrice);
      oneYard.setInventoryOfferings(new InventoryOffering[] {inventoryOffering});

      InventoryPropertyValue inventoryPropertyValue = new InventoryPropertyValue();
      inventoryPropertyValue.setPropertyId(506L);
      inventoryPropertyValue.setPropertyName("Length");
      String[] values = new String[] {"1 Yard"};
      inventoryPropertyValue.setValues(values);
      inventoryPropertyValue.setValueIds(new Long[] {0L});
      oneYard.setPropertyValues(new InventoryPropertyValue[] {inventoryPropertyValue});

      return new InventoryProduct[] {halfYard, oneYard};
   }

   public static InventoryProduct[] updatePriceAndQuantity(InventoryProduct[] products, int quantity, double halfYardPrice)
   {
      for (InventoryProduct product : products)
      {
         String value = product.getPropertyValues()[0].getValues()[0];
         if (value.startsWith("1/2 Yard"))
         {
            product.getInventoryOfferings()[0].setQuantity(quantity);

         }
         else if (value.startsWith("1 Yard"))
         {
            product.getInventoryOfferings()[0].setQuantity(quantity / 2);
            product.getInventoryOfferings()[0].getInventoryOfferingPrice().setAmount((int)((halfYardPrice * 2) * 100));
            String formattedPrice = format(halfYardPrice * 2.0);
            product.getInventoryOfferings()[0].getInventoryOfferingPrice().setCurrencyFormattedRaw(formattedPrice);
            product.getInventoryOfferings()[0].getInventoryOfferingPrice().setCurrencyFormattedShort("$" + formattedPrice);
            product.getInventoryOfferings()[0].getInventoryOfferingPrice().setCurrencyFormattedLong("$" + formattedPrice + " USD");
            product.getInventoryOfferings()[0].getInventoryOfferingPrice().setCurrencyCode("USD");
            product.getInventoryOfferings()[0].getInventoryOfferingPrice().setDivisor(100);

         }
         else
         {
            LOGGER.error("COULD NOT FIND VALID VARIATIONS");
         }
      }
      return products;
   }

   public static InventoryProduct[] updateHalfYardPriceAndQuantity(InventoryProduct[] products)
   {
      InventoryProduct halfYard = products[0];
      InventoryOffering halfYardInventoryOffering = halfYard.getInventoryOfferings()[0];
      Integer halfYardAmount = halfYardInventoryOffering.getInventoryOfferingPrice().getAmount();
      Integer halfYardQuantity = halfYardInventoryOffering.getQuantity();
      halfYardInventoryOffering.getInventoryOfferingPrice().setAmount(halfYardAmount - 100);
      halfYardInventoryOffering.setQuantity(halfYardQuantity - 1);
      return new InventoryProduct[] {halfYard};

      /*
      InventoryProduct oneYard = new InventoryProduct();
      InventoryOffering inventoryOffering = new InventoryOffering();
      inventoryOffering.setQuantity(halfYardQuantity / 2);
      InventoryOfferingPrice inventoryOfferingPrice = new InventoryOfferingPrice();
      inventoryOfferingPrice.setAmount(halfYardAmount * 2);
      inventoryOfferingPrice.setDivisor(100);
      inventoryOfferingPrice.setCurrencyCode("USD");
      String formattedPrice = format((double)inventoryOfferingPrice.getAmount() / 100.0);
      inventoryOfferingPrice.setCurrencyFormattedRaw(formattedPrice);
      inventoryOfferingPrice.setCurrencyFormattedShort("$" + formattedPrice);
      inventoryOfferingPrice.setCurrencyFormattedLong("$" + formattedPrice + " USD");
      inventoryOffering.setInventoryOfferingPrice(inventoryOfferingPrice);
      oneYard.setInventoryOfferings(new InventoryOffering[] {inventoryOffering});

      InventoryPropertyValue inventoryPropertyValue = new InventoryPropertyValue();
      inventoryPropertyValue.setPropertyId(506L);
      inventoryPropertyValue.setPropertyName("Length");
      String[] values = new String[] {"1 Yard"};
      inventoryPropertyValue.setValues(values);
      oneYard.setPropertyValues(new InventoryPropertyValue[] {inventoryPropertyValue});
      return new InventoryProduct[] {halfYard, oneYard};
      */

   }

   public static FabricInventory toFabricInventory(InventoryResponse inventory) throws Exception
   {
      FabricInventory fabricInventory = new FabricInventory();
      InventoryResults inventoryResults = inventory.getInventoryResults();
      InventoryProduct[] products = inventoryResults.getProducts();
      for (InventoryProduct product : products)
      {
         String value = product.getPropertyValues()[0].getValues()[0];
         if (value.startsWith("1 Yard"))
         {
            fabricInventory.setOneYardCount(product.getInventoryOfferings()[0].getQuantity());
         }
         else if (value.startsWith("3/4 Yard"))
         {
            fabricInventory.setThreeQuarterYardCount(product.getInventoryOfferings()[0].getQuantity());
         }
         else if (value.startsWith("1/2 Yard"))
         {
            fabricInventory.setHalfYardCount(product.getInventoryOfferings()[0].getQuantity());
         }
         else if (value.startsWith("1/4 Yard"))
         {
            fabricInventory.setQuarterYardCount(product.getInventoryOfferings()[0].getQuantity());
         }
         else if (value.startsWith("Fat Quarter"))
         {
            fabricInventory.setFatQuarterCount(product.getInventoryOfferings()[0].getQuantity());
         }
      }
      return fabricInventory;
   }

   public static boolean isBaseQuantityHalfYard(InventoryResponse inventory)
   {
      try
      {
         getProduct(inventory, "1/2");
         return true;

      }
      catch (Exception e)
      {
         return false;
      }
   }

   public static int getBaseQuantity(InventoryResponse inventory) throws Exception
   {
      InventoryResults inventoryResults = inventory.getInventoryResults();
      InventoryProduct[] products = inventoryResults.getProducts();
      return getBaseQuantity(products);
   }

   public static int getBaseQuantity(InventoryProduct[] products) throws Exception
   {
      LOGGER.info("getBaseQuantity: 1/2 yard is default base quantity");
      try
      {
         InventoryProduct halfYard = getProduct(products, "1/2");
         int halfYardQuantity = halfYard.getInventoryOfferings()[0].getQuantity();
         LOGGER.info("getBaseQuantity: halfYardQuantity={}", halfYardQuantity);
         return halfYardQuantity;
      }
      catch (Exception e)
      {
         LOGGER.info("getBaseQuantity: half yard product not found, using 1 yard as base quantity");
      }

      for (InventoryProduct product : products)
      {
         InventoryPropertyValue[] propertyValues = product.getPropertyValues();
         if (propertyValues == null || propertyValues.length == 0)
         {
            // this product type does not include variations
            return products[0].getInventoryOfferings()[0].getQuantity();
         }
         if (product.getPropertyValues()[0].getPropertyName().equals("Gift Amount"))
         {
            // this is a gift certificate
            return 0;
         }

         String value = product.getPropertyValues()[0].getValues()[0];
         if (value.startsWith("1 Yard") || value.startsWith("1 Panel"))
         {
            return product.getInventoryOfferings()[0].getQuantity();
         }
      }
      throw new Exception("Could not find base quantity");
   }

   public static boolean updateEtsyInventory(String title, InventoryResponse inventory, double baseUnitsPurchased) throws Exception
   {
      LOGGER.info("updateEtsyInventory: baseUnitsPurchased={} for title={}", baseUnitsPurchased, title);
      boolean halfYardBased = isBaseQuantityHalfYard(inventory);
      int totalRemainingQuantity = 0;
      InventoryProduct[] products = inventory.getInventoryResults().getProducts();
      if (products != null)
      {
         for (InventoryProduct product : products)
         {
            InventoryPropertyValue[] propertyValues = product.getPropertyValues();
            if (propertyValues == null || propertyValues.length == 0)
            {
               // this product type does not include variations
               Integer quantity = product.getInventoryOfferings()[0].getQuantity();
               LOGGER.info("updateEtsyInventory: this title does not have variations, currentQuantity={} for title={}", quantity, title);
               int newQuantity = quantity - (int)Math.ceil(baseUnitsPurchased);
               totalRemainingQuantity += newQuantity;
               LOGGER.info("updateEtsyInventory: newQuantity={} for title={}", newQuantity, title);
               product.getInventoryOfferings()[0].setQuantity(newQuantity);
            }
            else
            {
               String value = product.getPropertyValues()[0].getValues()[0];
               Integer quantity = product.getInventoryOfferings()[0].getQuantity();
               LOGGER.info("updateEtsyInventory: currentQuantity={} for variation={}", quantity, value);
               if (halfYardBased)
               {
                  if (value.startsWith("1/2"))
                  {
                     int newQuantity = quantity - ((int)Math.ceil(baseUnitsPurchased * 2));
                     totalRemainingQuantity += newQuantity;
                     LOGGER.info("updateEtsyInventory: newQuantity={} for variation={}", newQuantity, value);
                     product.getInventoryOfferings()[0].setQuantity(newQuantity);

                     // now let's set the 1 yard quantity based on the 1/2 yard quantity
                     InventoryProduct oneYard = getProduct(inventory, "1 Yard");
                     int oneYardQuantity = (int)Math.floor((double)newQuantity / 2.0);
                     totalRemainingQuantity += oneYardQuantity;
                     LOGGER.info("updateEtsyInventory: setting 1 Yard quantity to {} due to 1/2 quantity of {} for {}", oneYardQuantity, newQuantity, title);
                     oneYard.getInventoryOfferings()[0].setQuantity(oneYardQuantity);
                  }
               }
               else
               {
                  InventoryProduct oneYard = getBaseProduct(title, inventory);
                  int baseQuantity = (int)(quantity - baseUnitsPurchased);
                  totalRemainingQuantity += baseQuantity;
                  LOGGER.info("updateEtsyInventory: setting base quantity to {} for {}", baseQuantity, title);
                  oneYard.getInventoryOfferings()[0].setQuantity(baseQuantity);
               }
            }
         }
      }
      return totalRemainingQuantity == 0;
   }

   public static InventoryProduct getBaseProduct(String title, InventoryResponse inventory) throws Exception
   {
      if (title.toLowerCase().contains("panel"))
      {
         return getProduct(inventory, "1 Panel");
      }
      return getProduct(inventory, "1 Yard");
   }

   public static InventoryProduct getProduct(InventoryResponse inventory, String variation) throws Exception
   {
      return getProduct(inventory.getInventoryResults().getProducts(), variation);
   }

   public static InventoryProduct getProduct(InventoryProduct[] products, String variation) throws Exception
   {
      for (InventoryProduct product : products)
      {
         String value = product.getPropertyValues()[0].getValues()[0];
         if (value.startsWith(variation))
         {
            return product;
         }
      }
      throw new Exception("getProduct: " + variation + " product not found");
   }

   public static Collection<? extends Variant> createShopifyVariantsFromEtsyProducts(String title, double itemWeight, InventoryProduct[] products)
   {
      List<Variant> variants = new ArrayList<>();
      for (InventoryProduct product : products)
      {

         String variantName = product.getPropertyValues()[0].getValues()[0];
         double price = (double)product.getInventoryOfferings()[0].getInventoryOfferingPrice().getAmount() / 100.0;
         int quantity = product.getInventoryOfferings()[0].getQuantity();
         Variant variant = createVariantWithAdjustedWeightAndQuantity(variantName, format(price), calculateWeight(variantName, itemWeight, title), title, quantity);
         variants.add(variant);
      }
      return variants;
   }

   public static double getBasePrice(InventoryResponse inventory) throws Exception
   {
      InventoryResults inventoryResults = inventory.getInventoryResults();
      InventoryProduct[] products = inventoryResults.getProducts();
      return getBasePrice(products);
   }

   public static double getBasePrice(InventoryProduct[] products)
   {
      for (InventoryProduct product : products)
      {
         InventoryPropertyValue[] propertyValues = product.getPropertyValues();
         if (propertyValues == null || propertyValues.length == 0)
         {
            // this product type does not include variations
            int amount = products[0].getInventoryOfferings()[0].getInventoryOfferingPrice().getAmount();
            return (double)amount / 100.0;
         }
         if (product.getPropertyValues()[0].getPropertyName().equals("Gift Amount"))
         {
            // this is a gift certificate
            return 0.0;
         }

         String value = product.getPropertyValues()[0].getValues()[0];
         if (value.startsWith("1 Yard") || value.startsWith("1 Panel"))
         {
            int amount = product.getInventoryOfferings()[0].getInventoryOfferingPrice().getAmount();
            return (double)amount / 100.0;
         }
      }

      int amount = products[0].getInventoryOfferings()[0].getInventoryOfferingPrice().getAmount();
      return (double)amount / 100.0;
   }

   public static void write(String filename, String text)
   {
      write(filename, text, false);
   }

   public static void write(String filename, String text, boolean append)
   {
      try (Writer writer = new OutputStreamWriter(new FileOutputStream(filename, append), StandardCharsets.ISO_8859_1))
      {
         writer.write(text);
      }
      catch (Exception e)
      {
         LOGGER.error("write", e);
      }
   }

   public static String removeTextInParens(String text)
   {
      return text.replaceAll("\\(.*\\)", "").trim();
   }

   public static void main(String[] args)
   {
      LOGGER.info("domestic shipping supplies cost per order = {}", SHIPPING_SUPPLIES_COST_PER_DOMESTIC_ORDER);
      LOGGER.info("interational shipping supplies cost per order = {}", SHIPPING_SUPPLIES_COST_PER_INTERNATIONAL_ORDER);

      //String tag1 = "Floral +";
      //String tag2 = "Organic";
      //String tag3 = "Animals";
      //String tag4 = "Alice In Wonderland";
      //String tag4 = null;
      //String tag1 = "not set";
      //String tag2 = "not set";
      //String tag3 = "not set";
      //String tag4 = "not set";
      //String tags = createTagString(tag1, tag2, tag3, tag4);

      /*
      String test = "Birds,New Arrivals,Canvas";
      String removed;
      if (test.contains("New Arrivals,"))
      {
         removed = StringUtils.remove(test, "New Arrivals,");
      }
      else
      {
         removed = StringUtils.remove(test, "New Arrivals");
      }
      LOGGER.info("before={}", test);
      LOGGER.info("after={}", removed);
      */


      /*
      if (tags == null)
      {
         LOGGER.info("null");
      }
      else if (tags.isEmpty())
      {
         LOGGER.info("empty");
      }
      LOGGER.info("{}", tags);
      */


      //String before = "L&#39;Artista Con Alma Bright | Frida Fabric | Frida Panel 24 Inches x 44 Inches | Alexander Henry Fabrics | Folklorico | Mexican | Southwest";
      //String after = StringEscapeUtils.unescapeXml(before);
      //LOGGER.info("before={}", before);
      //LOGGER.info("after={}", after);

      /*
      String text = "Kanga-roo Fabric | Blue Fabric | Small Print Fabric | Quilting Fabric by the Yard | Australian Art | Animal Prints | Teal Fabric - 1 Yard";
      String[] split = splitOnLastDash(text);
      for (String s : split)
      {
         LOGGER.info("{}", s);
      }
      */
   }

   public static String[] splitOnLastDash(String text)
   {
      int i = text.lastIndexOf("-");
      if (i > 0)
      {
         String s1 = text.substring(0, i).trim();
         String s2 = text.substring(i + 1).trim();
         return new String[] { s1, s2 };
      }
      return new String[] { text };
   }


   public static String removeAllButLastDash(String text)
   {
      int i = text.lastIndexOf("-");
      if (i > 0)
      {
         String modified = text.substring(0, i);
         modified = modified.replaceAll("-", "");
         modified = modified + text.substring(i);
         return modified;
      }
      return text;
   }

   public static OrderStats calculateProfit(String channel, long orderId, String orderValue, String subTotal, String tax, double totalCost, double totalShippingCost, double weightInOunces, int unitsSold, String country, List<OrderItem> orderItems)
   {
      LOGGER.info("calculateProfit: orderId={} orderValue={} subTotal={} tax={} totalCost={} totalShippingCost={}", orderId, orderValue, subTotal, tax, totalCost, totalShippingCost, orderItems);
      try
      {
         double shipping = Double.valueOf(orderValue) - Double.valueOf(subTotal) - Double.valueOf(tax);
         return calculateProfit(channel, orderId, subTotal, String.valueOf(shipping), totalCost, totalShippingCost, weightInOunces, unitsSold, country, orderItems);
      }
      catch (Exception e)
      {
         LOGGER.error("calculateProfit", e);
         SMSClient.alertAdmin("error calculating profit: " + e.getMessage());
         return null;
      }
   }


   public static OrderStats calculateProfit(String channel, long orderId, String orderValue, String orderShipping, double totalCost, double totalShippingCost, double weightInOunces, int unitsSold, String country, List<OrderItem> orderItems)
   {
      LOGGER.info("calculateProfit: channel={} orderId={} orderValue={} orderShipping={} totalCost={} totalShippingCost={} weightInOunces={} unitsSold={}, country={}",
         channel, orderId, orderValue, orderShipping, totalCost, totalShippingCost, weightInOunces, unitsSold, country);
      try
      {
         double fee = 0.0;
         if (channel.toLowerCase().startsWith("etsy"))
         {
            fee = ((Double.valueOf(orderValue) * 0.05) + (Double.valueOf(orderShipping) * 0.05)) + (unitsSold * 0.20) + ((Double.valueOf(orderValue) * 0.03) + (Double.valueOf(orderShipping) * 0.03)) + 0.25;
         }
         else
         {
            fee = (Double.valueOf(orderValue) * 0.026) + (Double.valueOf(orderShipping) * 0.026) + 0.30;
         }
         double costToShip = calculateCostToShipToCustomer(weightInOunces, country);
         double cogs = totalCost + totalShippingCost;
         double revenue = Double.valueOf(orderValue) + Double.valueOf(orderShipping);
         double profit = revenue - cogs - fee - costToShip;
         double profitRatio = profit / revenue;
         double feeRatio = fee / revenue;
         SMSClient.alertAdmin(channel +
            " GP=" + CURRENCY_FORMAT.format(profit) +
            " Revenue=" + CURRENCY_FORMAT.format(revenue) +
            " GPR=" + PERCENT_FORMAT.format(profitRatio) +
            " COGS=" + CURRENCY_FORMAT.format(cogs) +
            " Fee=" + CURRENCY_FORMAT.format(fee) +
            " CostToShip=" + CURRENCY_FORMAT.format(costToShip) +
            " OrderId=" + orderId);
         OrderStats orderStats = new OrderStats();
         orderStats.setTime(System.currentTimeMillis());
         orderStats.setChannel(channel);
         orderStats.setCostOfGoods(cogs);
         orderStats.setCostToShipToCustomer(costToShip);
         orderStats.setCostToShipToUs(totalShippingCost);
         orderStats.setFee(fee);
         orderStats.setFeeRatio(feeRatio);
         orderStats.setOrderId(orderId);
         orderStats.setOrderShippingValue(Double.valueOf(orderShipping));
         orderStats.setOrderValue(Double.valueOf(orderValue));
         orderStats.setOurCost(totalCost);
         orderStats.setProfit(profit);
         orderStats.setProfitRatio(profitRatio);
         orderStats.setRevenue(revenue);
         orderStats.setUnitsSold(unitsSold);
         orderStats.setWeightInOunces(weightInOunces);
         orderStats.setWeightWithPackaging(weightInOunces + PACKAGE_WEIGHT);
         orderStats.setCountry(country);
         orderStats.setOrderItems(orderItems);
         FormattedOrderStats formattedOrderStats = new FormattedOrderStats(orderStats);
         LOGGER.info("calculateProfit: orderStats={}", formattedOrderStats);
         return orderStats;
      }
      catch (Exception e)
      {
         LOGGER.error("calculateProfit", e);
         SMSClient.alertAdmin("error calculating profit: " + e.getMessage());
         return null;
      }
   }

   public static Map<String, ProductInfo> createProductInfoForVariations(ProductInfo productInfo)
   {
      if (productInfo.getVariation().equals("1 Yard"))
      {
         Map<String, ProductInfo> variations = new HashMap<>();
         String etsyTitle = productInfo.getEtsyTitle();
         //String shopifyTitle = productInfo.getShopifyTitle();
         double cost = productInfo.getCost();
         //String autoUpdate = productInfo.getAutoUpdateCost();
         double shippingCost = productInfo.getShippingCost();
         double priceOverride = productInfo.getPriceOverride();
         Date dateAdded = productInfo.getDateAdded();
         String vendor = productInfo.getVendor();
         variations.put(createKey(etsyTitle, "3/4 Yard"), createProductInfo(dateAdded, etsyTitle, "3/4 Yard", cost * 0.75, shippingCost * 0.75, priceOverride * 0.75, vendor, ""));
         variations.put(createKey(etsyTitle, "1/2 Yard"), createProductInfo(dateAdded, etsyTitle, "1/2 Yard", cost / 2.0, shippingCost / 2.0, priceOverride > 0 ? priceOverride / 2.0 : 0.0, vendor, ""));
         variations.put(createKey(etsyTitle, "1/4 Yard"), createProductInfo(dateAdded, etsyTitle, "1/4 Yard", cost / 4.0, shippingCost / 4.0, priceOverride > 0 ? priceOverride / 4.0 : 0.0, vendor, ""));
         variations.put(createKey(etsyTitle, "Fat Quarter"), createProductInfo(dateAdded, etsyTitle, "Fat Quarter", cost / 4.0, shippingCost / 4.0, priceOverride > 0 ? priceOverride / 4.0 : 0.0, vendor, ""));
         return variations;
      }
      return null;
   }

   private static ProductInfo createProductInfo(Date dateAdded, String etsyTitle, String variation, double cost, double shippingCost, double priceOverride, String vendor, String sku)
   {
      ProductInfo productInfo = new ProductInfo();
      productInfo.setDateAdded(dateAdded);
      productInfo.setEtsyTitle(etsyTitle);
      productInfo.setVariation(variation);
      productInfo.setCost(cost);
      //productInfo.setAutoUpdateCost(autoUpdate);
      productInfo.setShippingCost(shippingCost);
      productInfo.setPriceOverride(priceOverride);
      productInfo.setVendor(vendor);
      productInfo.setSku(sku);
      return productInfo;
   }

   public static ProductInfo getFromMasterMap(String title, Map<String, ProductInfo> masterMap)
   {
      ProductInfo productInfo = masterMap.get(createKeyDefaultVariation(title));
      if (productInfo == null)
      {
         productInfo = masterMap.get(createKey(title, "Default Title"));
      }
      if (productInfo == null)
      {
         productInfo = masterMap.get(createKey(title, "1 Panel"));
      }
      if (productInfo == null)
      {
         productInfo = masterMap.get(createKey(title, "Last Piece"));
      }
      return productInfo;
   }

   public static String createKey(String title, String variation)
   {
      String s = Utils.removeTextInParens(variation);
      String key = title + " - " + s;
      key = key.replaceAll("'", "");
      return key.replaceAll("\\s+", "");
   }

   public static String createKeyDefaultVariation(String title)
   {
      return createKey(title, "1 Yard");
   }

   public static String keywordsToString(List<String> keywords)
   {
      StringBuilder sb = new StringBuilder();
      int size = keywords.size();
      int count = 0;
      for (String keyword : keywords)
      {
         sb.append(keyword);
         if (++count < size)
         {
            sb.append("|");
         }
      }
      return sb.toString();
   }

   public static String generateHandle(String shopifyTitle, String type)
   {
      LOGGER.info("generateHandle: title={} type={}", shopifyTitle, type);
      return type.toLowerCase() + "-" + shopifyTitle.toLowerCase().replaceAll(" ", "-");
   }

   public static void writeShopifyTitle2TitleTagMap(Map<String, String> title2tag, String filename)
   {
      StringBuilder sb = new StringBuilder();
      sb.append("Title,TitleTag                                                   x\n");
      for (Map.Entry<String, String> entry : title2tag.entrySet())
      {
         sb.append(entry.getKey()).append(",");
         sb.append(entry.getValue()).append("\n");
      }
      Utils.write(filename, sb.toString());
   }

   public static String createTagString(String ... tags)
   {
      StringBuilder sb = new StringBuilder();
      for (String tag : tags)
      {
         if (tag != null && !tag.isEmpty() && !tag.equals("not set"))
         {
            sb.append(tag).append(",");
         }
      }
      if (sb.length() == 0)
      {
         return null;
      }
      sb.deleteCharAt(sb.length() - 1);
      return sb.toString();
   }

   /*
   public static Map<String, ProductInfo> createShopifyTitle2ProductInfoMapFromEtsyTitle2ProductInfoMap(Map<String, ProductInfo> etsyTitle2ProductInfoMap)
   {
      Map<String, ProductInfo> shopifyTitle2ProductInfo = new HashMap<>();
      for (ProductInfo productInfo : etsyTitle2ProductInfoMap.values())
      {
         if (productInfo == null)
         {
            LOGGER.error("found null in master map");
         }
         if (productInfo.getShopifyTitle() == null)
         {
            LOGGER.error("found null shopify title in master map for " + productInfo.getEtsyTitle());
         }

         if (!productInfo.getShopifyTitle().equalsIgnoreCase("Not In Shopify"))
         {
            shopifyTitle2ProductInfo.put(Utils.createKey(productInfo.getShopifyTitle(), productInfo.getVariation()), productInfo);
         }
      }
      return shopifyTitle2ProductInfo;
   }
   */

   public static <E> List<List<E>> generatePerm(List<E> original) {
      if (original.size() == 0) {
         List<List<E>> result = new ArrayList<List<E>>();
         result.add(new ArrayList<E>());
         return result;
      }
      E firstElement = original.remove(0);
      List<List<E>> returnValue = new ArrayList<List<E>>();
      List<List<E>> permutations = generatePerm(original);
      for (List<E> smallerPermutated : permutations) {
         for (int index=0; index <= smallerPermutated.size(); index++) {
            List<E> temp = new ArrayList<E>(smallerPermutated);
            temp.add(index, firstElement);
            returnValue.add(temp);
         }
      }
      return returnValue;
   }

   public static List<String> augmentKeywords(List<String> keywords)
   {
      List<String> augmentedKeywords = new ArrayList<>();
      augmentedKeywords.add("SpindleAndRose");
      augmentedKeywords.add("spindleandrose.com");
      for (String keyword : keywords)
      {
         String augmentedKeyword = keyword.toLowerCase();
         augmentedKeywords.add(augmentedKeyword);
         augmentedKeywords.add(augmentedKeyword + " online");
         augmentedKeywords.add("buy " + augmentedKeyword);
         augmentedKeywords.add(augmentedKeyword + " for sale");
      }
      return augmentedKeywords;
   }

   public static String formatKeywords(List<String> keywords, char separator)
   {
      StringBuilder sb = new StringBuilder();
      int count = 0;
      for (String keyword : keywords)
      {
         sb.append(keyword);
         if (count++ < keywords.size() - 1)
         {
            sb.append(separator);
         }
      }
      return sb.toString();
   }

   public static void toFile(Set<String> set, String filename)
   {
      StringBuilder sb = new StringBuilder();
      for (String s : set)
      {
         sb.append(s).append("\n");
      }
      write(filename, sb.toString());
   }

   public static Set<String> setFromFile(String filename) throws Exception
   {
      File file = new File(filename);
      if (file.exists())
      {
         List<String> lines = Files.readAllLines(Paths.get(filename));
         return new HashSet<>(lines);
      }
      return null;
   }

   public static Set<String> lowerCaseSetFromFile(String filename) throws Exception
   {
      Set<String> set = new HashSet<>();
      File file = new File(filename);
      if (file.exists())
      {
         List<String> lines = Files.readAllLines(Paths.get(filename));
         for (String line : lines)
         {
            set.add(line.trim().toLowerCase());
         }
         return set;
      }
      return null;
   }

   public static String[] createFormattedPrices(int amount)
   {
      String formattedPrice = Utils.format((double)amount / 100.0);
      String formattedShort = "$" + formattedPrice;
      String formattedLong = "$" + formattedPrice + " USD";
      return new String[] {formattedPrice, formattedShort, formattedLong};
   }

   public static double gramsToOunces(int grams)
   {
      return OUNCES_PER_GRAM * grams;
   }

   public static String lastLine(String filename) throws Exception
   {

      int lines = 0;
      StringBuilder builder = new StringBuilder();
      RandomAccessFile randomAccessFile = null;
      try
      {
         File file = new File(filename);
         randomAccessFile = new RandomAccessFile(file, "r");
         long fileLength = file.length() - 1;
         // Set the pointer at the last of the file
         randomAccessFile.seek(fileLength);
         for (long pointer = fileLength; pointer >= 0; pointer--)
         {
            randomAccessFile.seek(pointer);
            char c;
            // read from the end of the line one char at a time
            c = (char)randomAccessFile.read();
            // break when end of the line
            if (c == '\n')
            {
               if (pointer < fileLength)
               {
                  break;
               }
               continue;

            }
            builder.append(c);
         }
         // Since line is read from the last so it
         // is in reverse so use reverse method to make it right
         builder.reverse();
         return builder.toString();
      }
      finally
      {
         if(randomAccessFile != null)
         {
            try
            {
               randomAccessFile.close();
            }
            catch (IOException e)
            {
               LOGGER.error("lastLine", e);
            }
         }
      }
   }

   public static List<String> readAllLinesContaining(String filename, String text)
   {
      LOGGER.debug("readAllLinesContaining: filename={} text={}", filename, text);
      List<String> lines = new ArrayList<>();
      try
      {
         File file = new File(filename);
         Scanner in = new Scanner(file);
         while(in.hasNext())
         {
            String line = in.nextLine();
            if (line.contains(text))
            {
               lines.add(line);
            }
         }
      }
      catch (Exception e)
      {
         LOGGER.error("readAllLinesContaining", e);
      }
      return lines;
   }

   public static Map<String,String> buildEtsyOriginalTitleToNewTitleMap(Map<String, Long> originalTitles, Map<Long, String> newTitles)
   {
      Map<String, String> originalToNew = new HashMap<>();
      for (Map.Entry<String, Long> original : originalTitles.entrySet())
      {
         String newTitle = newTitles.get(original.getValue());
         if (newTitle != null)
         {
            originalToNew.put(original.getKey().replaceAll(" ", ""), newTitle.replaceAll(" ", ""));
         }
      }
      return originalToNew;
   }

   public static List<OrderStats> toOrderStats(List<SpindleAndRoseOrder> orders) throws Exception
   {
      List<OrderStats> stats = new ArrayList<>();
      for (SpindleAndRoseOrder order : orders)
      {
         OrderStats os = new OrderStats();
         os.setChannel(order.getChannel());
         os.setOrderId(Long.valueOf(order.getOrderId().replaceAll("#", "")));
         os.setTime(order.getDate().getTime());
         os.setCountry(order.getShipCountry());
         os.setOrderValue(order.getOrderValue() - order.getDiscount());
         os.setOrderShippingValue(order.getShipping() - order.getShippingDiscount());
         os.setRevenue(order.getOrderValue() + order.getShipping() - order.getRefund());
         os.setOurCost(calculateOurCost(order.getProducts()));
         os.setCostToShipToUs(calculateCostToShipToUs(order.getProducts()));
         os.setCostOfGoods(os.getOurCost() + os.getCostToShipToUs());
         os.setWeightInOunces(calculateWeightInOunces(order.getProducts()));
         os.setWeightWithPackaging(os.getWeightInOunces() + PACKAGE_WEIGHT);
         os.setCostToShipToCustomer(calculateCostToShipToCustomer(os.getWeightInOunces(), os.getCountry(), "resources/shippinginfo", "resources/shippinginfo-canada", "resources/shippinginfo-international"));
         os.setUnitsSold(order.getProducts().size());
         os.setFee(calculateFee(order.getChannel(), order.getCardProcessingFee(), order.getOrderValue(), order.getShipping(), os.getUnitsSold()));
         os.setFeeRatio(os.getFee() / os.getRevenue());
         os.setProfit(os.getRevenue() - os.getCostOfGoods() - os.getFee() - os.getCostToShipToCustomer());
         os.setProfitRatio(os.getProfit() / os.getRevenue());
         os.setOrderItems(createOrderItems(order.getChannel(), order.getProducts()));
         stats.add(os);
      }
      return stats;
   }

   private static List<OrderItem> createOrderItems(String channel, List<ProductInfo> products)
   {
      Map<String, OrderItem> orderItemMap = new HashMap<>();
      for (ProductInfo product : products)
      {
         String title;
         if (channel.equalsIgnoreCase("etsy"))
         {
            title = product.getEtsyTitle();
         }
         else
         {
            title = product.getShopifyTitle();
         }
         String variation = product.getVariation();
         OrderItem orderItem = orderItemMap.get(title + variation);
         if (orderItem == null)
         {
            orderItem = new OrderItem();
            orderItem.setTitle(title);
            orderItem.setVariation(variation);
            orderItem.setQuantity(1);
            orderItemMap.put(title + variation, orderItem);
         }
         else
         {
            orderItem.setQuantity(orderItem.getQuantity() + 1);
         }
      }
      return new ArrayList<>(orderItemMap.values());
   }

   private static Double calculateCostToShipToCustomer(Double weightInOunces, String country) throws Exception
   {
      return calculateCostToShipToCustomer(weightInOunces, country, SHIPPING_INFO_FILE, SHIPPING_INFO_FILE_CANADA, SHIPPING_INFO_FILE_INTERNATIONAL);
   }
   private static Double calculateCostToShipToCustomer(Double weightInOunces, String country, String shippingInfoFileUS, String shippingInfoFileCanada, String shippingInfoFileInternational) throws Exception
   {
      boolean domestic = true;
      Double withPackaging = weightInOunces + PACKAGE_WEIGHT;
      LOGGER.info("calculateShipping: weightInOunces={} withPackaging={} country={}", weightInOunces, withPackaging, country);
      String filename;
      if (UNITED_STATES.equals(country) || US.equals(country))
      {
         filename = shippingInfoFileUS;
      }
      else if (CANADA.equals(country) || CA.equals(country))
      {
         filename = shippingInfoFileCanada;
         domestic = false;
      }
      else
      {
         filename = shippingInfoFileInternational;
         domestic = false;
      }
      double shippingSuppliesCost = domestic ? SHIPPING_SUPPLIES_COST_PER_DOMESTIC_ORDER : SHIPPING_SUPPLIES_COST_PER_INTERNATIONAL_ORDER;
      LOGGER.info("calculateShipping: domesticOrder={} shippingSuppliesCost= {} shippingInfoFile={}", domestic, shippingSuppliesCost, filename);
      List<String> lines = Files.readAllLines(Paths.get(filename));
      Map<Integer, Double> shippingInfo = new TreeMap<>();
      for (String line : lines)
      {
         String[] split = line.split("=");
         shippingInfo.put(Integer.valueOf(split[0]), Double.valueOf(split[1]));
      }
      for (Integer ounces : shippingInfo.keySet())
      {
         LOGGER.info("calculateShipping: comparing weightWithPacking to {}", ounces);
         if (withPackaging <= ounces)
         {
            return shippingInfo.get(ounces) + shippingSuppliesCost;
         }
      }
      return OTHER + shippingSuppliesCost;
   }

   private static double calculateWeightInOunces(List<ProductInfo> products)
   {
      double weight = 0.0;
      for (ProductInfo product : products)
      {
         String variation = product.getVariation();
         if (variation.startsWith("1/2"))
         {
            weight += 3.0;
         }
         else if (variation.startsWith("1/4"))
         {
            weight += 1.5;
         }
         else if (variation.toLowerCase().startsWith("fat quarter"))
         {
            weight += 1.5;
         }
         else
         {
            weight += 6.0;
         }
      }
      return weight;
   }

   private static double calculateCostToShipToUs(List<ProductInfo> products)
   {
      double cost = 0.0;
      for (ProductInfo product : products)
      {
         cost += product.getShippingCost();
      }
      return cost;
   }

   private static double calculateOurCost(List<ProductInfo> products)
   {
      double cost = 0.0;
      for (ProductInfo product : products)
      {
         cost += product.getCost();
      }
      return cost;
   }

   private static double calculateFee(String channel, double cardProcessingFee, double orderValue, double orderShipping, int unitsSold)
   {
      double fee = 0.0;
      if (channel.toLowerCase().startsWith("etsy"))
      {
         fee = ((orderValue * 0.05) + (orderShipping * 0.05)) + (unitsSold * 0.20) + cardProcessingFee;
      }
      else
      {
         fee = cardProcessingFee;
      }
      return fee;
   }
}
