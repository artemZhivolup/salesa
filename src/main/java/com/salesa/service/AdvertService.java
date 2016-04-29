package com.salesa.service;

import com.salesa.entity.Advert;
import com.salesa.filter.AdvertFilter;
import com.salesa.util.AdvertPageData;

import java.util.List;

public interface AdvertService {
    AdvertPageData get(AdvertFilter advertFilter);
    Advert get(int advertId);
    List<Advert> getByUserId(int userId);
    AdvertPageData getAll(AdvertFilter advertFilter);
}
