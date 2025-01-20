package cafeboard;

import cafeboard.Member.JwtProvider;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

@SpringBootTest
class ApplicationTests {

	@Autowired
	JwtProvider jwtProvider;

	@Test
	void name() {
		String token = jwtProvider.createToken("a@gmail.com");
		System.out.println("token = " + token);

		Boolean validToken = jwtProvider.isValidToken(token);
		System.out.println("validToken = " + validToken);

		String email = jwtProvider.getSubject(token);
		System.out.println("email = " + email);
	}



	}
