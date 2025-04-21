package com.example.salooniveryvells.Service;

import com.example.salooniveryvells.Dto.ProductDTO;
import com.example.salooniveryvells.Dto.ResponseDTO;

public interface ProductService {
    ResponseDTO addProduct(ProductDTO productDTO);
    ResponseDTO getAllProducts();
    ResponseDTO getProductById(int productId);
    ResponseDTO updateProduct(int productId, ProductDTO productDTO);
    ResponseDTO deleteProduct(int productId);
}