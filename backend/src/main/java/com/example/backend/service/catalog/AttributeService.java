package com.example.backend.service.catalog;

import com.example.backend.dto.catalog.AttributeRequest;
import com.example.backend.entity.catalog.Attribute;
import com.example.backend.repository.catalog.AttributeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttributeService {

    private final AttributeRepository attributeRepository;

    public List<Attribute> getAll() {
        return attributeRepository.findAll();
    }

    public Attribute getById(Integer id) {
        return attributeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy thuộc tính"));
    }

    public Attribute create(AttributeRequest req) {
        String name = req.getName() != null ? req.getName().trim() : "";
        String code = req.getCode() != null ? req.getCode().trim().toLowerCase() : "";

        if (name.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên thuộc tính không được để trống!");
        if (code.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mã thuộc tính không được để trống!");
        if (!code.matches("^[a-z0-9_]*$"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mã chỉ được chứa chữ thường, số và dấu gạch dưới (_)");
        if (attributeRepository.existsByNameIgnoreCase(name))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Tên thuộc tính này đã tồn tại!");
        if (attributeRepository.existsByCodeIgnoreCase(code))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Mã thuộc tính này đã tồn tại!");

        Attribute entity = new Attribute();
        entity.setName(name);
        entity.setCode(code);
        return attributeRepository.save(entity);
    }

    public Attribute update(Integer id, AttributeRequest req) {
        Attribute entity = getById(id);
        String newName = req.getName() != null ? req.getName().trim() : "";

        if (newName.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên không được để trống!");
        if (!entity.getName().equalsIgnoreCase(newName) && attributeRepository.existsByNameIgnoreCase(newName))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Tên này đã tồn tại ở thuộc tính khác!");

        entity.setName(newName);
        return attributeRepository.save(entity);
    }

    public void delete(Integer id) {
        Attribute entity = getById(id);
        // TODO: check khong cho xoa neu attribute_values cua no dang duoc product_variant su dung
        attributeRepository.delete(entity);
    }
}