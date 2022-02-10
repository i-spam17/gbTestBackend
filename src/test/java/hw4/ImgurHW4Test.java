package hw4;

import hw4.dto.*;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.File;

import static hw4.Constants.*;
import static hw4.EndPoints.*;
import static hw4.RequestResponseSpecifications.requestSpec;
import static hw4.RequestResponseSpecifications.responseSpec;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


@Story("Imgur Image")
@Feature("Тестирование Image API")
public class ImgurHW4Test {
    static String imageHash;
    static String imageDeleteHash;
    static String albumHash;
    static String albumDeleteHash;


//    @DisplayName("Загрузка тестового изображения, Загрузка тестового альбома")
    @BeforeAll
    static void setUp12() {
        //create Album
        AlbumUploadJson respAlbum = requestSpec
                .when()
                .multiPart("title", ALBUM_NAME)
                .post(album)
                .prettyPeek()
                .then()
                .spec(responseSpec)
                .extract()
                .body()
                .as(AlbumUploadJson.class);

        albumHash = respAlbum.getData().getId();
        albumDeleteHash = respAlbum.getData().getDeletehash();

        //upload image in created album
        ImageJson respImg = requestSpec
                .when()
                .multiPart("image", Helpers.encodeImageToBase64(new File(IMAGE_PATH)))
                .multiPart("type", "base64")
                .multiPart("title", IMAGE_NAME)
                .multiPart("album", albumHash)
                .post(upload)
                .prettyPeek()
                .then()
                .spec(responseSpec)
                .extract()
                .body()
                .as(ImageJson.class);

        imageHash = respImg.getData().getId();
        imageDeleteHash = respImg.getData().getDeletehash();

//        RestAssured.filters(new AllureRestAssured());
    }

    @BeforeEach
    void setUp() {
//        RestAssured.filters(new AllureRestAssured()
//                            .setRequestTemplate("src/test/resources/tpl/http-request.ftl")
//                            .setResponseTemplate("src/test/resources/tpl/http-response.ftl"));
//        RestAssured.filters(new AllureRestAssured()
//                .setRequestAttachmentName("requestAttachmentName")
//                .setResponseAttachmentName("responseAttachmentName")
//                .setRequestTemplate("src/test/resources/tpl/http-request.ftl")
//                .setResponseTemplate("src/test/resources/tpl/http-response.ftl"));
    }

    @DisplayName("Проверка данных пользователя")
    @Step("проверка пользовательских данных")
    @Test
    void getAccountInfoTest() {
        AccountGetInfo response = requestSpec
                .when()
                .get(account + USER_NAME)
                .prettyPeek()
                .then()
                .spec(responseSpec)
                .extract()
                .body()
                .as(AccountGetInfo.class);

        assertThat(response.getData().getUrl(), equalTo(USER_NAME));
    }

    @DisplayName("Проверка создания альбома")
    @Step("проверка создания альбома")
    @Test
    void checkInfoCreateAlbumTest() {
        AlbumJson getAlbumInfoResponse = requestSpec
                .when()
                .get(album + albumHash)
                .prettyPeek()
                .then()
                .spec(responseSpec)
                .extract()
                .body()
                .as(AlbumJson.class);

        assertThat(getAlbumInfoResponse.getData().getId(), equalTo(albumHash));
        assertThat(getAlbumInfoResponse.getData().getTitle(), equalTo(ALBUM_NAME));
        assertThat(getAlbumInfoResponse.getData().getDeletehash(), equalTo(albumDeleteHash));
    }

    @DisplayName("Проверка загрузки изображения")
    @Step("Проверка загрузки изображения")
    @Test
    void uploadInfoImageTest() {
        ImageAlbumInfoJson getImageInfo = requestSpec
                .when()
                .get(image + imageHash)
                .prettyPeek()
                .then()
                .spec(responseSpec)
                .extract()
                .body()
                .as(ImageAlbumInfoJson.class);

        assertThat(getImageInfo.getData().getId(), equalTo(imageHash));
        assertThat(getImageInfo.getData().getDeletehash(), equalTo(imageDeleteHash));
    }

    @DisplayName("Проверка инфо о загрузке изображения в альбом")
    @Step("Проверка инфо о загрузке изображения в альбом")
    @Test
    void checkImageInfoWhenUploadInAlbumTest() {
        AlbumJson getImageInfo = requestSpec
                .when()
                .get(album + albumHash)
                .prettyPeek()
                .then()
                .spec(responseSpec)
                .extract()
                .body()
                .as(AlbumJson.class);

        assertThat(getImageInfo.getData().getImages().get(0).getId(), equalTo(imageHash));
    }

