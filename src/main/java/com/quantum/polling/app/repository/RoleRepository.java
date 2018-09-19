package com.quantum.polling.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.quantum.polling.app.model.Role;
import com.quantum.polling.app.model.RoleName;

public interface RoleRepository extends JpaRepository<Role, Long> {

	Optional<Role> findByName(RoleName roleName);
}
