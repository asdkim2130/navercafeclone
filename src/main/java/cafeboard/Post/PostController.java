package cafeboard.Post;

import cafeboard.Member.JwtProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PostController {

    private final PostService postService;
    private final JwtProvider jwtProvider;

    public PostController(PostService postService, JwtProvider jwtProvider) {
        this.postService = postService;
        this.jwtProvider = jwtProvider;
    }

    @PostMapping("/posts")
    public PostResponse createPost(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
                                   @RequestBody CreatePostRequest request){

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

        Post post = postService.create(request, token);

        return new PostResponse(post.getPostId(),
                post.getPostTitle(),
                post.getContent());
    }

    //게시글 상세조회
    @GetMapping("/posts/{postId}")
    public DetailPostResponse findDetailPost (@PathVariable Long postId){
        return postService.findDetailPost(postId);
    }

    //게시글 수정
    @PutMapping("/posts/{postId}")
    public PostResponse updatePost(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
                                   @PathVariable Long postId,
                                   @RequestBody PostRequest request){

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



        return postService.update(postId, request, token);
    }

    //게시글삭제
    @DeleteMapping("/posts/{postId}")
    public void deletePost(@PathVariable Long postId,
                           @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization){

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



        postService.delete(postId, token);
    }

    //게시글 목록조회
    @GetMapping("postslist")
    public List<PostListResponse>getAllPostsList(){
        return postService.findAllPosts();
    }

}
