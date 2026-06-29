package org.kwakmunsu.fancafe.community.like.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.kwakmunsu.fancafe.global.support.BaseEntity;

@Getter
@Entity
@Table(name = "likes")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Like extends BaseEntity {
}
