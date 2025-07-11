package org.springboot.udemy.initial.category.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @Email
    @Column(nullable = false)
    private String email;

    private LocalDate orderDate;
    private String orderStatus;
    private Double totalAmount;



    @OneToMany(mappedBy = "order", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private List<OrderItem> orderItem = new ArrayList<>();


//    Uni directional
    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address address;

//    Payment Not implemented
    @OneToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

}
