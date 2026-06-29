package org.kwakmunsu.fancafe.member.domain;

public interface PasswordEncoder {

    String encode(String password);

    boolean matches(String rawPassword, String encodedPassword);

}