package com.example.EMTECH_ERP.BACKEND.AuthenticationModule.Roles;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(String name);
  Optional<Role> findById(Long id);

    @Override
    List<Role> findAll(Sort sort);


//    List<Role> findAll();
}
