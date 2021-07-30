package com.mediamarkt.task.repository;

import com.mediamarkt.task.model.Category;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface CategoryRepository extends CrudRepository<Category, Long> {

    @Override
    List<Category> findAll();

    Optional<Category> findByCategoryId(long categoryId);

    void deleteByCategoryId(long categoryId);
}
