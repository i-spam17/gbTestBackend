package hw3;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static helpers.VarData.*;

public class ImgurTest {
    static String albumName = "album test name";
    static String imageName = "myTestImgFromJava";

    static RequestSpecification requestSpecification = given()
            .headers(AUTH_TEXT, "Bearer " + AUTH_TOKEN)
            .log()
            .all()
            .baseUri("https://api.imgur.com/3");

    @Story("Imgur Image")
    @Feature("Тестирование Image API")

    @DisplayName("Загрузка тестового изображения")
    public static List<String> uploadImageAndGetHash() {
        Response resp = requestSpecification
                .when()
                .multiPart("image", new File(TEST_IMAGE_PATH))
                .multiPart("type", "png")
                .multiPart("title", imageName)
                .post("/upload")
                .prettyPeek()
                .then()
                .statusCode(200)
                .contentType("application/json")
                .extract()
                .response();

        String imageHash = resp.jsonPath().getString("data.id");
        String imageDeleteHash = resp.jsonPath().getString("data.deletehash");

        return Arrays.asList(imageHash, imageDeleteHash);
    }

    @DisplayName("Загрузка тестового альбома")
    public static List<String> uploadAlbumAndGetHash() {
        Response resp = requestSpecification
                .when()
                .multiPart("title", albumName)
                .post("/album")
                .prettyPeek()
                .then()
                .statusCode(200)
                .contentType("application/json")
                .extract()
                .response();

        String albumHash = resp.jsonPath().getString("data.id");
        String albumDeleteHash = resp.jsonPath().getString("data.deletehash");

        return Arrays.asList(albumHash, albumDeleteHash);
    }

    @BeforeAll
    static void setUp() {
        RestAssured.filters(new AllureRestAssured());
    }

    @DisplayName("Проверка данных пользователя")
    @Test
    void getAccountInfoTest() {
        requestSpecification
                .when()
                .get("/account/" + USER_NAME)
                .prettyPeek()
                .then()
                .statusCode(200)
                .contentType("application/json")
                .and()
                .body("data.url", is(USER_NAME));
    }

    @DisplayName("Проверка создания альбома")
    @Test
    void createAlbumTest() {
        requestSpecification
                .when()
                .multiPart("title", albumName)
                .post("/album")
                .prettyPeek()
                .then()
                .statusCode(200)
                .contentType("application/json")
                .and()
                .body("success", is(true));
    }

    @DisplayName("Проверка создания альбома")
    @Test
    void checkInfoCreateAlbumTest() {
        Response getAlbumInfo = requestSpecification
                .when()
                .get("/album/" + uploadAlbumAndGetHash().get(0))
                .prettyPeek()
                .then()
                .statusCode(200)
                .contentType("application/json")
                .extract()
                .response();

        assertThat(getAlbumInfo.jsonPath().getString("data.id"), equalTo(uploadAlbumAndGetHash().get(0)));
        assertThat(getAlbumInfo.jsonPath().getString("data.title"), equalTo(albumName));
        assertThat(getAlbumInfo.jsonPath().getString("data.deletehash"), equalTo(uploadAlbumAndGetHash().get(1)));
    }

    @DisplayName("Проверка загрузки изображения")
    @Test
    void uploadImageTest() {
        Response getImageInfo = requestSpecification
                .when()
                .get("/image/" + uploadImageAndGetHash().get(0))
                .prettyPeek()
                .then()
                .statusCode(200)
                .contentType("application/json")
                .extract()
                .response();

        assertThat(getImageInfo.jsonPath().getString("data.id"), equalTo(uploadImageAndGetHash().get(0)));
        assertThat(getImageInfo.jsonPath().getString("data.deletehash"), equalTo(uploadImageAndGetHash().get(1)));
    }

    @DisplayName("Проверка загрузки изображения в альбом")
    @Test
    void uploadImageInAlbumTest() {
        Response createImage = requestSpecification
                .when()
                .multiPart("image", new File(TEST_IMAGE_PATH))
                .multiPart("album", uploadAlbumAndGetHash().get(0))
                .multiPart("type", "png")
                .multiPart("title", imageName)
                .post("/upload")
                .prettyPeek()
                .then()
                .statusCode(200)
                .contentType("application/json")
                .extract()
                .response();

        Response getImageInfo = requestSpecification
                .when()
                .get("/album/" + uploadAlbumAndGetHash().get(0))
                .prettyPeek()
                .then()
                .statusCode(200)
                .contentType("application/json")
                .extract()
                .response();

        assertThat(getImageInfo.jsonPath().getString("data.images[0].id"), equalTo(uploadImageAndGetHash().get(0)));
    }

