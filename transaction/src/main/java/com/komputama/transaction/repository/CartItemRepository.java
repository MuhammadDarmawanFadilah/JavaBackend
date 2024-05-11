package com.komputama.transaction.repository;

import com.komputama.transaction.model.CartItem;
import com.komputama.transaction.model.Transaction;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    CartItem findByIdAndTransactionId(Long id, Transaction transaction);

}