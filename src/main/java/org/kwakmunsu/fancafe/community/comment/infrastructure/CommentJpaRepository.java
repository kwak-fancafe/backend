package org.kwakmunsu.fancafe.community.comment.infrastructure;

import org.kwakmunsu.fancafe.community.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentJpaRepository extends JpaRepository<Comment, Long> {
}
