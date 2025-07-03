package com.nhanab.demosecurity.repository;

import com.nhanab.demosecurity.entity.ERole;
import com.nhanab.demosecurity.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository  extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
