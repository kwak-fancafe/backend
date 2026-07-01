package org.kwakmunsu.fancafe.community.category.infrastructure;

import java.util.Optional;
import org.kwakmunsu.fancafe.community.category.domain.Category;
import org.kwakmunsu.fancafe.global.support.EntityStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryJpaRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByIdAndStatus(Long id, EntityStatus status);
}
