package cafeboard.Member;

import cafeboard.SecurityUtils;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
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



}
