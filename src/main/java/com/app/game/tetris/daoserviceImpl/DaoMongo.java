package com.app.game.tetris.daoserviceImpl;

import com.app.game.tetris.daoservice.DaoMongoService;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.GridFSUploadStream;
import com.mongodb.client.gridfs.model.GridFSDownloadOptions;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

@Service
public class DaoMongo implements DaoMongoService {

    @Override
    public void runMongoServer() {
        try {
            Runtime.getRuntime().exec(new String [] {MongoPath});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadMugShotFromMongodb(String playerName) {
        MongoClient mongoClient = MongoClients.create();
        MongoDatabase database = mongoClient.getDatabase("shopDB");
        GridFSBucket gridFSBucket = GridFSBuckets.create(database);
        GridFSDownloadOptions downloadOptions = new GridFSDownloadOptions().revision(0);
// Downloads a file to an output stream
        try (FileOutputStream streamToDownloadTo = new FileOutputStream(System.getProperty("user.dir") + "\\src\\main\\webapp\\shots\\mugShot.jpg")) {
            gridFSBucket.downloadToStream(playerName + ".jpg", streamToDownloadTo, downloadOptions);
            streamToDownloadTo.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mongoClient.close();
    }

    @Override
    public void loadSnapShotFromMongodb(String playerName, String fileName) {
        MongoClient mongoClient = MongoClients.create();
        MongoDatabase database = mongoClient.getDatabase("shopDB");
        GridFSBucket gridFSBucket = GridFSBuckets.create(database);
        GridFSDownloadOptions downloadOptions = new GridFSDownloadOptions().revision(0);
// Downloads a file to an output stream
        try (FileOutputStream streamToDownloadTo = new FileOutputStream(System.getProperty("user.dir") + "\\src\\main\\webapp\\shots\\"+fileName+".jpg")) {
            gridFSBucket.downloadToStream(playerName + fileName+".jpg", streamToDownloadTo, downloadOptions);
            streamToDownloadTo.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mongoClient.close();
    }

    @Override
    public void cleanMongodb(String playerName, String fileName) {
        MongoClient mongoClient = MongoClients.create();
        MongoDatabase database = mongoClient.getDatabase("shopDB");
        GridFSBucket gridFSBucket = GridFSBuckets.create(database);
        GridFSFindIterable gridFSFile = gridFSBucket.find(Filters.eq("filename", playerName + fileName+".jpg"));
        while (gridFSFile.cursor().hasNext()) {
            gridFSBucket.delete(gridFSFile.cursor().next().getId());
        }
        mongoClient.close();
    }

    @Override
    public void loadSnapShotIntoMongodb(String playerName, String fileName) {
        MongoClient mongoClient = MongoClients.create();
        MongoDatabase database = mongoClient.getDatabase("shopDB");
        GridFSBucket gridFSBucket = GridFSBuckets.create(database);
        byte[] data = new byte[0];
        try {
            data = Files.readAllBytes(Path.of(System.getProperty("user.dir") + "\\src\\main\\webapp\\shots\\"+fileName+".jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        GridFSUploadOptions options = new GridFSUploadOptions()
                .chunkSizeBytes(1048576)
                .metadata(new Document("type", "jpg"));
        try (GridFSUploadStream uploadStream = gridFSBucket.openUploadStream(playerName + fileName+".jpg", options)) {
            // Writes file data to the GridFS upload stream
            uploadStream.write(data);
            uploadStream.flush();
            // Prints the "_id" value of the uploaded file
            System.out.println("The file id of the uploaded file is: " + uploadStream.getObjectId().toHexString());
// Prints a message if any exceptions occur during the upload process
        } catch (Exception e) {
            System.err.println("The file upload failed: " + e);
        }
        Bson query = Filters.eq("metadata.type", "jpg");
        Bson sort = Sorts.ascending("filename");
// Retrieves 5 documents in the bucket that match the filter and prints metadata
        gridFSBucket.find(query)
                .sort(sort)
                .limit(5)
                .forEach(new Consumer<GridFSFile>() {
                    @Override
                    public void accept(final GridFSFile gridFSFile) {
                        System.out.println(gridFSFile);
                    }
                });
        // Now you can work with the 'database' object to perform CRUD operations.
        // Don't forget to close the MongoClient when you're done.
        mongoClient.close();
    }

    @Override
    public void makeDesktopSnapshot(String fileNameDetail) {
        Robot robot = null;
        System.setProperty("java.awt.headless", "false");
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        String format = "jpg";
        String fileName = System.getProperty("user.dir") + "\\src\\main\\webapp\\shots\\" + fileNameDetail + "." + format;
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        BufferedImage screenFullImage = robot.createScreenCapture(screenRect);
        try {
            ImageIO.write(screenFullImage, format, new File(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
