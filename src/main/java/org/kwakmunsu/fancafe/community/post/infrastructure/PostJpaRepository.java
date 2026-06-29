package org.kwakmunsu.fancafe.community.post.infrastructure;

import org.kwakmunsu.fancafe.community.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostJpaRepository extends JpaRepository<Post, Long> {
}
