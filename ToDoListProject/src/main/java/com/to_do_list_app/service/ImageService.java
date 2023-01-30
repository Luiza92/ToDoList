package com.to_do_list_app.service;

import com.to_do_list_app.model.Image;
import com.to_do_list_app.repository.ImageRepo;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.sql.SQLException;
import java.util.UUID;

@Service
public class ImageService {


    @Autowired
    ImageRepo imageRepo;


    public int add(Image image) throws SQLException {

        int imageId = this.imageRepo.insert(image);{
        }
        return imageId;
    }

    public Image get(int id) throws SQLException {
        return this.imageRepo.get(id);
    }
    public boolean delete(int id) throws SQLException {
        return this.imageRepo.delete(id);

    }
    public int update(Image image) throws SQLException {
        int imageId = this.imageRepo.update(image);{
        }
        return imageId;
    }
    public JSONObject saveFile(MultipartFile file, String path) throws Exception {
        Image image = new Image();

        image.setFileName(UUID.randomUUID().toString() + "_" + file.getOriginalFilename());
        image.setPath(String.format( "uploads/%s/%s", path, image.getFileName()));
        file.transferTo(new File(image.getPath()));

        int imageId = this.add(image);
        image.setId(imageId);

        return image.toJson();
    }


    public boolean deleteFile(Image image) throws  Exception {

        Path path = Paths.get(image.getPath().substring(1));

        try {
            Files.delete(path);
            System.out.println("File or directory deleted successfully");
            return true;
        } catch (NoSuchFileException ex) {
            System.out.printf("No such file or directory: %s\n", path);
        } catch (DirectoryNotEmptyException ex) {
            System.out.printf("Directory %s is not empty\n", path);
        } catch (IOException ex) {
            System.out.println(ex);
        }
        return false;
    }
}
