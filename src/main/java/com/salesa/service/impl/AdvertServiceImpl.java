package com.salesa.service.impl;

import com.salesa.dao.AdvertDao;
import com.salesa.entity.Advert;
import com.salesa.filter.AdvertFilter;
import com.salesa.service.AdvertService;
import com.salesa.util.CurrencyConverter;
import com.salesa.util.entity.AdvertPageData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


@Service
public class AdvertServiceImpl implements AdvertService {
    @Autowired
    private AdvertDao advertDao;

    @Autowired
    private CurrencyConverter currencyConverter;

    private final Comparator<Advert> PRICE_ASC_COMPARATOR = (a, b) ->
            Double.compare(currencyConverter.calculateRate(a.getCurrency(), "UAH") * a.getPrice(),
                    currencyConverter.calculateRate(b.getCurrency(), "UAH") * b.getPrice());

    @Override
    public AdvertPageData get(AdvertFilter advertFilter) {
        AdvertPageData advertPageData = advertDao.get(advertFilter);
        List<Advert> adverts = advertPageData.getAdverts();
        // sorting
        applyPriceSorting(adverts, advertFilter);
        return advertPageData;
    }

    private void applyPriceSorting(List<Advert> adverts, AdvertFilter advertFilter) {
        if (advertFilter.isSortPriceAsc() != null) {
            Collections.sort(adverts, PRICE_ASC_COMPARATOR);
            if (!advertFilter.isSortPriceAsc()) {
                Collections.reverse(adverts);
            }
        }
    }

    @Override
    public Advert get(int advertId) {
        return advertDao.get(advertId);
    }

    @Override
    public List<Advert> getByUserId(int userId) {
        return advertDao.getByUserId(userId);
    }

    @Override
    public int saveAdvert(Advert advert) {
        advertDao.saveAdvert(advert);
        return advert.getId();
    }

    @Override
    public void update(Advert advert) {
        advertDao.update(advert);
    }

    @Override
    public void delete(Advert advert) {
        advertDao.delete(advert);
    }
}
