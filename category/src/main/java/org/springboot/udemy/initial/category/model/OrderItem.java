package org.springboot.udemy.initial.category.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderItemId;

    private Double discount;
    private double orderedProductPrice;
    private int quantity;


    @ManyToOne
    @JoinColumn(name="order_id")
    private Orders order;


//    Uni-directional relation
    @ManyToOne
    @JoinColumn(name = "product_id")
    private  Products product;
}
