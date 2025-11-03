package com.riwi.mobile_fix.service.impl;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.riwi.mobile_fix.dto.CreateUserRequest;
import com.riwi.mobile_fix.exception.ConflictException;
import com.riwi.mobile_fix.exception.ResourceNotFoundException;
import com.riwi.mobile_fix.model.Role;
import com.riwi.mobile_fix.model.UserModel;
import com.riwi.mobile_fix.repository.UserRepository;
import com.riwi.mobile_fix.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserModel createUser(CreateUserRequest request) {
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("Username already exists");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already exists");
        }

        UserModel user = new UserModel();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setActive(true);

        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserModel> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public UserModel getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public UserModel getUserByUsername(String username) {
        UserModel user = userRepository.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with username: " + username);
        }
        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserModel> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }

    @Override
    public UserModel updateUser(Long id, CreateUserRequest request) {
        UserModel user = getUserById(id);

        // Check if username conflicts with another user
        if (userRepository.existsByUsername(request.getUsername())) {
            UserModel existing = userRepository.findByUsername(request.getUsername());
            if (!existing.getId().equals(id)) {
                throw new ConflictException("Username already exists");
            }
        }

        // Check if email conflicts with another user
        if (userRepository.existsByEmail(request.getEmail())) {
            UserModel existing = userRepository.findByEmail(request.getEmail());
            if (!existing.getId().equals(id)) {
                throw new ConflictException("Email already exists");
            }
        }

        user.setUsername(request.getUsername());
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }
        user.setRole(request.getRole());
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());

        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        UserModel user = getUserById(id);
        userRepository.delete(user);
    }
}
