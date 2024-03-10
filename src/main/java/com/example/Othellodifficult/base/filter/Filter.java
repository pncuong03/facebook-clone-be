package com.example.Othellodifficult.base.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Filter<T> {
    private TypedQuery<T> typedQuery;

    public static FilterBuilder builder(Class<?> clazz, EntityManager entityManager) {
        return new FilterBuilder<>(clazz, entityManager);
    }

    public List<T> getList(){
        return typedQuery.getResultList();
    }

    public static class FilterBuilder<T> implements IFilterBuilder {
        private TypedQuery<T> typedQuery;
        private StringBuilder query;
        private Class<T> entityClazz;
        private EntityManager entityManager;
        private String logicOperator;
        private Map<Object, Object> paramMap;
        private Integer paramCount;
        private StringBuilder conditions;

        public FilterBuilder(Class<T> clazz, EntityManager entityManager) {
            entityClazz = clazz;
            query = new StringBuilder("SELECT e FROM ").append(entityClazz.getSimpleName()).append(" e ");
            this.entityManager = entityManager;
            paramMap = new HashMap<>();
            paramCount = 1;
            conditions = new StringBuilder();
        }

        @Override
        public FilterBuilder<T> search() {
            logicOperator = " OR ";
            return this;
        }

        @Override
        public FilterBuilder<T> filter() {
            logicOperator = " AND ";
            return this;
        }

        @Override
        public FilterBuilder isContain(String fieldName, String value) {
            paramMap.put(paramCount, "%" + value + "%");
            conditions.append(this.logicOperator).append("e.").append(fieldName).append(" LIKE ?").append(paramCount++);
            return this;
        }

        @Override
        public FilterBuilder<T> isNotIn(String fieldName, Collection values) {
            paramMap.put(paramCount, values);
            conditions.append(this.logicOperator).append("e.").append(fieldName).append(" NOT IN (?").append(paramCount++).append(")");
            return this;
        }

        @Override
        public FilterBuilder<T> isIn(String fieldName, Collection values) {
            paramMap.put(paramCount, values);
            conditions.append(this.logicOperator).append("e.").append(fieldName).append(" IN (?").append(paramCount++).append(")");
            return this;
        }

        @Override
        public FilterBuilder<T> isEqual(String fieldName, Object value) {
            paramMap.put(paramCount, value);
            conditions.append(this.logicOperator).append("e.").append(fieldName).append(" = ?").append(paramCount++);
            return this;
        }

        public Filter<T> build(){
            String newConditions = "";
            if (conditions.length() > 0){
                newConditions = new String(this.conditions);
                if (newConditions.startsWith(" AND")){
                    newConditions = newConditions.substring(4);
                }
                else if (newConditions.startsWith(" OR")){
                    newConditions = newConditions.substring(3);
                }
                query.append("WHERE").append(newConditions);
            }
            this.typedQuery = entityManager.createQuery(new String(query), entityClazz);
            for (Object key : paramMap.keySet()) {
                Object value = paramMap.get(key);
                typedQuery.setParameter((int) key, value);
            }
            System.out.println(query);
            return new Filter<>(this.typedQuery);
        }
    }
}
