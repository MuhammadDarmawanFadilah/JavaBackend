package com.komputama.transaction.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import com.komputama.transaction.model.Transaction;
import com.komputama.transaction.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.web.bind.annotation.*; 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/cart")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    // Add more items to cart
    @PostMapping("/addcountcart")
    public ResponseEntity<Transaction> addItemToCart(@RequestParam Long itemId, @RequestParam int quantity, @RequestParam String tokenSession) {
        Transaction transaction = transactionService.getCartItemByTokenSession(tokenSession);
        if (transaction == null) {
            logger.error("Transaction not found: tokenSession={}", tokenSession);
            return ResponseEntity.notFound().build();
        }
        logger.info("Request to add item to cart: itemId={}, quantity={}", itemId, quantity);
        transaction = transactionService.addItemToCart(itemId, quantity, transaction);
        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
    }

    // Remove item from cart
    @DeleteMapping("/removecart")
    public ResponseEntity<Void> removeItemFromCart(@RequestParam Long itemId, @RequestParam String tokenSession) {
        logger.info("Request to remove item from cart: id={}, tokenSession={}", itemId, tokenSession);
        try {
            transactionService.removeItemFromCartByItemId(itemId, tokenSession);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Failed to remove Chartitem from Transaction: id={}, tokenSession={}", itemId, tokenSession, e);
            return ResponseEntity.notFound().build();
        }
    }

    // Finalize cart (calculate price)
    @PostMapping("/total")
    public ResponseEntity<Double> getTotalCartPrice(@RequestParam String tokenSession) {
        logger.info("Request to get total cart price");
        double total = transactionService.calculateTotalCartPrice(tokenSession);
        return ResponseEntity.ok(total);
    }
}
