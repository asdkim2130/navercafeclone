package cafeboard.Comment;

import cafeboard.Member.JwtProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CommentController {

    private final CommentService commentService;
    private final JwtProvider jwtProvider;

    public CommentController(CommentService commentService, JwtProvider jwtProvider) {
        this.commentService = commentService;
        this.jwtProvider = jwtProvider;
    }


    @PostMapping("/comments")
    public CommentResponse createComment(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
                                         @RequestBody CommentRequest request){

        String[] tokenFormat = authorization.split(" ");

        String tokenType = tokenFormat[0];
        String token = tokenFormat[1];

        if (tokenType.equals("Bearer") == false) {
            throw new IllegalArgumentException("로그인 정보가 유효하지 않습니다");
        }
        if (jwtProvider.isValidToken(token) == false) {
            throw new IllegalArgumentException("로그인 정보가 유효하지 않습니다");
        }

        String username = jwtProvider.getSubject(token);


        Comment comment = commentService.create(request, token);

        return new CommentResponse(comment.getCommentId(),
                comment.getContent());
    }

    @GetMapping("comments/{commentId}")
    public CommentResponse findComment(@PathVariable Long commentId){
        return commentService.findByCommentId(commentId);
    }


    @PutMapping("comments/{commentId}")
    public CommentResponse updateComment(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
                                         @PathVariable Long commentId,
                                         @RequestBody UpdateCommentRequest updateRequest){

        String[] tokenFormat = authorization.split(" ");

        String tokenType = tokenFormat[0];
        String token = tokenFormat[1];

        if (tokenType.equals("Bearer") == false) {
            throw new IllegalArgumentException("로그인 정보가 유효하지 않습니다");
        }
        if (jwtProvider.isValidToken(token) == false) {
            throw new IllegalArgumentException("로그인 정보가 유효하지 않습니다");
        }

        String username = jwtProvider.getSubject(token);

        return commentService.update(commentId, updateRequest, token);
    }

    @DeleteMapping("comments/{commentId}")
    public void deleteComment(@RequestHeader (HttpHeaders.AUTHORIZATION) String authorization,
                              @PathVariable Long commentId){

        String[] tokenFormat = authorization.split(" ");

        String tokenType = tokenFormat[0];
        String token = tokenFormat[1];

        if (tokenType.equals("Bearer") == false) {
            throw new IllegalArgumentException("로그인 정보가 유효하지 않습니다");
        }
        if (jwtProvider.isValidToken(token) == false) {
            throw new IllegalArgumentException("로그인 정보가 유효하지 않습니다");
        }

        String username = jwtProvider.getSubject(token);


        commentService.delete(commentId, token);
    }

    @GetMapping("commentslist")
    public List<CommentsListResponse> findAllCommentsList(){
        return commentService.findAll();
    }

}
