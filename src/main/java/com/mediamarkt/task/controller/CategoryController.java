package com.mediamarkt.task.controller;

import com.mediamarkt.task.model.Category;
import com.mediamarkt.task.service.CategoryService;
import com.mediamarkt.task.util.XLSUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {
    private final static Logger LOG = LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    private CategoryService categoryService;


    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        LOG.info("upload-categories called");

        if (XLSUtil.hasExcelFormat(file)) {
                categoryService.saveCSV(file);
                return ResponseEntity.status(HttpStatus.OK).body("Uploaded the file successfully: " + file.getOriginalFilename());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please upload a csv file!");
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        LOG.info("Category create called");
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.createCategory(category));
    }

    @PutMapping
    public ResponseEntity<Category> updateCategory(@RequestBody Category category) {
        LOG.info("Category update called");
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.updateCategory(category));
    }

    @DeleteMapping
    public void deleteCategory(long categoryId) {
        LOG.info("Category delete called");
        categoryService.deleteCategoryByCategoryId(categoryId);
    }

    @GetMapping
    public ResponseEntity<Category> getCategoryByCategoryId(long categoryId) {
        LOG.info("Category get called");
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.getCategoryByCategoryId(categoryId));
    }

    @GetMapping("/list")
    public ResponseEntity<List<Category>> getCategories() {
        LOG.info("Category get all called");
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.getCategories());
    }
}
