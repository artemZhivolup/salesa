package com.salesa.service.impl;

import com.salesa.dao.AdvertDao;
import com.salesa.entity.Advert;
import com.salesa.entity.Image;
import com.salesa.filter.AdvertFilter;
import com.salesa.service.AdvertService;
import com.salesa.util.AdvertPageData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class AdvertServiceImpl implements AdvertService {
    @Autowired
    private AdvertDao advertDao;

    @Override
    public AdvertPageData get(AdvertFilter advertFilter) {
        return advertDao.get(advertFilter);
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
    public AdvertPageData getAll(AdvertFilter advertFilter) {
        return advertDao.getAll(advertFilter);
    }

    public int saveAdvert(Advert advert) {
        return advertDao.saveAdvert(advert);
    }

    @Override
    public void saveAdvertImage(Image image, int advertId) {
        advertDao.saveAdvertImage(image, advertId);
    }

    @Override
    public Image getAdvertImage(int advertId) {
        return advertDao.getAdvertImage(advertId);
    }

    @Override
    public Image getAdvertImageById(int imageId) {
        return advertDao.getAdvertImageById(imageId);
    }


}
