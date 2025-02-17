package cafeboard;


import cafeboard.Board.BoardRequest;
import cafeboard.Board.BoardResponse;
import cafeboard.Comment.*;
import cafeboard.Post.*;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;


import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApiTest {

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
    public void 게시판삭제테스트(){
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


    @Test
    public void 게시글생성테스트(){
        //게시판생성
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

        //게시판에 게시글 생성
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(new CreatePostRequest(boardId, "게시글제목",  "게시글내용"))
                .when()
                .post("posts")
                .then().log().all()
                .statusCode(200);
    }


    @Test
    public void 게시글상세조회테스트(){
        //게시판생성
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

        //게시판에 게시글 생성
        PostResponse response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(new CreatePostRequest(boardId, "게시글제목", "게시글내용"))
                .when()
                .post("posts")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(PostResponse.class);

        //게시글 상세 조회
        DetailPostResponse detailPostResponse
                = RestAssured.given().log().all()
                .pathParam("postId", response.postId())
                .when()
                .get("/posts/{postId}") // 서버로 GET /products 요청
                .then().log().all()
                .statusCode(200) // 요청에 대한 서버 응답의 상태코드가 200인지 검증
                .extract()
                .as(DetailPostResponse.class);

        assertThat(detailPostResponse.postId()).isEqualTo(response.postId());
        assertThat(detailPostResponse.postTitle()).isEqualTo("게시글제목");
        assertThat(detailPostResponse.postContent()).isEqualTo("게시글내용");
    }


    @Test
    public void 게시글목록조회테스트(){
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

        //게시판에 게시글 생성
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
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(new CommentRequest(postId, "댓글내용2"))
                .when()
                .post("comments")
                .then().log().all()
                .statusCode(200);

        //목록조회
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .when()
                .get("postslist")
                .then().log().all()
                .statusCode(200)
                .extract().
                jsonPath()
                .getList(".", PostListResponse.class);
    }


    @Test
    public void 게시글수정테스트(){
        //게시판생성
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

        //게시판에 게시글 생성
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

        //게시글 수정
        String updatedTitle = "수정된 게시글 제목";
        String updatedContent = "수정된 게시글 내용";
        RestAssured.given().log().all()
                .pathParam("postId", postId)
                .contentType(ContentType.JSON)
                .body(new PostRequest(updatedTitle, updatedContent))
                .when()
                .put("/posts/{postId}", postId)
                .then()
                .log().all()
                .statusCode(200);

        //수정 조회
        DetailPostResponse response = RestAssured.given().log().all()
                .when()
                .get("/posts/{postId}", postId)
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .as(DetailPostResponse.class);

        assertThat(response.postContent()).isEqualTo(updatedContent);
    }


    @Test
    public void 게시글삭제테스트(){
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

        //게시글1 생성
                Long postId = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(new CreatePostRequest(boardId, "게시글제목1", "게시글내용1"))
                .when()
                .post("posts")
                .then().log().all()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getLong("postId");

        //게시글2 생성
        PostResponse response1 = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(new CreatePostRequest(boardId, "게시글제목2", "게시글내용2"))
                .when()
                .post("posts")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(PostResponse.class);

        //게시글1 삭제
        RestAssured.given().log().all()
                .pathParam("postId", postId)
                .when()
                .delete("/posts/{postId}")
                .then().log().all()
                .statusCode(200);

        //삭제조회
        List<PostListResponse> listResponse = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .when()
                .get("postslist")
                .then().log().all()
                .statusCode(200)
                .extract().
                jsonPath()
                .getList(".", PostListResponse.class);

        assertThat(listResponse).anyMatch(Response -> !Response.postTitle().equals("게시글제목1"));
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



