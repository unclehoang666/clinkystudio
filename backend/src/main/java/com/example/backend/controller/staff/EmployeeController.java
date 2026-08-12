package com.example.backend.controller.staff;

import com.example.backend.dto.staff.EmployeeCreateRequest;
import com.example.backend.dto.staff.EmployeeUpdateRequest;
import com.example.backend.entity.User;
import com.example.backend.entity.staff.Employee;
import com.example.backend.service.staff.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    private User currentUser(Authentication authentication) {
        return (User) authentication.getPrincipal();
    }

    @GetMapping
    public Page<Employee> list(@RequestParam(required = false) String q,
                                @RequestParam(required = false) Boolean status,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size) {
        return employeeService.search(q, status, page, size);
    }

    // Nhan vien/Admin dang nhap xem ho so cong viec cua chinh minh
    @GetMapping("/me")
    public Employee getMyProfile(Authentication authentication) {
        return employeeService.getByUserId(currentUser(authentication).getId());
    }

    @GetMapping("/{id}")
    public Employee getById(@PathVariable Integer id) {
        return employeeService.getById(id);
    }

    @PostMapping
    public ResponseEntity<Employee> create(@RequestBody EmployeeCreateRequest req) {
        return ResponseEntity.ok(employeeService.create(req));
    }

    @PutMapping("/{id}")
    public Employee update(@PathVariable Integer id, @RequestBody EmployeeUpdateRequest req) {
        return employeeService.update(id, req);
    }
}