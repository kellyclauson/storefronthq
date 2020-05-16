package com.islandsoftware.storefronhq.shopify;

import com.islandsoftware.storefronhq.shopify.sync.model.Product;
import com.islandsoftware.storefronhq.shopify.sync.model.Variant;
import com.islandsoftware.storefronhq.tools.Utils;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

public class InventoryCalculatorTest extends TestCase
{
   public InventoryCalculatorTest(String name)
   {
      super(name);
   }

   public static Test suite()
   {
      return new TestSuite( InventoryCalculatorTest.class );
   }

   public void testYard() throws Exception
   {
      Product product = createProduct();
      Utils.updateInventory(product, 5L, "1 Yard", 1);
      assertEquals(36, getInventory(product, "Fat Quarter"));
      assertEquals(36, getInventory(product, "1/4"));
      assertEquals(18, getInventory(product, "1/2"));
      assertEquals(11, getInventory(product, "3/4"));
      assertEquals(10, getInventory(product, "1 Yard"));
      product = createProduct();
      Utils.updateInventory(product, 5L, "1 Yard", 2);
      assertEquals(32, getInventory(product, "Fat Quarter"));
      assertEquals(32, getInventory(product, "1/4"));
      assertEquals(16, getInventory(product, "1/2"));
      assertEquals(10, getInventory(product, "3/4"));
      assertEquals(10, getInventory(product, "1 Yard"));
      product = createProduct();
      Utils.updateInventory(product, 5L, "1 Yard", 3);
      assertEquals(28, getInventory(product, "Fat Quarter"));
      assertEquals(28, getInventory(product, "1/4"));
      assertEquals(14, getInventory(product, "1/2"));
      assertEquals(9, getInventory(product, "3/4"));
      assertEquals(10, getInventory(product, "1 Yard"));
      product = createProduct();
      Utils.updateInventory(product, 5L, "1 Yard", 4);
      assertEquals(24, getInventory(product, "Fat Quarter"));
      assertEquals(24, getInventory(product, "1/4"));
      assertEquals(12, getInventory(product, "1/2"));
      assertEquals(7, getInventory(product, "3/4"));
      assertEquals(10, getInventory(product, "1 Yard"));
      product = createProduct();
      Utils.updateInventory(product, 5L, "1 Yard", 5);
      assertEquals(20, getInventory(product, "Fat Quarter"));
      assertEquals(20, getInventory(product, "1/4"));
      assertEquals(10, getInventory(product, "1/2"));
      assertEquals(6, getInventory(product, "3/4"));
      assertEquals(10, getInventory(product, "1 Yard"));
   }
   public void testThreeQuarters() throws Exception
   {
      Product product = createProduct();
      Utils.updateInventory(product, 4L, "3/4 Yard", 1);
      assertEquals(37, getInventory(product, "Fat Quarter"));
      assertEquals(37, getInventory(product, "1/4"));
      assertEquals(18, getInventory(product, "1/2"));
      assertEquals(13, getInventory(product, "3/4"));
      assertEquals(9, getInventory(product, "1 Yard"));
      product = createProduct();
      Utils.updateInventory(product, 4L, "3/4 Yard", 2);
      assertEquals(34, getInventory(product, "Fat Quarter"));
      assertEquals(34, getInventory(product, "1/4"));
      assertEquals(17, getInventory(product, "1/2"));
      assertEquals(13, getInventory(product, "3/4"));
      assertEquals(8, getInventory(product, "1 Yard"));
      product = createProduct();
      Utils.updateInventory(product, 4L, "3/4 Yard", 3);
      assertEquals(31, getInventory(product, "Fat Quarter"));
      assertEquals(31, getInventory(product, "1/4"));
      assertEquals(15, getInventory(product, "1/2"));
      assertEquals(13, getInventory(product, "3/4"));
      assertEquals(7, getInventory(product, "1 Yard"));
      product = createProduct();
      Utils.updateInventory(product, 4L, "3/4 Yard", 4);
      assertEquals(28, getInventory(product, "Fat Quarter"));
      assertEquals(28, getInventory(product, "1/4"));
      assertEquals(14, getInventory(product, "1/2"));
      assertEquals(13, getInventory(product, "3/4"));
      assertEquals(7, getInventory(product, "1 Yard"));
      product = createProduct();
      Utils.updateInventory(product, 4L, "3/4 Yard", 5);
      assertEquals(25, getInventory(product, "Fat Quarter"));
      assertEquals(25, getInventory(product, "1/4"));
      assertEquals(12, getInventory(product, "1/2"));
      assertEquals(13, getInventory(product, "3/4"));
      assertEquals(6, getInventory(product, "1 Yard"));
   }

