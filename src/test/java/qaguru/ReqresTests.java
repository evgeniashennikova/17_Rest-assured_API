package qaguru;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class ReqresTests {

    @BeforeAll
    static void setUp() {
        RestAssured.baseURI = "https://reqres.in/";
    }

    @Test
    void successfulLogin() {

        String data = "{ \"email\": \"eve.holt@reqres.in\", \"password\": \"cityslicka\" }";
        given()
                .contentType(JSON)
                .body(data)
                .when()
                .post("/api/login")
                .then()
                .statusCode(200)
                .body("token", is("QpwL5tke4Pnpja7X4"));

    }

    @Test
    void unsuccessfulLogin() {

        String data = "{ \"email\": \"peter@klaven\" }";

        given()
                .contentType(JSON)
                .body(data)
                .when()
                .post("/api/login")
                .then()
                .statusCode(400)
                .body("error", is("Missing password"));

    }

    @Test
    void checkListUsers() {

        get("/api/users?page=2")
                .then()
                .statusCode(200)
                .body("total", is(12));

    }

    @Test
    void checkSingleUser() {

        //"{ \"id\": 9, \"email\": \"tobias.funke@reqres.in\", \"first_name\": \"Tobias\"," +
        //        " \"last_name\": \"Funke\", \"avatar\": \"https://reqres.in/img/faces/9-image.jpg\" }";

        get("/api/users/9")
                .then()
                .statusCode(200)
                .body("data.id", is(9), "data.first_name", is("Tobias"),
                        "data.last_name", is("Funke"));
    }

    @Test
    void checkSingleUserNotFound() {

        get("/api/users/25")
                .then()
                .statusCode(404)
                .body(is("{}"));

    }

    @Test
    void createUsersTest() {

        String data = "{ \"name\": \"Teddy\", \"job\": \"QA\" }";

        Response response = given()
                .contentType(JSON)
                .body(data)
                .when()
                .post("/api/users")
                .then()
                .extract().response();

        System.out.println(response.asString());

        given()
                .then()
                .statusCode(201)
                .body("name", is("Teddy"), "job", is("QA"),
                        "id", notNullValue(), "createdAt", notNullValue());

    }

    @Test
    void checkUsersUpdate() {

        String data = "{ \"name\": \"Teddy\", \"job\": \"Lead QA\" }";

        given()
                .contentType(JSON)
                .body(data)
                .when()
                .put("/api/users/504")
                .then()
                .statusCode(200)
                .body("name", is("Teddy"), "job", is("Lead QA"),
                        "updatedAt", notNullValue());

    }

    @Test
    void checkDeleteUser() {

        given()
                .when()
                .delete("api/users/12")
                .then()
                .statusCode(204);

    }

    @Test
    void checkRegisterUnsuccessful() {

        String data = "{ \"email\": \"qwerty@gmail\" }";

        given()
                .contentType(JSON)
                .body(data)
                .when()
                .post("/api/register")
                .then()
                .statusCode(400)
                .body("error", is("Missing password"));

    }

}
