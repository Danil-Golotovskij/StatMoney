package com.example.StatMoney.repository;

import com.example.StatMoney.entity.Share;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ShareRepository extends JpaRepository<Share, Long> {

    @Modifying
    @Query(value = "insert into shares (ticker, id) values (:ticker, :id)", nativeQuery = true)
    void goodSave(@Param("ticker") String ticker, @Param("id") Long id);
}
