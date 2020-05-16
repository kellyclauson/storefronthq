package com.islandsoftware.storefronhq.etsy.model;

public class CustomListing
{
   private String title;
   private double weight;
   private double ourCost;
   private double costToShipToUs;

   @Override
   public String toString()
   {
      return "CustomListing{" +
         "title='" + title + '\'' +
         ", weight=" + weight +
         ", ourCost=" + ourCost +
         ", costToShipToUs=" + costToShipToUs +
         '}';
   }

   public String getTitle()
   {
      return title;
   }

   public void setTitle(String title)
   {
      this.title = title;
   }

   public double getWeight()
   {
      return weight;
   }

   public void setWeight(double weight)
   {
      this.weight = weight;
   }

   public double getOurCost()
   {
      return ourCost;
   }

   public void setOurCost(double ourCost)
   {
      this.ourCost = ourCost;
   }

   public double getCostToShipToUs()
   {
      return costToShipToUs;
   }

   public void setCostToShipToUs(double costToShipToUs)
   {
      this.costToShipToUs = costToShipToUs;
   }
}
