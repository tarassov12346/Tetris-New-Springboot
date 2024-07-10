package com.app.game.tetris.config;

import com.app.game.tetris.model.Player;
import com.app.game.tetris.model.SavedGame;
import com.app.game.tetris.model.Tetramino;
import com.app.game.tetris.serviceImpl.Stage;
import com.app.game.tetris.serviceImpl.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

@Configuration
public class RestartGameConfiguration {

    @Autowired
    private ApplicationContext context;

    public State recreateState() throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(System.getProperty("user.dir")+"\\src\\main\\resources\\save.ser");
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        SavedGame savedGame = (SavedGame) objectInputStream.readObject();
        Player player = context.getBean(Player.class, savedGame.getPlayerName(), savedGame.getPlayerScore());
        Stage recreatedStage = context.getBean(Stage.class, savedGame.getCells(), getTetramino0(), 0, 0, player.getPlayerScore() / 10);
        return context.getBean(State.class, recreatedStage, true, player).restartWithNewTetramino().orElse(context.getBean(State.class, recreatedStage, true, player));
    }

    private Tetramino getTetramino0(){
        ApplicationContext context =new AnnotationConfigApplicationContext("com.app.game.tetris.model");
        return context.getBean(Tetramino.class, (Object) new char[][]{{'0'}});
    }
}
