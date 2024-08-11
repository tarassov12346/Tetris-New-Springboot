package api_tests;

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
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.*;
import org.testng.internal.collections.Pair;


import java.util.*;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = {GameController.class, StartGameConfiguration.class,
                PlayGameConfiguration.class, SaveGameConfiguration.class,
                RestartGameConfiguration.class, Player.class, Tetramino.class, Stage.class, State.class,
                TetrisNewApplication.class, APITestService.class})
public class APITest extends AbstractTestNGSpringContextTests{
    private static final Logger log = Logger.getLogger(APITest.class);

    @Autowired
    APITestService apiTestService;

    @BeforeClass
    public void doBeforeTests() {
        log.info("APITests start");
    }

    @BeforeMethod
    public void doBeforeEachTestMethod() {
        log.info("Test Method  is called");
    }

    @DataProvider
    public Object[][] dataProviderMethod() {
        return new Object[][]{
                {"hello"},
                {"save"},
                {"restart"},
                {"0"},
                {"1"},
                {"2"},
                {"3"},
                {"4"}
        };
    }

    @Test(description = "checks if client requests receive successful responses from the server", dataProvider = "dataProviderMethod")
    public void doRequestsGetSuccessfulResponses(String data) {
        log.info("testing for 200 OK response  request-  /" + data);
        RestAssured.when().get("http://localhost:8080/" + data).then().assertThat().statusCode(200);
    }

    @Test(description = "checks if the Game is ON should appear after 'start' request is sent")
    public void shouldGameIsONAppear() {
        log.info("shouldGameIsONAppear Test start");
        Response responseToHello =
                given().baseUri("http://localhost:8080/")
                        .when()
                        .get("hello")
                        .then()
                        .extract()
                        .response();
        String id = responseToHello.sessionId();
        Response response =
                given().sessionId(id)
                        .baseUri("http://localhost:8080/")
                        .when()
                        .get("start")
                        .then()
                        .extract()
                        .response();
        String bodyTxt = response.getBody().asString();
        Document document = Jsoup.parse(bodyTxt);
        Element contentElement = document.getElementById("header");
        log.info(contentElement.text());
        Assert.assertTrue(contentElement.text().contains("Game is ON"));
    }

    @Test(description = "checks if Player name should  appear after 'start' request is sent")
    public void shouldPlayerNameAppear() {
        log.info("shouldPlayerNameAppear Test start");
        boolean isPLayerNamePresent = false;
        List<String> list = new ArrayList<>();
        list.add("Oswaldo");
        list.add("Tommy");
        list.add("Dunny");
        list.add("Bonny");
        list.add("Ira");
        list.add("Wolfy");
        Response responseToHello =
                given().baseUri("http://localhost:8080/")
                        .when()
                        .get("hello")
                        .then()
                        .extract()
                        .response();
        String id = responseToHello.sessionId();
        Response response =
                given().sessionId(id)
                        .baseUri("http://localhost:8080/")
                        .when()
                        .get("start")
                        .then()
                        .extract()
                        .response();
        String bodyTxt = response.getBody().asString();
        Document document = Jsoup.parse(bodyTxt);
        Element contentElement = document.getElementById("content");
        for (String s : list) {
            if (contentElement.text().contains(s)) {
                log.info(contentElement.text());
                isPLayerNamePresent = true;
            }
        }
        Assert.assertTrue(isPLayerNamePresent);
    }

    @Test(description = "checks if the new tetramino image should appear after 'start' request is sent")
    public void shouldTetraminoImageAppear() {
        log.info("shouldTetraminoImageAppear Test start");
        boolean isTetraminoImagePresent = false;
        String[] images = {"I.png", "J.png", "K.png", "L.png", "O.png", "S.png", "T.png", "Z.png"};
        Response responseToHello =
                given().baseUri("http://localhost:8080/")
                        .when()
                        .get("hello")
                        .then()
                        .extract()
                        .response();

        String id = responseToHello.sessionId();
        Response response =
                given().sessionId(id)
                        .baseUri("http://localhost:8080/")
                        .when()
                        .get("start")
                        .then()
                        .extract()
                        .response();
        String bodyTxt = response.getBody().asString();
        Document document = Jsoup.parse(bodyTxt);
        Element tableElement = document.select("table").get(1);
        Elements rows = tableElement.select("tr");// разбиваем нашу таблицу на строки по тегу
        //по номеру индекса получает строку
        for (Element row : rows) {
            Elements cols = row.select("td");// разбиваем полученную строку по тегу  на столбы
            for (int j = 0; j < 12; j++) {
                for (int k = 0; k < 8; k++) {
                    if (cols.get(j).toString().contains(images[k])) {
                        log.info(cols.get(j));
                        isTetraminoImagePresent = true;
                    }
                }
            }
        }
        Assert.assertTrue(isTetraminoImagePresent);
    }

