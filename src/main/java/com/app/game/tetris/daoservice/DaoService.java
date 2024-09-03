package com.app.game.tetris.daoservice;

import com.app.game.tetris.model.Player;

public interface DaoService {
    void recordScore(Player player);
    void retrieveScores();
    void retrievePlayerScores(Player player);
    String getBestPlayer();
    int getBestScore();
    int getPlayerBestScore();
    int getPlayerAttemptsNumber();
}
