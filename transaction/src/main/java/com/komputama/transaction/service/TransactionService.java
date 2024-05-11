package com.komputama.transaction.service;

import com.komputama.transaction.model.CartItem;
import com.komputama.transaction.model.Item;
import com.komputama.transaction.model.Transaction;
import com.komputama.transaction.repository.CartItemRepository;
import com.komputama.transaction.repository.TransactionRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Arrays;

@Service
public class TransactionService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private ItemService itemService;

    public Transaction getCartItemByTokenSession(String session) {
        logger.info("Fetching CartItem by TokenSession: {}", session);
        return transactionRepository.findByTokenSession(session).orElse(null);
    }

    public Transaction createNewTransaction(String session, Item item) {
        logger.info("Create new Transaction for new TokenSession: {}", session);
        Transaction transaction = new Transaction();
        transaction.setTokenSession(session);
        transaction.setTimeInserted(LocalDateTime.now());
        logger.info("Create new cart item for new TokenSession: {}", session);
        CartItem newCartItem = new CartItem();
        newCartItem.setItem(item);
        newCartItem.setQuantity(1);
        transaction.setCartItems(Arrays.asList(newCartItem));
        newCartItem.setTransaction(transaction);
        transactionRepository.save(transaction);
        return transaction;
    }

    public Transaction addItemTransaction(Transaction transaction, Item item) {
        logger.info("Add new cart item for new TokenSession: {}", transaction.getTokenSession());
        CartItem newCartItem = new CartItem();
        newCartItem.setItem(item);
        newCartItem.setQuantity(1);
        newCartItem.setTransaction(transaction);
        transaction.getCartItems().add(newCartItem);
        transactionRepository.save(transaction);
        return transaction;
    }


    public CartItem addItem(Long itemId, int quantity) {
        logger.info("Adding item to cart: itemId={}, quantity={}", itemId, quantity);
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        Item item = itemService.getItemById(itemId);
        if (item == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found with id: " + itemId);
        }
        CartItem cartItem = new CartItem();
        cartItem.setItem(item);
        cartItem.setQuantity(quantity);
        return cartItemRepository.save(cartItem);
    }

    public void removeItemFromCartByItemId(Long itemId, String tokenSession) {
        Transaction transaction = getCartItemByTokenSession(tokenSession);
        CartItem cartItem = cartItemRepository.findByIdAndTransactionId(itemId, transaction);
        logger.info("Removing cart item from cart: itemId={}", cartItem);
        cartItemRepository.delete(cartItem);
    }

    public Transaction addItemToCart(Long itemId, int quantity, Transaction transaction) {
        //find item id
        logger.info("Adding item to cart: itemId={}, quantity={}", itemId, quantity);
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        Item item = itemService.getItemById(itemId);
        if (item == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found with id: " + itemId);
        }
        // find the CartItem in transactional
        CartItem existingCartItem = transaction.getCartItems().stream()
                .filter(cartItem -> cartItem.getId().equals(itemId))
                .findFirst()
                .orElse(null);

        // if exist change only quantity
        if (existingCartItem != null) {
            existingCartItem.setQuantity(quantity);
            cartItemRepository.save(existingCartItem);
        } else {
        // if not exist create new CartItem and add to transactional
            CartItem newCartItem = new CartItem();
            newCartItem.setItem(item);
            newCartItem.setQuantity(quantity);
            cartItemRepository.save(newCartItem);
            transaction.getCartItems().add(newCartItem);
            transactionRepository.save(transaction);
        }
        return transaction;
    }

    public void removeItemFromCart(Long cartItemId) {
        logger.info("Removing item from cart: cartItemId={}", cartItemId);
        cartItemRepository.deleteById(cartItemId);
    }

    public double calculateTotalCartPrice(String tokenSession) {
        Transaction transaction = getCartItemByTokenSession(tokenSession);
        logger.info("Calculating total cart price");
        return transaction.getCartItems().stream()
                .mapToDouble(cartItem -> cartItem.getItem().getPrice() * cartItem.getQuantity())
                .sum();
    }
}