   public void testHalf() throws Exception
   {
      Product product = createProduct();
      Utils.updateInventory(product, 3L, "1/2 Yard", 1);
      assertEquals(38, getInventory(product, "Fat Quarter"));
      assertEquals(38, getInventory(product, "1/4"));
      assertEquals(20, getInventory(product, "1/2"));
      assertEquals(12, getInventory(product, "3/4"));
      assertEquals(9, getInventory(product, "1 Yard"));
      product = createProduct();
      Utils.updateInventory(product, 3L, "1/2 Yard", 2);
      assertEquals(36, getInventory(product, "Fat Quarter"));
      assertEquals(36, getInventory(product, "1/4"));
      assertEquals(20, getInventory(product, "1/2"));
      assertEquals(11, getInventory(product, "3/4"));
      assertEquals(9, getInventory(product, "1 Yard"));
      product = createProduct();
      Utils.updateInventory(product, 3L, "1/2 Yard", 3);
      assertEquals(34, getInventory(product, "Fat Quarter"));
      assertEquals(34, getInventory(product, "1/4"));
      assertEquals(20, getInventory(product, "1/2"));
      assertEquals(11, getInventory(product, "3/4"));
      assertEquals(8, getInventory(product, "1 Yard"));
      product = createProduct();
      Utils.updateInventory(product, 3L, "1/2 Yard", 4);
      assertEquals(32, getInventory(product, "Fat Quarter"));
      assertEquals(32, getInventory(product, "1/4"));
      assertEquals(20, getInventory(product, "1/2"));
      assertEquals(10, getInventory(product, "3/4"));
      assertEquals(8, getInventory(product, "1 Yard"));
      product = createProduct();
      Utils.updateInventory(product, 3L, "1/2 Yard", 5);
      assertEquals(30, getInventory(product, "Fat Quarter"));
      assertEquals(30, getInventory(product, "1/4"));
      assertEquals(20, getInventory(product, "1/2"));
      assertEquals(9, getInventory(product, "3/4"));
      assertEquals(7, getInventory(product, "1 Yard"));
   }

   public void testQuarter() throws Exception
   {
      Product product = createProduct();
      Utils.updateInventory(product, 2L, "1/4 Yard", 1);
      assertEquals(39, getInventory(product, "Fat Quarter"));
      assertEquals(40, getInventory(product, "1/4"));
      assertEquals(19, getInventory(product, "1/2"));
      assertEquals(12, getInventory(product, "3/4"));
      assertEquals(9, getInventory(product, "1 Yard"));
      product = createProduct();
      Utils.updateInventory(product, 2L, "1/4 Yard", 2);
      assertEquals(38, getInventory(product, "Fat Quarter"));
      assertEquals(40, getInventory(product, "1/4"));
      assertEquals(19, getInventory(product, "1/2"));
      assertEquals(12, getInventory(product, "3/4"));
      assertEquals(9, getInventory(product, "1 Yard"));
      product = createProduct();
      Utils.updateInventory(product, 2L, "1/4 Yard", 3);
      assertEquals(37, getInventory(product, "Fat Quarter"));
      assertEquals(40, getInventory(product, "1/4"));
      assertEquals(18, getInventory(product, "1/2"));
      assertEquals(12, getInventory(product, "3/4"));
      assertEquals(9, getInventory(product, "1 Yard"));
      product = createProduct();
      Utils.updateInventory(product, 2L, "1/4 Yard", 4);
      assertEquals(36, getInventory(product, "Fat Quarter"));
      assertEquals(40, getInventory(product, "1/4"));
      assertEquals(18, getInventory(product, "1/2"));
      assertEquals(11, getInventory(product, "3/4"));
      assertEquals(9, getInventory(product, "1 Yard"));
      product = createProduct();
      Utils.updateInventory(product, 2L, "1/4 Yard", 5);
      assertEquals(35, getInventory(product, "Fat Quarter"));
      assertEquals(40, getInventory(product, "1/4"));
      assertEquals(17, getInventory(product, "1/2"));
      assertEquals(11, getInventory(product, "3/4"));
      assertEquals(8, getInventory(product, "1 Yard"));
   }

