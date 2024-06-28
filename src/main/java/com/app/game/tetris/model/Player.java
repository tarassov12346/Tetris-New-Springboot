package com.app.game.tetris.model;


import jakarta.persistence.*;
import lombok.Data;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;




@Component
@Scope("prototype")
@Data
@Entity
@Table(name = "player5")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String playerName;

    @Column(name = "score")
    private int playerScore;

    public Player() {
    }

    public Player(String playerName, int playerScore) {
        this.playerName = playerName;
        this.playerScore = playerScore;
    }

    public void setPlayerScore(int collapsedLayersCount) {
        this.playerScore = collapsedLayersCount * 10;
    }
}