    @ParameterizedTest
    @CsvSource({"title, new title", "description, new description"})
    @DisplayName("Проверка обновления названия изображения")
    @Test
    void updateImageInfoTest(String id, String name) {
        //change image title
        Response getUpdateImageInfoTitle = requestSpecification
                .when()
                .multiPart(id, name)
                .post("/image/" + uploadImageAndGetHash().get(0))
                .prettyPeek()
                .then()
                .statusCode(200)
                .contentType("application/json")
                .extract()
                .response();
        assertThat(getUpdateImageInfoTitle.jsonPath().getString("data"), equalTo("true"));
        assertThat(getUpdateImageInfoTitle.jsonPath().getString("success"), equalTo("true"));

        //check updated image title in image
        Response getImageInfo = requestSpecification
                .when()
                .get("/image/" + uploadImageAndGetHash().get(0))
                .prettyPeek()
                .then()
                .statusCode(200)
                .contentType("application/json")
                .extract()
                .response();
        assertThat(getImageInfo.jsonPath().getString("data." + id), equalTo(name));

        //check updated image title in album
        Response getAlbumInfo = requestSpecification
                .when()
                .get("/album")
                .prettyPeek()
                .then()
                .statusCode(200)
                .contentType("application/json")
                .extract()
                .response();
        assertThat(getAlbumInfo.jsonPath().getString("data.images[0]." + id), equalTo(name));
    }

    @DisplayName("Проверка изменения Favorite-статуса изображения")
    @Test
    void updateFavoriteStatusTest() {
        requestSpecification
                .when()
                .post("/image/" + uploadImageAndGetHash().get(0) + "/favorite")
                .prettyPeek()
                .then()
                .statusCode(200)
                .and()
                .body("data", is("favorited"))
                .body("success", is(true));
    }

    @DisplayName("Проверка обновления Favorite-статуса изображения")
    @Test
    void checkUpdateFavoriteStatusTest() {
        //check updated image favorite status in image
        Response getImageInfo = requestSpecification
                .when()
                .get("/image/" + uploadImageAndGetHash().get(0))
                .prettyPeek()
                .then()
                .statusCode(200)
                .contentType("application/json")
                .extract()
                .response();
        assertThat(getImageInfo.jsonPath().getString("data.favorite"), equalTo("true"));

        //check updated image favorite status in album
        Response getAlbumInfo = requestSpecification
                .when()
                .get("/album")
                .prettyPeek()
                .then()
                .statusCode(200)
                .contentType("application/json")
                .extract()
                .response();
        assertThat(getAlbumInfo.jsonPath().getString("data.images[0].favorite"), equalTo("true"));
    }

    @DisplayName("Проверка обновления description изображения не авторизованным пользователем")
    @Test
    void updateImageInfoUnAuthTest() {
        //change image description
        Response getUpdateImageInfoTitle = requestSpecification
                .when()
                .multiPart("description", "new description")
                .post("/image/" + uploadImageAndGetHash().get(1))
                .prettyPeek()
                .then()
                .statusCode(200)
                .contentType("application/json")
                .extract()
                .response();
        assertThat(getUpdateImageInfoTitle.jsonPath().getString("data"), equalTo("true"));
        assertThat(getUpdateImageInfoTitle.jsonPath().getString("success"), equalTo("true"));

        //check updated image description in image
        Response getImageInfo = requestSpecification
                .when()
                .get("/image/" + uploadImageAndGetHash().get(0))
                .prettyPeek()
                .then()
                .statusCode(200)
                .contentType("application/json")
                .extract()
                .response();
        assertThat(getImageInfo.jsonPath().getString("data.description"), equalTo("new description"));

        //check updated image description in album
        Response getAlbumInfo = requestSpecification
                .when()
                .get("/album/" + uploadAlbumAndGetHash().get(0))
                .prettyPeek()
                .then()
                .statusCode(200)
                .contentType("application/json")
                .extract()
                .response();
        assertThat(getAlbumInfo.jsonPath().getString("data.images[0].description"), equalTo("new description"));
    }

    @AfterAll
    static void tearDown() {
        requestSpecification
                .when()
                .delete("/image/" + uploadImageAndGetHash().get(0))
                .prettyPeek()
                .then()
                .statusCode(200);

        requestSpecification
                .when()
                .delete("/album/" + uploadAlbumAndGetHash().get(0))
                .prettyPeek()
                .then()
                .statusCode(200);
    }
}
