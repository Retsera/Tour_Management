package com.J2EE.TourManagement.Model.DTO;

public class Meta {
  private int pageCurrent;
  private int pageSize;
  private int pages;
  private long total;

  public int getPageCurrent() { return this.pageCurrent; }

  public void setPageCurrent(int pageCurrent) {
    this.pageCurrent = pageCurrent;
  }

  public int getPageSize() { return this.pageSize; }

  public void setPageSize(int pageSize) { this.pageSize = pageSize; }

  public int getPages() { return this.pages; }

  public void setPages(int pages) { this.pages = pages; }

  public long getTotal() { return this.total; }

  public void setTotal(long total) { this.total = total; }
}
