package cafeboard.Member;

import cafeboard.Comment.Comment;
import cafeboard.Post.Post;
import cafeboard.SecurityUtils;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;  //

    @Column(nullable = false)
    private String nickname;

//    @OneToMany(mappedBy = "member")
//    List<Comment> commentList;

//    @OneToMany(mappedBy = "member")
//    List<Post> postList;


    protected Member() {
    }

    public Member(String username, String password, String nickname) {
        this.username = username;
        this.password = password;
//        if(nickname == null){
//            this.nickname == username;
//        }else
//            this.nickname = nickname;
//        }

        this.nickname = nickname == null ? username : nickname;
//        this.nickname = nickname;
    }

    public Member(String username, String nickname) {
        this.username = username;
        this.nickname = nickname;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }

    public void encodePassword(String password){
        this.password = SecurityUtils.sha256EncryptBase64(password);

    }


}
