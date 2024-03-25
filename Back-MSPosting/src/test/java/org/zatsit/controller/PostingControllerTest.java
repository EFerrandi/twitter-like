package org.zatsit.controller;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.zatsit.domain.PostingKafkaAdapter;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@QuarkusTest
class PostingControllerTest {

    @InjectMock
    PostingKafkaAdapter mockPostingKafkaAdapter;

    String baseUri = "/posting";

    @Test
    void getPosts_Success() {
    }

    @Test
    void getPosts_Fail() {
    }

    @Test
    void createPost_Success() {
        UUID exceptedUuid = UUID.fromString("018e7533-d58f-7cb7-a635-590fea56ea37");
        when(mockPostingKafkaAdapter.sendToKafka(Mockito.anyString(), Mockito.any()))
                .thenReturn(exceptedUuid);

        String post = """
                {
                    "user":{
                        "uuid":"126a7b62-0940-4fa0-8c91-3015abf6d125",
                        "username":"TestPerson",
                        "profilePictureUrl":"http://picture.test"
                    },
                    "message":"This is a valid test",
                    "image":"http://my.img"
                }
                """;
        given()
                .contentType(ContentType.JSON)
                .body(post)
                .when()
                .put(STR."\{baseUri}/createPost")
                .then()
                .statusCode(202)
                .body(equalTo(STR."{\"PostUuidpostUuid\":\"\{exceptedUuid.toString()}\"}"));
    }

    @Test
    void createPost_Fail_EmptyDto() {
        String post = "{}";
        String expected = "{\"title\":\"Constraint Violation\"," +
                "\"status\":400," +
                "\"violations\":[{\"field\":\"createPost.postDto.contentValid\"," +
                "\"message\":\"No content in the post\"}]}";
        given()
                .contentType(ContentType.JSON)
                .body(post)
                .when()
                .put(STR."\{baseUri}/createPost")
                .then()
                .statusCode(400)
                .body(equalTo(expected));
    }

    @Test
    void createPost_Fail_InvalidDto() {
        String post = """
                {"user":{}}""";
        String expectedViolation1 = "[field:createPost.postDto.user.uuid, message:No user UUID]";
        String expectedViolation2 = "[field:createPost.postDto.user.username, message:Invalid Username]";
        String expectedViolation3 = "[field:createPost.postDto.contentValid, message:No content in the post]";
        given()
                .contentType(ContentType.JSON)
                .body(post)
                .when()
                .put(STR."\{baseUri}/createPost")
                .then()
                .statusCode(400)
                .body("title", equalTo("Constraint Violation"))
                .body("violations.size()", equalTo(3))
                .body("violations[0].toString()", either(containsString(expectedViolation1))
                        .or(containsString(expectedViolation2))
                        .or(containsString(expectedViolation3)))
                .body("violations[1].toString()", either(containsString(expectedViolation1))
                        .or(containsString(expectedViolation2))
                        .or(containsString(expectedViolation3)))
                .body("violations[2].toString()", either(containsString(expectedViolation1))
                        .or(containsString(expectedViolation2))
                        .or(containsString(expectedViolation3)));
    }

    @Test
    void createPost_Fail_NoDto() {
        String post = "";
        String expected = "Invalid post: No post :(";
        given()
                .contentType(ContentType.JSON)
                .body(post)
                .when()
                .put(STR."\{baseUri}/createPost")
                .then()
                .statusCode(400)
                .body(equalTo(expected));
    }

