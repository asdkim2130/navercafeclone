package cafeboard.Comment;

import cafeboard.Member.Member;
import cafeboard.Post.Post;
import jakarta.persistence.*;

@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    private String content;
    private int commentCount;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Member writer;


    protected Comment() {
    }

    public Comment(Long commentId, String content) {
        this.commentId = commentId;
        this.content = content;
    }

    public Comment(String content, Post post) {
        this.content = content;
        this.post = post;
    }

    public Comment(String content, Post post, Member writer) {
        this.content = content;
        this.post = post;
        this.writer = writer;
    }

    public Comment(String content) {
        this.content = content;
    }

    public Comment(int commentCount) {
        this.commentCount = commentCount;
    }

    public Long getCommentId() {
        return commentId;
    }

    public String getContent() {
        return content;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public Post getPost() {
        return post;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Member getWriter() {
        return writer;
    }
}
