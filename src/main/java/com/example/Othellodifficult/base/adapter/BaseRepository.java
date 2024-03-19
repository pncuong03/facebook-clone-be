package com.example.Othellodifficult.base.adapter;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

// BaseRepository, BaseRepositoryAdapter, JPARepository = Object Adapter
// BaseRepositoryAdapter, JPARepository, NewChatRepository = Class Adapter
// Target
public interface BaseRepository <T>{
    List<T> findAll();
    Optional<T> findById(Long id);
    void delete(T entity);
    void deleteById(Long id);
    void save(T entity);
    void saveAll(Collection<T> entities);
}
