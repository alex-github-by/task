package com.mediamarkt.task.controller;

import com.mediamarkt.task.model.Product;
import com.mediamarkt.task.service.ProductService;
import com.mediamarkt.task.util.XLSUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final static Logger LOG = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    @PostMapping
    public ResponseEntity<Product> createCategory(@RequestBody Product product) {
        LOG.info("Product create called");
        return ResponseEntity.status(HttpStatus.OK).body(productService.createProduct(product));
    }

    @PutMapping
    public ResponseEntity<Product> updateCategory(@RequestBody Product product) {
        LOG.info("Product update called");
        return ResponseEntity.status(HttpStatus.OK).body(productService.updateProduct(product));
    }

    @DeleteMapping
    public void deleteCategory(String name) {
        LOG.info("Product delete called");
        productService.deleteProductByName(name);
    }

    @GetMapping
    public ResponseEntity<Product> getCategoryByCategoryId(String name) {
        LOG.info("Product get called");
        return ResponseEntity.status(HttpStatus.OK).body(productService.getProductByName(name));
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        LOG.info("upload-products called");

        if (XLSUtil.hasExcelFormat(file)) {
                productService.saveCSV(file);
                return ResponseEntity.status(HttpStatus.OK).body("Uploaded the file successfully: " + file.getOriginalFilename());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please upload a csv file!");
    }

    @GetMapping("/list")
    public ResponseEntity<List<Product>> getCategories() {
        return ResponseEntity.status(HttpStatus.OK).body(productService.getProducts());
    }

    @GetMapping("/category-path")
    public ResponseEntity<Map<String, List<Long>>> getCategoryPathForProduct(String productName) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.getProductCategoriesPath(productName));
    }
}
