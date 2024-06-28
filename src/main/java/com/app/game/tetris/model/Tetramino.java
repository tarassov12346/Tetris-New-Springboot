package com.app.game.tetris.model;

import lombok.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@Value
public class Tetramino {
    char[][] shape;
}
