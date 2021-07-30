package com.mediamarkt.task.service;


import com.mediamarkt.task.model.Category;
import com.mediamarkt.task.model.Product;
import com.mediamarkt.task.repository.ProductRepository;
import com.mediamarkt.task.util.BusinessException;
import com.mediamarkt.task.util.XLSUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final static Logger LOG = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryService categoryService;

    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    public void saveCSV(MultipartFile csvFile) {
            List<Product> products = XLSUtil.excelToProduct(csvFile);
            productRepository.saveAll(products);
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(Product product) {
        Product existingProduct = productRepository.findByName(product.getName())
                .orElseThrow(() -> new BusinessException(String.format("Product with name not found: %s", product.getName())));

        existingProduct.setName(product.getName());
        existingProduct.setCategory(product.getCategory());
        existingProduct.setOnlineStatus(product.getOnlineStatus());
        existingProduct.setShortDescription(product.getShortDescription());
        existingProduct.setLongDescription(product.getLongDescription());
        return productRepository.save(existingProduct);
    }

    public void deleteProductByName(String name) {
        productRepository.deleteByName(name);
    }

    public Product getProductByName(String name) {
        return productRepository.findByName(name)
                .orElseThrow(() -> new BusinessException(String.format("Product with name not found: %s", name)));
    }

    public Map<String, List<Long>>  getProductCategoriesPath(String productName) {
        Product product = productRepository.findByName(productName)
                .orElseThrow(() -> new BusinessException(String.format("Product with name not found: %s", productName)));

        List<Category> categories = categoryService.getCategories();

        List<String> productCategories = Arrays.asList(product.getCategory().split(";"));

        return productCategories.stream()
                .collect(Collectors.toMap(category -> category, category -> getCategoryPath(category, categories)));
    }

    private List<Long> getCategoryPath(String category, List<Category> categories) {
        LOG.info("Searching path for category {}", category);
        List<Long> categoryPath = new ArrayList<>();
        Long categoryValue = Long.parseLong(category);
        Map<Long, Long> categoryMap = categories.stream()
                .collect(Collectors.toMap(Category::getCategoryId, Category::getParentId));

        Long currentParent = categoryMap.get(categoryValue);

        if(currentParent == null) {
            return categoryPath;
        }

        Long intermediatePath;
        while(currentParent != null) {
            LOG.info("currentParent {}", currentParent);
            categoryPath.add(currentParent);
            intermediatePath = categoryMap.get(currentParent);
            if(currentParent.equals(intermediatePath)) {
                throw new BusinessException("Failed to get product path, categoryId should not be equal to parent");
            }
            currentParent = intermediatePath;
        }

        return categoryPath;
    }

}
