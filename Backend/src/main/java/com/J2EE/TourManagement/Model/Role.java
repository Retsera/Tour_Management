package com.J2EE.TourManagement.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "nameRole") private String nameRole;

  @Column(name = "description") private String description;

  @Column(name = "status") private boolean status;

  public long getId() { return id; }

  public String getNameRole() { return nameRole; }

  public void setNameRole(String nameRole) { this.nameRole = nameRole; }

  public String getDescription() { return description; }

  public void setDescription(String description) {
    this.description = description;
  }

  public boolean isStatus() { return status; }

  public void setStatus(boolean status) { this.status = status; }
}
