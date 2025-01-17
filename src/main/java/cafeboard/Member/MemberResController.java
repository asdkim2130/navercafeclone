package cafeboard.Member;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
public class MemberResController {

    private final MemberService memberService;

    public MemberResController(MemberService memberService) {
        this.memberService = memberService;
    }

    //회원가입 API
    @PostMapping("/members")
    public MemberResponse createLoginId(@Valid @RequestBody CreateMemberRequest request){
        return memberService.create(request);
    }

    @DeleteMapping("/members/{memberId}")
    public void deleteLoginId(@PathVariable (name = "memberId")Long id){
        memberService.deleteId(id);
    }

    @PostMapping("/login")
    public MemberLoginResponse memberLogIn(@RequestBody MemberLoginRequest loginRequest){
        return memberService.logIn(loginRequest);
    }

}
