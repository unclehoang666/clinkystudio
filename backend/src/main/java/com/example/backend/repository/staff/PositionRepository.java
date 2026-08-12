package com.example.backend.repository.staff;

import com.example.backend.entity.staff.Position;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PositionRepository extends JpaRepository<Position, Integer> {
    boolean existsByNameIgnoreCase(String name);
}