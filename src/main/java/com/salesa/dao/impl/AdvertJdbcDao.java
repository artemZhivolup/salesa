package com.salesa.dao.impl;

import com.salesa.dao.AdvertDao;
import com.salesa.dao.mapper.AdvertDetailsMapper;
import com.salesa.dao.mapper.AdvertMapper;
import com.salesa.dao.mapper.AdvertRestMapper;
import com.salesa.dao.mapper.ImageMapper;
import com.salesa.dao.util.QueryAndParams;
import com.salesa.dao.util.QueryGenerator;
import com.salesa.entity.Advert;
import com.salesa.entity.AdvertRest;
import com.salesa.entity.Image;
import com.salesa.filter.AdvertFilter;
import com.salesa.util.AdvertPageData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class AdvertJdbcDao implements AdvertDao {
    public static final ImageMapper IMAGE_ROW_MAPPER = new ImageMapper();
    private final Logger log = LoggerFactory.getLogger(getClass());
    public static final int MAX_ADVERTS_PER_PAGE = 9;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private QueryGenerator queryGenerator;

    @Autowired
    private String getAdvertsCountSQL;

    @Autowired
    private String saveAdvertSQL;

    @Autowired
    private String saveAdvertImageSQL;

    @Autowired
    private String getAdvertImageSQL;

    @Override
    public AdvertPageData get(AdvertFilter advertFilter) {
        QueryAndParams queryAndParams = queryGenerator.generateAdvertQuery(advertFilter);

        long startTime = System.currentTimeMillis();
        log.info("Query adverts information for request {}", advertFilter);
        List<Advert> adverts = namedParameterJdbcTemplate.query(queryAndParams.query, queryAndParams.params, new AdvertMapper());
        log.info("Query advert for page took {} ms", queryAndParams.query, System.currentTimeMillis() - startTime);

        Integer advertsCount;
        Map<String, Object> paramMap = new HashMap<>();
        if (advertFilter.getCategoryId() > 0) {
            paramMap.put("categoryId", advertFilter.getCategoryId());
            String andStatement = " AND categoryId = :categoryId;";
            advertsCount = namedParameterJdbcTemplate.queryForObject(getAdvertsCountSQL + andStatement, paramMap, Integer.class);
        } else {
            advertsCount = namedParameterJdbcTemplate.queryForObject(getAdvertsCountSQL, new HashMap<>(), Integer.class);
        }

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
        QueryAndParams queryAndParams = queryGenerator.generateAdvertByUserIdQuery(userId);
        return namedParameterJdbcTemplate.query(queryAndParams.query, queryAndParams.params, new AdvertMapper());
    }

    @Override
    public AdvertPageData getAll(AdvertFilter advertFilter) {
        QueryAndParams queryAndParams = queryGenerator.generateAll(advertFilter);
        List<AdvertRest> advertRests = namedParameterJdbcTemplate.query(queryAndParams.query, queryAndParams.params, new AdvertRestMapper());

        int advertsCount = namedParameterJdbcTemplate.queryForObject(getAdvertsCountSQL, new HashMap<>(), Integer.class);
        int pageCount = advertsCount / MAX_ADVERTS_PER_PAGE;

        AdvertPageData advertPageData = new AdvertPageData();
        advertPageData.setAdvertRests(advertRests);
        advertPageData.setPageCount(advertsCount % MAX_ADVERTS_PER_PAGE == 0 ? pageCount : pageCount + 1);
        return advertPageData;
    }

    @Override
    public int saveAdvert(Advert advert) {
        log.info("Saving advert {}", advert);
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
        log.info("saving  advert finished");
        return advert.getId();
    }


    public void saveAdvertImage(Image image, int advertId) {
        log.info("Saving image for advert with id {}", advertId);
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("picture", image.getContent());
        mapSqlParameterSource.addValue("type", image.getType());
        mapSqlParameterSource.addValue("advertId", advertId);
        namedParameterJdbcTemplate.update(saveAdvertImageSQL, mapSqlParameterSource);
    }

    @Override
    public Image getAdvertImage(int advertId) {
        log.info("query image for advert with id {}", advertId);
        Image image = namedParameterJdbcTemplate.queryForObject(getAdvertImageSQL, new MapSqlParameterSource("advertId", advertId), IMAGE_ROW_MAPPER);
        log.info("query image for advert with id {} finished", advertId);
        return image;
    }
}
