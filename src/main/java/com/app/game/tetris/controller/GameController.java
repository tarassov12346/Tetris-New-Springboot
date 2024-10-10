package com.app.game.tetris.controller;

import com.app.game.tetris.config.PlayGameConfiguration;
import com.app.game.tetris.config.RestartGameConfiguration;
import com.app.game.tetris.config.SaveGameConfiguration;
import com.app.game.tetris.config.StartGameConfiguration;
import com.app.game.tetris.daoservice.DaoMongoService;
import com.app.game.tetris.daoservice.DaoService;
import com.app.game.tetris.model.Player;
import com.app.game.tetris.model.SavedGame;
import com.app.game.tetris.serviceImpl.State;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Optional;

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
    private DaoMongoService daoMongoService;

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
        daoMongoService.runMongoServer();
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
        if (!daoMongoService.isMongoDBNotEmpty()) daoMongoService.prepareMongoDB();
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
                daoMongoService.makeDesktopSnapshot("deskTopSnapShot");
                daoMongoService.cleanMongodb(player.getPlayerName(), "deskTopSnapShot");
                daoMongoService.loadSnapShotIntoMongodb(player.getPlayerName(), "deskTopSnapShot");
                if (player.getPlayerScore() >= daoService.getPlayerBestScore()) {
                    daoMongoService.makeDesktopSnapshot("deskTopSnapShotBest");
                    daoMongoService.cleanMongodb(player.getPlayerName(), "deskTopSnapShotBest");
                    daoMongoService.loadSnapShotIntoMongodb(player.getPlayerName(), "deskTopSnapShotBest");
                }
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

    @GetMapping({"/getPhoto"})
    public void getPhoto(HttpServletRequest request,
                         HttpServletResponse response) {
        byte[] imagenEnBytes = daoMongoService.loadByteArrayFromMongodb(player.getPlayerName(), "mugShot");
        response.setHeader("Accept-ranges", "bytes");
        response.setContentType("image/jpeg");
        response.setContentLength(imagenEnBytes.length);
        response.setHeader("Expires", "0");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Content-Description", "File Transfer");
        response.setHeader("Content-Transfer-Encoding:", "binary");
        try {
            OutputStream out = response.getOutputStream();
            out.write(imagenEnBytes);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping({"/getSnapShot"})
    public void getSnapShot(HttpServletRequest request,
                         HttpServletResponse response) {
        byte[] imagenEnBytes = daoMongoService.loadByteArrayFromMongodb(player.getPlayerName(), "deskTopSnapShot");
        response.setHeader("Accept-ranges", "bytes");
        response.setContentType("image/jpeg");
        response.setContentLength(imagenEnBytes.length);
        response.setHeader("Expires", "0");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Content-Description", "File Transfer");
        response.setHeader("Content-Transfer-Encoding:", "binary");
        try {
            OutputStream out = response.getOutputStream();
            out.write(imagenEnBytes);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping({"/getSnapShotBest"})
    public void getSnapShotBest(HttpServletRequest request,
                            HttpServletResponse response) {
        byte[] imagenEnBytes = daoMongoService.loadByteArrayFromMongodb(player.getPlayerName(), "deskTopSnapShotBest");
        response.setHeader("Accept-ranges", "bytes");
        response.setContentType("image/jpeg");
        response.setContentLength(imagenEnBytes.length);
        response.setHeader("Expires", "0");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Content-Description", "File Transfer");
        response.setHeader("Content-Transfer-Encoding:", "binary");
        try {
            OutputStream out = response.getOutputStream();
            out.write(imagenEnBytes);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
}
