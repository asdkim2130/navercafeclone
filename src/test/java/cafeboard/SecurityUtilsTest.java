package cafeboard;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SecurityUtilsTest {

    @DisplayName("input(비밀번호)이 같으면 output(해시값)도 같다")
    @Test
    void 해시화_base64_테스트() {
        // given
        String 비밀번호 = "myPAssword123!@#";
        String 첫번째해시값 = SecurityUtils.sha256EncryptBase64(비밀번호);

        // when
        String 두번째해시값 = SecurityUtils.sha256EncryptBase64(비밀번호);

        // then
        Assertions.assertThat(첫번째해시값).isEqualTo(두번째해시값);
    }

    @DisplayName("input(비밀번호)이 다르면 output(해시값)도 다르다")
    @Test
    void 해시화_base64_테스트2() {
        // given
        String 비밀번호 = "myPAssword123!@#";
        String 틀린비밀번호 = "nyPAssword123!@#"; // my 대신 ny
        String 올바른비밀번호_해시값 = SecurityUtils.sha256EncryptBase64(비밀번호);

        // when
        String 틀린비밀번호_해시값 = SecurityUtils.sha256EncryptBase64(틀린비밀번호);

        // then
        Assertions.assertThat(올바른비밀번호_해시값).isNotEqualTo(틀린비밀번호_해시값);
    }

    @DisplayName("input(비밀번호)이 같으면 output(해시값)도 같다")
    @Test
    void 해시화_hex_테스트() {
        // given
        String 비밀번호 = "myPAssword123!@#";
        String 첫번째해시값 = SecurityUtils.sha256EncryptHex1(비밀번호);

        // when
        String 두번째해시값 = SecurityUtils.sha256EncryptHex1(비밀번호);

        // then
        Assertions.assertThat(첫번째해시값).isEqualTo(두번째해시값);
    }

    @DisplayName("input(비밀번호)이 다르면 output(해시값)도 다르다")
    @Test
    void 해시화_hex_테스트2() {
        // given
        String 비밀번호 = "myPAssword123!@#";
        String 틀린비밀번호 = "nyPAssword123!@#"; // my 대신 ny
        String 올바른비밀번호_해시값 = SecurityUtils.sha256EncryptHex1(비밀번호);

        // when
        String 틀린비밀번호_해시값 = SecurityUtils.sha256EncryptHex1(틀린비밀번호);

        // then
        Assertions.assertThat(올바른비밀번호_해시값).isNotEqualTo(틀린비밀번호_해시값);
    }
}