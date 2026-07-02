package org.kwakmunsu.fancafe.member.domain;

public enum Role {
    ROLE_FAN,
    ROLE_MANAGER,
    ROLE_CREATOR,
    ;

    public boolean hasWritePermission() {
        return this == ROLE_MANAGER || this == ROLE_CREATOR;
    }

}