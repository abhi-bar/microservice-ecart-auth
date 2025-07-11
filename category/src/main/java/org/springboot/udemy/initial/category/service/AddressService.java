package org.springboot.udemy.initial.category.service;

import com.embarkx.ecommerce.model.User;
import com.embarkx.ecommerce.payload.AddressDTO;

import java.util.List;

public interface AddressService {
    AddressDTO createAddress(AddressDTO addressDTO, User user);

    List<AddressDTO> getAllAddress();

    AddressDTO getAddressById(Long addressID);

    List<AddressDTO> getUserAddress(User user);

    AddressDTO updateAddress(Long addressId, AddressDTO addressDTO);

    String deleteAddress(Long addressID);
}
