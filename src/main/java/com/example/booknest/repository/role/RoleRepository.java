package com.example.booknest.repository.role;

import com.example.booknest.model.Role;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface RoleRepository extends JpaRepository<Role, Long>,
        JpaSpecificationExecutor<Role> {
    @Query("from Role r where r.name in :rolesList")
    Set<Role> findAllByNameContaining(List<Role.RoleName> rolesList);
}
