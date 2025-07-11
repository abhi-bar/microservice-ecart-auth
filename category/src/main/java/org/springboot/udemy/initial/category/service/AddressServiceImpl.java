package org.springboot.udemy.initial.category.service;

import com.embarkx.ecommerce.exception.ResourceNotFoundException;
import com.embarkx.ecommerce.model.Address;
import com.embarkx.ecommerce.model.User;
import com.embarkx.ecommerce.payload.AddressDTO;
import com.embarkx.ecommerce.repository.AddressRepo;
import com.embarkx.ecommerce.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService{

    @Autowired
    AddressRepo addressRepo;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public AddressDTO createAddress(AddressDTO addressDTO, User user) {
        Address address = modelMapper.map(addressDTO, Address.class);
        address.setUser(user);
        List<Address> addressList = user.getAddresses();
        addressList.add(address);
        user.setAddresses(addressList);
        Address savedAddress = addressRepo.save(address);
        return modelMapper.map(savedAddress, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAllAddress() {
        List<Address> allAddress = addressRepo.findAll();
        return allAddress.stream()
                .map(address-> modelMapper.map(address, AddressDTO.class))
                .toList();
    }

    @Override
    public AddressDTO getAddressById(Long addressID) {
//        reserve getById for cases when it is certain that the entity exist in DB
//        OR
//        null checks are made before hand so getById si safe to use

//        By default findById returns optional, if below method not suitable use address.get()
        Address address = addressRepo.findById(addressID)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressID));
        return modelMapper.map(address, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getUserAddress(User user) {
        List<Address> addresses = user.getAddresses();
        return addresses.stream()
                .map(address -> modelMapper.map(address, AddressDTO.class))
                .toList();
    }

    @Override
    public AddressDTO updateAddress(Long addressId, AddressDTO addressDTO) {
        Address address = addressRepo.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));

        address.setCity(addressDTO.getCity());
        address.setPinCode(addressDTO.getPinCode());
        address.setState(addressDTO.getState());
        address.setCountry(addressDTO.getCountry());
        address.setAddressLine(addressDTO.getAddressLine());

        Address updatedAddress = addressRepo.save(address);

        User user = address.getUser();
        user.getAddresses().removeIf(address1 -> address1.getAddressId().equals(addressId));
        user.getAddresses().add(updatedAddress);
        userRepository.save(user);

        return modelMapper.map(address, AddressDTO.class);
    }

    @Override
    public String deleteAddress(Long addressID) {
        Address address = addressRepo.findById(addressID)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressID));

//        1st delete the address from the user associated
        User user = address.getUser();
        user.getAddresses().removeIf(address1->address.getAddressId().equals(addressID));
//        Save the user
        userRepository.save(user);

//        Delete the address
        addressRepo.delete(address);

        return "Address deleted successfully with addressId: " + addressID;
    }
}
