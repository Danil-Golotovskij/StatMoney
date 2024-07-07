package com.example.StatMoney.repository;

import com.example.StatMoney.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
    List<Asset> findByPortfolioId(Long portfolioId);
    List<Asset> findByPortfolioIdAndName(Long portfolioId, String name);  // Новый метод
}
