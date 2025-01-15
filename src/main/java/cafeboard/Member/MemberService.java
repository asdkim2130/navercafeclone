package cafeboard.Member;

import cafeboard.SecurityUtils;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public void create(CreateMemberRequest request){
        memberRepository.save(new Member(
                request.username(),
                SecurityUtils.sha256EncryptBase64(request.password()),  //사용자가 입력한 패스워드
                request.nickname()));
    }
}
