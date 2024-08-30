package com.app.game.tetris.controller;

import com.app.game.tetris.config.PlayGameConfiguration;
import com.app.game.tetris.config.RestartGameConfiguration;
import com.app.game.tetris.config.SaveGameConfiguration;
import com.app.game.tetris.config.StartGameConfiguration;
import com.app.game.tetris.daoservice.DaoService;
import com.app.game.tetris.model.Player;
import com.app.game.tetris.model.SavedGame;
import com.app.game.tetris.serviceImpl.State;
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
import jakarta.servlet.http.HttpSession;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;

@Controller
public class GameController {
    private HttpSession currentSession;
    private Player player;
    private State state;

    @Autowired
    private StartGameConfiguration startGameConfiguration;

    @Autowired
    private PlayGameConfiguration playGameConfiguration;

    @Autowired
    private DaoService daoService;

    @Autowired
    private SaveGameConfiguration saveGameConfiguration;

    @Autowired
    private RestartGameConfiguration restartGameConfiguration;

    @GetMapping({
            "/hello"
    })
    public String hello() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        currentSession = attr.getRequest().getSession(true);
        player = startGameConfiguration.createPlayer();
        state = startGameConfiguration.initiateState();
        daoService.retrieveScores();
        makeHelloView();
        return "hello";
    }

    @GetMapping({"/start"})
    public String gameStart() {
        initiateView();
        makeGamePageView();
        return "index";
    }

    @GetMapping({"/profile"})
    public String profile() {
        daoService.retrievePlayerScores(player);
        loadMugShotFromMongodb(player.getPlayerName());
        loadSnapShotFromMongodb(player.getPlayerName());
        makeProfileView();
        return "profile";
    }

    @GetMapping({"/{moveId}"})
    public String gamePlay(@PathVariable Integer moveId) {
        switch (moveId) {
            case 0 -> {
                Optional<State> moveDownState = playGameConfiguration.moveDownState(state);
                if (moveDownState.isEmpty()) {
                    Optional<State> newTetraminoState = playGameConfiguration.newTetraminoState(state);
                    if (newTetraminoState.isEmpty()) {
                        currentSession.setAttribute("isGameOn", false);
                        currentSession.setAttribute("gameStatus", "Game over");
                        state.stop();
                    } else state = newTetraminoState.orElse(state);
                }
                state = moveDownState.orElse(state);
            }
            case 1 -> {
                state = playGameConfiguration.moveDownState(state).orElse(state);
                state = playGameConfiguration.rotateState(state);
            }
            case 2 -> {
                state = playGameConfiguration.moveDownState(state).orElse(state);
                state = playGameConfiguration.moveLeftState(state);
            }
            case 3 -> {
                state = playGameConfiguration.moveDownState(state).orElse(state);
                state = playGameConfiguration.moveRightState(state);
            }
            case 4 -> state = playGameConfiguration.dropDownState(state);
            case 5 -> {
                daoService.recordScore(player);
                daoService.retrieveScores();
                makeDesktopSnapshot("deskTopSnapShot");
                cleanMongodb(player.getPlayerName());
                loadSnapShotIntoMongodb(player.getPlayerName());
            }
        }
        makeGamePageView();
        return "index";
    }

    @GetMapping({"/save"})
    public String gameSave() throws IOException {
        state.setPause();
        currentSession.setAttribute("isGameOn", false);
        SavedGame savedGame = saveGameConfiguration.saveGame(player, state);
        FileOutputStream outputStream = new FileOutputStream(System.getProperty("user.dir") + "\\src\\main\\resources\\save.ser");
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(savedGame);
        objectOutputStream.close();
        currentSession.setAttribute("gameStatus", "Game Saved");
        return "index";
    }

    @GetMapping({"/restart"})
    public String gameRestart() throws IOException, ClassNotFoundException {
        state = restartGameConfiguration.recreateState();
        initiateView();
        makeGamePageView();
        return "index";
    }

    private void initiateView() {
        currentSession.setAttribute("gameStatus", "Game is ON");
        currentSession.setAttribute("isGameOn", true);
        state.unsetPause();
    }

    private void makeHelloView() {
        player = state.getPlayer();
        currentSession.setAttribute("player", player.getPlayerName());
        currentSession.setAttribute("bestPlayer", daoService.getBestPlayer());
        currentSession.setAttribute("bestScore", daoService.getBestScore());
    }

    private void makeProfileView() {
        player = state.getPlayer();
        currentSession.setAttribute("player", player.getPlayerName());
        currentSession.setAttribute("playerBestScore", daoService.getPlayerBestScore());
        currentSession.setAttribute("playerAttemptsNumber", daoService.getPlayerAttemptsNumber());
        currentSession.setAttribute("mugShot", "shots/mugShot.jpg");
        currentSession.setAttribute("snapShot", "shots/deskTopSnapShot.jpg");
    }

    private void makeGamePageView() {
        char[][] cells = state.getStage().drawTetraminoOnCells();
        player = state.getPlayer();
        state.setStepDown(player.getPlayerScore() / 10 + 1);
        currentSession.setAttribute("player", player.getPlayerName());
        currentSession.setAttribute("score", player.getPlayerScore());
        currentSession.setAttribute("bestplayer", daoService.getBestPlayer());
        currentSession.setAttribute("bestscore", daoService.getBestScore());
        currentSession.setAttribute("stepdown", state.getStepDown());
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 12; j++) {
                currentSession.setAttribute(new StringBuilder("cells").append(i).append("v").append(j).toString(),
                        new StringBuilder("/img/").append(cells[i][j]).append(".png").toString());
            }
        }
    }

    private void loadMugShotFromMongodb(String playerName) {
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

    private void loadSnapShotFromMongodb(String playerName) {
        MongoClient mongoClient = MongoClients.create();
        MongoDatabase database = mongoClient.getDatabase("shopDB");
        GridFSBucket gridFSBucket = GridFSBuckets.create(database);
        GridFSDownloadOptions downloadOptions = new GridFSDownloadOptions().revision(0);
// Downloads a file to an output stream
        try (FileOutputStream streamToDownloadTo = new FileOutputStream(System.getProperty("user.dir") + "\\src\\main\\webapp\\shots\\deskTopSnapShot.jpg")) {
            gridFSBucket.downloadToStream(playerName + "deskTopSnapShot.jpg", streamToDownloadTo, downloadOptions);
            streamToDownloadTo.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mongoClient.close();
    }

    private void cleanMongodb(String fileName) {
        MongoClient mongoClient = MongoClients.create();
        MongoDatabase database = mongoClient.getDatabase("shopDB");
        GridFSBucket gridFSBucket = GridFSBuckets.create(database);
        GridFSFindIterable gridFSFile = gridFSBucket.find(Filters.eq("filename", fileName + "deskTopSnapShot.jpg"));
        while (gridFSFile.cursor().hasNext()) {
            gridFSBucket.delete(gridFSFile.cursor().next().getId());
        }
        mongoClient.close();
    }

    private void loadSnapShotIntoMongodb(String fileName) {
        MongoClient mongoClient = MongoClients.create();
        MongoDatabase database = mongoClient.getDatabase("shopDB");
        GridFSBucket gridFSBucket = GridFSBuckets.create(database);
        byte[] data = new byte[0];
        try {
            data = Files.readAllBytes(Path.of(System.getProperty("user.dir") + "\\src\\main\\webapp\\shots\\deskTopSnapShot.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        GridFSUploadOptions options = new GridFSUploadOptions()
                .chunkSizeBytes(1048576)
                .metadata(new Document("type", "jpg"));
        try (GridFSUploadStream uploadStream = gridFSBucket.openUploadStream(fileName + "deskTopSnapShot.jpg", options)) {
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

    private void makeDesktopSnapshot(String fileNameDetail) {
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
