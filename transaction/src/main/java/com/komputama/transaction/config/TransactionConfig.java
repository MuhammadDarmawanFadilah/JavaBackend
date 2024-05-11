package com.komputama.transaction.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import com.komputama.transaction.model.Item;
import com.komputama.transaction.repository.ItemRepository;

@Configuration
@EnableTransactionManagement
public class TransactionConfig {
    @Autowired
    private ItemRepository itemRepository; 

    @PostConstruct
    @Lazy
    @Transactional
    public void insertInitialItems() {
        itemRepository.save(new Item("Item1", 10.00, "Description1"));
        itemRepository.save(new Item("Item2", 15.00, "Description2"));
        itemRepository.save(new Item("Item3", 20.00, "Description3"));
        itemRepository.save(new Item("Item4", 25.00, "Description4"));
        itemRepository.save(new Item("Item5", 30.00, "Description5"));
    }
}
