package cafeboard.Member;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MemberResController {

    private final MemberService memberService;

    public MemberResController(MemberService memberService) {
        this.memberService = memberService;
    }

    //회원가입 API
    public void create(@Valid @RequestBody CreateMemberRequest request){
        memberService.create(request);

    }
}
