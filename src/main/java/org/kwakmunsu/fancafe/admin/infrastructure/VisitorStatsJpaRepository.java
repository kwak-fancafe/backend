package org.kwakmunsu.fancafe.admin.infrastructure;

import org.kwakmunsu.fancafe.admin.domain.VisitorStats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisitorStatsJpaRepository extends JpaRepository<VisitorStats, Long> {
}
