package com.example.backend.service.staff;

import com.example.backend.dto.staff.PositionRequest;
import com.example.backend.entity.staff.Position;
import com.example.backend.repository.staff.PositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PositionService {

    private final PositionRepository positionRepository;

    public List<Position> getAll() {
        return positionRepository.findAll();
    }

    public Position getById(Integer id) {
        return positionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy chức vụ"));
    }

    public Position create(PositionRequest req) {
        String name = req.getName() != null ? req.getName().trim() : "";
        if (name.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên chức vụ không được để trống!");
        if (positionRepository.existsByNameIgnoreCase(name))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Chức vụ này đã tồn tại!");

        Position position = new Position();
        position.setCode((req.getCode() == null || req.getCode().isBlank())
                ? "CV" + UUID.randomUUID().toString().substring(0, 6).toUpperCase()
                : req.getCode().toUpperCase());
        position.setName(name);
        position.setDescription(req.getDescription());
        return positionRepository.save(position);
    }
}