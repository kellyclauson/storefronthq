package com.islandsoftware.storefronhq.stats;

import com.islandsoftware.storefronhq.tools.Utils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderStatsCache
{
   private static final String ORDER_ITEM_FILE = "/home/pi/spindleandrose/sync/data/orderitems.csv";
   private static final String STATS_FILE = "/home/pi/spindleandrose/sync/data/stats.csv";
   //private static final String STATS_FILE = "C://tmp//stats.csv";

   private List<OrderStats> orderStats;
   private Map<Long, List<OrderItem>> orderItemMap;

   private String statsFilename;
   private String orderItemFileName;

   public OrderStatsCache() throws Exception
   {
      orderItemFileName = ORDER_ITEM_FILE;
      statsFilename = STATS_FILE;
      orderItemMap = orderItemsFromFile();
      orderStats = orderStatsFromFile();
   }

   public OrderStatsCache(String statsFilename, String orderItemFilename) throws Exception
   {
      this.orderItemFileName = orderItemFilename;
      this.statsFilename = statsFilename;
      orderItemMap = orderItemsFromFile();
      orderStats = orderStatsFromFile();
   }

   public void addOrderStats(OrderStats orderStats)
   {
      this.orderStats.add(orderStats);
      List<OrderItem> orderItems = orderStats.getOrderItems();
      addOrderItems(orderStats.getOrderId(), orderItems);
      write();
   }

   private void addOrderItems(long orderId, List<OrderItem> orderItems)
   {
      List<OrderItem> list = orderItemMap.get(orderId);
      if (list == null)
      {
         orderItemMap.put(orderId, orderItems);
      }
      else
      {
         list.addAll(orderItems);
      }
   }

   public List<OrderStats> getOrderStats()
   {
      return orderStats;
   }

   private List<OrderStats> orderStatsFromFile() throws Exception
   {
      List<OrderStats> stats = new ArrayList<>();
      File file = new File(statsFilename);
      if (!file.exists())
      {
         return stats;
      }
      List<String> lines = Files.readAllLines(Paths.get(statsFilename), StandardCharsets.ISO_8859_1);
      int count = 0;
      for (String line : lines)
      {
         if (++ count == 1)
         {
            continue;
         }
         OrderStats orderStats = toStats(line);
         List<OrderItem> orderItems = orderItemMap.get(orderStats.getOrderId());
         orderStats.setOrderItems(orderItems);
         stats.add(orderStats);
      }
      stats.sort(new OrderStatsComparator());
      return stats;
   }

   private OrderStats toStats(String line) throws Exception
   {
      OrderStats orderStats = new OrderStats();
      String[] split = line.split(",");
      long time = Utils.dateStringToMillis(split[0]);
      orderStats.setTime(time);
      orderStats.setChannel(split[1]);
      long orderId = Long.parseLong(split[2]);
      orderStats.setOrderId(orderId);
      orderStats.setCountry(split[3]);
      orderStats.setOrderValue(Double.parseDouble(split[4]));
      orderStats.setOrderShippingValue(Double.parseDouble(split[5]));
      orderStats.setRevenue(Double.parseDouble(split[6]));
      orderStats.setOurCost(Double.parseDouble(split[7]));
      orderStats.setCostToShipToUs(Double.parseDouble(split[8]));
      orderStats.setCostOfGoods(Double.parseDouble(split[9]));
      orderStats.setCostToShipToCustomer(Double.parseDouble(split[10]));
      orderStats.setFee(Double.parseDouble(split[11]));
      orderStats.setFeeRatio(Double.parseDouble(split[12]));
      orderStats.setProfit(Double.parseDouble(split[13]));
      orderStats.setProfitRatio(Double.parseDouble(split[14]));
      orderStats.setUnitsSold(Integer.parseInt(split[15]));
      orderStats.setWeightInOunces(Double.parseDouble(split[16]));
      orderStats.setWeightWithPackaging(Double.parseDouble(split[17]));
      return orderStats;
   }

   private Map<Long, List<OrderItem>> orderItemsFromFile() throws Exception
   {
      Map<Long, List<OrderItem>> orderItems = new HashMap<>();
      File file = new File(orderItemFileName);
      if (!file.exists())
      {
         return orderItems;
      }
      List<String> lines = Files.readAllLines(Paths.get(orderItemFileName), StandardCharsets.ISO_8859_1);
      int count = 0;
      for (String line : lines)
      {
         if (++count == 1)
         {
            continue;
         }
         OrderItem orderItem = toOrderItem(line);
         List<OrderItem> list = orderItems.get(orderItem.getOrderId());
         if (list == null)
         {
            list = new ArrayList<>();
            orderItems.put(orderItem.getOrderId(), list);
         }
         list.add(orderItem);
      }
      return orderItems;
   }

   private OrderItem toOrderItem(String line) throws Exception
   {
      OrderItem orderItem = new OrderItem();
      String[] split = line.split(",");
      orderItem.setOrderId(Long.parseLong(split[0]));
      orderItem.setTitle(split[1]);
      orderItem.setVariation(split[2]);
      orderItem.setQuantity(Integer.parseInt(split[3]));
      return orderItem;
   }

   private void write()
   {
      StringBuilder statsBuffer = new StringBuilder();
      statsBuffer.append("Time,Channel,OrderId,Country,OrderValue,OrderShippingValue,Revenue,OurCost,CostToShipToUs,CostOfGoods,CostToShipToCustomer,Fees,FeeRatio,Profit,ProfitRatio,UnitsSold,WeightInOunces,WeightWithPackaging\n");
      for (OrderStats os : orderStats)
      {
         statsBuffer.append(Utils.millisToDateString(os.getTime())).append(",");
         statsBuffer.append(os.getChannel()).append(",");
         statsBuffer.append(os.getOrderId()).append(",");
         statsBuffer.append(os.getCountry()).append(",");
         statsBuffer.append(os.getOrderValue()).append(",");
         statsBuffer.append(os.getOrderShippingValue()).append(",");
         statsBuffer.append(os.getRevenue()).append(",");
         statsBuffer.append(os.getOurCost()).append(",");
         statsBuffer.append(os.getCostToShipToUs()).append(",");
         statsBuffer.append(os.getCostOfGoods()).append(",");
         statsBuffer.append(os.getCostToShipToCustomer()).append(",");
         statsBuffer.append(os.getFee()).append(",");
         statsBuffer.append(os.getFeeRatio()).append(",");
         statsBuffer.append(os.getProfit()).append(",");
         statsBuffer.append(os.getProfitRatio()).append(",");
         statsBuffer.append(os.getUnitsSold()).append(",");
         statsBuffer.append(os.getWeightInOunces()).append(",");
         statsBuffer.append(os.getWeightWithPackaging()).append("\n");
      }
      Utils.write(statsFilename, statsBuffer.toString());
      writeOrderItems();
   }

   private void writeOrderItems()
   {
      StringBuilder sb = new StringBuilder();
      sb.append("OrderId,Title,Variation,Quantity\n");
      for (Map.Entry<Long, List<OrderItem>> entry : orderItemMap.entrySet())
      {
         Long orderId = entry.getKey();
         List<OrderItem> orderItems = entry.getValue();
         for (OrderItem orderItem : orderItems)
         {
            sb.append(orderId).append(",");
            sb.append(orderItem.getTitle()).append(",");
            sb.append(orderItem.getVariation()).append(",");
            sb.append(orderItem.getQuantity()).append("\n");
         }
      }
      Utils.write(orderItemFileName, sb.toString());
   }

   public OrderStats lastEntry()
   {
      return orderStats.get(orderStats.size() - 1);
   }
}
