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
public class CommentApiTest {

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    public void 댓글생성테스트(){
        //게시판 생성
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

        //게시글 생성
        Long postId = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(new CreatePostRequest(boardId, "게시글제목", "게시글내용"))
                .when()
                .post("posts")
                .then().log().all()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getLong("postId");

        //댓글생성
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(new CommentRequest(postId, "댓글내용"))
                .when()
                .post("comments")
                .then().log().all()
                .statusCode(200);
    }


    @Test
    public void 댓글수정테스트() {
        //게시판 생성
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

        //게시글 생성
        Long postId = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(new CreatePostRequest(boardId, "게시글제목", "게시글내용"))
                .when()
                .post("posts")
                .then().log().all()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getLong("postId");

        Long commentId = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(new CommentRequest(postId, "댓글내용"))
                .when()
                .post("comments")
                .then().log().all()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getLong("commentId");

        //put으로 수정
        String updatedContent = "수정된 댓글";
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(new UpdateCommentRequest(updatedContent))
                .when()
                .put("/comments/{commentId}", commentId)
                .then()
                .log().all()
                .statusCode(200);

        //get으로 조회
        CommentResponse response = RestAssured.given().log().all()
                .when()
                .get("/comments/{commentId}", commentId)
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .as(CommentResponse.class);

        assertThat(response.content()).isEqualTo(updatedContent);
    }


    @Test
    public void 댓글삭제테스트(){
        //게시판 생성
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

        //게시글 생성
        Long postId = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(new CreatePostRequest(boardId, "게시글제목", "게시글내용"))
                .when()
                .post("posts")
                .then().log().all()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getLong("postId");

        //댓글생성
        Long commentId = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(new CommentRequest(postId, "댓글내용"))
                .when()
                .post("comments")
                .then().log().all()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getLong("commentId");

        //댓글생성2
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(new CommentRequest(postId, "댓글내용2"))
                .when()
                .post("comments")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(CommentResponse.class);

        //댓글 삭제
        RestAssured.given().log().all()
                .pathParam("commentId", commentId)
                .when()
                .delete("/comments/{commentId}")
                .then().log().all()
                .statusCode(200);

        //삭제 조회
        List<CommentsListResponse> commentResponses = RestAssured.given().log().all()
                .when()
                .get("/commentslist")
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList(".", CommentsListResponse.class);

        assertThat(commentResponses).anyMatch(Response -> !Response.content().equals("댓글내용"));
    }

}