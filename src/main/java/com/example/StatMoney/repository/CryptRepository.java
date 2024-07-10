package com.example.StatMoney.repository;

import com.example.StatMoney.entity.Cryptocurrency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CryptRepository extends JpaRepository<Cryptocurrency, Long> {
}
