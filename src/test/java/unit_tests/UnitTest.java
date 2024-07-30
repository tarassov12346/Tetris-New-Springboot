package unit_tests;


import com.app.game.tetris.model.Tetramino;
import com.app.game.tetris.serviceImpl.Stage;
import com.app.game.tetris.serviceImpl.State;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

import static com.app.game.tetris.service.GameLogic.HEIGHT;
import static com.app.game.tetris.service.GameLogic.WIDTH;

import com.app.game.tetris.TetrisNewApplication;
import com.app.game.tetris.config.PlayGameConfiguration;
import com.app.game.tetris.config.RestartGameConfiguration;
import com.app.game.tetris.config.SaveGameConfiguration;
import com.app.game.tetris.config.StartGameConfiguration;
import com.app.game.tetris.controller.GameController;
import com.app.game.tetris.model.Player;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = {GameController.class, StartGameConfiguration.class,
                PlayGameConfiguration.class, SaveGameConfiguration.class,
                RestartGameConfiguration.class, Player.class, Tetramino.class, Stage.class, State.class,
                TetrisNewApplication.class})
public class UnitTest extends AbstractTestNGSpringContextTests {
    protected static final Logger log = Logger.getLogger(UnitTest.class.getName());
    int moveCount;

    @BeforeClass
    public void doBeforeTests() {
        log.info("UnitTests start");
    }

    @BeforeMethod
    public void doBeforeEachTestMethod() {
        log.info("Test Method  is called");
    }

    @DataProvider
    public Object[][] data() {
        return new State[][]{{new State(makeStageWith2FilledRows(), true, new Player("Tester", 0)),}, {new State(makeStageWith3FilledRows(), true, new Player("Tester", 0))}};
    }

    @Test(dataProvider = "data", groups = {"rowsProcessingChecks"})
    public void doFullRowsCollapseAndScoreIsUpdated(State state) {
        log.info("doFullRowsCollapseAndScoreIsUpdated Test start");
        log.info("filled rows number is " + countFilledCells(state));
        State newState = state.createStateWithNewTetramino().orElse(state);
        Tetramino tetramino = newState.getStage().getTetramino();
        log.info("new tetramino is called with the shape type " + getShapeTypeByTetramino(tetramino));
        int tetraminoX = newState.getStage().getTetraminoX();
        int tetraminoY = newState.getStage().getTetraminoY();
        int collapsedLayersCount = newState.getStage().getCollapsedLayersCount();
        log.info("collapsed layers count=" + collapsedLayersCount);
        log.info("players score =" + newState.getPlayer().getPlayerScore());
        State expectedState = new State(makeStageWithOnlyLeftUnfilledRows(collapsedLayersCount).setTetramino(tetramino, tetraminoX, tetraminoY), true, new Player("Tester", collapsedLayersCount * 10));
        Assert.assertEquals(newState, expectedState);
    }

    @Test(dataProvider = "data", groups = {"tetraminoBehaviourChecks"})
    public void doesTetraminoMoveRight(State state) {
        log.info("doesTetraminoMoveRight Test start");
        State stateWithNewTetramino = state.createStateWithNewTetramino().orElse(state);
        log.info("new tetramino is called with the shape type " + getShapeTypeByTetramino(stateWithNewTetramino.getStage().getTetramino()));
        int tetraminoX = stateWithNewTetramino.getStage().getTetraminoX();
        int tetraminoY = stateWithNewTetramino.getStage().getTetraminoY();
        State newState = stateWithNewTetramino.moveRight().orElse(stateWithNewTetramino);
        Tetramino tetramino = newState.getStage().getTetramino();
        int collapsedLayersCount = newState.getStage().getCollapsedLayersCount();
        State expectedState = new State(makeStageWithOnlyLeftUnfilledRows(collapsedLayersCount).setTetramino(tetramino, tetraminoX + 1, tetraminoY), true, new Player("Tester", collapsedLayersCount * 10));
        log.info("Tetramino initial position x=" + tetraminoX + " y=" + tetraminoY);
        log.info("moveRight is called");
        log.info("Tetramino after moveRight new position x=" + newState.getStage().getTetraminoX() + " y=" + newState.getStage().getTetraminoY());
        Assert.assertEquals(newState, expectedState);
    }