    @DisplayName("Проверка обновления названия изображения")
    @Step("Проверка обновления названия изображения")
    @ParameterizedTest
    @CsvSource({"title, new_title", "description, new_description"})
    void updateImageInfoTest(String id, String name) {
        //change image title
        DataJson getUpdateImageInfoTitle = requestSpec
                .when()
                .multiPart(id, name)
                .post(image + imageHash)
                .prettyPeek()
                .then()
                .spec(responseSpec)
                .extract()
                .body()
                .as(DataJson.class);
        assertThat(getUpdateImageInfoTitle.getData(), equalTo(true));
        assertThat(getUpdateImageInfoTitle.getStatus(), equalTo(200));
        assertThat(getUpdateImageInfoTitle.getSuccess(), equalTo(true));

        //check updated image title in image
        Response getImageInfo = requestSpec
                .when()
                .get(image + imageHash)
                .prettyPeek()
                .then()
                .spec(responseSpec)
                .extract()
                .response();
        assertThat(getImageInfo.jsonPath().getString("data." + id), equalTo(name));

        //check updated image title in album
        Response getAlbumInfo = requestSpec
                .when()
                .get(album + albumHash)
                .prettyPeek()
                .then()
                .spec(responseSpec)
                .extract()
                .response();
        assertThat(getAlbumInfo.jsonPath().getString("data.images[0]." + id), equalTo(name));
//        assertThat(getAlbumInfo.getData().getImages().get(0).getId(), equalTo(name));
//        TODO  параметрозовать ассерт с помощью JSON-объекта не получилось!
    }

    @DisplayName("Проверка изменения Favorite-статуса изображения")
    @Step("Проверка изменения Favorite-статуса изображения")
    @Test
    void updateFavoriteStatusTest() {
        DataJson updateFavoriteStatusResponse = requestSpec
                .when()
                .post(image + imageHash + favorite)
                .prettyPeek()
                .then()
                .spec(responseSpec)
                .extract()
                .body()
                .as(DataJson.class);

        assertThat(updateFavoriteStatusResponse.getStatus(), equalTo(200));
        assertThat(updateFavoriteStatusResponse.getData(), equalTo("favorited"));
        assertThat(updateFavoriteStatusResponse.getSuccess(), equalTo(true));

        //check updated image favorite status in image
        ImageAlbumInfoJson getImageInfo = requestSpec
                .when()
                .get(image + imageHash)
                .prettyPeek()
                .then()
                .spec(responseSpec)
                .extract()
                .body()
                .as(ImageAlbumInfoJson.class);
        assertThat(getImageInfo.getData().getFavorite(), equalTo(true));

        //check updated image favorite status in album
        ImageAlbumInfoJson getAlbumInfo = requestSpec
                .when()
                .get(album + albumHash + image + imageHash)
                .prettyPeek()
                .then()
                .spec(responseSpec)
                .extract()
                .body()
                .as(ImageAlbumInfoJson.class);
        assertThat(getAlbumInfo.getData().getFavorite(), equalTo(true));
    }

    @DisplayName("Проверка обновления description изображения не авторизованным пользователем")
    @Step("Проверка обновления description изображения не авторизованным пользователем")
    @Test
    void updateImageInfoUnAuthTest() {
        //change image description
        DataJson getUpdateImageInfoTitle = requestSpec
                .when()
                .multiPart("description", "new description")
                .post(image + imageDeleteHash)
                .prettyPeek()
                .then()
                .spec(responseSpec)
                .extract()
                .body()
                .as(DataJson.class);
        assertThat(getUpdateImageInfoTitle.getData(), equalTo(true));
        assertThat(getUpdateImageInfoTitle.getSuccess(), equalTo(true));
        assertThat(getUpdateImageInfoTitle.getStatus(), equalTo(200));

        //check updated image description in image
        ImageAlbumInfoJson getImageInfo = requestSpec
                .when()
                .get(image + imageHash)
                .prettyPeek()
                .then()
                .spec(responseSpec)
                .extract()
                .body()
                .as(ImageAlbumInfoJson.class);
        assertThat(getImageInfo.getData().getDescription(), equalTo("new description"));

        //check updated image description in album
        AlbumJson getAlbumInfo = requestSpec
                .when()
                .get(album + albumHash)
                .prettyPeek()
                .then()
                .spec(responseSpec)
                .extract()
                .body()
                .as(AlbumJson.class);
        assertThat(getAlbumInfo.getData().getImages().get(0).getDescription(), equalTo("new description"));
    }

    @AfterAll
    static void tearDown() {
        requestSpec
                .when()
                .delete(image + imageHash)
                .prettyPeek()
                .then()
                .spec(responseSpec);

        requestSpec
                .when()
                .delete(album + albumHash)
                .prettyPeek()
                .then()
                .spec(responseSpec);
    }
}
