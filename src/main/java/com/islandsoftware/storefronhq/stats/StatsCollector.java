package com.islandsoftware.storefronhq.stats;

import com.islandsoftware.storefronhq.tools.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

public class StatsCollector
{
   private static final long DAY = 1000 * 60 * 60 * 24;
   private static final long WEEK = DAY * 7;
   private static final long MONTH = DAY * 30;
   private static final long QUARTER = MONTH * 3;
   private static final long YEAR = DAY * 365;


   private static final Logger LOGGER = LoggerFactory.getLogger(StatsCollector.class);

   private static final List<String> DAYS_OF_WEEK = Arrays.asList("sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday");
   private static final List<String> MONTHS_OF_YEAR = Arrays.asList("january", "february", "march", "april", "may", "june", "july", "august", "september", "october", "november", "december");

   private static final String DATE_FORMAT = "MM-dd-yyyy";
   private static final SimpleDateFormat FORMATTER = new SimpleDateFormat(DATE_FORMAT);

   private OrderStatsCache statsCache;

   public StatsCollector() throws Exception
   {
      statsCache = new OrderStatsCache();
   }

   public StatsCollector(String statsFilename, String orderItemFilename) throws Exception
   {
      statsCache = new OrderStatsCache(statsFilename, orderItemFilename);
   }

   public void collect(OrderStats orderStats)
   {
      statsCache.addOrderStats(orderStats);
   }

   public StatTotals statTotals(String begin, String end, String channel) throws Exception
   {
      long beginTime = FORMATTER.parse(begin).getTime();
      long endTime = FORMATTER.parse(end).getTime() + DAY;
      List<OrderStats> statsList = statsCache.getOrderStats();
      List<OrderStats> statsForPeriod = statsBetween(statsList, beginTime, endTime, channel);
      return createStatTotals(statsForPeriod, begin + " thru " + end);
   }

   public FormattedOrderStats lastOrder() throws Exception
   {
      return new FormattedOrderStats(statsCache.lastEntry());
   }

   public List<StatTotalsBySegment> dayOfWeekStats(String channel) throws Exception
   {
      List<StatTotalsBySegment> dayOfWeekList = new ArrayList<>();
      for (String s : DAYS_OF_WEEK)
      {
         Map<Integer, List<OrderStats>> map = statsForDayOfWeek(s, channel);
         List<OrderStats> allStatsForDayOfWeek = new ArrayList<>();
         for (List<OrderStats> list : map.values())
         {
            allStatsForDayOfWeek.addAll(list);
         }
         StatTotals statTotals = createStatTotals(allStatsForDayOfWeek, s);
         dayOfWeekList.add(toStatTotalsBySegment(statTotals, map.size()));
      }
      return dayOfWeekList;
   }

   public List<StatTotalsBySegment> monthOfYearStats(String channel) throws Exception
   {
      List<StatTotalsBySegment> monthOfYearList = new ArrayList<>();
      for (String s : MONTHS_OF_YEAR)
      {
         Map<Integer, List<OrderStats>> map = statsForMonthOfYear(s, channel);
         List<OrderStats> allStatsForMonthOfYear = new ArrayList<>();
         for (List<OrderStats> list : map.values())
         {
            allStatsForMonthOfYear.addAll(list);
         }
         StatTotals statTotals = createStatTotals(allStatsForMonthOfYear, s);
         monthOfYearList.add(toStatTotalsBySegment(statTotals, map.size()));
      }
      return monthOfYearList;
   }

   private StatTotalsBySegment toStatTotalsBySegment(StatTotals statTotals, int numberOfSegments)
   {
      StatTotalsBySegment bySegment = new StatTotalsBySegment();
      bySegment.setSummary(statTotals);

      if (numberOfSegments == 0)
      {
         bySegment.setAverageOrders(Utils.numberFormat(0));
         bySegment.setAverageProfit(Utils.currencyFormat(0));
         bySegment.setAverageRevenue(Utils.currencyFormat(0));
      }
      else
      {
         bySegment.setAverageOrders(Utils.numberFormat((double)statTotals.getOrders() / (double)numberOfSegments));
         bySegment.setAverageProfit(Utils.currencyFormat(Utils.fromCurrencyFormat(statTotals.getProfit()) / numberOfSegments));
         bySegment.setAverageRevenue(Utils.currencyFormat(Utils.fromCurrencyFormat(statTotals.getRevenue()) / numberOfSegments));
      }

      return bySegment;
   }

