package com.komputama.transaction.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import com.komputama.transaction.model.Item;
import com.komputama.transaction.model.Transaction;
import com.komputama.transaction.service.ItemService;
import com.komputama.transaction.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.web.bind.annotation.*; 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List; 

@RestController
@RequestMapping("/api/item")
public class ItemController {
    @Autowired
    private TransactionService transactionService;

    @Autowired
    private ItemService itemService;

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    // Get/Search item
    @GetMapping("/items")
    public ResponseEntity<List<Item>> getAllItems() {
        logger.info("Request to get all items");
        List<Item> items = itemService.getAllItems();
        return ResponseEntity.ok(items);
    }

    // For selected item, add item to cart
    @PostMapping("/additemcart")
    public ResponseEntity<Transaction> getItemById(@RequestParam Long itemId, @RequestParam String tokenSession) {
        logger.info("Request to get item by id: {}", itemId);
        Item item = itemService.getItemById(itemId);
        if (item == null) {
            logger.error("Item not found: id={}", itemId);
            return ResponseEntity.notFound().build();
        }
        Transaction transaction = transactionService.getCartItemByTokenSession(tokenSession);
        if (transaction == null) {
            transaction = transactionService.createNewTransaction(tokenSession, item);
        }else{
            transaction = transactionService.addItemTransaction(transaction, item);
        }
        return ResponseEntity.ok(transaction);
    }
}
