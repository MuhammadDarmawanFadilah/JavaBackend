package com.komputama.transaction.model;

import javax.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL)
    private List<CartItem> cartItems;

    @Column(name = "token_session")
    private String tokenSession;

    @Column(name = "time_inserted")
    private LocalDateTime timeInserted;

    public void setTokenSession(String session) {
        this.tokenSession = session;
    }

    public void setTimeInserted(LocalDateTime now) {
        this.timeInserted = now;
    }

    public void setCartItems(List<CartItem> asList) {
        this.cartItems = asList;
    }

    public List<CartItem> getCartItems() {
        return this.cartItems;
    }

    public String getTokenSession() {
        return this.tokenSession;
    }
}
