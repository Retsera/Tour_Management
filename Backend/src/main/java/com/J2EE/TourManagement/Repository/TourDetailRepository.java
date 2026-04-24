package com.J2EE.TourManagement.Repository;

import com.J2EE.TourManagement.Model.TourDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TourDetailRepository extends JpaRepository<TourDetail, Long>, JpaSpecificationExecutor<TourDetail> {

}