package cafeboard.Comment;

import cafeboard.Member.JwtProvider;
import cafeboard.Member.Member;
import cafeboard.Member.MemberRepository;
import cafeboard.Post.Post;
import cafeboard.Post.PostRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;

    public CommentService(CommentRepository commentRepository, PostRepository postRepository, MemberRepository memberRepository, JwtProvider jwtProvider) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.memberRepository = memberRepository;
        this.jwtProvider = jwtProvider;
    }

    //댓글생성
    public Comment create(CommentRequest request, String token) {
        Post post = postRepository.findById(request.postId()).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 게시글입니다. 댓글을 생성할 수 없습니다.")
        );

        String username = jwtProvider.getSubject(token);
        Member writer = memberRepository.findByUsername(username).orElseThrow(
                () -> new NoSuchElementException("유효하지 않은 사용자입니다. 댓글을 생성할 수 없습니다.")
        );


        Comment comment = new Comment(request.content(), post, writer);
        return commentRepository.save(comment);
    }

    public CommentResponse findByCommentId(Long commentId){
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다.")
                );

        return new CommentResponse(commentId,
                comment.getContent());
    }

    //댓글수정
    @Transactional
    public CommentResponse update(Long commentId, UpdateCommentRequest request, String token){
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다.")
        );

        String username = jwtProvider.getSubject(token);
        if(!comment.getWriter().getUsername().equals(username)){
            throw new NoSuchElementException("댓글 작성자만 수정할 수 있습니다.");
        }

        comment.setContent(request.content());

        return new CommentResponse(comment.getCommentId(),
                comment.getContent());
    }

    //댓글삭제
    @Transactional
    public void delete (Long commentId, String token){
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다.")
        );

        String username = jwtProvider.getSubject(token);
        if(comment.getWriter().getUsername().equals(username)){
            throw new NoSuchElementException("댓글 작성자만 삭제할 수 있습니다.");
        }

        commentRepository.delete(comment);
    }

    public List<CommentsListResponse> findAll(){
        return commentRepository.findAll()
                .stream()
                .map(comment -> new CommentsListResponse(comment.getContent()))
                .toList();
    }

}
