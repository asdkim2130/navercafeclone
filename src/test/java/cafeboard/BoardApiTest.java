package cafeboard;

import cafeboard.Board.BoardRequest;
import cafeboard.Board.BoardResponse;
import cafeboard.Comment.*;
import cafeboard.Post.*;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;


import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BoardApiTest {

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    public void 게시판생성테스트(){
        BoardResponse boardResponse = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(new BoardRequest("게시판 제목"))
                .when()
                .post("boards")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(BoardResponse.class);

        assertThat(boardResponse.boardId()).isGreaterThan(0);
    }


    @Test
    public void 게시판조회테스트(){
        //생성
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(new BoardRequest("게시판 제목"))
                .when()
                .post("boards")
                .then().log().all()
                .statusCode(200);


        List<BoardResponse> boardResponse = RestAssured.given().log().all()
                .when()
                .get("boards")
                .then().log().all()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList(".",  BoardResponse.class);


        assertThat(boardResponse.size()).isEqualTo(1);
    }


    @Test
    public void 게시판수정테스트() {
        //생성
        long boardId = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(new BoardRequest("게시판 제목"))
                .when()
                .post("boards")
                .then().log().all()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getLong("boardId");

        //수정
        String updatedTitle = "수정된 제목";

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .pathParam("boardId", boardId)
                .body(new BoardRequest(updatedTitle))
                .when()
                .put("/boards/{boardId}")
                .then()
                .log().all()
                .statusCode(200);

        //수정 조회
        List<BoardResponse> boardResponse = RestAssured.given().log().all()
                .when()
                .get("boards")
                .then().log().all()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList(".", BoardResponse.class);

        assertThat(boardResponse).anyMatch(boardResponse1 -> boardResponse1.title().equals("수정된 제목"));

    }


    @Test
    public void 게시판삭제테스트() {
        //생성1
        Long boardId = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(new BoardRequest("게시판 제목1"))
                .when()
                .post("boards")
                .then().log().all()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getLong("boardId");

        //생성2
        BoardResponse board2 = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(new BoardRequest("게시판 제목2"))
                .when()
                .post("boards")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(BoardResponse.class);

        //생성1 삭제
        RestAssured.given().log().all()
                .pathParam("boardId", boardId)
                .when()
                .delete("/boards/{boardId}")
                .then().log().all()
                .statusCode(200);

        //삭제조회
        List<BoardResponse> boardList = RestAssured.given().log().all()
                .when()
                .get("boards")
                .then().log().all()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList(".", BoardResponse.class);

        assertThat(boardList).anyMatch(boardResponse -> !boardResponse.title().equals("게시판 제목1"));
    }
}