package com.J2EE.TourManagement.Repository;

import com.J2EE.TourManagement.Model.TourPrice;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TourPriceRepository extends JpaRepository<TourPrice, Long> {
}