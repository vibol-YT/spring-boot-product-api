package com.crud.product_demo.product;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository
        extends JpaRepository<Products, Long> {

    boolean existsByProductCode(String productCode);

    boolean existsByProductCodeAndIdNot(
            String productCode,
            Long id
    );
}


