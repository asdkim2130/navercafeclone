package cafeboard.Post;

import cafeboard.LoginMember;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PostController {

    private final PostService postService;


    public PostController(PostService postService) {
        this.postService = postService;}

    @PostMapping("/posts")
    public PostResponse createPost(@LoginMember String username,  //username으로 받고
                                   @RequestBody CreatePostRequest request){

//        String token = jwtProvider.createToken(username);  //username으로 토큰 생성(새로운 토큰을 또 만드는 것)
//        -> 없어도 됨(로그인에서 토큰 만들어놓았고 username으로 검증 충분)

        Post post = postService.create(request, username);

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
    public PostResponse updatePost(@LoginMember String username,
                                   @PathVariable Long postId,
                                   @RequestBody PostRequest request){

       return postService.update(postId, request, username);
    }

    //게시글삭제
    @DeleteMapping("/posts/{postId}")
    public void deletePost(@PathVariable Long postId,
                           @LoginMember String username){

        postService.delete(postId, username);
    }

    //게시글 목록조회
    @GetMapping("postslist")
    public List<PostListResponse>getAllPostsList(){
        return postService.findAllPosts();
    }

}
