package com.riwi.mobile_fix.service;

import java.util.List;

import com.riwi.mobile_fix.dto.CreateUserRequest;
import com.riwi.mobile_fix.model.Role;
import com.riwi.mobile_fix.model.UserModel;

public interface UserService {
    
    UserModel createUser(CreateUserRequest request);
    
    List<UserModel> getAllUsers();
    
    UserModel getUserById(Long id);
    
    UserModel getUserByUsername(String username);
    
    List<UserModel> getUsersByRole(Role role);
    
    UserModel updateUser(Long id, CreateUserRequest request);
    
    void deleteUser(Long id);
}
