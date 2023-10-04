package com.mydemo.demoproject.service.admin.product;

import com.mydemo.demoproject.Entity.Image;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface ImageService {

    /*save img*/
    Image save(Image imageEntity);


    /*find Img*/
    List<Image> findImagesByProductId(UUID uuid);


    List<Image>findAllImage();


    /*Delete*/
    void deleteImages(UUID uuid);

    /*find file name*/
    Image findFileNameById(UUID uuid);


    /*delete unused img*/
    public void deleteUnusedImages();


/*    handle delete*/
    public void handleDelete(String fileName)throws IOException;

    /*Crop img*/
    public BufferedImage cropImageSquare(byte[] image) throws IOException;
}