   private Map<Integer, List<OrderStats>> statsForDayOfWeek(String dayOfWeek, String channel) throws Exception
   {
      Map<Integer, List<OrderStats>> stats = new HashMap<>();
      List<OrderStats> statsList = statsCache.getOrderStats();
      for (OrderStats orderStats : statsList)
      {
         if (isChannelMatch(orderStats, channel))
         {
            String day = Utils.toDayOfWeek(orderStats.getTime());
            if (day.equalsIgnoreCase(dayOfWeek))
            {
               int yearAndDay = Utils.toYear(orderStats.getTime()) + Utils.toDayOfYear(orderStats.getTime());
               List<OrderStats> list = stats.get(yearAndDay);
               if (list == null)
               {
                  list = new ArrayList<>();
                  stats.put(yearAndDay, list);
               }
               list.add(orderStats);
            }
         }
      }
      return stats;
   }

   private Map<Integer, List<OrderStats>> statsForMonthOfYear(String monthOfYear, String channel) throws Exception
   {
      Map<Integer, List<OrderStats>> stats = new HashMap<>();
      List<OrderStats> statsList = statsCache.getOrderStats();
      for (OrderStats orderStats : statsList)
      {
         if (isChannelMatch(orderStats, channel))
         {
            String month = Utils.toMonthOfYear(orderStats.getTime());
            if (month.equalsIgnoreCase(monthOfYear))
            {
               int yearAndMonth = Utils.toYear(orderStats.getTime()) + Utils.toMonthOfYearValue(orderStats.getTime());
               List<OrderStats> list = stats.get(yearAndMonth);
               if (list == null)
               {
                  list = new ArrayList<>();
                  stats.put(yearAndMonth, list);
               }
               list.add(orderStats);
            }
         }
      }
      return stats;
   }

   public List<FormattedOrderStats> allStats(String timePeriod, String channel) throws Exception
   {
      List<OrderStats> statsList = statsForTimePeriod(timePeriod, channel);
      return createFormattedStats(statsList);
   }

   public List<FormattedOrderStats> allStats(int numdays, String channel) throws Exception
   {
      List<OrderStats> statsList = statsForDays(numdays, channel);
      return createFormattedStats(statsList);
   }

   private List<FormattedOrderStats> createFormattedStats(List<OrderStats> statsList)
   {
      List<FormattedOrderStats> formatted = new ArrayList<>(statsList.size());
      for (OrderStats orderStats : statsList)
      {
         FormattedOrderStats fs = new FormattedOrderStats(orderStats);
         formatted.add(fs);
      }
      return formatted;
   }

   public StatTotals statTotals(int numdays, String channel) throws Exception
   {
      List<OrderStats> statsList = statsForDays(numdays, channel);
      return createStatTotals(statsList, "" +  numdays + " days");
   }

   public StatTotals statTotals(String timePeriod, String channel) throws Exception
   {
      List<OrderStats> statsList = statsForTimePeriod(timePeriod, channel);
      return createStatTotals(statsList, timePeriod);
   }


   private StatTotals createStatTotals(List<OrderStats> statsList, String timePeriod)
   {
      int orders = 0;
      double revenue = 0.0;
      double orderValue = 0.0;
      double profit = 0.0;
      double yardsSold = 0.0;
      double costOfGoods = 0.0;
      for (OrderStats orderStats : statsList)
      {
         orders++;
         orderValue += orderStats.getOrderValue();
         revenue += orderStats.getRevenue();
         profit += orderStats.getProfit();
         costOfGoods += orderStats.getCostOfGoods();
         yardsSold += calculateYardsSold(orderStats);
      }

      double profitRatio = orders == 0 ? 0.0 : profit / revenue;
      double profitPerOrder = orders == 0 ? 0.0 : profit / orders;
      double revenuePerOrder = orders == 0 ? 0.0 : revenue / orders;
      StatTotals statTotals = new StatTotals();
      statTotals.setTimePeriod(timePeriod);
      statTotals.setOrders(orders);
      statTotals.setRevenue(Utils.currencyFormat(revenue));
      statTotals.setProfit(Utils.currencyFormat(profit));
      statTotals.setCogs(Utils.currencyFormat(costOfGoods));
      statTotals.setProfitRatio(Utils.percentageFormat(profitRatio));
      statTotals.setProfitPerOrder(Utils.currencyFormat(profitPerOrder));
      statTotals.setRevenuePerOrder(Utils.currencyFormat(revenuePerOrder));
      statTotals.setYardsSold(yardsSold);
      return statTotals;
   }

