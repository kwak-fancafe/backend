package org.kwakmunsu.fancafe.admin.domain;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.kwakmunsu.fancafe.global.support.BaseEntity;

@Getter
@Entity
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class VisitorStats extends BaseEntity {
}
