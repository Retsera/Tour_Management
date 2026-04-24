package com.J2EE.TourManagement.Repository;

import com.J2EE.TourManagement.Model.Tour;
import com.J2EE.TourManagement.Model.TourDetail;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TourRepository
    extends JpaRepository<Tour, Long>, JpaSpecificationExecutor {
  Page<Tour> findByStatusContainingIgnoreCaseAndLocationContainingIgnoreCase(
      String status, String location, Pageable pageable);

  @Query("SELECT DISTINCT t FROM Tour t "
         + "LEFT JOIN FETCH t.tourDetails td "
         + "WHERE t.status = 'ACTIVE'")
  List<Tour>
  findActiveToursWithDetails();

  @Query("SELECT DISTINCT td FROM TourDetail td "
         + "LEFT JOIN FETCH td.tourPrices tp "
         + "WHERE td.tour IN :tours")
  Set<TourDetail>
  fetchPricesForTourDetails(@Param("tours") List<Tour> tours);

@Query("""
    SELECT DISTINCT t FROM Tour t
    JOIN t.tourDetails td
    WHERE (:location IS NULL OR t.location LIKE CONCAT('%', :location, '%'))
    AND (:startLocation IS NULL OR td.startLocation LIKE CONCAT('%', :startLocation, '%'))
    AND (:remainingSeats IS NULL OR td.remainingSeats >= :remainingSeats)
""")
List<Tour> searchTours(
        @Param("location") String location,
        @Param("startLocation") String startLocation,
        @Param("remainingSeats") Integer remainingSeats
);
}
