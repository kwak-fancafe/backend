package org.kwakmunsu.fancafe.community.category.infrastructure;

import org.kwakmunsu.fancafe.community.category.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryJpaRepository extends JpaRepository<Category, Long> {
}
