package com.app.game.tetris.serviceImpl;

import com.app.game.tetris.model.Player;
import com.app.game.tetris.model.Tetramino;
import com.app.game.tetris.service.GameLogic;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Scope("prototype")
public class State implements GameLogic<Optional<State>> {
    private final Stage stage;
    private final boolean isRunning;
    private final Player player;
    private int stepDown = 1;

    public State(Stage stage, boolean isRunning, Player player) {
        this.stage = Objects.requireNonNull(stage);
        this.isRunning = isRunning;
        this.player = player;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        return isRunning == state.isRunning && stage.equals(state.stage) && player.equals(state.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stage, isRunning, player);
    }


    public State start() {
        return new State(stage, true, player);
    }

    public void stop() {
        new State(stage, false, player);
    }

    @Override
    public Optional<State> moveLeft() {
        return !checkCollision(-1, 0, false) ? Optional.of(moveTetraminoLeft()) : Optional.empty();
    }

    @Override
    public Optional<State> moveRight() {
        return !checkCollision(1, 0, false) ? Optional.of(moveTetraminoRight()) : Optional.empty();
    }

    @Override
    public Optional<State> moveDown(int step) {
        int yToStepDown;
        for (yToStepDown = 0; (yToStepDown <= step) && (yToStepDown < GameLogic.HEIGHT); yToStepDown++) {
            if (checkCollision(0, yToStepDown, false)) break;
        }
        return !checkCollision(0, 1, false) ? Optional.of(moveTetraminoDown(yToStepDown - 1)) : Optional.empty();
    }

    @Override
    public Optional<State> rotate() {
        return !checkCollision(0, 0, true) ? Optional.of(rotateTetramino()) : Optional.empty();
    }

    @Override
    public void setPause() {
        stage.setPause();
    }

    @Override
    public void unsetPause() {
        stage.unsetPause();
    }

    @Override
    public Optional<State> setTetramino(Tetramino tetramino, int x, int y) {
        return Optional.of(new State(stage.setTetramino(tetramino, x, y), isRunning, player));
    }

    @Override
    public Optional<State> addTetramino() {
        return Optional.of(new State(stage.addTetramino(), isRunning, player));
    }

    @Override
    public Optional<State> collapseFilledLayers() {
        return Optional.of(new State(stage.collapseFilledLayers(), isRunning, player));
    }

    @Override
    public boolean checkCollision(int dx, int dy, boolean rotate) {
        return stage.checkCollision(dx, dy, rotate);
    }

    public Optional<State> createStateWithNewTetramino() {
        final Tetramino t = getRandomTetramino();
        final State newState = addTetramino().orElse(this)
                .collapseFilledLayers().orElse(this)
                .updatePlayerScore()
                .setTetramino(t, (GameLogic.WIDTH - t.getShape().length) / 2, 0).orElse(this);
        return !newState.checkCollision(0, 0, false) ? Optional.of(newState) : Optional.empty();
    }

    public Optional<State> restartWithNewTetramino() {
        final Tetramino t = getRandomTetramino();
        final State newState = addTetramino().orElse(this)
                .setTetramino(t, (GameLogic.WIDTH - t.getShape().length) / 2, 0).orElse(this);
        return !newState.checkCollision(0, 0, false) ? Optional.of(newState) : Optional.empty();
    }

    public Optional<State> dropDown() {
        int yToDropDown;
        for (yToDropDown = 0; yToDropDown < GameLogic.HEIGHT; yToDropDown++) {
            if (checkCollision(0, yToDropDown, false)) break;
        }
        return !checkCollision(0, yToDropDown - 1, false) ? Optional.of(moveTetraminoDown(yToDropDown - 1)) : Optional.empty();
    }

    public Stage getStage() {
        return stage;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public Player getPlayer() {
        return player;
    }

    public int getStepDown() {
        return stepDown;
    }

    public void setStepDown(int stepDown) {
        this.stepDown = stepDown;
    }

    private State updatePlayerScore() {
        player.setPlayerScore(stage.getCollapsedLayersCount());
        stepDown = 1 + stage.getCollapsedLayersCount();
        return new State(stage.collapseFilledLayers(), isRunning, player);
    }

    private State moveTetraminoDown(int yToMoveDown) {
        return new State(stage.moveDown(yToMoveDown), isRunning, player);
    }

    private State moveTetraminoLeft() {
        return new State(stage.moveLeft(), isRunning, player);
    }

    private State moveTetraminoRight() {
        return new State(stage.moveRight(), isRunning, player);
    }

    private State rotateTetramino() {
        return new State(stage.rotate(), isRunning, player);
    }

    private Tetramino getRandomTetramino() {
        final Map<Character, Tetramino> tetraminoMap = new HashMap<>();
        tetraminoMap.put('0', new Tetramino(new char[][]{{'0'}}));
        tetraminoMap.put('I', new Tetramino(new char[][]{{'0', 'I', '0', '0'}, {'0', 'I', '0', '0'}, {'0', 'I', '0', '0'}, {'0', 'I', '0', '0'}}));
        tetraminoMap.put('J', new Tetramino(new char[][]{{'0', 'J', '0'}, {'0', 'J', '0'}, {'J', 'J', '0'}}));
        tetraminoMap.put('L', new Tetramino(new char[][]{{'0', 'L', '0'}, {'0', 'L', '0'}, {'0', 'L', 'L'}}));
        tetraminoMap.put('O', new Tetramino(new char[][]{{'O', 'O'}, {'O', 'O'}}));
        tetraminoMap.put('S', new Tetramino(new char[][]{{'0', 'S', 'S'}, {'S', 'S', '0'}, {'0', '0', '0'}}));
        tetraminoMap.put('T', new Tetramino(new char[][]{{'0', '0', '0'}, {'T', 'T', 'T'}, {'0', 'T', '0'}}));
        tetraminoMap.put('Z', new Tetramino(new char[][]{{'Z', 'Z', '0'}, {'0', 'Z', 'Z'}, {'0', '0', '0'}}));
        tetraminoMap.put('K', new Tetramino(new char[][]{{'K', 'K', 'K'}, {'0', 'K', '0'}, {'0', 'K', '0'}}));

        final char[] tetraminos = "IJLOSTZK".toCharArray();
        return tetraminoMap.get(tetraminos[new Random().nextInt(tetraminos.length)]);
    }
}
