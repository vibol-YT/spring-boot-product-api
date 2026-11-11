
package com.crud.product_demo.product;

import com.crud.product_demo.product.dto.ProductRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = {
        "http://localhost:5173"
})
@Tag(name = "Product API", description = "Product management API")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // CREATE PRODUCT
    @Operation(
            summary = "Create Product",
            description = "Create a product with an optional image upload"
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Products> createProduct(

            @Valid
            @RequestPart("product")
            @Parameter(
                    description = "Product information in JSON format",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ProductRequest.class)
                    )
            )
            ProductRequest request,

            @RequestPart(value = "image", required = false)
            MultipartFile image

    ) {

        Products product = productService.createProduct(request, image);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(product);
    }


    // GET ALL PRODUCTS
    @Operation(summary = "Get All Products")
    @GetMapping
    public ResponseEntity<List<Products>> getAllProducts() {

        return ResponseEntity.ok(
                productService.getAllProducts()
        );
    }


    // GET PRODUCT BY ID
    @Operation(summary = "Get Product By ID")
    @GetMapping("/{id}")
    public ResponseEntity<Products> getProductById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                productService.getProductById(id)
        );
    }


    // UPDATE PRODUCT
    @Operation(
            summary = "Update Product",
            description = "Update product information and optionally upload a new image"
    )
    @PutMapping(
            value = "/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Products> updateProduct(

            @PathVariable Long id,

            @Valid
            @RequestPart("product")
            @Parameter(
                    description = "Updated product information in JSON format",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ProductRequest.class)
                    )
            )
            ProductRequest request,

            @RequestPart(value = "image", required = false)
            MultipartFile image

    ) {

        Products product = productService.updateProduct(
                id,
                request,
                image
        );

        return ResponseEntity.ok(product);
    }


    // DELETE PRODUCT
    @Operation(summary = "Delete Product")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteProduct(
            @PathVariable Long id
    ) {

        productService.deleteProduct(id);

        return ResponseEntity.ok(
                Map.of("message", "Product deleted successfully")
        );
    }
}
