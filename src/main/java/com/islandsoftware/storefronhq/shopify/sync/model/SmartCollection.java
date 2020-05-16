package com.islandsoftware.storefronhq.shopify.sync.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SmartCollection
{
   private long id;
   private String title;
   private String handle;

   @JsonIgnore
   private String titleTag = "set me please!";
   @JsonIgnore
   private String descriptionTag = "set me please!";
   @JsonIgnore
   private List<String> keywords = new ArrayList<>();

   @Override
   public String toString()
   {
      return "SmartCollection{" +
            "id=" + id +
            ", title='" + title + '\'' +
            ", handle='" + handle + '\'' +
            ", titleTag='" + titleTag + '\'' +
            ", descriptionTag='" + descriptionTag + '\'' +
            ", keywords='" + keywords + '\'' +
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

   public String getTitle()
   {
      return title;
   }

   public void setTitle(String title)
   {
      this.title = title;
   }

   public String getHandle()
   {
      return handle;
   }

   public void setHandle(String handle)
   {
      this.handle = handle;
   }

   public String getTitleTag()
   {
      return titleTag;
   }

   public void setTitleTag(String titleTag)
   {
      this.titleTag = titleTag;
   }

   public String getDescriptionTag()
   {
      return descriptionTag;
   }

   public void setDescriptionTag(String descriptionTag)
   {
      this.descriptionTag = descriptionTag;
   }

   public List<String> getKeywords()
   {
      return keywords;
   }

   public void setKeywords(List<String> keywords)
   {
      this.keywords = keywords;
   }
}
