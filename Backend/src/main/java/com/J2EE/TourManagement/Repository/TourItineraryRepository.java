package com.J2EE.TourManagement.Repository;

import com.J2EE.TourManagement.Model.TourItinerary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TourItineraryRepository extends JpaRepository<TourItinerary, Long>, JpaSpecificationExecutor {
}