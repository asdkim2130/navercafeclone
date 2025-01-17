package cafeboard.Member;

import cafeboard.SecurityUtils;
import org.springframework.stereotype.Service;

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
        Member member = memberRepository.save(new Member(
                request.username(),
                SecurityUtils.sha256EncryptBase64(request.password()),  //사용자가 입력한 패스워드
                request.nickname()));

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

}
