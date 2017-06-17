package com.moonclound.android.iptv.util;

import java.io.Serializable;
import java.util.List;

public class VodCategory implements Serializable
{
  private String id;
  private String name;
  private String pic;
  private List<VodProgram> programs;
  private String subcount;

  public String getId()
  {
    return this.id;
  }

  public String getName()
  {
    return this.name;
  }

  public String getPic()
  {
    return this.pic;
  }

  public List<VodProgram> getPrograms()
  {
    return this.programs;
  }

  public String getSubcount()
  {
    return this.subcount;
  }

  public void setId(String paramString)
  {
    this.id = paramString;
  }

  public void setName(String paramString)
  {
    this.name = paramString;
  }

  public void setPic(String paramString)
  {
    this.pic = paramString;
  }

  public void setPrograms(List<VodProgram> paramList)
  {
    this.programs = paramList;
  }

  public void setSubcount(String paramString)
  {
    this.subcount = paramString;
  }
}

/* Location:           E:\ria\反编译\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.moon.moonlibrary.model.VodCategory
 * JD-Core Version:    0.6.2
 */