   private double calculateYardsSold(OrderStats orderStats)
   {
      double totalYards = 0.0;
      List<OrderItem> orderItems = orderStats.getOrderItems();
      if (orderItems != null)
      {
         for (OrderItem orderItem : orderItems)
         {
            double yards = 0.0;
            String variation = orderItem.getVariation();
            if (variation != null)
            {
               if (variation.startsWith("1 Yard"))
               {
                  yards += 1.0;
               }
               else if (variation.startsWith("3/4 Yard"))
               {
                  yards += 0.75;
               }
               else if (variation.startsWith("1/2 Yard"))
               {
                  yards += 0.5;
               }
               else if (variation.startsWith("1/4 Yard") || variation.startsWith("Fat"))
               {
                  yards += 0.25;
               }
            }
            int quantity = orderItem.getQuantity();
            totalYards += yards * quantity;
         }
      }
      return totalYards;
   }

   private List<OrderStats> statsForDays(int numdays, String channel) throws Exception
   {
      List<OrderStats> statsList = statsCache.getOrderStats();
      long now = System.currentTimeMillis();
      return statsBetween(statsList, now - (DAY * numdays), now, channel);
   }

   private List<OrderStats> statsForTimePeriod(String timePeriod, String channel) throws Exception
   {
      List<OrderStats> statsForPeriod;
      List<OrderStats> statsList = statsCache.getOrderStats();
      long now = System.currentTimeMillis();
      if (timePeriod.equalsIgnoreCase("week"))
      {
         statsForPeriod = statsBetween(statsList, now - WEEK, now, channel);
      }
      else if (timePeriod.equalsIgnoreCase("month"))
      {
         statsForPeriod = statsBetween(statsList, now - MONTH, now, channel);
      }
      else if (timePeriod.equalsIgnoreCase("quarter"))
      {
         statsForPeriod = statsBetween(statsList, now - QUARTER, now, channel);
      }
      else if (timePeriod.equalsIgnoreCase("year"))
      {
         statsForPeriod = statsBetween(statsList, now - YEAR, now, channel);
      }
      else if (timePeriod.equalsIgnoreCase("day"))
      {
         statsForPeriod = statsBetween(statsList, now - DAY, now, channel);
      }
      else if (timePeriod.equalsIgnoreCase("yesterday"))
      {
         statsForPeriod = statsBetween(statsList, beginningOfYesterday(), beginningOfDay(), channel);
      }
      else if (timePeriod.equalsIgnoreCase("yearToDate"))
      {
         statsForPeriod = statsBetween(statsList, beginningOfYear(), now, channel);
      }
      else if (timePeriod.equalsIgnoreCase("monthToDate"))
      {
         statsForPeriod = statsBetween(statsList, beginningOfMonth(), now, channel);
      }
      else if (timePeriod.equalsIgnoreCase("today"))
      {
         statsForPeriod = statsBetween(statsList, beginningOfDay(), now, channel);
      }
      else
      {
         long beginTime = FORMATTER.parse(timePeriod).getTime();
         long endTime = FORMATTER.parse(timePeriod).getTime() + DAY;
         statsForPeriod = statsBetween(statsList, beginTime, endTime, channel);
      }

      return statsForPeriod;
   }

   private List<OrderStats> statsBetween(List<OrderStats> statsList, long begin, long end, String channel)
   {
      LOGGER.debug("statsBetween: begin={} end={}", begin, end);
      List<OrderStats> statsForPeriod = new ArrayList<>();
      for (OrderStats stats : statsList)
      {
         long time = stats.getTime();
         if (time >= begin && time < end)
         {
            if (isChannelMatch(stats, channel))
            {
               LOGGER.debug("statsBetween: adding {}", time);
               statsForPeriod.add(stats);
            }
         }
         else
         {
            LOGGER.debug("statsBetween: NOT adding {}", time);
         }
      }
      return statsForPeriod;
   }

   public static void main(String[] args)
   {
      OrderStats os = new OrderStats();

      os.setChannel("etsy");

      String channel = "all";
      LOGGER.info("statChannel={} requestChannel={} isMatch={}", os.getChannel(), channel, isChannelMatch(os, channel));
      channel = "shopify";
      LOGGER.info("statChannel={} requestChannel={} isMatch={}", os.getChannel(), channel, isChannelMatch(os, channel));
      channel = "etsy";
      LOGGER.info("statChannel={} requestChannel={} isMatch={}", os.getChannel(), channel, isChannelMatch(os, channel));
      channel = "spindle";
      LOGGER.info("statChannel={} requestChannel={} isMatch={}", os.getChannel(), channel, isChannelMatch(os, channel));
      channel = "imagine";
      LOGGER.info("statChannel={} requestChannel={} isMatch={}", os.getChannel(), channel, isChannelMatch(os, channel));

   }

