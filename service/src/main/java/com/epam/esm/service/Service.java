package com.epam.esm.service;

import com.epam.esm.constant.GenericExceptionMessageConstants;

import java.util.List;

public interface Service<T> {

    List<T> getAll(int limit, int offset);
    T getById(long id);
    long create(T entity);
    void delete(long id);

    default void checkPaginationParameters(int limit, int offset) {
        if (limit < 0 || offset < 0) {
            throw new IllegalArgumentException(GenericExceptionMessageConstants.NEGATIVE_VALUE_PROHIBITED);
        }
    }
}
