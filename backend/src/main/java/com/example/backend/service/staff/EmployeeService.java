package com.example.backend.service.staff;

import com.example.backend.dto.staff.EmployeeCreateRequest;
import com.example.backend.dto.staff.EmployeeUpdateRequest;
import com.example.backend.entity.Role;
import com.example.backend.entity.User;
import com.example.backend.entity.staff.Employee;
import com.example.backend.entity.staff.Position;
import com.example.backend.repository.RoleRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.repository.staff.EmployeeRepository;
import com.example.backend.repository.staff.PositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PositionRepository positionRepository;
    private final PasswordEncoder passwordEncoder;

    public Page<Employee> search(String q, Boolean status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return employeeRepository.search(q, status, pageable);
    }

    public Employee getById(Integer id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy nhân viên"));
    }

    public Employee getByUserId(Integer userId) {
        return employeeRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tài khoản này chưa được gán hồ sơ nhân viên"));
    }

    @Transactional
    public Employee create(EmployeeCreateRequest req) {
        String username = req.getUsername() != null ? req.getUsername().trim() : "";
        String password = req.getPassword() != null ? req.getPassword() : "";
        List<String> allowedRoles = List.of("ADMIN", "STAFF");

        if (username.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username không được để trống!");
        if (password.length() < 6)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mật khẩu phải từ 6 ký tự trở lên!");
        if (userRepository.existsByUsername(username))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username đã tồn tại!");
        if (req.getRole() == null || !allowedRoles.contains(req.getRole().toUpperCase()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vai trò phải là ADMIN hoặc STAFF!");

        Role role = roleRepository.findByCode(req.getRole().toUpperCase())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Chưa cấu hình role này trong DB"));

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setFullName(req.getFullName());
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        user.setRole(role);
        user.setStatus(true);
        User savedUser = userRepository.save(user);

        Employee employee = new Employee();
        employee.setCode("NV" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        employee.setUser(savedUser);
        employee.setStatus(true);

        if (req.getPositionId() != null) {
            Position position = positionRepository.findById(req.getPositionId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy chức vụ"));
            employee.setPosition(position);
        }

        return employeeRepository.save(employee);
    }

    @Transactional
    public Employee update(Integer id, EmployeeUpdateRequest req) {
        Employee employee = getById(id);
        User user = employee.getUser();

        if (req.getFullName() != null) user.setFullName(req.getFullName());
        if (req.getEmail() != null) user.setEmail(req.getEmail());
        if (req.getPhone() != null) user.setPhone(req.getPhone());
        userRepository.save(user);

        if (req.getPositionId() != null) {
            Position position = positionRepository.findById(req.getPositionId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy chức vụ"));
            employee.setPosition(position);
        }
        if (req.getStatus() != null) {
            employee.setStatus(req.getStatus());
            user.setStatus(req.getStatus());
            userRepository.save(user);
        }

        return employeeRepository.save(employee);
    }
}