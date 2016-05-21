package com.salesa.dao.impl;

import com.salesa.dao.AdvertDao;
import com.salesa.dao.mapper.AdvertDetailsMapper;
import com.salesa.dao.mapper.AdvertMapper;
import com.salesa.dao.util.QueryAndParams;
import com.salesa.dao.util.QueryGenerator;
import com.salesa.entity.Advert;
import com.salesa.filter.AdvertFilter;
import com.salesa.util.entity.AdvertPageData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class AdvertJdbcDao implements AdvertDao {
    private final Logger log = LoggerFactory.getLogger(getClass());
    public static final int MAX_ADVERTS_PER_PAGE = 9;
    private static final AdvertMapper ADVERT_MAPPER = new AdvertMapper();

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private QueryGenerator queryGenerator;

    @Autowired
    private String getAdvertsCountSQL;

    @Autowired
    private String saveAdvertSQL;

    @Autowired
    private String updateAdvertSQL;

    @Autowired
    private String deleteAdvertSQL;

    @Override
    public AdvertPageData get(AdvertFilter advertFilter) {
        QueryAndParams queryAndParams = queryGenerator.generateAdvertQuery(advertFilter);

        long startTime = System.currentTimeMillis();
        log.info("Query adverts information for request {}", advertFilter);

        List<Advert> adverts = namedParameterJdbcTemplate.query(queryAndParams.query, queryAndParams.params, ADVERT_MAPPER);
        log.info("Query adverts for page took {} ms", System.currentTimeMillis() - startTime);

        Integer advertsCount;
        Map<String, Object> paramMap = new HashMap<>();
        String query = getAdvertsCountSQL;
        log.info("Query page information for request {}", advertFilter);
        if (advertFilter.getCategoryId() > 0 || advertFilter.isActive()) {
            if (advertFilter.getCategoryId() > 0) {
                paramMap.put("categoryId", advertFilter.getCategoryId());
                query += " AND categoryId = :categoryId";
            }
            if (advertFilter.isActive()) {
                query += " AND status = 'A'";
            }
            query += ";";
            advertsCount = namedParameterJdbcTemplate.queryForObject(query, paramMap, Integer.class);
        } else {
            advertsCount = namedParameterJdbcTemplate.queryForObject(query, new HashMap<>(), Integer.class);
        }

        log.info("Obtained {} adverts for filter {}", advertsCount, advertFilter);

        int pageCount = advertsCount / MAX_ADVERTS_PER_PAGE;
        AdvertPageData advertPageData = new AdvertPageData();
        advertPageData.setAdverts(adverts);
        advertPageData.setPageCount(advertsCount % MAX_ADVERTS_PER_PAGE == 0 ? pageCount : pageCount + 1);
        return advertPageData;
    }

    @Override
    public Advert get(int advertId) {
        QueryAndParams queryAndParams = queryGenerator.generateAdvertQuery(advertId);
        return namedParameterJdbcTemplate.queryForObject(queryAndParams.query, queryAndParams.params, new AdvertDetailsMapper());
    }

    @Override
    public List<Advert> getByUserId(int userId) {
        log.info("Getting adverts by user with id {}", userId);
        QueryAndParams queryAndParams = queryGenerator.generateAdvertByUserIdQuery(userId);
        return namedParameterJdbcTemplate.query(queryAndParams.query, queryAndParams.params, new AdvertMapper());
    }

    @Override
    public int saveAdvert(Advert advert) {
        log.info("Making query for save advert: {}", advert);
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("title", advert.getTitle());
        mapSqlParameterSource.addValue("text", advert.getText());
        mapSqlParameterSource.addValue("categoryId", advert.getCategory().getId());
        mapSqlParameterSource.addValue("price", advert.getPrice());
        mapSqlParameterSource.addValue("currency", advert.getCurrency());
        mapSqlParameterSource.addValue("status", advert.getStatus());
        mapSqlParameterSource.addValue("modificationDate", advert.getModificationDate());
        mapSqlParameterSource.addValue("userId", advert.getUser().getId());

        namedParameterJdbcTemplate.update(saveAdvertSQL, mapSqlParameterSource);

        log.info("Finish saving advert: {}", advert);
        return advert.getId();

    }

    @Override
    public void update(Advert advert) {
        log.info("Updating advert {}", advert);
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("title", advert.getTitle());
        mapSqlParameterSource.addValue("text", advert.getText());
        mapSqlParameterSource.addValue("categoryId", advert.getCategory().getId());
        mapSqlParameterSource.addValue("price", advert.getPrice());
        mapSqlParameterSource.addValue("currency", advert.getCurrency());
        mapSqlParameterSource.addValue("status", advert.getStatus());
        mapSqlParameterSource.addValue("modificationDate", advert.getModificationDate());
        mapSqlParameterSource.addValue("id", advert.getId());

        namedParameterJdbcTemplate.update(updateAdvertSQL, mapSqlParameterSource);
    }

    @Override
    public void delete(Advert advert) {
        log.info("Start deleting advert {}", advert);

        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("id", advert.getId());
        namedParameterJdbcTemplate.update(deleteAdvertSQL, mapSqlParameterSource);
    }
}