    @Test(dataProvider = "data", groups = {"tetraminoBehaviourChecks"})
    public void doesTetraminoMoveRightStopAtBorder(State state) {
        log.info("doesTetraminoMoveRightStopAtBorder Test start");
        moveCount = 0;
        State stateWithNewTetramino = state.createStateWithNewTetramino().orElse(state);
        log.info("new tetramino is called with the shape type " + getShapeTypeByTetramino(stateWithNewTetramino.getStage().getTetramino()));
        int tetraminoX = stateWithNewTetramino.getStage().getTetraminoX();
        int tetraminoY = stateWithNewTetramino.getStage().getTetraminoY();
        State newState = moveFarRight(stateWithNewTetramino);
        State expectedState;
        Tetramino tetramino = newState.getStage().getTetramino();
        int collapsedLayersCount = newState.getStage().getCollapsedLayersCount();
        switch (getShapeTypeByTetramino(newState.getStage().getTetramino()).toString()) {
            case "O", "J", "I" -> expectedState = new State(makeStageWithOnlyLeftUnfilledRows(collapsedLayersCount).setTetramino(tetramino, 10, tetraminoY), true, new Player("Tester", collapsedLayersCount * 10));
            default -> expectedState = new State(makeStageWithOnlyLeftUnfilledRows(collapsedLayersCount).setTetramino(tetramino, 9, tetraminoY), true, new Player("Tester", collapsedLayersCount * 10));
        }
        log.info("Tetramino initial position x=" + tetraminoX + " y=" + tetraminoY);
        log.info("moveRight 13 times is performed");
        log.info("Tetramino moveRight 13 times new position x=" + newState.getStage().getTetraminoX() + " y=" + newState.getStage().getTetraminoY());
        log.info("Tetramino type " + getShapeTypeByTetramino(newState.getStage().getTetramino()));
        Assert.assertEquals(newState, expectedState);
    }

    @Test(dataProvider = "data", groups = {"tetraminoBehaviourChecks"})
    public void doesTetraminoMoveLeft(State state) {
        log.info("doesTetraminoMoveLeft Test start");
        State stateWithNewTetramino = state.createStateWithNewTetramino().orElse(state);
        log.info("new tetramino is called with the shape type " + getShapeTypeByTetramino(stateWithNewTetramino.getStage().getTetramino()));
        int tetraminoX = stateWithNewTetramino.getStage().getTetraminoX();
        int tetraminoY = stateWithNewTetramino.getStage().getTetraminoY();
        State newState = stateWithNewTetramino.moveLeft().orElse(stateWithNewTetramino);
        Tetramino tetramino = newState.getStage().getTetramino();
        int collapsedLayersCount = newState.getStage().getCollapsedLayersCount();
        State expectedState = new State(makeStageWithOnlyLeftUnfilledRows(collapsedLayersCount).setTetramino(tetramino, tetraminoX - 1, tetraminoY), true, new Player("Tester", collapsedLayersCount * 10));
        log.info("Tetramino initial position x=" + stateWithNewTetramino.getStage().getTetraminoX() + " y=" + stateWithNewTetramino.getStage().getTetraminoY());
        log.info("moveLeft is called");
        log.info("Tetramino after moveLeft new position x=" + newState.getStage().getTetraminoX() + " y=" + newState.getStage().getTetraminoY());
        Assert.assertEquals(newState, expectedState);
    }

