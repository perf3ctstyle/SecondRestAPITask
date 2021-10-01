package com.epam.esm.service;

import java.util.List;

public interface Service<T> {

    List<T> getAll(int limit, int offset);
    T getById(long id);
    long create(T entity);
    void delete(long id);
}
