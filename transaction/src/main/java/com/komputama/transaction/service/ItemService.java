package com.komputama.transaction.service;

import com.komputama.transaction.model.CartItem;
import com.komputama.transaction.model.Item;
import com.komputama.transaction.repository.CartItemRepository;
import com.komputama.transaction.repository.ItemRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class ItemService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private CartItemRepository cartItemRepository;

    public List<Item> getAllItems() {
        logger.info("Fetching all items");
        return itemRepository.findAll();
    }

    public Item getItemById(Long id) {
        logger.info("Fetching item by id: {}", id);
        return itemRepository.findById(id).orElse(null);
    }

    public CartItem addItem(Long itemId, int quantity) {
        logger.info("Adding item to cart: itemId={}, quantity={}", itemId, quantity);
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        Item item = getItemById(itemId);
        if (item == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found with id: " + itemId);
        }
        CartItem cartItem = new CartItem();
        cartItem.setItem(item);
        cartItem.setQuantity(quantity);
        return cartItemRepository.save(cartItem);
    }


}
