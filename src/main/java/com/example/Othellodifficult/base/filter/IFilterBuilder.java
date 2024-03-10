package com.example.Othellodifficult.base.filter;

import java.util.Collection;

public interface IFilterBuilder<T> {
    Filter.FilterBuilder<T> search();
    Filter.FilterBuilder<T> filter();
    Filter.FilterBuilder<T> isContain(String fieldName, String value);
    Filter.FilterBuilder<T> isIn(String fieldName, Collection<Object> value);
    Filter.FilterBuilder<T> isNotIn(String fieldName, Collection<Object> values);
    Filter.FilterBuilder<T> isEqual(String fieldName, Object value);
}
