package com.mediamarkt.task.service;

import com.mediamarkt.task.model.Category;
import com.mediamarkt.task.repository.CategoryRepository;
import com.mediamarkt.task.util.BusinessException;
import com.mediamarkt.task.util.XLSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }

    public Category createCategory(Category category) {
        if(category.getCategoryId() == category.getParentId()) {
            throw new BusinessException("Category id should not be equal to its parent");
        }
        return categoryRepository.save(category);
    }

    public Category updateCategory(Category category) {
        if(category.getCategoryId() == category.getParentId()) {
            throw new BusinessException("Category id should not be equal to its parent");
        }
        Category existingCategory = categoryRepository.findByCategoryId(category.getCategoryId())
                .orElseThrow(() -> new BusinessException(String.format("Category with id not found: %s", category.getCategoryId())));

        existingCategory.setName(category.getName());
        existingCategory.setParentId(category.getParentId());
        return categoryRepository.save(existingCategory);
    }


    public void deleteCategoryByCategoryId(long categoryId) {
        categoryRepository.deleteByCategoryId(categoryId);
    }

    public Category getCategoryByCategoryId(long categoryId) {
        return categoryRepository.findByCategoryId(categoryId)
                .orElseThrow(() -> new BusinessException(String.format("Category with id not found: %s", categoryId)));
    }


    public void saveCSV(MultipartFile csvFile) {
            List<Category> categories = XLSUtil.excelToCategory(csvFile);
            categoryRepository.saveAll(categories);
    }
}
