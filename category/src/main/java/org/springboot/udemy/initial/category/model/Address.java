package org.springboot.udemy.initial.category.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "address")
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @NotBlank
    @Size(min = 6,max = 6,message = "Pin code does not exist")
    private int pinCode;

    @NotBlank
    @Size(min = 1,max = 6,message = "Address cannot be larger")
    private String addressLine;

    @NotBlank
    @Size(min = 1,max = 6,message = "Insufficient length for city")
    private String city;

    @NotBlank
    @Size(min = 1,max = 6,message = "Insufficient length for state")
    private String state;


    @NotBlank
    @Size(min = 1,max = 6,message = "Insufficient length for country")
    private String country;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    public Address(String state, int pinCode, String addressLine, String city, String country) {
        this.state = state;
        this.pinCode = pinCode;
        this.addressLine = addressLine;
        this.city = city;
        this.country = country;
    }
}