    @Test
    void createPost_Fail_NoKafka() {
        doThrow(IllegalStateException.class)
                .when(mockPostingKafkaAdapter).sendToKafka(Mockito.anyString(), Mockito.any());

        String post = """
                {
                    "user":{
                        "uuid":"126a7b62-0940-4fa0-8c91-3015abf6d125",
                        "username":"TestPerson",
                        "profilePictureUrl":"http://picture.test"
                    },
                    "message":"This is a valid test",
                    "image":"http://my.img"
                }
                """;
        given()
                .contentType(ContentType.JSON)
                .body(post)
                .when()
                .put(STR."\{baseUri}/createPost")
                .then()
                .statusCode(500)
                .body(equalTo("Kafka resource is unavailable"));
    }

//    @Test
//    void updatePost_Success() {
//        String post = """
//                {
//                    "uuid":"018e752b-d38b-7ca8-9231-8ad7cba7316f",
//                    "user":{
//                        "uuid":"126a7b62-0940-4fa0-8c91-3015abf6d125",
//                        "username":"TestPerson",
//                        "profilePictureUrl":"http://picture.test"
//                    },
//                    "message":"This is a valid test",
//                    "image":"http://my.img"
//                }
//                """;
//        given()
//                .contentType(ContentType.JSON)
//                .body(post)
//                .when()
//                .patch(STR."\{baseUri}/updatePost")
//                .then()
//                .statusCode(201);
//    }
//
//    @Test
//    void updatePost_Fail_EmptyDto() {
//        String post = "{}";
//        String expected = "{\"title\":\"Constraint Violation\"," +
//                "\"status\":400," +
//                "\"violations\":[{\"field\":\"createPost.postDto.contentValid\"," +
//                "\"message\":\"No content in the post\"}]}";
//        given()
//                .contentType(ContentType.JSON)
//                .body(post)
//                .when()
//                .patch(STR."\{baseUri}/updatePost")
//                .then()
//                .statusCode(400)
//                .body(equalTo(expected));
//    }
//
//    @Test
//    void updatePost_Fail_InvalidDto() {
//        String post = """
//                {"user":{}}""";
//        String expectedViolation1 = "[field:createPost.postDto.user.uuid, message:No user UUID]";
//        String expectedViolation2 = "[field:createPost.postDto.user.username, message:Invalid Username]";
//        String expectedViolation3 = "[field:createPost.postDto.contentValid, message:No content in the post]";
//        given()
//                .contentType(ContentType.JSON)
//                .body(post)
//                .when()
//                .patch(STR."\{baseUri}/updatePost")
//                .then()
//                .statusCode(400)
//                .body("title", equalTo("Constraint Violation"))
//                .body("violations.size()", equalTo(3))
//                .body("violations[0].toString()", either(containsString(expectedViolation1))
//                        .or(containsString(expectedViolation2))
//                        .or(containsString(expectedViolation3)))
//                .body("violations[1].toString()", either(containsString(expectedViolation1))
//                        .or(containsString(expectedViolation2))
//                        .or(containsString(expectedViolation3)))
//                .body("violations[2].toString()", either(containsString(expectedViolation1))
//                        .or(containsString(expectedViolation2))
//                        .or(containsString(expectedViolation3)));
//    }
//
//    @Test
//    void cupdatePost_Fail_NoDto() {
//        String post = "";
//        String expected = "Invalid post: No post :(";
//        given()
//                .contentType(ContentType.JSON)
//                .body(post)
//                .when()
//                .patch(STR."\{baseUri}/updatePost")
//                .then()
//                .statusCode(400)
//                .body(equalTo(expected));
//    }
//
//    @Test
//    void updatePost_Fail_NoKafka() {
//        doThrow(IllegalStateException.class)
//                .when(mockPostingKafkaAdapter).sendToKafka(Mockito.anyString(), Mockito.any());
//
//        String post = """
//                {
//                    "user":{
//                        "uuid":"126a7b62-0940-4fa0-8c91-3015abf6d125",
//                        "username":"TestPerson",
//                        "profilePictureUrl":"http://picture.test"
//                    },
//                    "message":"This is a valid test",
//                    "image":"http://my.img"
//                }
//                """;
//        given()
//                .contentType(ContentType.JSON)
//                .body(post)
//                .when()
//                .patch(STR."\{baseUri}/updatePost")
//                .then()
//                .statusCode(500)
//                .body(equalTo("Kafka resource is unavailable"));
//    }
}