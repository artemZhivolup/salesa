package com.salesa.dao;

import com.salesa.entity.Image;

import java.util.List;

public interface ImageDao {
    void saveAdvertImage(Image image, int advertId);
    Image getAdvertImageById(int imageId);
    Image getAdvertImage(int advertId);
}
