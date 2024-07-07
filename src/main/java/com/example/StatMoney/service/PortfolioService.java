package com.example.StatMoney.service;

import com.example.StatMoney.entity.Portfolio;
import com.example.StatMoney.entity.User;
import com.example.StatMoney.repository.PortfolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PortfolioService {

    @Autowired
    private PortfolioRepository portfolioRepository;

    public Optional<Portfolio> findByUser(User user) {
        return portfolioRepository.findByUser(user);
    }
}
