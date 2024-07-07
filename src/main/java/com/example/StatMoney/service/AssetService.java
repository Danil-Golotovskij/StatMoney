package com.example.StatMoney.service;

import com.example.StatMoney.entity.Asset;
import com.example.StatMoney.repository.AssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AssetService {

    @Autowired
    private AssetRepository assetRepository;

    public List<Asset> getAllAssets() {
        return assetRepository.findAll();
    }

    public Optional<Asset> getAssetById(Long id) {
        return assetRepository.findById(id);
    }

    public Asset addAsset(Asset asset) {
        return assetRepository.save(asset);
    }

    public Asset updateAsset(Long id, Asset assetDetails) {
        Optional<Asset> optionalAsset = assetRepository.findById(id);
        if (optionalAsset.isPresent()) {
            Asset asset = optionalAsset.get();
            asset.setName(assetDetails.getName());
            asset.setCategory(assetDetails.getCategory());
            asset.setPurchasePriceRub(assetDetails.getPurchasePriceRub());
            asset.setPurchasePriceUsd(assetDetails.getPurchasePriceUsd());
            asset.setQuantity(assetDetails.getQuantity());
            return assetRepository.save(asset);
        } else {
            throw new RuntimeException("Не найден актив с заданным id");
        }
    }

    public void deleteAsset(Long id) {
        assetRepository.deleteById(id);
    }

    public void deleteAssetsByPortfolioIdAndName(Long portfolioId, String name) {
        List<Asset> assets = assetRepository.findByPortfolioIdAndName(portfolioId, name);
        assetRepository.deleteAll(assets);
    }

    public List<Asset> findByPortfolioId(Long portfolioId) {
        return assetRepository.findByPortfolioId(portfolioId);
    }
}
