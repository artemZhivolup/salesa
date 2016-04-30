package com.salesa.dao.util;


import com.salesa.filter.AdvertFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.salesa.dao.impl.AdvertJdbcDao.MAX_ADVERTS_PER_PAGE;

@Service
public class QueryGenerator {
    private static final char END_SEPARATOR = ';';
    private static final String WHERE_STATEMENT = " WHERE ";
    private static final String AND_STATEMENT = " AND ";
    @Autowired
    private String getAdvertsTemplateSQL;
    @Autowired
    private String addPagingTemplateSQL;
    @Autowired
    private String getAdvertByIdSQL;
    @Autowired
    private String getAdvertsByUserIdSQL;
    @Autowired
    private String getUserByIdSQL;
    @Autowired
    private String getAllAdvertsSQL;

    public void setGetAdvertsTemplateSQL(String getAdvertsTemplateSQL) {
        this.getAdvertsTemplateSQL = getAdvertsTemplateSQL;
    }

    public void setAddPagingTemplateSQL(String addPagingTemplateSQL) {
        this.addPagingTemplateSQL = addPagingTemplateSQL;
    }

    public QueryAndParams generateAll(AdvertFilter advertFilter) {
        StringBuilder query = new StringBuilder(getAllAdvertsSQL);
        Map<String, Object> params = new HashMap<>();
        addPagination(advertFilter.getPage(), query, params);
        query.append(END_SEPARATOR);
        return new QueryAndParams(query.toString(), params);

    }

    public QueryAndParams generateAllAdverts() {
        StringBuilder query = new StringBuilder(getAdvertsByUserIdSQL);
        Map<String, Object> params = new HashMap<>();
        return new QueryAndParams(query.toString(), params);
    }

    public QueryAndParams generateAdvertQuery(AdvertFilter advertFilter) {
        StringBuilder query = new StringBuilder(getAdvertsTemplateSQL);
        Map<String, Object> params = new HashMap<>();

        // category
        if (advertFilter.getCategoryId() != 0) {
            query.append(AND_STATEMENT);
            addCategoryFiltering(advertFilter.getCategoryId(), query, params);
        }

        // pagination
        addPagination(advertFilter.getPage(), query, params);

        // post processing
        query.append(END_SEPARATOR);
        return new QueryAndParams(query.toString(), params);
    }

    private void addCategoryFiltering(int categoryId, StringBuilder query, Map<String, Object> params) {
        params.put("categoryId", categoryId);
        query.append("categoryId = :categoryId");
    }

    private void addPagination(int page, StringBuilder query, Map<String, Object> params) {
        query.append(addPagingTemplateSQL);
        int startPosition = MAX_ADVERTS_PER_PAGE * (page - 1);
        params.put("startPosition", startPosition);
        params.put("dataAmount", MAX_ADVERTS_PER_PAGE);
    }

    public QueryAndParams generateAdvertQuery(int advertId) {
        StringBuilder query = new StringBuilder(getAdvertByIdSQL);
        Map<String, Object> params = new HashMap<>();
        params.put("a.id", advertId);
        query.append(WHERE_STATEMENT);
        query.append("a.id = :a.id");
        query.append(END_SEPARATOR);
        return new QueryAndParams(query.toString(), params);

    }

    public QueryAndParams generateAdvertByUserIdQuery(int userId) {
        StringBuilder query = new StringBuilder(getAdvertsByUserIdSQL);
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        query.append(WHERE_STATEMENT);
        query.append("userId = :userId");
        query.append(END_SEPARATOR);
        return new QueryAndParams(query.toString(), params);
    }

    public QueryAndParams generateUserById(int userId) {
        StringBuilder query = new StringBuilder(getUserByIdSQL);
        Map<String, Object> params = new HashMap<>();
        params.put("u.id", userId);
        query.append(WHERE_STATEMENT);
        query.append("u.id = :u.id");
        query.append(END_SEPARATOR);
        return new QueryAndParams(query.toString(), params);
    }
}
