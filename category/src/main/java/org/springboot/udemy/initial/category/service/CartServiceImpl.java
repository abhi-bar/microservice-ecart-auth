package org.springboot.udemy.initial.category.service;

import com.embarkx.ecommerce.controller.AuthUtil;
import com.embarkx.ecommerce.exception.APIException;
import com.embarkx.ecommerce.exception.ResourceNotFoundException;
import com.embarkx.ecommerce.model.CartItem;
import com.embarkx.ecommerce.model.Carts;
import com.embarkx.ecommerce.model.Products;
import com.embarkx.ecommerce.payload.CartDTO;
import com.embarkx.ecommerce.payload.CartItemDTO;
import com.embarkx.ecommerce.payload.ProductDTO;
import com.embarkx.ecommerce.repository.CartItemRepo;
import com.embarkx.ecommerce.repository.CartRepo;
import com.embarkx.ecommerce.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class CartServiceImpl implements CartService{

    @Autowired
    private CartRepo cartRepo;

    @Autowired
    private CartItemRepo cartItemRepo;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    ModelMapper modelMapper;


    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {
        Carts cart = createCart();

        Products products = productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product","productId", productId));

        CartItem cartItem = cartItemRepo.findCartItemByProductAndCart_CartId(cart.getCartId(),productId);

        if(cartItem!=null){
            throw new APIException("Product" + products.getProductName() + " already exist");
        }

        if(products.getQuantity() == 0){
            throw new APIException(products.getProductName() + " is not available");
        }

        if (products.getQuantity() < quantity) {
            throw new APIException("Please, make an order of the " + products.getProductName()
                    + " less than or equal to the quantity " + products.getQuantity() + ".");
        }

        CartItem newCartItem = new CartItem();

        newCartItem.setProduct(products);
        newCartItem.setCart(cart);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(products.getDiscount());
        newCartItem.setProductPrice(products.getPrice());

//        New Item save in cart
        cartItemRepo.save(newCartItem);

        products.setQuantity(products.getQuantity()-quantity);
//        repo.save() used since @Transactional not used in this
        productRepository.save(products);

//        Cart saved with details
        cart.setTotalPrice(cart.getTotalPrice() + (products.getSpecialPrice() * quantity));
        cartRepo.save(cart);



//      Updating cartDTO
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
//        Since new product is added to cart
        List<CartItem> cartItemList = cart.getCartItems();
        Stream<ProductDTO> productDTOStream = cartItemList.stream().map(item->{
            ProductDTO productDTO = modelMapper.map(item.getProduct(), ProductDTO.class);
            productDTO.setQuantity(item.getQuantity());
            return productDTO;
        });

        cartDTO.setProducts(productDTOStream.toList());

        return cartDTO;
    }

    private Carts createCart() {
        Carts userCart = cartRepo.findCartByEmail(authUtil.loggedInEmail());
        if(userCart!=null){
            return userCart;
        }

        Carts carts = new Carts();
        carts.setTotalPrice(0.00);
        carts.setUser(authUtil.loggesInUser());
        Carts newCart = cartRepo.save(carts);

        return newCart;
    }

    @Override
    public List<CartDTO> getAllCarts() {

        List<Carts> carts = cartRepo.findAll();

        if(carts.isEmpty()){
            throw new APIException("No cart service");
        }

        List<CartDTO> cartDTOS = carts.stream().map(cart->{
            CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

            List<ProductDTO> products = cart.getCartItems().stream().map(cartItem -> {
                ProductDTO productDTO = modelMapper.map(cartItem.getProduct(), ProductDTO.class);
                productDTO.setQuantity(cartItem.getQuantity()); // Set the quantity from CartItem
                return productDTO;
            }).collect(Collectors.toList());


            cartDTO.setProducts(products);

            return  cartDTO;
        }).toList();

        return cartDTOS;
    }

    @Override
    public CartDTO getCart(String emailId, Long cartId) {
        Carts cart = cartRepo.findCartByEmailAndCartId(emailId, cartId);
        if (cart == null){
            throw new ResourceNotFoundException("Cart", "cartId", cartId);
        }
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

//        Cart entity does not have a mapping to product class

        //Convert each CartItem to a ProductDTO with its quantity
        List<ProductDTO> products = cart.getCartItems().stream()
                .map(cartItem -> {
                    ProductDTO productDTO = modelMapper.map(cartItem.getProduct(), ProductDTO.class);
                    productDTO.setQuantity(cartItem.getQuantity()); // set quantity specific to this cart
                    return productDTO;
                })
                .toList();

        //Attach product list to the cart DTO
        cartDTO.setProducts(products);
        return cartDTO;
    }

    @Transactional
    @Override
    public CartDTO updateProductQuantityInCart(Long productId, Integer quantity) {

        String emailId = authUtil.loggedInEmail();

        Carts cart = cartRepo.findCartByEmail(emailId);
        if (cart == null) {
            throw new ResourceNotFoundException("Cart", "emailId", emailId);
        }

        Long cartId = cart.getCartId();


        Products product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        if (product.getQuantity() == 0) {
            throw new APIException(product.getProductName() + " is not available");
        }

        if (product.getQuantity() < quantity) {
            throw new APIException("Please, make an order of the " + product.getProductName()
                    + " less than or equal to the quantity " + product.getQuantity() + ".");
        }

        CartItem cartItem = cartItemRepo.findCartItemByProductAndCart_CartId(cartId, productId);

//        Needs to be changed to create a new cart
        if (cartItem == null) {
            throw new APIException("Product " + product.getProductName() + " not available in the cart!!!");
        }

        // Calculate new quantity
        int newQuantity = cartItem.getQuantity() + quantity;

        // Validation to prevent negative quantities
        if (newQuantity < 0) {
            throw new APIException("The resulting quantity cannot be negative.");
        }

        if (newQuantity == 0){
            // delete cart item
            cartItemRepo.deleteById(cartItem.getCartItemId());
            // restore product stock
            product.setQuantity(product.getQuantity() + cartItem.getQuantity());
            // adjust total price
            cart.setTotalPrice(cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity()));
        } else {
            cartItem.setProductPrice(product.getSpecialPrice());
            cartItem.setQuantity(newQuantity);
            cartItem.setDiscount(product.getDiscount());
            cart.setTotalPrice(cart.getTotalPrice() + (cartItem.getProductPrice() * quantity));
//            Quantity can work both ways
            product.setQuantity(product.getQuantity() - quantity);
            productRepository.save(product);
            cartRepo.save(cart);
        }


        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        List<CartItem> cartItems = cart.getCartItems();

        List<ProductDTO> products = cartItems.stream()
                .map(item -> {
                    ProductDTO dto = modelMapper.map(item.getProduct(), ProductDTO.class);
                    dto.setQuantity(item.getQuantity());
                    return dto;
                }).toList();
        cartDTO.setProducts(products);

        return cartDTO;
    }

    @Transactional
    @Override
    public String deleteProductFromCart(Long cartId, Long productId) {
        Carts cart = cartRepo.findById(Math.toIntExact(cartId))
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));

        CartItem cartItem = cartItemRepo.findCartItemByProductAndCart_CartId(cartId, productId);

        if (cartItem == null) {
            throw new ResourceNotFoundException("Product", "productId", productId);
        }


        cart.setTotalPrice(cart.getTotalPrice() -
                (cartItem.getProductPrice() * cartItem.getQuantity()));

        cartItemRepo.deleteCartItemsByCartAndProduct(cartId, productId);
        cartRepo.save(cart);

        return "Product " + cartItem.getProduct().getProductName() + " removed from the cart !!!";
    }

