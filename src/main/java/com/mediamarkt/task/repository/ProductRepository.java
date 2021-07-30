package com.mediamarkt.task.repository;

import com.mediamarkt.task.model.Product;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Transactional
public interface ProductRepository extends CrudRepository<Product, Long> {

    @Override
    List<Product> findAll();

    Optional<Product> findByName(String name);

    void deleteByName(String name);
}
