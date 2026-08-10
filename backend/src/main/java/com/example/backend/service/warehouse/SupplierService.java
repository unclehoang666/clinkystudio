package com.example.backend.service.warehouse;

import com.example.backend.dto.warehouse.SupplierRequest;
import com.example.backend.entity.warehouse.Supplier;
import com.example.backend.repository.warehouse.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;

    private String generateCode() {
        return "NCC" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    public Page<Supplier> search(String q, Boolean status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return supplierRepository.search(q, status, pageable);
    }

    public Supplier getById(Integer id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy nhà cung cấp"));
    }

    public Supplier create(SupplierRequest req) {
        String name = req.getName() != null ? req.getName().trim() : "";
        String code = (req.getCode() == null || req.getCode().trim().isEmpty())
                ? generateCode()
                : req.getCode().trim().toUpperCase();

        if (name.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên nhà cung cấp không được để trống!");
        if (supplierRepository.existsByNameIgnoreCase(name))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Tên nhà cung cấp này đã tồn tại!");
        if (supplierRepository.existsByCodeIgnoreCase(code))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Mã nhà cung cấp này đã tồn tại!");

        Supplier entity = new Supplier();
        entity.setCode(code);
        entity.setName(name);
        entity.setPhone(req.getPhone());
        entity.setEmail(req.getEmail());
        entity.setAddress(req.getAddress());
        entity.setStatus(req.getStatus() != null ? req.getStatus() : true);
        entity.setCreatedAt(LocalDateTime.now());

        return supplierRepository.save(entity);
    }

    public Supplier update(Integer id, SupplierRequest req) {
        Supplier entity = getById(id);

        String newName = req.getName() != null ? req.getName().trim() : "";
        if (newName.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên không được để trống!");
        if (!entity.getName().equalsIgnoreCase(newName) && supplierRepository.existsByNameIgnoreCase(newName))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Tên này đã tồn tại ở nhà cung cấp khác!");

        entity.setName(newName);
        if (req.getPhone() != null) entity.setPhone(req.getPhone());
        if (req.getEmail() != null) entity.setEmail(req.getEmail());
        if (req.getAddress() != null) entity.setAddress(req.getAddress());
        if (req.getStatus() != null) entity.setStatus(req.getStatus());

        return supplierRepository.save(entity);
    }

    public void toggleStatus(Integer id) {
        Supplier entity = getById(id);
        entity.setStatus(!entity.getStatus());
        supplierRepository.save(entity);
    }
}