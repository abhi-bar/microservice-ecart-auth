package org.springboot.udemy.initial.category.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {
    private Long addressId;
    private String street;
//    private String buildingName;
    private String city;
    private String state;
    private String country;
    private int pinCode;
    private String addressLine;

//    No 'User' for recursive loop
}
