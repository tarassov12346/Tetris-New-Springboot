package com.app.game.tetris.config;

import com.app.game.tetris.model.Player;
import com.app.game.tetris.model.SavedGame;
import com.app.game.tetris.serviceImpl.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SaveGameConfiguration {

    @Autowired
    private ApplicationContext context;

    public SavedGame saveGame(Player player, State state) {
        return context.getBean(SavedGame.class,player.getPlayerName(), player.getPlayerScore(), state.getStage().getCells());
    }
}
