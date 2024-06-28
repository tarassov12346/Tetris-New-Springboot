package com.app.game.tetris.config;

import com.app.game.tetris.serviceImpl.State;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.Optional;

@Configuration
public class PlayGameConfiguration {
    @Bean
    @Scope("prototype")
    public State dropDownState(State state){
        return state.dropDown().orElse(state);
    }

    @Bean
    @Scope("prototype")
    public State moveRightState(State state){
        return state.moveRight().orElse(state);
    }

    @Bean
    @Scope("prototype")
    public State moveLeftState(State state){
        return state.moveLeft().orElse(state);
    }

    @Bean
    @Scope("prototype")
    public State rotateState(State state){
        return state.rotate().orElse(state);
    }

    @Bean
    @Scope("prototype")
    public Optional<State> moveDownState(State state){
        return state.moveDown(state.getStepDown());
    }

    @Bean
    @Scope("prototype")
    public Optional<State> newTetraminoState(State state){
        return state.createStateWithNewTetramino();
    }

}
