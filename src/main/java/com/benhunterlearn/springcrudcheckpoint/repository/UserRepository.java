package com.benhunterlearn.springcrudcheckpoint.repository;

import com.benhunterlearn.springcrudcheckpoint.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
    User findUserByEmail(String email);
}