    @Test(dataProvider = "data", groups = {"tetraminoBehaviourChecks"})
    public void doesTetraminoMoveLeftStopAtBorder(State state) {
        log.info("doesTetraminoMoveLeftStopAtBorder Test start");
        moveCount = 0;
        State stateWithNewTetramino = state.createStateWithNewTetramino().orElse(state);
        log.info("new tetramino is called with the shape type " + getShapeTypeByTetramino(stateWithNewTetramino.getStage().getTetramino()));
        int tetraminoX = stateWithNewTetramino.getStage().getTetraminoX();
        int tetraminoY = stateWithNewTetramino.getStage().getTetraminoY();
        State expectedState;
        State newState = moveFarLeft(stateWithNewTetramino);
        Tetramino tetramino = newState.getStage().getTetramino();
        int collapsedLayersCount = newState.getStage().getCollapsedLayersCount();
        switch (getShapeTypeByTetramino(newState.getStage().getTetramino()).toString()) {
            case "L", "I" -> expectedState = new State(makeStageWithOnlyLeftUnfilledRows(collapsedLayersCount).setTetramino(tetramino, -1, tetraminoY), true, new Player("Tester", collapsedLayersCount * 10));
            default -> expectedState = new State(makeStageWithOnlyLeftUnfilledRows(collapsedLayersCount).setTetramino(tetramino, 0, tetraminoY), true, new Player("Tester", collapsedLayersCount * 10));
        }
        log.info("Tetramino initial position x=" + tetraminoX + " y=" + tetraminoY);
        log.info("moveLeft 13 times is performed");
        log.info("Tetramino moveLeft 13 times new position x=" + newState.getStage().getTetraminoX() + " y=" + newState.getStage().getTetraminoY());
        log.info("Tetramino type " + getShapeTypeByTetramino(newState.getStage().getTetramino()));
        Assert.assertEquals(newState, expectedState);
    }

    @Test(dataProvider = "data", groups = {"tetraminoBehaviourChecks"})
    public void doesTetraminoMoveDown(State state) {
        log.info("doesTetraminoMoveDown Test start");
        State stateWithNewTetramino = state.createStateWithNewTetramino().orElse(state);
        log.info("new tetramino is called with the shape type " + getShapeTypeByTetramino(stateWithNewTetramino.getStage().getTetramino()));
        int tetraminoX = stateWithNewTetramino.getStage().getTetraminoX();
        int tetraminoY = stateWithNewTetramino.getStage().getTetraminoY();
        State newState = stateWithNewTetramino.moveDown(1).orElse(stateWithNewTetramino);
        Tetramino tetramino = newState.getStage().getTetramino();
        int collapsedLayersCount = newState.getStage().getCollapsedLayersCount();
        State expectedState = new State(makeStageWithOnlyLeftUnfilledRows(collapsedLayersCount).setTetramino(tetramino, tetraminoX, tetraminoY + 1), true, new Player("Tester", collapsedLayersCount * 10));
        log.info("Tetramino initial position x=" + stateWithNewTetramino.getStage().getTetraminoX() + " y=" + stateWithNewTetramino.getStage().getTetraminoY());
        log.info("moveDown is called");
        log.info("Tetramino after moveDown with step 1 new position x=" + newState.getStage().getTetraminoX() + " y=" + newState.getStage().getTetraminoY());
        Assert.assertEquals(newState, expectedState);
    }

    @Test(dataProvider = "data", groups = {"tetraminoBehaviourChecks"})
    public void doesTetraminoMoveDownStopAtUnfilledLayers(State state) {
        log.info("doesTetraminoMoveDownStopAtUnfilledLayers Test start");
        moveCount = 0;
        State stateWithNewTetramino = state.createStateWithNewTetramino().orElse(state);
        log.info("new tetramino is called with the shape type " + getShapeTypeByTetramino(stateWithNewTetramino.getStage().getTetramino()));
        int tetraminoX = stateWithNewTetramino.getStage().getTetraminoX();
        int tetraminoY = stateWithNewTetramino.getStage().getTetraminoY();
        State expectedState;
        State newState = moveDeepDown(stateWithNewTetramino);
        Tetramino tetramino = newState.getStage().getTetramino();
        int collapsedLayersCount = newState.getStage().getCollapsedLayersCount();
        switch (getShapeTypeByTetramino(newState.getStage().getTetramino()).toString()) {
            case "L", "J" -> expectedState = new State(makeStageWithOnlyLeftUnfilledRows(collapsedLayersCount).setTetramino(tetramino, tetraminoX, 15), true, new Player("Tester", collapsedLayersCount * 10));
            case "K" -> expectedState = new State(makeStageWithOnlyLeftUnfilledRows(collapsedLayersCount).setTetramino(tetramino, tetraminoX, 17), true, new Player("Tester", collapsedLayersCount * 10));
            default -> expectedState = new State(makeStageWithOnlyLeftUnfilledRows(collapsedLayersCount).setTetramino(tetramino, tetraminoX, 16), true, new Player("Tester", collapsedLayersCount * 10));
        }
        log.info("Tetramino initial position x=" + tetraminoX + " y=" + tetraminoY);
        log.info("moveDown 25 times is performed");
        log.info("Tetramino moveDown 25 times new position x=" + newState.getStage().getTetraminoX() + " y=" + newState.getStage().getTetraminoY());
        log.info("Tetramino type " + getShapeTypeByTetramino(newState.getStage().getTetramino()));
        Assert.assertEquals(newState, expectedState);
    }

