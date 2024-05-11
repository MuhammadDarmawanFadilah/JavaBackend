package com.komputama.transaction.repository;

import com.komputama.transaction.model.Transaction;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByTokenSession(String session);

}