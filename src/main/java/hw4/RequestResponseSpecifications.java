package hw4;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static hw4.Constants.AUTH_TEXT;
import static hw4.Constants.AUTH_TOKEN;
import static hw4.EndPoints.base;
import static io.restassured.RestAssured.given;

public class RequestResponseSpecifications {
    public static RequestSpecification requestSpec =
//            new RequestSpecBuilder()
//                    .setBaseUri(base)
//                    .addHeader(AUTH_TEXT, "Bearer " + AUTH_TOKEN)
//                    .setContentType(ContentType.JSON)
//                    .addFilter(new AllureRestAssured())
//                    .build();

            given()
                    .log()
                    .all()
                    .filter(new AllureRestAssured().setRequestAttachmentName("sdf"))
                    .baseUri(base)
                    .headers(AUTH_TEXT, "Bearer " + AUTH_TOKEN)
    ;

    public static ResponseSpecification responseSpec = new ResponseSpecBuilder()
            .expectStatusCode(200)
            .expectStatusLine("HTTP/1.1 200 OK")
            .expectContentType(ContentType.JSON)
            .build();
}
