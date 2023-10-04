package com.mydemo.demoproject.service.admin.product;

import com.mydemo.demoproject.Entity.Image;
import com.mydemo.demoproject.Repository.admin.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
@Service
public class ImageServiceImp implements ImageService {

    @Autowired
    ImageRepository imageRepository;

    /*Save*/
    @Override
    public Image save(Image image) {
        return imageRepository.save(image);
    }


 /*  Find img by id*/
    @Override
    public List<Image> findImagesByProductId(UUID uuid) {
        return imageRepository.findByProductId(uuid);
    }

    @Override
    public List<Image>  findAllImage() {
        return imageRepository.findAll();
    }


    /*Delete*/
    @Override
    public void deleteImages(UUID uuid) {
        System.out.println("image delete in Image service+++++++++++++++++++++++++++"+uuid);
        imageRepository.deleteById(uuid);
    }

    /*find file name by id*/
    @Override
    public Image findFileNameById(UUID uuid) {

        return imageRepository.findById(uuid).orElse(null);
    }



   /* delete unused*/
    @Override
    @Scheduled(cron = "0 0 21 * * *")
    public void deleteUnusedImages() {



        System.out.println("scheduled task execution started\ndeleting all unused images");
        List<Image> productImages = imageRepository.findAll();
        Set<String> imagesInUse = new HashSet<>();
        for (Image image : productImages) {
            imagesInUse.add(image.getFileName());
        }

        String rootPath = System.getProperty("user.dir");
        String uploadDir = rootPath + "/src/main/resources/static/uploads";

        File directory = new File(uploadDir);


//        // Check if the directory exists
        if (directory.exists() && directory.isDirectory()) {

            // Get the list of files in the directory
            File[] files = directory.listFiles();

            // Iterate over the files
            for (File file : files) {
                if (file.isFile()) {
                    // Get the file name
                    String fileName = file.getName();

                    if (!imagesInUse.contains(fileName)) {
                        // Call your handleDelete method with the file name
                        try {
                            handleDelete(fileName);
                        } catch (IOException e) {
                            // Handle any exceptions
                            e.printStackTrace();
                        }
                    }

                }
            }
        }
        else {
            System.out.println("Directory not found!");
        }

    }


    /*handle delete*/
    public void handleDelete(String fileName) throws IOException{
        // Define the directory
        String rootPath = System.getProperty("user.dir");
        String uploadDir = rootPath + "/src/main/resources/static/uploads";

        // Get the file path
        String filePath = uploadDir + "/" + fileName;

        // Create a file object for the file to be deleted
        File file = new File(filePath);

        // Check if the file exists
        if (file.exists()) {
            // Delete the file
            file.delete();
            System.out.println(fileName + " deleted");
        } else {
            System.out.println("File not found!");
        }
    }



    /*Crop img*/
    public BufferedImage cropImageSquare(byte[] image) throws IOException {
        // Get a BufferedImage object from a byte array
        InputStream in = new ByteArrayInputStream(image);
        BufferedImage originalImage = ImageIO.read(in);

        // Get image dimensions
        int height = originalImage.getHeight();
        int width = originalImage.getWidth();

        // The image is already a square
        if (height == width) {
            return originalImage;
        }

        // Compute the size of the square
        int squareSize = (height > width ? width : height);

        // Coordinates of the image's middle
        int xc = width / 2;
        int yc = height / 2;

        // Crop
        BufferedImage croppedImage = originalImage.getSubimage(
                xc - (squareSize / 2), // x coordinate of the upper-left corner
                yc - (squareSize / 2), // y coordinate of the upper-left corner
                squareSize,            // widht
                squareSize             // height
        );

        return croppedImage;
}


}
