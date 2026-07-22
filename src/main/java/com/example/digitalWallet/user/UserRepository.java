package com.example.digitalWallet.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * ตัวกลางคุยกับตาราง users
 * Spring Data JPA จะสร้าง implementation ให้อัตโนมัติจากชื่อเมธอด
 */
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
}
