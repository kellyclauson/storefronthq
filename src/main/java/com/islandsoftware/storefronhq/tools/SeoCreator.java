package com.islandsoftware.storefronhq.tools;

import com.islandsoftware.storefronhq.GoogleSheets;
import com.islandsoftware.storefronhq.SMSClient;
import com.islandsoftware.storefronhq.orderprocessing.ProductInfo;
import com.islandsoftware.storefronhq.shopify.sync.ShopifyClient;
import com.islandsoftware.storefronhq.shopify.sync.model.TitleTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class SeoCreator
{
   public static final TitleTag DEFAULT_TITLE_TAG = new TitleTag("Fat Quarter | Unique Fabric | SpindleAndRose");

   private static final Logger LOGGER = LoggerFactory.getLogger(SeoCreator.class);

   private static final int TITLE_TAG_MAX_LENGTH = 60;
   private static final int MIN_VOLUME = 250;
   private static final List<String> NOT_QUILTING_FABRIC = new ArrayList<>();
   private static final Keyword CLOUD9 = new Keyword("Cloud 9 Fabrics", 0);
   private static final Keyword EE_SCHENCK = new Keyword("EE Schenck", 0);
   private static final Keyword BEAR = new Keyword("Bear Fabric", 0);
   private static final Keyword CAT = new Keyword("Cat Fabric", 0);
   private static final Keyword ORGANIC = new Keyword("Organic Fabric", 0);
   static
   {
      NOT_QUILTING_FABRIC.add("canvas");
      NOT_QUILTING_FABRIC.add("knit");
      NOT_QUILTING_FABRIC.add("double gauze");
      NOT_QUILTING_FABRIC.add("laminate");
      NOT_QUILTING_FABRIC.add("tailored cloth");
      NOT_QUILTING_FABRIC.add("oxford cotton");
   }

   public static void main(String[] args)
   {
      try
      {
         Map<String, Keyword> masterKeywordMap = GoogleSheets.readQueryVolume();
         ShopifyClient shopifyClient = new ShopifyClient();
         Map<Long, String> shopifyMap = shopifyClient.getId2Title();
         Map<String, ProductInfo> productInfoMap = GoogleSheets.readProductInfo(false);
         Map<String, ProductInfo> shopifyTitle2productInfo = new HashMap<>();
         for (ProductInfo productInfo : productInfoMap.values())
         {
            shopifyTitle2productInfo.put(productInfo.getShopifyTitle(), productInfo);
         }

         List<Seo> seoList = new ArrayList<>();
         Set<String> titleTags = new HashSet<>();
         for (String shopifyTitle : shopifyMap.values())
         {
            ProductInfo productInfo = shopifyTitle2productInfo.get(shopifyTitle);
            Seo seo = createSeo(productInfo.getEtsyTitle(), shopifyTitle, productInfo.getVendor(), masterKeywordMap, false, titleTags);
            seoList.add(seo);
         }

         writeSeo(seoList);

         //List<Seo> seoList = readSeo("C:\\tmp\\seo.csv");

         //Seo seo = createSeo("Snow Leopard Fabric | Cute Cat Fabric | Black and White | Cotton and Steel | White Cats with Black Spots | Unique | Animal Fabric", "Snow Leopards", "Cotton and Steel", masterKeywordMap);
         //LOGGER.info("{}", seo);
         //Map<String, String> map = updateTitleTag("Japanese Fabric | Bird Fabric | Polka Dot Fabric | SpindleAndRose", "Bluebird Teal");
         //LOGGER.info("{}", map);
      }
      catch (Exception e)
      {
         LOGGER.error("Error", e);
      }
   }

   private static String extractDescriptionText(String bodyHtml)
   {
      int begin = bodyHtml.indexOf("<p>");
      if (begin > -1)
      {
         int end = bodyHtml.indexOf("</p>");
         if (end > -1)
         {
            String text = bodyHtml.substring(begin + 3, end);
            return text.replaceAll(",", "-");
         }
      }
      return "not set";
   }

   public static Seo createSeo(String etsyTitle, String shopifyTitle, String vendor, Map<String, Keyword> masterKeywordMap, boolean titleTagContainsShopifyTitle, Set<String> allTitleTags)
   {
      Seo seo = new Seo();
      Set<Keyword> keywords = findKeywords(etsyTitle, masterKeywordMap);
      Keyword vendorKeyword = masterKeywordMap.get(vendor.toLowerCase());
      if (vendorKeyword == null)
      {
         LOGGER.warn("{} not found in master keyword map", vendor.toLowerCase());
      }
      else
      {
         keywords.add(vendorKeyword);
      }
      if (keywords.contains(CLOUD9) && keywords.contains(EE_SCHENCK))
      {
         keywords.remove(EE_SCHENCK);
      }
      if (etsyTitle.toLowerCase().contains("bear bois"))
      {
         keywords.remove(BEAR);
      }
      if (etsyTitle.toLowerCase().contains("cat tails"))
      {
         keywords.remove(CAT);
      }
      if (etsyTitle.toLowerCase().contains("organic shapes"))
      {
         keywords.remove(ORGANIC);
      }
      List<Keyword> allKeywords = sortByVolume(keywords);
      for (Keyword keyword : allKeywords)
      {
         seo.getKeywords().add(keyword.getQuery());
      }

      String[] headers = createHeaders(sortByVolume(keywords));
      seo.setH1(headers[0]);
      seo.setH2(headers[1]);
      seo.setShopifyTitle(shopifyTitle);
      seo.setEtsyTitle(etsyTitle);

      List<TitleTag> titleTags = generateTitleTagList(keywords, etsyTitle, masterKeywordMap, shopifyTitle, titleTagContainsShopifyTitle);
      TitleTag tag = selectTitleTag(titleTags, shopifyTitle, titleTagContainsShopifyTitle, allTitleTags);
      if (tag != null)
      {
         LOGGER.info("using tag {}", tag);
         seo.setTitleTag(tag);
      }
      else
      {
         LOGGER.info("Could not find a unique title tag for {}, adding Fat Quarter to the keyword set", shopifyTitle);
         keywords.addAll(allKeywords);
         keywords.add(new Keyword("Fat Quarter", 5000));
         titleTags = generateTitleTagList(keywords, etsyTitle, masterKeywordMap, shopifyTitle, titleTagContainsShopifyTitle);
         tag = selectTitleTag(titleTags, shopifyTitle, titleTagContainsShopifyTitle, allTitleTags);
         if (tag != null)
         {
            LOGGER.info("using tag {}", tag);
            seo.setTitleTag(tag);
         }
         else
         {
            LOGGER.error("Could not find a unique title tag for {}", shopifyTitle);
            SMSClient.alertAdmin("could not find a unique title tag for " + shopifyTitle);
            seo.setTitleTag(DEFAULT_TITLE_TAG);
         }
      }

      seo.setKeywords(Utils.augmentKeywords(seo.getKeywords()));
      return seo;
   }

   private static TitleTag selectTitleTag(List<TitleTag> titleTags, String shopifyTitle, boolean titleTagContainsShopifyTitle, Set<String> allTitleTags)
   {
      LOGGER.info("selectTitleTag: selecting from {}", titleTags);
      for (TitleTag titleTag : titleTags)
      {
         if (allTitleTags.add(titleTag.getTitleTag()))
         {
            return titleTag;
         }
         LOGGER.debug("{} is a duplicate, trying next one...", titleTag.getTitleTag());
      }
      return null;
   }

   private static List<TitleTag> generateTitleTagList(Set<Keyword> originalKeywordList, String etsyTitle, Map<String, Keyword> masterKeywordMap, String shopifyTitle, boolean titleTagContainsShopifyTitle)
   {
      List<TitleTag> titleTags = new ArrayList<>();
      while (originalKeywordList.size() > 0)
      {
         Set<Keyword> keywords;
         if (etsyTitle.toLowerCase().contains("fabric"))
         {
            keywords = augmentKeywordSet(originalKeywordList, etsyTitle, masterKeywordMap, shopifyTitle, titleTagContainsShopifyTitle);
            keywords = limitKeywordSet(keywords, shopifyTitle, titleTagContainsShopifyTitle);
            // removing a lower volume keyword may have left room for a high volume one
            keywords = augmentKeywordSet(keywords, etsyTitle, masterKeywordMap, shopifyTitle, titleTagContainsShopifyTitle);
         }
         else
         {
            keywords = limitKeywordSet(originalKeywordList, shopifyTitle, titleTagContainsShopifyTitle);
         }
         List<List<Keyword>> lists = Utils.generatePerm(new ArrayList<>(keywords));
         int priority = 1;
         int volume = 0;
         for (List<Keyword> list : lists)
         {
            for (Keyword keyword : list)
            {
               volume += keyword.getVolume();
            }
            TitleTag titleTag = new TitleTag();
            titleTag.setPriority(priority);
            titleTag.getKeywords().addAll(list);
            titleTag.setTotalVolume(volume);
            String tag = createTitleTag(shopifyTitle, titleTag.getKeywords(), titleTagContainsShopifyTitle);
            titleTag.setTitleTag(tag);
            titleTags.add(titleTag);
            priority++;
         }
         removeHighestVolumeKeyword(originalKeywordList);
      }
      return titleTags;
   }

   private static void removeHighestVolumeKeyword(Set<Keyword> keywords)
   {
      int max = 0;
      Keyword maxKeyword = null;
      for (Keyword keyword : keywords)
      {
         if (keyword.getVolume() > max)
         {
            max = keyword.getVolume();
            maxKeyword = keyword;
         }
      }
      keywords.remove(maxKeyword);
   }

   public static void writeSeo(List<Seo> seoList)
   {
      StringBuilder sb = new StringBuilder();
      sb.append("EtsyTitle,ShopifyTitle,TitleTag,TitleTagVolume,H1,H2,KeyWords").append("\n");
      for (Seo seo : seoList)
      {
         sb.append(seo.getEtsyTitle()).append(",")
               .append(seo.getShopifyTitle()).append(",")
               .append(seo.getTitleTag().getTitleTag()).append(",")
               .append(seo.getTitleTag().getTotalVolume()).append(",")
               .append(seo.getH1()).append(",")
               .append(seo.getH2()).append(",")
               .append(Utils.keywordsToString(seo.getKeywords())).append("\n");
      }
      Utils.write("C:\\tmp\\seo.csv", sb.toString());
   }

   public static List<Seo> readSeo(String filename) throws Exception
   {
      List<Seo> seoList = new ArrayList<>();
      List<String> lines = Files.readAllLines(Paths.get(filename), StandardCharsets.ISO_8859_1);
      int count = 0;
      for (String line : lines)
      {
         if (++count == 1)
         {
            continue;
         }
         Seo seo = new Seo();
         String[] split = line.split(",");
         seo.setEtsyTitle(split[0].trim());
         seo.setShopifyTitle(split[1].trim());
         TitleTag titleTag = new TitleTag();
         titleTag.setTitleTag(split[2].trim());
         titleTag.setTotalVolume(Integer.parseInt(split[3].trim()));
         seo.setTitleTag(titleTag);
         seo.setH1(split[4].trim());
         seo.setH2(split[5].trim());
         List<String> keywordList = new ArrayList<>();
         String[] keywords = split[6].trim().split("\\|");
         for (String keyword : keywords)
         {
            keywordList.add(keyword.trim());
         }
         seo.setKeywords(keywordList);
         seoList.add(seo);
      }
      return seoList;
   }

   private static String[] createHeaders(List<Keyword> list)
   {
      if (list.size() > 2)
      {
         return new String[] {list.get(0).getQuery(), list.get(1).getQuery() + " | " + list.get(2).getQuery()};
      }
      else if (list.size() > 1)
      {
         return new String[] {list.get(0).getQuery(), list.get(1).getQuery()};
      }
      return new String[] {"Fat Quarter Fabric", list.get(0).getQuery()};
   }

   private static String createTitleTag(String shopifyTitle, List<Keyword> keywords, boolean titleTagContainsShopifyTitle)
   {
      StringBuilder sb = new StringBuilder();
      for (Keyword keyword : keywords)
      {
         sb.append(keyword.getQuery()).append(" | ");
      }
      if (titleTagContainsShopifyTitle)
      {
         sb.append(shopifyTitle);
         sb.append(" | ");

      }
      sb.append("SpindleAndRose");
      return sb.toString();
   }

   private static List<Keyword> sortByVolume(Set<Keyword> keywords)
   {
      List<Keyword> list = new ArrayList<>(keywords);
      Collections.sort(list, new KeywordVolumeComparator());
      return list;
   }

   private static Set<Keyword> findThreadKeywords(String title, Map<String, Keyword> masterKeywordMap)
   {
      Keyword organicFabric = masterKeywordMap.get("organic");
      Keyword oceanFabric = masterKeywordMap.get("ocean");

      Set<Keyword> keywords = new TreeSet<>();
      String[] split = title.split("\\|");
      for (String s : split)
      {
         Keyword keyword = masterKeywordMap.get(s.toLowerCase().trim());
         if (keyword != null)
         {
            if (keyword.equals(organicFabric))
            {
               keywords.add(masterKeywordMap.get("organic thread"));
            }
            else if (!keyword.equals(oceanFabric))
            {
               keywords.add(keyword);
            }
         }
      }
      keywords.add(masterKeywordMap.get("thread"));
      keywords.add(masterKeywordMap.get("sewing thread"));
      return keywords;

   }

   private static Set<Keyword> findRibbonKeywords(String title, Map<String, Keyword> masterKeywordMap)
   {
      Set<Keyword> keywords = new TreeSet<>();
      String[] split = title.split("\\|");
      for (String s : split)
      {
         Keyword keyword = masterKeywordMap.get(s.toLowerCase().trim());
         if (keyword != null)
         {
            keywords.add(keyword);
         }
      }
      keywords.add(masterKeywordMap.get("ribbon"));
      keywords.add(masterKeywordMap.get("sewing ribbon"));
      return keywords;
   }

   private static Set<Keyword> findKeywords(String title, Map<String, Keyword> masterKeywordMap)
   {
      if (title.toLowerCase().contains("ribbon"))
      {
         return findRibbonKeywords(title, masterKeywordMap);
      }
      if (title.toLowerCase().contains("thread"))
      {
         return findThreadKeywords(title, masterKeywordMap);
      }

      Set<Keyword> keywords = new TreeSet<>();
      String[] split = title.split("\\|");
      for (String s : split)
      {
         Keyword keyword = masterKeywordMap.get(s.toLowerCase().trim());
         if (keyword != null)
         {
            if (keyword.getVolume() >= MIN_VOLUME)
            {
               keywords.add(keyword);
            }
         }
      }
      split = title.split(" ");
      for (String s : split)
      {
         Keyword keyword = masterKeywordMap.get(s.toLowerCase().trim());
         if (keyword != null)
         {
            if (keyword.getVolume() >= MIN_VOLUME)
            {
               keywords.add(keyword);
            }
         }
         else
         {
            // if word in title is plural, remove the s (or es) and try again
            String singular = makeSingular(s.toLowerCase().trim());
            if (singular != null)
            {
               keyword = masterKeywordMap.get(singular);
               if (keyword != null)
               {
                  if (keyword.getVolume() >= MIN_VOLUME)
                  {
                     keywords.add(keyword);
                  }
               }
            }
         }
      }
      return keywords;
   }

   private static Set<Keyword> limitKeywordSet(Set<Keyword> keywords, String shopifyTitle, boolean titleTagContainsShopifyTitle)
   {
      Set<Keyword> limitedKeywords = new TreeSet<>(keywords);
      while (calculateQueryLength(limitedKeywords, shopifyTitle, titleTagContainsShopifyTitle) > TITLE_TAG_MAX_LENGTH)
      {
         int minVolume = Integer.MAX_VALUE;
         Keyword keywordWithMinVolume = null;
         for (Keyword keyword : limitedKeywords)
         {
            if (keyword.getVolume() < minVolume)
            {
               minVolume = keyword.getVolume();
               keywordWithMinVolume = keyword;
            }
         }
         limitedKeywords.remove(keywordWithMinVolume);
      }
      return limitedKeywords;
   }


   private static Set<Keyword> augmentKeywordSet(Set<Keyword> keywords, String title, Map<String, Keyword> masterKeywordMap, String shopifyTitle, boolean titleTagContainsShopifyTitle)
   {
      Set<Keyword> augmentedKeywords = new TreeSet<>(keywords);
      Keyword quilt = masterKeywordMap.get("quilt");
      Keyword fatQuarter = masterKeywordMap.get("fat quarter");
      Keyword byTheYard = masterKeywordMap.get("fabric by the yard");

      boolean isQuiltingFabric = true;
      String[] split = title.split("\\|");
      for (String s : split)
      {
         if (NOT_QUILTING_FABRIC.contains(s.trim().toLowerCase()))
         {
            isQuiltingFabric = false;
         }
      }
      if (isQuiltingFabric)
      {
         if (calculateQueryLength(augmentedKeywords, shopifyTitle, titleTagContainsShopifyTitle) + quilt.getQuery().length() + 3 <= TITLE_TAG_MAX_LENGTH)
         {
            augmentedKeywords.add(quilt);
         }
      }
      /*
      if (calculateQueryLength(augmentedKeywords, shopifyTitle) + fatQuarter.getQuery().length() + 3 <= TITLE_TAG_MAX_LENGTH)
      {
         augmentedKeywords.add(fatQuarter);
      }
      */
      if (calculateQueryLength(augmentedKeywords, shopifyTitle, titleTagContainsShopifyTitle) + byTheYard.getQuery().length() + 3 <= TITLE_TAG_MAX_LENGTH)
      {
         augmentedKeywords.add(byTheYard);
      }
      return augmentedKeywords;
   }

   private static int calculateQueryLength(Set<Keyword> keywords, String shopifyTitle, boolean titleTagContainsShopifyTitle)
   {
      int length = 17;
      int size = keywords.size();
      length = length + 3 * (size - 1);
      if (titleTagContainsShopifyTitle)
      {
         length = length + 3 + shopifyTitle.length();
      }
      for (Keyword keyword : keywords)
      {
         length = length + keyword.getQuery().length();
      }
      return length;
   }


   private static String makeSingular(String keyword)
   {
      if (keyword.endsWith("es"))
      {
         return keyword.substring(0, keyword.lastIndexOf("es"));
      }
      if (keyword.endsWith("s"))
      {
         return keyword.substring(0, keyword.lastIndexOf("s"));
      }
      return null;
   }

   private static Map<String, String> updateTitleTag(String originalTag, String shopifyTitle)
   {
      String[] split = originalTag.split("\\|");
      StringBuilder sb = new StringBuilder();
      int count = 0;
      for (String keyword : split)
      {
         if (count == split.length - 2)
         {
            sb.append(shopifyTitle.trim());
         }
         else
         {
            sb.append(split[count].trim());
         }

         if (count < split.length - 1)
         {
            sb.append(" | ");
         }
         count++;
      }
      Map<String, String> title2tag = new HashMap<>(1);
      title2tag.put(shopifyTitle, sb.toString());
      return title2tag;
   }


   public static Map<String,String> updateTitleTags(Map<String, List<String>> duplicateTitleTags) throws Exception
   {
      Map<String, String> title2tag = new TreeMap<>();
      for (Map.Entry<String, List<String>> entry : duplicateTitleTags.entrySet())
      {
         String titleTag = entry.getKey();
         List<String> titles = entry.getValue();
         for (String title : titles)
         {
            title2tag.putAll(updateTitleTag(titleTag, title));
         }
      }
      return title2tag;
   }
}
