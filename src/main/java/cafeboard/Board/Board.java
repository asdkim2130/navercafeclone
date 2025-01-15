package cafeboard.Board;

import cafeboard.Post.Post;
import jakarta.persistence.*;


import java.util.List;

@Entity
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;

    private String title;

    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE)
    private List<Post> post;


    protected Board() {
    }

    public Board(String title) {
        this.title = title;
    }

    public Board(Long boardId, String title) {
        this.boardId = boardId;
        this.title = title;

    }

    public Long getBoardId() {
        return boardId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Post> getPost() {
        return post;
    }
}
