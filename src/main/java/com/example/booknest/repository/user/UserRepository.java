package com.example.booknest.repository.user;

import com.example.booknest.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long>,
        JpaSpecificationExecutor<User> {
    @Query("from User u left join fetch u.roles where u.email = :email")
    Optional<User> findByEmail(String email);
}
