package com.example.salooniveryvells.Service.Impl;


import com.example.salooniveryvells.Advisor.ResourceNotFoundException;
import com.example.salooniveryvells.Dto.ProductDTO;
import com.example.salooniveryvells.Dto.ResponseDTO;
import com.example.salooniveryvells.Entity.Product;
import com.example.salooniveryvells.Entity.User;
import com.example.salooniveryvells.Repo.ProductRepository;
import com.example.salooniveryvells.Repo.UserRepository;
import com.example.salooniveryvells.Service.ProductService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ResponseDTO addProduct(ProductDTO productDTO) {
        if (productRepository.existsById(productDTO.getProductId())) {
            return new ResponseDTO(400, "Product already exists with id: " + productDTO.getProductId(), null);
        }

        // Fetch the service provider (User) from the database
        User serviceProvider = userRepository.findById(productDTO.getServiceProviderId())
                .orElseThrow(() -> new ResourceNotFoundException("Service provider not found with id: " + productDTO.getServiceProviderId()));

        // Map ProductDTO to Product entity
        Product product = modelMapper.map(productDTO, Product.class);
        product.setServiceProvider(serviceProvider); // Set the service provider

        productRepository.save(product);
        return new ResponseDTO(200, "Product added successfully", productDTO);
    }

    @Override
    public ResponseDTO getAllProducts() {
        List<ProductDTO> productList = modelMapper.map(productRepository.findAll(),
                new TypeToken<List<ProductDTO>>() {}.getType());
        return new ResponseDTO(200, "Products retrieved successfully", productList);
    }

    @Override
    public ResponseDTO getProductById(int productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
        ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);
        productDTO.setServiceProviderId(product.getServiceProvider().getUserId()); // Set service provider ID
        return new ResponseDTO(200, "Product retrieved successfully", productDTO);
    }

    @Override
    public ResponseDTO updateProduct(int productId, ProductDTO productDTO) {
        if (!productRepository.existsById(productId)) {
            return new ResponseDTO(404, "Product not found with id: " + productId, null);
        }

        // Fetch the service provider (User) from the database
        User serviceProvider = userRepository.findById(productDTO.getServiceProviderId())
                .orElseThrow(() -> new ResourceNotFoundException("Service provider not found with id: " + productDTO.getServiceProviderId()));

        // Map ProductDTO to Product entity
        Product product = modelMapper.map(productDTO, Product.class);
        product.setProductId(productId); // Ensure the ID is preserved
        product.setServiceProvider(serviceProvider); // Set the service provider

        productRepository.save(product);
        return new ResponseDTO(200, "Product updated successfully", productDTO);
    }

    @Override
    public ResponseDTO deleteProduct(int productId) {
        if (!productRepository.existsById(productId)) {
            return new ResponseDTO(404, "Product not found with id: " + productId, null);
        }
        productRepository.deleteById(productId);
        return new ResponseDTO(200, "Product deleted successfully", null);
    }
}