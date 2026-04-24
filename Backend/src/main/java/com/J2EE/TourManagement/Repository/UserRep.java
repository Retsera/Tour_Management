package com.J2EE.TourManagement.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.J2EE.TourManagement.Model.User;
import java.util.List;


@Repository
public interface UserRep extends JpaRepository<User, Long>, JpaSpecificationExecutor<User>  {
     boolean existsByEmail(String email);
     User findByEmail(String email);
     User findByRefreshTokenAndEmail(String token, String Email);
}