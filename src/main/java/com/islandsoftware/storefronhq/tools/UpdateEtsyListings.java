package com.islandsoftware.storefronhq.tools;

import com.islandsoftware.storefronhq.etsy.EtsyClient;
import com.islandsoftware.storefronhq.etsy.SpindleAndRoseEtsyClient;
import com.islandsoftware.storefronhq.etsy.model.ListingsResponse;
import com.islandsoftware.storefronhq.etsy.model.ListingsResult;
import com.islandsoftware.storefronhq.etsy.model.Variation;
import com.islandsoftware.storefronhq.etsy.model.VariationOption;
import com.islandsoftware.storefronhq.etsy.model.inventory.InventoryProduct;
import com.islandsoftware.storefronhq.etsy.model.inventory.InventoryResponse;
import com.islandsoftware.storefronhq.etsy.model.inventory.InventoryResults;
import com.islandsoftware.storefronhq.shopify.sync.ShopifyClient;
import com.islandsoftware.storefronhq.shopify.sync.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class UpdateEtsyListings
{
   private static final String STR = "{\"count\":1,\"results\":{\"products\":[{\"product_id\":1827337675,\"property_values\":[{\"property_id\":506,\"property_name\":\"Length\",\"scale_id\":null,\"scale_name\":null,\"value_ids\":[3535129615],\"values\":[\"1\\/2 Yard\"]}],\"offerings\":[{\"offering_id\":1932348233,\"price\":{\"amount\":300,\"divisor\":100,\"currency_code\":\"USD\",\"currency_formatted_short\":\"$4.00\",\"currency_formatted_long\":\"$4.00 USD\",\"currency_formatted_raw\":\"4.00\"},\"quantity\":23}]}]},\"params\":{\"listing_id\":\"570104565\",\"write_missing_inventory\":false},\"type\":\"ListingInventory\",\"pagination\":{}}";
   private static final Logger LOGGER = LoggerFactory.getLogger(UpdateEtsyListings.class);

   private static final String PATTERN = "## We offer more options including increments of 1/4 yard, 1/2 yard, 3/4 yard, 1 yard, and Fat Quarters on www.spindleandrose.com. We also offer free shipping (U.S. only) on orders of $50 or more!";
   private static final String REPLACEMENT = "## Please visit www.spindleandrose.com for free shipping on US orders of $50 or more AND to find additional cuts of fabric including 1/4 yard, 1/2 yard, 3/4 yard, 1 yard, and Fat Quarters!";

   public static void main(String[] args)
   {
      try
      {
         List<String> notUpdated = new ArrayList<>();
         EtsyClient client = new SpindleAndRoseEtsyClient();
         Map<String, Long> title2Id = client.getTitle2Id();
         int count = 0;
         for (Map.Entry<String, Long> entry : title2Id.entrySet())
         {
            LOGGER.info("{} of {} {}", ++count, title2Id.size(), entry.getKey());
            long id = entry.getValue();
            InventoryResponse inventory = client.getInventory(id);
            String s = doPrice(entry.getKey(), inventory);
            if (s != null)
            {
               client.updateInventory(id, s);
               Utils.sleep(1000L);
            }
            else
            {
               notUpdated.add(entry.getKey());
            }
         }
         for (String s : notUpdated)
         {
            LOGGER.info("NotUpdated: {}", s);
         }
      }
      catch (Exception e)
      {
         LOGGER.error("UpdateEtsyListings", e);
      }
   }

   private static String doPrice(String title, InventoryResponse inventory) throws Exception
   {
      InventoryResults inventoryResults = inventory.getInventoryResults();
      InventoryProduct[] products = inventoryResults.getProducts();
      boolean updated = false;
      for (InventoryProduct product : products)
      {
         if (product.getPropertyValues() != null && product.getPropertyValues().length > 0)
         {
            String[] values = product.getPropertyValues()[0].getValues();
            if (values != null && values.length > 0)
            {
               Integer amount = product.getInventoryOfferings()[0].getInventoryOfferingPrice().getAmount();
               Integer updatedAmount = (int)((double)amount * 1.15);
               LOGGER.info("current={} updated={} title={}", amount, updatedAmount, title);
               String[] formattedPrices = Utils.createFormattedPrices(updatedAmount);
               product.getInventoryOfferings()[0].getInventoryOfferingPrice().setAmount(updatedAmount);
               product.getInventoryOfferings()[0].getInventoryOfferingPrice().setCurrencyFormattedRaw(formattedPrices[0]);
               product.getInventoryOfferings()[0].getInventoryOfferingPrice().setCurrencyFormattedShort(formattedPrices[1]);
               product.getInventoryOfferings()[0].getInventoryOfferingPrice().setCurrencyFormattedLong(formattedPrices[2]);
               updated = true;
            }
         }
      }
      if (updated)
      {
         return Utils.toJson(products);
      }
      LOGGER.info("NOT UPDATING {}", title);
      return null;
   }

   private static Map<String, String> doWeight(ListingsResult listingsResult)
   {
      double currentWeight = Double.valueOf(listingsResult.getItemWeight());
      LOGGER.info("currentWeight={}", currentWeight);
      double newWeight;
      if (currentWeight >= 6.0)
      {
         newWeight = 6.0;
      }
      else
      {
         newWeight = currentWeight + 2.0;
      }
      LOGGER.info("newWeight={}", newWeight);
      Map<String, String> map = new HashMap<>();
      map.put("item_weight", String.valueOf(newWeight));
      map.put("item_length", "3");
      map.put("item_width", "2");
      map.put("item_height", "1");
      return map;
   }


   private static String checkInventory(EtsyClient etsyClient, String title, long id) throws Exception
   {
      int halfYardQuantity = -1;
      int oneYardQuantity = -1;
      InventoryResponse response = etsyClient.getInventory(id);
      InventoryProduct[] products = response.getInventoryResults().getProducts();
      if (products.length == 2)
      {
         for (InventoryProduct product : products)
         {
            if (product.getPropertyValues().length == 1)
            {
               String value = product.getPropertyValues()[0].getValues()[0];
               if (value.startsWith("1/2 Yard"))
               {
                  halfYardQuantity = product.getInventoryOfferings()[0].getQuantity();
               }
               else if (value.startsWith("1 Yard"))
               {
                  oneYardQuantity = product.getInventoryOfferings()[0].getQuantity();
               }
            }
         }
      }
      if (halfYardQuantity > -1 && oneYardQuantity > -1)
      {
         //LOGGER.info("1 Yard quantity={} 1/2 Yard quantity={} for {}", oneYardQuantity, halfYardQuantity, title);
         if (halfYardQuantity / 2 != oneYardQuantity)
         {
            LOGGER.warn("1 Yard quantity={} 1/2 Yard quantity={} for {}", oneYardQuantity, halfYardQuantity, title);
            return new StringBuilder().append(oneYardQuantity).append(",").append(halfYardQuantity).append(",").append(title).toString();
         }
      }
      return null;
   }

   private static void updateListing(EtsyClient etsyClient, ShopifyClient shopifyClient, Map<String, Long> title2Id, Long id)
   {
      try
      {
         ListingsResponse fullListing = etsyClient.getListing(id, "Variations");
         ListingsResult listing = fullListing.getResults()[0];
         String halfYardOptionValue = null;
         double price = 0.0;
         ListingsResult[] results = fullListing.getResults();
         for (ListingsResult result : results)
         {
            Variation[] variations = result.getVariations();
            if (variations != null)
            {
               for (Variation variation : variations)
               {
                  VariationOption[] options = variation.getOptions();
                  if (options != null)
                  {
                     for (VariationOption option : options)
                     {
                        String value = option.getValue();
                        if (value.startsWith("1/2"))
                        {
                           halfYardOptionValue = value;
                           price = option.getPrice();
                        }
                     }
                  }
                  else
                  {
                     LOGGER.info("no options for listing {}", listing.getTitle());
                  }
               }
            }
            else
            {
               LOGGER.info("no variations for listing {}", listing.getTitle());
            }
         }
         if (halfYardOptionValue != null)
         {
            /*
            POST /v2/listings/:listing_id/variations
            variations=[{"property_id":506, "value":"1/2 Yard", "is_available":true, "price": 100}]
             */
            StringBuilder sb = new StringBuilder();
            sb.append("[{");
            sb.append("\"property_id\":506,");
            sb.append(" \"value\":");
            sb.append("\"");
            //sb.append(halfYardOptionValue);
            sb.append("1/2 Yard");
            sb.append("\",");
            sb.append(" \"is_available\":true,");
            sb.append(" \"price\":");
            sb.append(price);
            sb.append("}]");
            LOGGER.info(sb.toString());
            etsyClient.updateVariation(id, sb.toString());

            String handle = null;
            Long shopifyId = title2Id.get(listing.getTitle());
            if (shopifyId != null)
            {
               Product product = shopifyClient.getProduct(shopifyId);
               if (product != null)
               {
                  handle = product.getHandle();
               }
            }

            Map<String, String> params = new HashMap<>();
            params.put("quantity", String.valueOf(updateQuantity(listing)));
            params.put("item_weight", String.valueOf(updateWeight(listing)));
            params.put("description", updateDescription(listing, PATTERN, REPLACEMENT, halfYardOptionValue, handle));
            etsyClient.updateListing(listing.getListingId(),params);
         }
      }
      catch (Exception e)
      {
         LOGGER.error("ERROR", e);
      }
   }

   private static double updateWeight(ListingsResult listing)
   {
      double weight = Double.parseDouble(listing.getItemWeight());
      if (weight == 5.0)
      {
         weight = 6.0;
      }
      return weight / 2;
   }

   private static int updateQuantity(ListingsResult listing)
   {
      int quantity = listing.getQuantity();
      return quantity * 2;
   }

   private static String updateDescription(ListingsResult listing, String pattern, String replacement, String variantValue, String handle)
   {
      String description = listing.getDescription();
      if (handle != null)
      {
         replacement += "\n\n## This product can be found at www.spindleandrose.com/products/" + handle + "\n";
      }
      String s = replacement.replace("VALUE", variantValue.replaceAll("&quot;", "").replace(")", " inches)"));
      return description.replace(pattern, s);
   }

   private static boolean containsHalfYardOrLess(VariationOption[] options)
   {
      for (VariationOption option : options)
      {
         if (option.getValue().startsWith("1/2"))
         {
            return true;
         }
      }
      return false;
   }


   public static void update()
   {
      try
      {
         EtsyClient etsyClient = new SpindleAndRoseEtsyClient();
         Map<String, ListingsResult> listings = etsyClient.listings("description");
         LOGGER.info("found {} etsy listings", listings.size());
         for (ListingsResult listing : listings.values())
         {
            Utils.sleep(500L);
            long listingId = listing.getListingId();
            ListingsResponse listingResponse = etsyClient.getListing(listingId, "Variations");
            ListingsResult[] results = listingResponse.getResults();
            for (ListingsResult result : results)
            {
               if (result.getTitle().contains("Fabric"))
               {
                  Variation[] variations = result.getVariations();
                  if (variations != null)
                  {
                     for (Variation variation : variations)
                     {
                        VariationOption[] options = variation.getOptions();
                        if (options != null)
                        {
                           if (containsHalfYardOrLess(options))
                           {
                              for (VariationOption option : options)
                              {
                                 String title = option.getValue();
                                 if (!Utils.isValidVariationTitle(title))
                                 {
                                    LOGGER.error("Invalid variation title [{}] for listing [{}]", title, listing.getTitle());
                                 }
                              }
                           }
                           else
                           {
                              LOGGER.info("{} does not contain half yard or less option", result.getTitle());
                           }
                        }
                        else
                        {
                           LOGGER.info("no options for listing {}", listing.getTitle());
                        }
                     }
                  }
                  else
                  {
                     LOGGER.info("no variations for listing {}", listing.getTitle());
                  }

               }
            }
         }
         //find(listings.values(), "Nautilus");

         /*
         for (ListingsResult listing : listings.values())
         {
            String description = listing.getDescription();
            //LOGGER.info("before\n{}", description);
            int i = description.indexOf("\n");
            if (i > -1)
            {
               StringBuilder sb = new StringBuilder();
               sb.append(description.substring(0, i));
               sb.append("\nVisit spindleandrose.com for more options!");
               sb.append("\n");
               sb.append(description.substring(i + 1, description.length()));
               //LOGGER.info("\nafter\n{}", sb.toString());
               Map<String, String> params = new HashMap<String, String>(1);
               params.put("description", sb.toString());
               etsyClient.updateListing(listing.getListingId(), params);
               LOGGER.info("updated {}", listing.getEtsyTitle());
            }
         }
         */

      }
      catch (Exception e)
      {
         LOGGER.error("error", e);
      }
   }

   private static void find(Collection<ListingsResult> listings, String reference)
   {
      int count = 0;
      for (ListingsResult listing : listings)
      {
         String description = listing.getDescription();
         if (description.contains(reference))
         {
            count++;
            LOGGER.info("{}", listing.getTitle());
         }
      }
      LOGGER.info("found {} references to {}", count, reference);
   }
}
