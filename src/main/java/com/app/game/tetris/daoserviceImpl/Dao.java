package com.app.game.tetris.daoserviceImpl;

import com.app.game.tetris.daoservice.DaoService;
import com.app.game.tetris.model.Player;
import com.app.game.tetris.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


@Service
public class Dao implements DaoService {
    public String bestPlayer = "To be shown";
    public int bestScore;
    public int playerBestScore;
    public int playerAttemptsNumber;

    @Autowired
    private PlayerRepository playerRepository;

    @Override
    public void recordScore(Player player) {
        playerRepository.save(player);
    }

    @Override
    public void retrieveScores() {
        List<Player> playerList = playerRepository.findAll();
        Collections.sort(playerList, Comparator.comparingInt(Player::getPlayerScore));
        bestPlayer = playerList.get(playerList.size() - 1).getPlayerName();
        bestScore = playerList.get(playerList.size() - 1).getPlayerScore();
    }

    @Override
    public void retrievePlayerScores(Player player) {
        List<Player> playerByNameList = new ArrayList<>();
        List<Player> playerList = playerRepository.findAll();
        playerList.forEach(allPlayer -> {
            if (allPlayer.getPlayerName().equals(player.getPlayerName())) playerByNameList.add(allPlayer);
        });
        Collections.sort(playerByNameList, Comparator.comparingInt(Player::getPlayerScore));
        playerBestScore = playerByNameList.get(playerByNameList.size() - 1).getPlayerScore();
        playerAttemptsNumber = playerByNameList.size();
    }

    @Override
    public String getBestPlayer() {
        return bestPlayer;
    }

    @Override
    public int getBestScore() {
        return bestScore;
    }

    @Override
    public int getPlayerBestScore() {
        return playerBestScore;
    }

    @Override
    public int getPlayerAttemptsNumber() {
        return playerAttemptsNumber;
    }
}
