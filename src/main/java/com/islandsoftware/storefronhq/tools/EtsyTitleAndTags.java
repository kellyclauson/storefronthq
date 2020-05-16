package com.islandsoftware.storefronhq.tools;

import java.util.Set;

public class EtsyTitleAndTags
{
   private Long id;
   private String title;
   private Set<String> tags;

   @Override
   public String toString()
   {
      return "EtsyTitleAndTags{" +
         "id='" + id + '\'' +
         "title='" + title + '\'' +
         ", tags='" + tags + '\'' +
         '}';
   }

   public Long getId()
   {
      return id;
   }

   public void setId(Long id)
   {
      this.id = id;
   }

   public String getTitle()
   {
      return title;
   }

   public void setTitle(String title)
   {
      this.title = title;
   }

   public Set<String> getTags()
   {
      return tags;
   }

   public void setTags(Set<String> tags)
   {
      this.tags = tags;
   }
}
