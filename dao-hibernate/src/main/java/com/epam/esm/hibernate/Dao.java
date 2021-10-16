package com.epam.esm.hibernate;

import java.util.List;
import java.util.Optional;

public interface Dao<T> {

    List<T> getAll(int limit, int offset);
    Optional<T> getById(long id);
    long create(T entity);
    void delete(T entity);
}