    @Test(description = "checks if the tetramino image should move 1 step down after '0' request is sent")
    public void shouldTetraminoImageMoveDown() {
        log.info("shouldTetraminoImageMoveDown Test start");
        Response responseToHello =
                given().baseUri("http://localhost:8080/")
                        .when()
                        .get("hello")
                        .then()
                        .extract()
                        .response();
        String id = responseToHello.sessionId();
        Response responseToStart =
                given().sessionId(id)
                        .baseUri("http://localhost:8080/")
                        .when()
                        .get("start")
                        .then()
                        .extract()
                        .response();
        String bodyResponseToStartTxt = responseToStart.getBody().asString();
        Response responseToDown =
                given().sessionId(id)
                        .baseUri("http://localhost:8080/")
                        .when()
                        .get("0")
                        .then()
                        .extract()
                        .response();
        String bodyResponseToDownTxt = responseToDown.getBody().asString();
        Assert.assertEquals(apiTestService.getTetraminoCoordinates(bodyResponseToStartTxt, 1, 0), apiTestService.getTetraminoCoordinates(bodyResponseToDownTxt, 0, 0));
    }

    @Test(description = "checks if the tetramino image should move 1 position left after '2' request is sent")
    public void shouldTetraminoImageMoveLeft() {
        log.info("shouldTetraminoImageMoveLeft Test start");
        Response responseToHello =
                given().baseUri("http://localhost:8080/")
                        .when()
                        .get("hello")
                        .then()
                        .extract()
                        .response();

        String id = responseToHello.sessionId();
        Response responseToStart =
                given().sessionId(id)
                        .baseUri("http://localhost:8080/")
                        .when()
                        .get("start")
                        .then()
                        .extract()
                        .response();
        String bodyResponseToStartTxt = responseToStart.getBody().asString();
        Response responseToMoveLeft =
                given().sessionId(id)
                        .baseUri("http://localhost:8080/")
                        .when()
                        .get("2")
                        .then()
                        .extract()
                        .response();
        String bodyResponseToMoveLeftTxt = responseToMoveLeft.getBody().asString();
        Assert.assertEquals(apiTestService.getTetraminoCoordinates(bodyResponseToStartTxt, 1, -1), apiTestService.getTetraminoCoordinates(bodyResponseToMoveLeftTxt, 0, 0));
    }

    @Test(description = "checks if the tetramino image should move 1 position right after '3' request is sent")
    public void shouldTetraminoImageMoveRight() {
        log.info("shouldTetraminoImageMoveRight Test start");
        Response responseToHello =
                given().baseUri("http://localhost:8080/")
                        .when()
                        .get("hello")
                        .then()
                        .extract()
                        .response();
        String id = responseToHello.sessionId();
        Response responseToStart =
                given().sessionId(id)
                        .baseUri("http://localhost:8080/")
                        .when()
                        .get("start")
                        .then()
                        .extract()
                        .response();
        String bodyResponseToStartTxt = responseToStart.getBody().asString();
        Response responseToMoveRight =
                given().sessionId(id)
                        .baseUri("http://localhost:8080/")
                        .when()
                        .get("3")
                        .then()
                        .extract()
                        .response();
        String bodyResponseToMoveRightTxt = responseToMoveRight.getBody().asString();
        Assert.assertEquals(apiTestService.getTetraminoCoordinates(bodyResponseToStartTxt, 1, 1), apiTestService.getTetraminoCoordinates(bodyResponseToMoveRightTxt, 0, 0));
    }

    @Test(description = "checks if the tetramino image should rotate after '1' request is sent")
    public void shouldTetraminoImageRotate() {
        log.info("shouldTetraminoImageRotate Test start");
        Response responseToHello =
                given().baseUri("http://localhost:8080/")
                        .when()
                        .get("hello")
                        .then()
                        .extract()
                        .response();
        String id = responseToHello.sessionId();
        Response responseToStart =
                given().sessionId(id)
                        .baseUri("http://localhost:8080/")
                        .when()
                        .get("start")
                        .then()
                        .extract()
                        .response();
        Response responseToRotate =
                given().sessionId(id)
                        .baseUri("http://localhost:8080/")
                        .when()
                        .get("1")
                        .then()
                        .extract()
                        .response();
        String bodyResponseToRotateTxt = responseToRotate.getBody().asString();
        List<Pair<Integer, Integer>> tetraminoCoordinates = apiTestService.getTetraminoCoordinates(bodyResponseToRotateTxt, 0, 0);
        Assert.assertEquals(apiTestService.getCoordinatesCheckSample(), tetraminoCoordinates);
    }


    @AfterMethod
    public void doAfterEachTestMethod() {
        log.info("Test Method  is finished");
    }

    @AfterClass
    public void doAfterTests() {
        log.info("APITests are finished");
    }

}
