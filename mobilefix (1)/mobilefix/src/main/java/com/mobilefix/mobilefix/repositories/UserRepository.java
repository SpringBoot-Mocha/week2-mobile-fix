package com.mobilefix.mobilefix.repositories;

import com.mobilefix.mobilefix.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Integer> {

UserModel findByUsername(String username);
UserModel findUserbyEmail(String email);
// el resto de los metodos estan ya por defecto , por ejemplo el getall y por id, delete por id
}

