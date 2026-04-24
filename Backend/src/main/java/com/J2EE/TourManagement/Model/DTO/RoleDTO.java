package com.J2EE.TourManagement.Model.DTO;

public class RoleDTO {
  private long id;
  private String nameRole;
  private String description;
  private boolean status;

  public RoleDTO(long id, String nameRole, String description, boolean status) {
    this.id = id;
    this.nameRole = nameRole;
    this.description = description;
    this.status = status;
  }
  public long getId() { return id; }
  public void setId(long id) { this.id = id; }
  public String getNameRole() { return nameRole; }
  public void setNameRole(String nameRole) { this.nameRole = nameRole; }

  public String getDescription() { return description; }

  public void setDescription(String description) {
    this.description = description;
  }

  public boolean isStatus() { return status; }

  public void setStatus(boolean status) { this.status = status; }
}
