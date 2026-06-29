package org.kwakmunsu.fancafe.community.like.infrastructure;

import org.kwakmunsu.fancafe.community.like.domain.Like;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeJpaRepository extends JpaRepository<Like, Long> {
}
