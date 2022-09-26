package com.xm.cryptoinvestment.repository;

import com.xm.cryptoinvestment.entity.CryptoInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface CryptoRepository extends JpaRepository<CryptoInfo, Integer> {

    List<CryptoInfo> findByName(String name);

    List<CryptoInfo> findByDateBetween(Date startDate, Date endDate);
}