//    Used for updating the price if product already there
    @Transactional
    @Override
    public void updateProductInCarts(Long cartId, Long productId) {
        Carts cart = cartRepo.findById(Math.toIntExact(cartId))
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));

        Products product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        CartItem cartItem = cartItemRepo.findCartItemByProductAndCart_CartId(cartId, productId);

        if (cartItem == null) {
            throw new APIException("Product " + product.getProductName() + " not available in the cart!!!");
        }

        double cartPrice = cart.getTotalPrice()
                - (cartItem.getProductPrice() * cartItem.getQuantity());

        cartItem.setProductPrice(product.getSpecialPrice());

        cart.setTotalPrice(cartPrice
                + (cartItem.getProductPrice() * cartItem.getQuantity()));

        cartItemRepo.save(cartItem);
    }

    @Transactional
    @Override
    public String createOrUpdateCartWithItems(List<CartItemDTO> cartItems) {
        // Get user's email
        String emailId = authUtil.loggedInEmail();

        // Check if an existing cart is available or create a new one
        Carts existingCart = cartRepo.findCartByEmail(emailId);
        if (existingCart == null) {
            existingCart = new Carts();
            existingCart.setTotalPrice(0.00);
            existingCart.setUser(authUtil.loggesInUser());
            existingCart = cartRepo.save(existingCart);
        } else {
            // Clear all current items in the existing cart
            cartItemRepo.deleteCartItemsByCart_CartId(existingCart.getCartId());
        }

        double totalPrice = 0.00;

        // Process each item in the request to add to the cart
        for (CartItemDTO cartItemDTO : cartItems) {
            Long productId = cartItemDTO.getProductId();
            Integer quantity = cartItemDTO.getQuantity();

            // Find the product by ID
            Products product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

            // Directly update product stock and total price
            // product.setQuantity(product.getQuantity() - quantity);
            totalPrice += product.getSpecialPrice() * quantity;

            // Create and save cart item
            CartItem cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setCart(existingCart);
            cartItem.setQuantity(quantity);
            cartItem.setProductPrice(product.getSpecialPrice());
            cartItem.setDiscount(product.getDiscount());
            cartItemRepo.save(cartItem);
        }

        // Update the cart's total price and save
        existingCart.setTotalPrice(totalPrice);
        cartRepo.save(existingCart);
        return "Cart created/updated with the new items successfully";
    }
}
