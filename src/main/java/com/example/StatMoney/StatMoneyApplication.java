package com.example.StatMoney;

import org.springframework.beans.factory.annotation.Autowired;
import com.example.StatMoney.service.CbrService;
import com.example.StatMoney.service.MoexService;
import com.example.StatMoney.service.CryptoCompareService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StatMoneyApplication implements CommandLineRunner {

    @Autowired
    private CbrService cbrService;

    @Autowired
    private MoexService moexService;

    @Autowired
    private CryptoCompareService cryptoCompareService;

    public static void main(String[] args) {
        SpringApplication.run(StatMoneyApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

    }
}
