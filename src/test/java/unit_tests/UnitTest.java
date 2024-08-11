package unit_tests;

import com.app.game.tetris.TetrisNewApplication;
import com.app.game.tetris.config.PlayGameConfiguration;
import com.app.game.tetris.config.RestartGameConfiguration;
import com.app.game.tetris.config.SaveGameConfiguration;
import com.app.game.tetris.config.StartGameConfiguration;
import com.app.game.tetris.controller.GameController;
import com.app.game.tetris.model.Player;
import com.app.game.tetris.model.Tetramino;
import com.app.game.tetris.serviceImpl.Stage;
import com.app.game.tetris.serviceImpl.State;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = {GameController.class, StartGameConfiguration.class,
                PlayGameConfiguration.class, SaveGameConfiguration.class,
                RestartGameConfiguration.class, Player.class, Tetramino.class, Stage.class, State.class,
                TetrisNewApplication.class, UnitTestService.class})
public class UnitTest extends AbstractTestNGSpringContextTests {
    protected static final Logger log = Logger.getLogger(UnitTest.class.getName());

    @Autowired
    private UnitTestService unitTestService;

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
        return new State[][]{{new State(unitTestService.makeStageWith2FilledRows(), true, new Player("Tester", 0)),}, {new State(unitTestService.makeStageWith3FilledRows(), true, new Player("Tester", 0))}};
    }

    @Test(dataProvider = "data", groups = {"rowsProcessingChecks"})
    public void doFullRowsCollapseAndScoreIsUpdated(State state) {
        log.info("doFullRowsCollapseAndScoreIsUpdated Test start");
        log.info("filled rows number is " + unitTestService.countFilledCells(state));
        State newState = state.createStateWithNewTetramino().orElse(state);
        Tetramino tetramino = newState.getStage().getTetramino();
        log.info("new tetramino is called with the shape type " + unitTestService.getShapeTypeByTetramino(tetramino));
        int tetraminoX = newState.getStage().getTetraminoX();
        int tetraminoY = newState.getStage().getTetraminoY();
        int collapsedLayersCount = newState.getStage().getCollapsedLayersCount();
        log.info("collapsed layers count=" + collapsedLayersCount);
        log.info("players score =" + newState.getPlayer().getPlayerScore());
        State expectedState = new State(unitTestService.makeStageWithOnlyLeftUnfilledRows(collapsedLayersCount).setTetramino(tetramino, tetraminoX, tetraminoY), true, new Player("Tester", collapsedLayersCount * 10));
        Assert.assertEquals(newState, expectedState);
    }

    @Test(dataProvider = "data", groups = {"tetraminoBehaviourChecks"})
    public void doesTetraminoMoveRight(State state) {
        log.info("doesTetraminoMoveRight Test start");
        State stateWithNewTetramino = state.createStateWithNewTetramino().orElse(state);
        log.info("new tetramino is called with the shape type " + unitTestService.getShapeTypeByTetramino(stateWithNewTetramino.getStage().getTetramino()));
        int tetraminoX = stateWithNewTetramino.getStage().getTetraminoX();
        int tetraminoY = stateWithNewTetramino.getStage().getTetraminoY();
        State newState = stateWithNewTetramino.moveRight().orElse(stateWithNewTetramino);
        Tetramino tetramino = newState.getStage().getTetramino();
        int collapsedLayersCount = newState.getStage().getCollapsedLayersCount();
        State expectedState = new State(unitTestService.makeStageWithOnlyLeftUnfilledRows(collapsedLayersCount).setTetramino(tetramino, tetraminoX + 1, tetraminoY), true, new Player("Tester", collapsedLayersCount * 10));
        log.info("Tetramino initial position x=" + tetraminoX + " y=" + tetraminoY);
        log.info("moveRight is called");
        log.info("Tetramino after moveRight new position x=" + newState.getStage().getTetraminoX() + " y=" + newState.getStage().getTetraminoY());
        Assert.assertEquals(newState, expectedState);
    }

    @Test(dataProvider = "data", groups = {"tetraminoBehaviourChecks"})
    public void doesTetraminoMoveRightStopAtBorder(State state) {
        log.info("doesTetraminoMoveRightStopAtBorder Test start");
        State stateWithNewTetramino = state.createStateWithNewTetramino().orElse(state);
        log.info("new tetramino is called with the shape type " + unitTestService.getShapeTypeByTetramino(stateWithNewTetramino.getStage().getTetramino()));
        int tetraminoX = stateWithNewTetramino.getStage().getTetraminoX();
        int tetraminoY = stateWithNewTetramino.getStage().getTetraminoY();
        State newState = unitTestService.moveFarRight(stateWithNewTetramino,0);
        State expectedState;
        Tetramino tetramino = newState.getStage().getTetramino();
        int collapsedLayersCount = newState.getStage().getCollapsedLayersCount();
        switch (unitTestService.getShapeTypeByTetramino(newState.getStage().getTetramino()).toString()) {
            case "O", "J", "I" -> expectedState = new State(unitTestService.makeStageWithOnlyLeftUnfilledRows(collapsedLayersCount).setTetramino(tetramino, 10, tetraminoY), true, new Player("Tester", collapsedLayersCount * 10));
            default -> expectedState = new State(unitTestService.makeStageWithOnlyLeftUnfilledRows(collapsedLayersCount).setTetramino(tetramino, 9, tetraminoY), true, new Player("Tester", collapsedLayersCount * 10));
        }
        log.info("Tetramino initial position x=" + tetraminoX + " y=" + tetraminoY);
        log.info("moveRight 13 times is performed");
        log.info("Tetramino moveRight 13 times new position x=" + newState.getStage().getTetraminoX() + " y=" + newState.getStage().getTetraminoY());
        log.info("Tetramino type " + unitTestService.getShapeTypeByTetramino(newState.getStage().getTetramino()));
        Assert.assertEquals(newState, expectedState);
    }

    @Test(dataProvider = "data", groups = {"tetraminoBehaviourChecks"})
    public void doesTetraminoMoveLeft(State state) {
        log.info("doesTetraminoMoveLeft Test start");
        State stateWithNewTetramino = state.createStateWithNewTetramino().orElse(state);
        log.info("new tetramino is called with the shape type " + unitTestService.getShapeTypeByTetramino(stateWithNewTetramino.getStage().getTetramino()));
        int tetraminoX = stateWithNewTetramino.getStage().getTetraminoX();
        int tetraminoY = stateWithNewTetramino.getStage().getTetraminoY();
        State newState = stateWithNewTetramino.moveLeft().orElse(stateWithNewTetramino);
        Tetramino tetramino = newState.getStage().getTetramino();
        int collapsedLayersCount = newState.getStage().getCollapsedLayersCount();
        State expectedState = new State(unitTestService.makeStageWithOnlyLeftUnfilledRows(collapsedLayersCount).setTetramino(tetramino, tetraminoX - 1, tetraminoY), true, new Player("Tester", collapsedLayersCount * 10));
        log.info("Tetramino initial position x=" + stateWithNewTetramino.getStage().getTetraminoX() + " y=" + stateWithNewTetramino.getStage().getTetraminoY());
        log.info("moveLeft is called");
        log.info("Tetramino after moveLeft new position x=" + newState.getStage().getTetraminoX() + " y=" + newState.getStage().getTetraminoY());
        Assert.assertEquals(newState, expectedState);
    }

    @Test(dataProvider = "data", groups = {"tetraminoBehaviourChecks"})
    public void doesTetraminoMoveLeftStopAtBorder(State state) {
        log.info("doesTetraminoMoveLeftStopAtBorder Test start");
        State stateWithNewTetramino = state.createStateWithNewTetramino().orElse(state);
        log.info("new tetramino is called with the shape type " + unitTestService.getShapeTypeByTetramino(stateWithNewTetramino.getStage().getTetramino()));
        int tetraminoX = stateWithNewTetramino.getStage().getTetraminoX();
        int tetraminoY = stateWithNewTetramino.getStage().getTetraminoY();
        State expectedState;
        State newState = unitTestService.moveFarLeft(stateWithNewTetramino,0);
        Tetramino tetramino = newState.getStage().getTetramino();
        int collapsedLayersCount = newState.getStage().getCollapsedLayersCount();
        switch (unitTestService.getShapeTypeByTetramino(newState.getStage().getTetramino()).toString()) {
            case "L", "I" -> expectedState = new State(unitTestService.makeStageWithOnlyLeftUnfilledRows(collapsedLayersCount).setTetramino(tetramino, -1, tetraminoY), true, new Player("Tester", collapsedLayersCount * 10));
            default -> expectedState = new State(unitTestService.makeStageWithOnlyLeftUnfilledRows(collapsedLayersCount).setTetramino(tetramino, 0, tetraminoY), true, new Player("Tester", collapsedLayersCount * 10));
        }
        log.info("Tetramino initial position x=" + tetraminoX + " y=" + tetraminoY);
        log.info("moveLeft 13 times is performed");
        log.info("Tetramino moveLeft 13 times new position x=" + newState.getStage().getTetraminoX() + " y=" + newState.getStage().getTetraminoY());
        log.info("Tetramino type " + unitTestService.getShapeTypeByTetramino(newState.getStage().getTetramino()));
        Assert.assertEquals(newState, expectedState);
    }

    @Test(dataProvider = "data", groups = {"tetraminoBehaviourChecks"})
    public void doesTetraminoMoveDown(State state) {
        log.info("doesTetraminoMoveDown Test start");
        State stateWithNewTetramino = state.createStateWithNewTetramino().orElse(state);
        log.info("new tetramino is called with the shape type " + unitTestService.getShapeTypeByTetramino(stateWithNewTetramino.getStage().getTetramino()));
        int tetraminoX = stateWithNewTetramino.getStage().getTetraminoX();
        int tetraminoY = stateWithNewTetramino.getStage().getTetraminoY();
        State newState = stateWithNewTetramino.moveDown(1).orElse(stateWithNewTetramino);
        Tetramino tetramino = newState.getStage().getTetramino();
        int collapsedLayersCount = newState.getStage().getCollapsedLayersCount();
        State expectedState = new State(unitTestService.makeStageWithOnlyLeftUnfilledRows(collapsedLayersCount).setTetramino(tetramino, tetraminoX, tetraminoY + 1), true, new Player("Tester", collapsedLayersCount * 10));
        log.info("Tetramino initial position x=" + stateWithNewTetramino.getStage().getTetraminoX() + " y=" + stateWithNewTetramino.getStage().getTetraminoY());
        log.info("moveDown is called");
        log.info("Tetramino after moveDown with step 1 new position x=" + newState.getStage().getTetraminoX() + " y=" + newState.getStage().getTetraminoY());
        Assert.assertEquals(newState, expectedState);
    }

    @Test(dataProvider = "data", groups = {"tetraminoBehaviourChecks"})
    public void doesTetraminoMoveDownStopAtUnfilledLayers(State state) {
        log.info("doesTetraminoMoveDownStopAtUnfilledLayers Test start");
        State stateWithNewTetramino = state.createStateWithNewTetramino().orElse(state);
        log.info("new tetramino is called with the shape type " + unitTestService.getShapeTypeByTetramino(stateWithNewTetramino.getStage().getTetramino()));
        int tetraminoX = stateWithNewTetramino.getStage().getTetraminoX();
        int tetraminoY = stateWithNewTetramino.getStage().getTetraminoY();
        State expectedState;
        State newState = unitTestService.moveDeepDown(stateWithNewTetramino,0);
        Tetramino tetramino = newState.getStage().getTetramino();
        int collapsedLayersCount = newState.getStage().getCollapsedLayersCount();
        switch (unitTestService.getShapeTypeByTetramino(newState.getStage().getTetramino()).toString()) {
            case "L", "J" -> expectedState = new State(unitTestService.makeStageWithOnlyLeftUnfilledRows(collapsedLayersCount).setTetramino(tetramino, tetraminoX, 15), true, new Player("Tester", collapsedLayersCount * 10));
            case "K" -> expectedState = new State(unitTestService.makeStageWithOnlyLeftUnfilledRows(collapsedLayersCount).setTetramino(tetramino, tetraminoX, 17), true, new Player("Tester", collapsedLayersCount * 10));
            default -> expectedState = new State(unitTestService.makeStageWithOnlyLeftUnfilledRows(collapsedLayersCount).setTetramino(tetramino, tetraminoX, 16), true, new Player("Tester", collapsedLayersCount * 10));
        }
        log.info("Tetramino initial position x=" + tetraminoX + " y=" + tetraminoY);
        log.info("moveDown 25 times is performed");
        log.info("Tetramino moveDown 25 times new position x=" + newState.getStage().getTetraminoX() + " y=" + newState.getStage().getTetraminoY());
        log.info("Tetramino type " + unitTestService.getShapeTypeByTetramino(newState.getStage().getTetramino()));
        Assert.assertEquals(newState, expectedState);
    }

    @Test(dataProvider = "data", groups = {"tetraminoBehaviourChecks"})
    public void doesTetraminoRotate(State state) {
        log.info("doesTetraminoRotate Test start");
        State stateWithNewTetramino = state.createStateWithNewTetramino().orElse(state);
        log.info("new tetramino is called with the shape type " + unitTestService.getShapeTypeByTetramino(stateWithNewTetramino.getStage().getTetramino()));
        int tetraminoX = stateWithNewTetramino.getStage().getTetraminoX();
        int tetraminoY = stateWithNewTetramino.getStage().getTetraminoY();
        State newState = stateWithNewTetramino.rotate().orElse(stateWithNewTetramino);
        Tetramino newTetramino = new Tetramino(unitTestService.rotateMatrix(stateWithNewTetramino.getStage().getTetramino().getShape()));
        int collapsedLayersCount = newState.getStage().getCollapsedLayersCount();
        State expectedState = new State(unitTestService.makeStageWithOnlyLeftUnfilledRows(collapsedLayersCount).setTetramino(newTetramino, tetraminoX, tetraminoY), true, new Player("Tester", collapsedLayersCount * 10));
        log.info("Tetramino initial shape " + unitTestService.matrixToString(stateWithNewTetramino.getStage().getTetramino().getShape()));
        log.info("Tetramino after rotate new shape " + unitTestService.matrixToString(newState.getStage().getTetramino().getShape()));
        Assert.assertEquals(newState, expectedState);
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
