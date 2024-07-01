package com.app.game.tetris.controller;

import com.app.game.tetris.config.PlayGameConfiguration;
import com.app.game.tetris.config.StartGameConfiguration;
import com.app.game.tetris.daoservice.DaoService;
import com.app.game.tetris.model.Player;
import com.app.game.tetris.serviceImpl.State;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

@Controller
public class GameController {
    private HttpSession currentSession;
    private Player player;
    private State state;

    @Autowired
    private DaoService daoService;

    @GetMapping({
            "/hello"
    })
    public String hello(){
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        currentSession = attr.getRequest().getSession(true);
        ApplicationContext context = new AnnotationConfigApplicationContext(StartGameConfiguration.class);
        player = context.getBean(Player.class);
        state = context.getBean(State.class);
        makeHelloView();
        return "hello";
    }

    @GetMapping({"/start"})
    public String gameStart() {
        initiateView();
        makeGamePageView();
        return "index";
    }

    @GetMapping({"/{moveId}"})
    public String gamePlay(@PathVariable Integer moveId) {
        ApplicationContext context = new AnnotationConfigApplicationContext(PlayGameConfiguration.class);
        switch (moveId) {
            case 0 -> {
                Optional<State> moveDownState = (Optional<State>) context.getBean("moveDownState", state);
                if (moveDownState.isEmpty()) {
                    Optional<State> newTetraminoState = (Optional<State>) context.getBean("newTetraminoState", state);
                    if (newTetraminoState.isEmpty()) {
                        currentSession.setAttribute("isGameOn", false);
                        currentSession.setAttribute("gameStatus", "Game over");
                        state.stop();
                    } else state = newTetraminoState.orElse(state);
                }
                state = moveDownState.orElse(state);
            }
            case 1 -> state = (State) context.getBean("rotateState", state);
            case 2 -> state = (State) context.getBean("moveLeftState", state);
            case 3 -> state = (State) context.getBean("moveRightState", state);
            case 4 -> state = (State) context.getBean("dropDownState", state);
            case 5 -> {
                daoService.recordScore(player);
                daoService.retrieveScores();
            }
        }
        makeGamePageView();
        return "index";
    }
    
    private void initiateView() {
        currentSession.setAttribute("gameStatus", "Game is ON");
        currentSession.setAttribute("isGameOn", true);
        state.unsetPause();
    }

    private void makeHelloView(){
        player = state.getPlayer();
        currentSession.setAttribute("player", player.getPlayerName());
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
