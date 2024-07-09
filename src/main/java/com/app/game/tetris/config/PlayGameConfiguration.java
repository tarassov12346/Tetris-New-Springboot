package com.app.game.tetris.config;

import com.app.game.tetris.serviceImpl.State;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
public class PlayGameConfiguration {

    public State dropDownState(State state){
        return state.dropDown().orElse(state);
    }

    public State moveRightState(State state){
        return state.moveRight().orElse(state);
    }

    public State moveLeftState(State state){
        return state.moveLeft().orElse(state);
    }

    public State rotateState(State state){
        return state.rotate().orElse(state);
    }

    public Optional<State> moveDownState(State state){
        return state.moveDown(state.getStepDown());
    }

    public Optional<State> newTetraminoState(State state){
        return state.createStateWithNewTetramino();
    }
}
