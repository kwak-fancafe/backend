package org.kwakmunsu.fancafe.community.viewcount.infrastructure;

import org.kwakmunsu.fancafe.community.viewcount.domain.ViewCount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ViewCountJpaRepository extends JpaRepository<ViewCount, Long> {
}
