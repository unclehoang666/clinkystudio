package com.example.backend.service.catalog;

import com.example.backend.dto.catalog.AttributeValueRequest;
import com.example.backend.entity.catalog.Attribute;
import com.example.backend.entity.catalog.AttributeValue;
import com.example.backend.repository.catalog.AttributeValueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttributeValueService {

    private final AttributeValueRepository attributeValueRepository;
    private final AttributeService attributeService;

    public List<AttributeValue> getByAttribute(Integer attributeId) {
        return attributeValueRepository.findByAttribute_Id(attributeId);
    }

    public AttributeValue getById(Integer id) {
        return attributeValueRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy giá trị thuộc tính"));
    }

    public AttributeValue create(AttributeValueRequest req) {
        if (req.getAttributeId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phải chọn thuộc tính!");

        String value = req.getValue() != null ? req.getValue().trim() : "";
        if (value.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Giá trị không được để trống!");

        Attribute attribute = attributeService.getById(req.getAttributeId());

        if (attributeValueRepository.existsByAttribute_IdAndValueIgnoreCase(attribute.getId(), value))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Giá trị này đã tồn tại trong thuộc tính đã chọn!");

        AttributeValue entity = new AttributeValue();
        entity.setAttribute(attribute);
        entity.setValue(value);
        return attributeValueRepository.save(entity);
    }

    public AttributeValue update(Integer id, AttributeValueRequest req) {
        AttributeValue entity = getById(id);
        String newValue = req.getValue() != null ? req.getValue().trim() : "";

        if (newValue.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Giá trị không được để trống!");
        if (!entity.getValue().equalsIgnoreCase(newValue) &&
                attributeValueRepository.existsByAttribute_IdAndValueIgnoreCase(entity.getAttribute().getId(), newValue))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Giá trị này đã tồn tại trong thuộc tính này!");

        entity.setValue(newValue);
        return attributeValueRepository.save(entity);
    }

    public void delete(Integer id) {
        AttributeValue entity = getById(id);
        // TODO: check khong cho xoa neu dang duoc product_variant su dung
        attributeValueRepository.delete(entity);
    }
}