    @Test(dataProvider = "data", groups = {"tetraminoBehaviourChecks"})
    public void doesTetraminoRotate(State state) {
        log.info("doesTetraminoRotate Test start");
        State stateWithNewTetramino = state.createStateWithNewTetramino().orElse(state);
        log.info("new tetramino is called with the shape type " + getShapeTypeByTetramino(stateWithNewTetramino.getStage().getTetramino()));
        int tetraminoX = stateWithNewTetramino.getStage().getTetraminoX();
        int tetraminoY = stateWithNewTetramino.getStage().getTetraminoY();
        State newState = stateWithNewTetramino.rotate().orElse(stateWithNewTetramino);
        Tetramino newTetramino = new Tetramino(rotateMatrix(stateWithNewTetramino.getStage().getTetramino().getShape()));
        int collapsedLayersCount = newState.getStage().getCollapsedLayersCount();
        State expectedState = new State(makeStageWithOnlyLeftUnfilledRows(collapsedLayersCount).setTetramino(newTetramino, tetraminoX, tetraminoY), true, new Player("Tester", collapsedLayersCount * 10));
        log.info("Tetramino initial shape " + matrixToString(stateWithNewTetramino.getStage().getTetramino().getShape()));
        log.info("Tetramino after rotate new shape " + matrixToString(newState.getStage().getTetramino().getShape()));
        Assert.assertEquals(newState, expectedState);
    }

    private Stage makeStageWithOnlyLeftUnfilledRows(int collapsedLayerCount) {
        final char[][] c = new char[HEIGHT][WIDTH];
        IntStream.range(0, HEIGHT - 2).forEach(y -> IntStream.range(0, WIDTH).forEach(x -> c[y][x] = '0'));
        IntStream.range(HEIGHT - 2, HEIGHT).forEach(y -> IntStream.range(0, WIDTH).forEach(x -> {
                    switch (x % 3) {
                        case 0 -> c[y][x] = 'S';
                        case 1 -> c[y][x] = 'I';
                        default -> c[y][x] = '0';
                    }
                })
        );
        return new Stage(c, getTetramino0(), 0, 0, collapsedLayerCount);
    }

    private Stage makeStageWith2FilledRows() {
        final char[][] c = new char[HEIGHT][WIDTH];
        IntStream.range(0, HEIGHT - 4).forEach(y -> IntStream.range(0, WIDTH).forEach(x -> c[y][x] = '0'));
        IntStream.range(HEIGHT - 4, HEIGHT - 2).forEach(y -> IntStream.range(0, WIDTH).forEach(x -> {
                    switch (x % 3) {
                        case 0 -> c[y][x] = 'S';
                        case 1 -> c[y][x] = 'L';
                        case 2 -> c[y][x] = 'O';
                        default -> c[y][x] = 'I';
                    }
                })
        );
        IntStream.range(HEIGHT - 2, HEIGHT).forEach(y -> IntStream.range(0, WIDTH).forEach(x -> {
                    switch (x % 3) {
                        case 0 -> c[y][x] = 'S';
                        case 1 -> c[y][x] = 'I';
                        default -> c[y][x] = '0';
                    }
                })
        );
        return new Stage(c, getTetramino0(), 0, 0, 0);
    }

