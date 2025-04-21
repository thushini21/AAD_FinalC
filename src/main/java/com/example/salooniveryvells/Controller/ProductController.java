package com.example.salooniveryvells.Controller;


import com.example.salooniveryvells.Dto.ProductDTO;
import com.example.salooniveryvells.Dto.ResponseDTO;
import com.example.salooniveryvells.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> addProduct(@RequestBody ProductDTO productDTO) {
        ResponseDTO response = productService.addProduct(productDTO);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }

    @GetMapping
    public ResponseEntity<ResponseDTO> getAllProducts() {
        ResponseDTO response = productService.getAllProducts();
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ResponseDTO> getProductById(@PathVariable int productId) {
        ResponseDTO response = productService.getProductById(productId);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }

    @PutMapping("/{productId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> updateProduct(@PathVariable int productId, @RequestBody ProductDTO productDTO) {
        ResponseDTO response = productService.updateProduct(productId, productDTO);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> deleteProduct(@PathVariable int productId) {
        ResponseDTO response = productService.deleteProduct(productId);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }
}