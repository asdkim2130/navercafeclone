package cafeboard.Member;

import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

@RestController
public class MemberRestController {

    private final MemberService memberService;

    public MemberRestController(MemberService memberService) {
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

    @GetMapping("/me")
    public String getProfile(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization){
        return memberService.getProfile(authorization);
    }
}