   private static boolean isChannelMatch(OrderStats stats, String channel)
   {
      // shopify, etsy, etsy:imagine, etsy:spindle

      if ("all".equals(channel.toLowerCase()))
      {
         return true;
      }
      if (stats.getChannel().toLowerCase().contains(channel.toLowerCase()))
      {
         return true;
      }
      if (channel.toLowerCase().equals(stats.getChannel().toLowerCase()))
      {
         return true;
      }
      if (stats.getChannel().toLowerCase().equals("etsy") && channel.equalsIgnoreCase("spindle"))
      {
         return true;
      }
      return false;
   }

   private long beginningOfMonth()
   {
      ZonedDateTime zdt = ZonedDateTime.now(ZoneId.of("US/Eastern"))
         .withHour(0)
         .withMinute(0)
         .withSecond(0)
         .withNano(0);
      int dayOfMonth = zdt.getDayOfMonth();
      long beginning = zdt.minusDays(dayOfMonth - 1).toEpochSecond() * 1000;
      LOGGER.info("beginningOfMonth: {}", beginning);
      return beginning;
   }

   private long beginningOfYear()
   {
      ZonedDateTime zdt = ZonedDateTime.now(ZoneId.of("US/Eastern"))
         .withHour(0)
         .withMinute(0)
         .withSecond(0)
         .withNano(0);
      int dayOfYear = zdt.getDayOfYear();
      long beginning = zdt.minusDays(dayOfYear - 1).toEpochSecond() * 1000;
      LOGGER.info("beginningOfYear: {}", beginning);
      return beginning;
   }

   private long beginningOfYesterday()
   {
      long beginning = ZonedDateTime.now(ZoneId.of("US/Eastern"))
         .withHour(0)
         .withMinute(0)
         .withSecond(0)
         .withNano(0).minusDays(1).toEpochSecond() * 1000;

      LOGGER.info("beginningOfYesterday: {}", beginning);
      return beginning;
   }

   private long beginningOfDay()
   {
      long beginning = ZonedDateTime.now(ZoneId.of("US/Eastern"))
         .withHour(0)
         .withMinute(0)
         .withSecond(0)
         .withNano(0).toEpochSecond() * 1000;
      LOGGER.info("beginningOfDay: {}", beginning);
      return beginning;
   }

   // Sort by time period, orders, revenue, profit, profit ratio...
   // Order by ascending or descending
   public void sort(List<StatTotalsBySegment> statTotals, String sortBy, String orderBy)
   {
      if (sortBy.equalsIgnoreCase("orders"))
      {
         Collections.sort(statTotals, new StatTotalsByOrdersComparator(orderBy));
      }
      else if (sortBy.equalsIgnoreCase("avgorders"))
      {
         Collections.sort(statTotals, new StatTotalsByAverageOrdersComparator(orderBy));
      }
      else if (sortBy.equalsIgnoreCase("dayofweek"))
      {
         Collections.sort(statTotals, new StatTotalsByDayOfWeekComparator(orderBy));
      }
      else if (sortBy.equalsIgnoreCase("monthofyear"))
      {
         Collections.sort(statTotals, new StatTotalsByMonthOfYearComparator(orderBy));
      }
      else if (sortBy.equalsIgnoreCase("revenue"))
      {
         Collections.sort(statTotals, new StatTotalsByRevenueComparator(orderBy));
      }
      else if (sortBy.equalsIgnoreCase("avgrevenue"))
      {
         Collections.sort(statTotals, new StatTotalsByAverageRevenueComparator(orderBy));
      }
      else if (sortBy.equalsIgnoreCase("profit"))
      {
         Collections.sort(statTotals, new StatTotalsByProfitComparator(orderBy));
      }
      else if (sortBy.equalsIgnoreCase("avgprofit"))
      {
         Collections.sort(statTotals, new StatTotalsByAverageProfitComparator(orderBy));
      }
      else if (sortBy.equalsIgnoreCase("profitratio"))
      {
         Collections.sort(statTotals, new StatTotalsByProfitRatioComparator(orderBy));
      }
      else
      {
         LOGGER.error("sort: unknown sortBy={}", sortBy);
      }
   }
}