    private Stage makeStageWith3FilledRows() {
        final char[][] c = new char[HEIGHT][WIDTH];
        IntStream.range(0, HEIGHT - 5).forEach(y -> IntStream.range(0, WIDTH).forEach(x -> c[y][x] = '0'));
        IntStream.range(HEIGHT - 5, HEIGHT - 2).forEach(y -> IntStream.range(0, WIDTH).forEach(x -> {
                    switch (x % 3) {
                        case 0 -> c[y][x] = 'O';
                        case 1 -> c[y][x] = 'S';
                        case 2 -> c[y][x] = 'I';
                        default -> c[y][x] = 'L';
                    }
                })
        );
        IntStream.range(HEIGHT - 2, HEIGHT).forEach(y -> IntStream.range(0, WIDTH).forEach(x -> {
                    switch (x % 3) {
                        case 0 -> c[y][x] = 'S';
                        case 1 -> c[y][x] = 'I';
                        default -> c[y][x] = '0';
                    }
                })
        );
        return new Stage(c, getTetramino0(), 0, 0, 0);
    }

    private State moveFarRight(State state) {
        state = state.moveRight().orElse(state);
        log.info("move tetramino 1 step right");
        log.info("tetramino position x=" + state.getStage().getTetraminoX() + " y=" + state.getStage().getTetraminoY());
        moveCount++;
        if (moveCount > 12) return state;
        return moveFarRight(state);
    }

    private State moveFarLeft(State state) {
        state = state.moveLeft().orElse(state);
        log.info("move tetramino 1 step left");
        log.info("tetramino position x=" + state.getStage().getTetraminoX() + " y=" + state.getStage().getTetraminoY());
        moveCount++;
        if (moveCount > 12) return state;
        return moveFarLeft(state);
    }

    private State moveDeepDown(State state) {
        state = state.moveDown(1).orElse(state);
        log.info("move tetramino 1 step down");
        log.info("tetramino position x=" + state.getStage().getTetraminoX() + " y=" + state.getStage().getTetraminoY());
        moveCount++;
        if (moveCount > 25) return state;
        return moveDeepDown(state);
    }

    private Tetramino getTetramino0() {
        return new Tetramino(new char[][]{{'0'}});
    }

    private Character getShapeTypeByTetramino(Tetramino tetramino) {
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
        for (Map.Entry<Character, Tetramino> entry : tetraminoMap.entrySet()) {
            if (Objects.equals(tetramino, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    private int countFilledCells(State state) {
        char[][] cells = state.getStage().getCells();
        int count = 0;
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                if (cells[i][j] == '0') {
                    count++;
                    break;
                }
            }
        }
        return HEIGHT - count;
    }

    private char[][] rotateMatrix(char[][] m) {
        final int h = m.length;
        final int w = m[0].length;
        final char[][] t = new char[h][w];
        IntStream.range(0, h).forEach(y -> IntStream.range(0, w).forEach(x -> t[w - x - 1][y] = m[y][x]));
        return t;
    }

    private String matrixToString(char[][] m) {
        StringBuilder expectedStr = new StringBuilder();
        expectedStr.append("{");
        for (char[] strings : m) {
            expectedStr.append("{");
            for (char s : strings) {
                expectedStr.append('"');
                expectedStr.append(s);
                expectedStr.append('"');
                expectedStr.append(',');
            }
            expectedStr.deleteCharAt(expectedStr.length() - 1);
            expectedStr.append("},");
        }
        expectedStr.deleteCharAt(expectedStr.length() - 1);
        expectedStr.append("}");
        return expectedStr.toString();
    }

    @AfterMethod
    public void doAfterEachTestMethod() {
        log.info("Test Method  is finished");
    }

    @AfterClass
    public void doAfterTests() {
        log.info("UnitTests are finished");
    }

}
