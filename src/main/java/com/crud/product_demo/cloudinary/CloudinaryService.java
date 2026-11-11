package com.crud.product_demo.cloudinary;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public Map uploadImage(MultipartFile file) {

        try {

            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException(
                        "Image file is required"
                );
            }

            return cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "products",
                            "resource_type", "image"
                    )
            );

        } catch (Exception e) {
            throw new RuntimeException(
                    "Image upload failed: " + e.getMessage()
            );
        }
    }

    public void deleteImage(String publicId) {

        if (publicId == null || publicId.isBlank()) {
            return;
        }

        try {

            cloudinary.uploader().destroy(
                    publicId,
                    ObjectUtils.emptyMap()
            );

        } catch (Exception e) {
            throw new RuntimeException(
                    "Image deletion failed: " + e.getMessage()
            );
        }
    }
}
