package org.kwakmunsu.fancafe.fixture;

import java.util.Objects;
import org.kwakmunsu.fancafe.member.domain.Member;
import org.kwakmunsu.fancafe.member.domain.PasswordEncoder;

public class MemberFixture {

    public static final String LOGIN_ID = "user01";
    public static final String RAW_PASSWORD = "Pass1!aa";
    public static final String NICKNAME = "홍길동";

    public static final PasswordEncoder ENCODER = new PasswordEncoder() {
        @Override
        public String encode(String password) {
            return Objects.requireNonNull(password);
        }

        @Override
        public boolean matches(String rawPassword, String encodedPassword) {
            return Objects.requireNonNull(rawPassword).equals(encodedPassword);
        }

    };

    public static Member member() {
        return Member.register(LOGIN_ID, RAW_PASSWORD, NICKNAME, ENCODER);
    }

    public static Member bannedMember() {
        Member member = member();
        member.ban();
        return member;
    }

    public static Member withdrawnMember() {
        Member member = member();
        member.withdraw();
        return member;
    }

}