   public void testFQ() throws Exception
   {
      Product product = createProduct();
      Utils.updateInventory(product, 1L, "Fat Quarter", 1);
      assertEquals(40, getInventory(product, "Fat Quarter"));
      assertEquals(38, getInventory(product, "1/4"));
      assertEquals(19, getInventory(product, "1/2"));
      assertEquals(12, getInventory(product, "3/4"));
      assertEquals(9, getInventory(product, "1 Yard"));
      product = createProduct();
      Utils.updateInventory(product, 1L, "Fat Quarter", 2);
      assertEquals(40, getInventory(product, "Fat Quarter"));
      assertEquals(36, getInventory(product, "1/4"));
      assertEquals(18, getInventory(product, "1/2"));
      assertEquals(11, getInventory(product, "3/4"));
      assertEquals(9, getInventory(product, "1 Yard"));
      product = createProduct();
      Utils.updateInventory(product, 1L, "Fat Quarter", 3);
      assertEquals(40, getInventory(product, "Fat Quarter"));
      assertEquals(34, getInventory(product, "1/4"));
      assertEquals(17, getInventory(product, "1/2"));
      assertEquals(11, getInventory(product, "3/4"));
      assertEquals(8, getInventory(product, "1 Yard"));
      product = createProduct();
      Utils.updateInventory(product, 1L, "Fat Quarter", 4);
      assertEquals(40, getInventory(product, "Fat Quarter"));
      assertEquals(32, getInventory(product, "1/4"));
      assertEquals(16, getInventory(product, "1/2"));
      assertEquals(10, getInventory(product, "3/4"));
      assertEquals(8, getInventory(product, "1 Yard"));
      product = createProduct();
      Utils.updateInventory(product, 1L, "Fat Quarter", 5);
      assertEquals(40, getInventory(product, "Fat Quarter"));
      assertEquals(30, getInventory(product, "1/4"));
      assertEquals(15, getInventory(product, "1/2"));
      assertEquals(9, getInventory(product, "3/4"));
      assertEquals(7, getInventory(product, "1 Yard"));
   }

   private int getInventory(Product product, String variantTitle)
   {
      List<Variant> variants = product.getVariants();
      for (Variant variant : variants)
      {
         if (variant.getTitle().startsWith(variantTitle))
         {
            return variant.getInventoryQuantity();
         }
      }
      return 0;
   }

   private Product createProduct()
   {
      List<Variant> variants = new ArrayList<Variant>();
      Variant fq = new Variant();
      fq.setId(1L);
      fq.setTitle("Fat Quarter");
      fq.setInventoryQuantity(40);
      variants.add(fq);
      Variant quarter = new Variant();
      quarter.setId(2L);
      quarter.setTitle("1/4 Yard");
      quarter.setInventoryQuantity(40);
      variants.add(quarter);
      Variant half = new Variant();
      half.setId(3L);
      half.setTitle("1/2 Yard");
      half.setInventoryQuantity(20);
      variants.add(half);
      Variant threeQuarters = new Variant();
      threeQuarters.setId(4L);
      threeQuarters.setTitle("3/4 Yard");
      threeQuarters.setInventoryQuantity(13);
      variants.add(threeQuarters);
      Variant yard = new Variant();
      yard.setId(5L);
      yard.setTitle("1 Yard");
      yard.setInventoryQuantity(10);
      variants.add(yard);
      Product product = new Product();
      product.setVariants(variants);
      product.setProductType("Fabric");
      return product;
   }

}
