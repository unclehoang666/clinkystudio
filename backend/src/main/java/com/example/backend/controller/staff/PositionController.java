package com.example.backend.controller.staff;

import com.example.backend.dto.staff.PositionRequest;
import com.example.backend.entity.staff.Position;
import com.example.backend.service.staff.PositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/positions")
@RequiredArgsConstructor
public class PositionController {

    private final PositionService positionService;

    @GetMapping
    public List<Position> list() {
        return positionService.getAll();
    }

    @PostMapping
    public ResponseEntity<Position> create(@RequestBody PositionRequest req) {
        return ResponseEntity.ok(positionService.create(req));
    }
}