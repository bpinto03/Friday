package fr.uge;

import fr.uge.events.Event;
import fr.uge.users.User;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import javax.transaction.Transactional;
import javax.ws.rs.core.MediaType;
import java.util.Date;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;


@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserEventServiceTest {

    @Transactional
    private void createMultipleEventsShouldWork(List<Event> events){
        events.forEach(event -> given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(event)
                .when()
                .post("/friday/createEvent")
                .then()
                .statusCode(201));
    }

    /* Fail if it doesn't take order in charge. like ./mvnw package
    @Test
    @Order(1)
    @Transactional
    public void modifyEventShouldBe200Ok(){
        var eventToModify =  new Event(1, "Examen Java", "Examen fin de semestre de Java", "Université Gustave Eiffel",
                new Date(1641292200000L), new Date(1641299400000L), "");
        var newEvent = new Event(1, "Examen Crypto", "Examen fin de semestre de Cryptographie", "Université Gustave Eiffel",
                new Date(1641371400000L), new Date(1641377700000L), "");

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(eventToModify)
                .when()
                .post("/friday/createEvent")
                .then()
                .statusCode(201);

        given()
                .pathParams("eventId", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(newEvent)
                .when()
                .put("/friday/modifyEvent/{eventId}")
                .then()
                .statusCode(200);
        given()
                .pathParams("eventId", 1)
                .when()
                .delete("/friday/delete/{eventId}");
    }

    @Test
    @Order(2)
    @Transactional
    public void deleteEventResponseShouldBe200Ok(){
        var event = new Event(1, "Examen Java", "Examen fin de semestre de Java", "Université Gustave Eiffel",
                new Date(1646127000000L), new Date(1646134200000L), "");
        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(event)
                .when()
                .post("/friday/createEvent")
                .then()
                .statusCode(201);

        given()
                .pathParams("eventId", 2)
                .when()
                .delete("/friday/delete/{eventId}")
                .then()
                .statusCode(200);

        given()
                .pathParams("userId", 1)
                .when().get("/friday/getEventsFromUserId/{userId}")
                .then()
                .statusCode(200)
                .body(is("[]"));
    }*/

    @Test
    @Transactional
    @Order(3)
    public void deleteEventResponseShouldBe404NotFound(){
        given()
                .pathParams("eventId", -1)
                .when()
                .delete("/friday/delete/{eventId}")
                .then()
                .statusCode(404);
    }

    @Test
    @Order(4)
    @Transactional
    public void modifyEventShouldBe404NotFound(){
        var eventToModify =  new Event(1, "Examen Java", "Examen fin de semestre de Java", "Université Gustave Eiffel",
                new Date(1641292200000L), new Date(1641299400000L), "");
        var newEvent = new Event(3, "Examen Crypto", "Examen fin de semestre de Cryptographie", "Université Gustave Eiffel",
                new Date(1641371400000L), new Date(1641377700000L), "");

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(eventToModify)
                .when()
                .post("/friday/createEvent")
                .then()
                .statusCode(201);

        given()
                .pathParams("eventId", -1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(eventToModify)
                .when()
                .put("/friday/modifyEvent/{eventId}")
                .then()
                .statusCode(404);

        given()
                .pathParams("eventId", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(newEvent)
                .when()
                .put("/friday/modifyEvent/{eventId}")
                .then()
                .statusCode(404);
    }

    @Test
    @Order(5)
    @Transactional
    public void addEventResponseShouldBe201Created(){
        var event = new Event(1, "Examen Java", "Examen fin de semestre de Java", "Université Gustave Eiffel",
                new Date(1646127000000L), new Date(1646134200000L), "");
        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(event)
                .when()
                .post("/friday/createEvent")
                .then()
                .statusCode(201);
    }

    @Test
    @Order(6)
    @Transactional
    public void addEventResponseShouldBe304NotAdded() {
        //using uni to make the request asynchronous.
        Uni.createFrom().item(() -> {
            var event = new Event(1, "Examen Java", "Examen fin de semestre de Java", "Université Gustave Eiffel",
                    new Date(1646127000000L), new Date(1646134200000L), "");
            event.setEventId(45);
            given().contentType(MediaType.APPLICATION_JSON)
                    .body(event)
                    .when()
                    .post("/friday/createEvent")
                    .then()
                    .statusCode(304);
            return event;
        });
    }

    @Test
    @Order(7)
    @Transactional
    public void getEventsFromUserIdShouldBe200Ok(){
        var event1 = new Event(2, "Examen Java", "Examen fin de semestre de Java", "Université Gustave Eiffel",
                new Date(1646127000000L), new Date(1646134200000L), "");
        var event2 = new Event(2, "Examen modélisation", "Examen fin de semestre de modélisation", "Université Gustave Eiffel",
                new Date(1646127000000L), new Date(1646134200000L), "");
        var event3 = new Event(1, "Examen Crypto", "Examen fin de semestre de Cryptographie", "Université Gustave Eiffel",
                new Date(1646127000000L), new Date(1646134200000L), "");
        var event4 = new Event(2, "Examen Java", "Examen fin de semestre de Java", "Université Gustave Eiffel",
                new Date(1646127000000L), new Date(1646134200000L), "");
        createMultipleEventsShouldWork(List.of(event1, event2, event3, event4));
        given()
                .pathParams("userId", 2)
                .when().get("/friday/getEventsFromUserId/{userId}")
                .then()
                .statusCode(200)
                .body("$.size()", is(3),
                        "[0].userId", is(2),
                        "[1].userId", is(2),
                        "[2].userId", is(2));
    }

    @Test
    @Order(8)
    @Transactional
    public void getEventsFromUserIdShouldBe200WithEmptyJSON() {
        given()
                .pathParams("userId", -1)
                .when().get("/friday/getEventsFromUserId/{userId}")
                .then()
                .statusCode(200)
                .body(is("[]"));
    }

    @Test
    @Order(9)
    @Transactional
    public void getEventsFromUserIdBetweenDatesShouldBe200Ok(){
        var event1 = new Event(5, "Examen Java", "Examen fin de semestre de Java", "Université Gustave Eiffel",
                new Date(1641292200000L), new Date(1641299400000L), "");
        var event2 = new Event(5, "Examen modélisation", "Examen fin de semestre de modélisation", "Université Gustave Eiffel",
                new Date(1641378600000L), new Date(1641385800000L), "");
        var event3 = new Event(5, "Examen Crypto", "Examen fin de semestre de Cryptographie", "Université Gustave Eiffel",
                new Date(1641371400000L), new Date(1641377700000L), "");
        var event4 = new Event(3, "Examen Java", "Examen fin de semestre de Java", "Université Gustave Eiffel",
                new Date(1646127000000L), new Date(1646134200000L), "");
        createMultipleEventsShouldWork(List.of(event1, event2, event3, event4));
        given()
                .pathParam("userId", 5)
                .when().get("/friday/getEventsBetween/{userId}?start=2022-01-04T10:30&end=2022-01-05T10:15")
                .then()
                .statusCode(200)
                .body("$.size()", is(2),
                        "[0].userId", is(5),
                        "[0].title", is("Examen Java"),
                        "[1].userId", is(5),
                        "[1].title", is("Examen Crypto"));
    }

    @Test
    @Order(10)
    @Transactional
    public void createUserShouldBe201Create(){
        var user = new User();
        user.setUsername("Bryan");
        user.setPassword("azerty123");
        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(user)
                .when()
                .post("/friday/newUser")
                .then()
                .statusCode(201);
    }

    @Test
    @Order(11)
    @Transactional
    public void createUserShouldBe304NotAdded(){
        //Use uni to make request asynchronous
        Uni.createFrom().item(() -> {
            var user = new User();
            user.setUsername("Bryan");
            user.setPassword("azerty123");
            user.setUserId(5);
            given()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(user)
                    .when()
                    .post("/friday/newUser")
                    .then()
                    .statusCode(304);
            return user;
        });
    }

    @Test
    @Order(12)
    @Transactional
    public void loginShouldBe200Ok(){
        var user = new User();
        user.setUsername("John");
        user.setPassword("BestPasswordOfTheWorld");

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(user)
                .when()
                .post("/friday/newUser")
                .then()
                .statusCode(201);

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(user)
                .when()
                .post("/friday/login")
                .then()
                .statusCode(200);
    }

    @Test
    @Order(13)
    @Transactional
    public void loginShouldBe406BadRequest(){
        var user = new User();
        user.setUsername("Mark");
        user.setPassword("BestPasswordOfTheWorld2");
        var userToLog = new User();
        userToLog.setUsername("Mark");
        userToLog.setPassword("azerty123456");
        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(user)
                .when()
                .post("/friday/newUser")
                .then()
                .statusCode(201);

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userToLog)
                .when()
                .post("/friday/login")
                .then()
                .statusCode(406);

        userToLog.setUsername("markk");
        userToLog.setPassword("BestPasswordOfTheWorld2");
        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userToLog)
                .when()
                .post("/friday/login")
                .then()
                .statusCode(406);
    }

    @Test
    @Order(14)
    @Transactional
    public void getUserByUsernameShouldBe200Ok(){
        var user = new User();
        user.setUsername("Test");
        user.setPassword("BestPasswordOfTheWorld2");
        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(user)
                .when()
                .post("/friday/newUser")
                .then()
                .statusCode(201);
        given()
                .pathParams("username", "Test")
                .when()
                .get("/friday/getUser/{username}")
                .then()
                .statusCode(200)
                .body("username", is("Test"));
    }

    @Test
    @Order(15)
    @Transactional
    public void getUserByUsernameShouldBe200WithEmptyJson(){
        given()
                .pathParams("username", "NotFoundUser")
                .when()
                .get("/friday/getUser/{username}")
                .then()
                .statusCode(200)
                .body(is("{}"));
    }
}