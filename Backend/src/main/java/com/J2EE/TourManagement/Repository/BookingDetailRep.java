package com.J2EE.TourManagement.Repository;

import com.J2EE.TourManagement.Model.BookingDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingDetailRep extends JpaRepository<BookingDetail, Long> {
    
}
