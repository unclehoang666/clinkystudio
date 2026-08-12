package com.example.backend.repository.staff;

import com.example.backend.entity.staff.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    Optional<Employee> findByUser_Id(Integer userId);

    boolean existsByCodeIgnoreCase(String code);

    @Query("SELECT e FROM Employee e WHERE " +
           "(:q IS NULL OR LOWER(e.user.fullName) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(e.user.username) LIKE LOWER(CONCAT('%', :q, '%'))) AND " +
           "(:status IS NULL OR e.status = :status)")
    Page<Employee> search(@Param("q") String q, @Param("status") Boolean status, Pageable pageable);
}