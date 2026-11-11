package com.crud.product_demo.product;

import com.crud.product_demo.cloudinary.CloudinaryService;
import com.crud.product_demo.product.dto.ProductRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CloudinaryService cloudinaryService;

    public ProductService(
            ProductRepository productRepository,
            CloudinaryService cloudinaryService
    ) {
        this.productRepository = productRepository;
        this.cloudinaryService = cloudinaryService;
    }

    // CREATE PRODUCT
    public Products createProduct(
            ProductRequest request,
            MultipartFile image
    ) {

        if (productRepository.existsByProductCode(
                request.getProductCode())) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Product code already exists"
            );
        }

        Products product = new Products();

        mapRequestToProduct(request, product);

        // Upload image to Cloudinary
        if (image != null && !image.isEmpty()) {

            Map result = cloudinaryService.uploadImage(image);

            product.setImage((String) result.get("secure_url"));
            product.setImagePublicId(
                    (String) result.get("public_id")
            );
        }

        return productRepository.save(product);
    }

    // GET ALL PRODUCTS
    public List<Products> getAllProducts() {

        return productRepository.findAll();
    }

    // GET PRODUCT BY ID
    public Products getProductById(Long id) {

        return productRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Product not found"
                        )
                );
    }

    // UPDATE PRODUCT
    public Products updateProduct(
            Long id,
            ProductRequest request,
            MultipartFile image
    ) {

        Products product = getProductById(id);

        if (productRepository.existsByProductCodeAndIdNot(
                request.getProductCode(), id)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Product code already exists"
            );
        }

        mapRequestToProduct(request, product);

        // Replace old image if a new image is provided
        if (image != null && !image.isEmpty()) {

            Map result = cloudinaryService.uploadImage(image);

            String oldPublicId = product.getImagePublicId();

            product.setImage((String) result.get("secure_url"));
            product.setImagePublicId(
                    (String) result.get("public_id")
            );

            // Delete previous image
            cloudinaryService.deleteImage(oldPublicId);
        }

        return productRepository.save(product);
    }

    // DELETE PRODUCT
    public void deleteProduct(Long id) {

        Products product = getProductById(id);

        // Delete image from Cloudinary
        cloudinaryService.deleteImage(
                product.getImagePublicId()
        );

        // Delete product from PostgreSQL
        productRepository.delete(product);
    }

    // MAP REQUEST TO PRODUCT
    private void mapRequestToProduct(
            ProductRequest request,
            Products product
    ) {

        product.setProductName(request.getProductName());
        product.setProductCode(request.getProductCode());
        product.setDescription(request.getDescription());
        product.setCategory(request.getCategory());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());

        String status = request.getStatus();

        if (status != null && !status.isBlank()) {

            if (!status.equals("ACTIVE")
                    && !status.equals("INACTIVE")) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Status must be ACTIVE or INACTIVE"
                );
            }

            product.setStatus(status);
        }
    }
}

