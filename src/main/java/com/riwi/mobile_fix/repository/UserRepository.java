package com.riwi.mobile_fix.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.riwi.mobile_fix.model.Role;
import com.riwi.mobile_fix.model.UserModel;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Long> {
    UserModel findByUsername(String username);
    UserModel findByEmail(String email);
    List<UserModel> findByRole(Role role);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
