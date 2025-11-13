package com.projet.rest_benchmark.repository;

import com.projet.rest_benchmark.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RepositoryRestResource(path = "items", collectionResourceRel = "items")
public interface ItemRepository extends JpaRepository<Item, Long> {
    
    Page<Item> findByCategoryId(Long categoryId, Pageable pageable);
    
    @Query("SELECT i FROM Item i WHERE i.category.id = :categoryId")
    Page<Item> findAllByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);
    
    @Query("SELECT i FROM Item i JOIN FETCH i.category WHERE i.id = :id")
    Optional<Item> findByIdWithCategory(@Param("id") Long id);
    
    @Query("SELECT i FROM Item i")
    Page<Item> findAllItems(Pageable pageable);
}

