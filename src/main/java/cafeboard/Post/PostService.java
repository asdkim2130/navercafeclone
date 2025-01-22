package cafeboard.Post;

import cafeboard.Board.Board;
import cafeboard.Board.BoardRepository;
import cafeboard.Comment.CommentRepository;
import cafeboard.Member.JwtProvider;
import cafeboard.Member.Member;
import cafeboard.Member.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;


    public PostService(PostRepository postRepository, CommentRepository commentRepository, BoardRepository boardRepository, MemberRepository memberRepository) {
        this.postRepository = postRepository;
        this.boardRepository = boardRepository;
        this.commentRepository = commentRepository;
        this.memberRepository = memberRepository;
    }

    //게시글 생성
    public Post create (CreatePostRequest createRequest, String username){
        Board board = boardRepository.findById(createRequest.boardId()).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 게시판입니다. 게시글을 생성할 수 없습니다.")
        );

//        String username = jwtProvider.getSubject(token);
        Member writer = memberRepository.findByUsername(username).orElseThrow(
                () -> new IllegalArgumentException("유효하지 않은 사용자입니다. 게시글을 생성할 수 없습니다.")
        );

        Post post = new Post(createRequest.postTitle(),
                createRequest.postContent(),
                board,
                writer);

        return postRepository.save(post);
    }


    //게시글 상세조회(댓글목록 포함)
    public DetailPostResponse findDetailPost(Long postId) {
        Post posts = postRepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("해당 게시물이 존재하지 않습니다."));

        return new DetailPostResponse(posts.getPostId(),
                posts.getPostTitle(),
                posts.getContent(),
                posts.getComment()
                        .stream()
                        .map(comment -> new DetailPostResponse.Comment(comment.getContent()))
                        .toList()
        );
    }


    //게시글 수정
    @Transactional
    public PostResponse update(Long postId, PostRequest request, String username){
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("수정할 게시글을 찾을 수 없습니다.")
        );

//        String username = jwtProvider.getSubject(token);
        if(!post.getWriter().getUsername().equals(username)){
            throw new NoSuchElementException("게시물 작성자만 수정할 수 있습니다.");
        }

        post.setPostTitle(request.postTitle());
        post.setContent(request.postContent());

        return new PostResponse(post.getPostId(),
                post.getPostTitle(),
                post.getContent());
    }

    // 게시글삭제
    @Transactional
    public void delete(Long postId, String username){
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("삭제할 게시글을 찾을 수 없습니다.")
        );

//        String username = jwtProvider.getSubject(token);
        if(!post.getWriter().getUsername().equals(username)){
            throw new NoSuchElementException("게시물 작성자만 삭제할 수 있습니다.");
        }


        postRepository.delete(post);
    }

    //게시글 목록 조회(댓글 수 포함)
    public List<PostListResponse> findAllPosts() {

        List<PostListResponse> findAllPostsList = postRepository.findAll()
                .stream()
                .map(post -> {
                    int commentCount = commentRepository.countByPost_PostId(post.getPostId());

                    return new PostListResponse(
                            post.getPostId(),
                            post.getPostTitle(),
                            new PostListResponse.Comment(commentCount));
                })
                .toList();
        return findAllPostsList;
    }



}
