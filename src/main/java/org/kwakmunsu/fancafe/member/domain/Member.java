package org.kwakmunsu.fancafe.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.kwakmunsu.fancafe.global.support.BaseEntity;
import org.kwakmunsu.fancafe.global.support.error.CoreException;
import org.kwakmunsu.fancafe.global.support.error.ErrorType;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Entity
public class Member extends BaseEntity {

    @Embedded
    private LoginId loginId;

    @Embedded
    private Password password;

    @Embedded
    private Nickname nickname;

    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MemberStatus memberStatus;

    public static Member register(
            String loginId,
            String password,
            String nickname,
            PasswordEncoder encoder
    ) {
        Member member = new Member();

        member.loginId = new LoginId(loginId);
        member.password = Password.create(password, encoder);
        member.nickname = new Nickname(nickname);
        member.role = Role.ROLE_FAN;
        member.memberStatus = MemberStatus.ACTIVE;
        member.profileImageUrl = null;

        return member;
    }
    public void ban() {
        if (this.memberStatus != MemberStatus.ACTIVE) {
            throw new CoreException(ErrorType.MEMBER_CANNOT_BAN);
        }
        this.memberStatus = MemberStatus.BANNED;
    }

    public void unban() {
        if (this.memberStatus != MemberStatus.BANNED) {
            throw new CoreException(ErrorType.MEMBER_CANNOT_UNBAN);
        }
        this.memberStatus = MemberStatus.ACTIVE;
    }

    public void withdraw() {
        if (this.memberStatus == MemberStatus.WITHDRAWN) {
            throw new CoreException(ErrorType.MEMBER_CANNOT_WITHDRAW);
        }
        this.memberStatus = MemberStatus.WITHDRAWN;
        this.delete();
    }

    public void changeRole(Role role) {
        this.role = role;
    }

    public void changeNickname(String newNickname) {
        if (this.memberStatus != MemberStatus.ACTIVE) {
            throw new CoreException(ErrorType.MEMBER_CANNOT_UPDATE_PROFILE);
        }
        this.nickname = new Nickname(newNickname);
    }

    public void updateProfileImage(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void validateWritePermission() {
        if (this.role != Role.ROLE_CREATOR && this.role != Role.ROLE_MANAGER) {
            throw new CoreException(ErrorType.MEMBER_CANNOT_WRITE_POST);
        }
    }

    public void verifyPassword(String rawPassword, PasswordEncoder encoder) {
        if (password.matches(rawPassword, encoder)) {
            return;
        }
        throw new CoreException(ErrorType.MEMBER_NOT_FOUND_ACCOUNT);
    }

}