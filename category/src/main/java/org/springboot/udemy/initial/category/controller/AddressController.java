package org.springboot.udemy.initial.category.controller;

import com.embarkx.ecommerce.model.User;
import com.embarkx.ecommerce.payload.AddressDTO;
import com.embarkx.ecommerce.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AddressController {

    @Autowired
    AuthUtil authUtil;

//    “Spring, please inject me something that implements AddressService(interface).”
//    InCase multiple implementations are there use (@Primary, @Qualifier)
    @Autowired
    AddressService addressService;

    @PostMapping("/address")
    public ResponseEntity<AddressDTO> createAddress(@Valid @RequestBody AddressDTO addressDTO){
        User user = authUtil.loggesInUser();
        AddressDTO savedAddressDTO = addressService.createAddress(addressDTO,user);
        return new ResponseEntity<>(savedAddressDTO, HttpStatus.CREATED);
    }

    @GetMapping("/address")
    public ResponseEntity<List<AddressDTO>> getAddress(){
        List<AddressDTO> addressDTOS = addressService.getAllAddress();
        return new ResponseEntity<>(addressDTOS, HttpStatus.OK);
    }

    @GetMapping("/address/{addressID}")
    public ResponseEntity<AddressDTO> getAddressById(@PathVariable Long addressID){
        AddressDTO addressDTO = addressService.getAddressById(addressID);
        return new ResponseEntity<>(addressDTO, HttpStatus.OK);
    }

    @GetMapping("/user/address")
    public ResponseEntity<List<AddressDTO>> getUserAddress(){
        User user = authUtil.loggesInUser();
        List<AddressDTO> addressDTOList = addressService.getUserAddress(user);
        return new ResponseEntity<>(addressDTOList, HttpStatus.OK);
    }

    @PutMapping("/address/{addressId}")
    public ResponseEntity<AddressDTO> updateAddres(@PathVariable Long addressId,@RequestBody AddressDTO addressDTO){
        AddressDTO updatedAddress = addressService.updateAddress(addressId,addressDTO);
        return new ResponseEntity<>(updatedAddress, HttpStatus.OK);
    }

    @DeleteMapping("/address/{addressId}")
    public ResponseEntity<String> deleteAddress(@PathVariable Long addressId){
        String status = addressService.deleteAddress(addressId);
        return ResponseEntity.ok(status);
    }

//    "return ResponseEntity.ok(status)"   Internally uses->
//    public static <T> ResponseEntity<T> ok(T body) {
//        return new ResponseEntity<>(body, HttpStatus.OK);
//    }
}
