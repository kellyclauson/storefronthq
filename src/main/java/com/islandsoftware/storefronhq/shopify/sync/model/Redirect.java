package com.islandsoftware.storefronhq.shopify.sync.model;

public class Redirect
{
   private long id;
   private String path;
   private String target;

   @Override
   public String toString()
   {
      return "Redirect{" +
            "id=" + id +
            ", path='" + path + '\'' +
            ", target='" + target + '\'' +
            '}';
   }

   public long getId()
   {
      return id;
   }

   public void setId(long id)
   {
      this.id = id;
   }

   public String getTarget()
   {
      return target;
   }

   public void setTarget(String target)
   {
      this.target = target;
   }

   public String getPath()
   {
      return path;
   }

   public void setPath(String path)
   {
      this.path = path;
   }
}
