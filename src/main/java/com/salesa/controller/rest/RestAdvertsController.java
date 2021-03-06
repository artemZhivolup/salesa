package com.salesa.controller.rest;

import com.salesa.filter.AdvertFilter;
import com.salesa.service.AdvertService;
import com.salesa.util.entity.AdvertPageData;
import com.salesa.util.mapper.AdvertParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/v1/adverts")
public class RestAdvertsController {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private AdvertService advertService;
    @Autowired
    private AdvertParser advertParser;

    @RequestMapping(headers = {"Accept=application/json;charset=UTF-8"})
    public String advertsJsonRest(@RequestParam(name = "page", defaultValue = "1") int page) {
        AdvertFilter advertFilter = new AdvertFilter();
        advertFilter.setPage(page);
        log.info("Query get adverts(JSON) from REST api for filter{}", advertFilter);
        AdvertPageData advertPageData = advertService.get(advertFilter);

        return advertParser.toJSON(advertPageData, page);
    }

    // TODO: 5/14/2016 xml
}
