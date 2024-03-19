package com.example.Othellodifficult.base.adapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

// Adapter
public class BaseRepositoryAdapter<T> implements BaseRepository<T>{
    @Autowired
    private JpaRepository<T, Long> jpaRepository; // Adaptee

    @Override
    public List<T> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public Optional<T> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public void delete(T entity) {
        jpaRepository.delete(entity);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public void save(T entity) {
        jpaRepository.save(entity);
    }

    @Override
    public void saveAll(Collection<T> entities) {
        jpaRepository.saveAll(entities);
    }
}
