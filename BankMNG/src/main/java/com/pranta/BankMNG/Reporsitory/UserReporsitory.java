package com.pranta.BankMNG.Reporsitory;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pranta.BankMNG.Entity.User;



public interface UserReporsitory extends JpaRepository<User,Long> {
    Boolean existsByEmail(String email);

    Boolean existsByAccountNumber(String accountNumber);  // Fixed the typo here

    User findByAccountNumber(String accountNumber);
}
