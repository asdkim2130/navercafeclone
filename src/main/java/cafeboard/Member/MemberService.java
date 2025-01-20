package cafeboard.Member;

import cafeboard.SecurityUtils;
import org.springframework.stereotype.Service;

import java.net.http.HttpHeaders;
import java.util.NoSuchElementException;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;

    public MemberService(MemberRepository memberRepository, JwtProvider jwtProvider) {
        this.memberRepository = memberRepository;
        this.jwtProvider = jwtProvider;
    }

    public MemberResponse create(CreateMemberRequest request){
        Member member = new Member(
                request.username(),
                request.nickname());
        member.encodePassword(request.password());

        memberRepository.save(member);

        return new MemberResponse(member.getUsername(),
                member.getNickname());
    }

    public void deleteId(Long id){
        Member member = memberRepository.findById(id).orElseThrow(
                ()-> new NoSuchElementException("등록되지 않은 id입니다.")
        );

        memberRepository.delete(member);
    }


    public MemberLoginResponse logIn(MemberLoginRequest loginRequest){
        Member member = memberRepository.findByUsername(loginRequest.username()).orElseThrow(
                ()-> new NoSuchElementException("등록되지 않은 사용자입니다.")
        );
        if(!member.getPassword().equals(SecurityUtils.sha256EncryptBase64(loginRequest.password()))){
            throw new NoSuchElementException("아이디 또는 비밀번호가 잘못 되었습니다." +
                                            "아이디와 비밀번호를 정확히 입력해 주세요.");
        }
        System.out.println("로그인 성공");
        return new MemberLoginResponse(jwtProvider.createToken(member.getUsername()));
    }


    public String getProfile (String authorization){

        String[] tokenFormat = authorization.split(" ");

        String tokenType = tokenFormat[0];
        String token = tokenFormat[1];

        if(tokenType.equals("Bearer") == false){
            throw new IllegalArgumentException("로그인 정보가 유효하지 않습니다.");
        }

        if(jwtProvider.isValidToken(token) == false){
            throw new IllegalArgumentException("로그인 정보가 유효하지 않습니다.");
        }

        String username = jwtProvider.getSubject(token);

        return username;
    }

}
