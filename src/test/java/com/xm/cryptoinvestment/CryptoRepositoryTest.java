package com.xm.cryptoinvestment;

import com.xm.cryptoinvestment.entity.CryptoInfo;
import com.xm.cryptoinvestment.repository.CryptoRepository;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CryptoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private CryptoRepository cryptoRepository;

    @Test
    public void shouldFindDataAfterSpringBootStarted() {
        List<CryptoInfo> cryptoInfos = cryptoRepository.findAll();

        assertThat(cryptoInfos).isNotEmpty();
    }

    @Test
    public void shouldStoreData() {
        CryptoInfo cryptoInfo = cryptoRepository.save(CryptoInfo.builder()
                .date(Date.valueOf(LocalDate.now()))
                .name("SHIBA")
                .price(BigDecimal.valueOf(213.99D))
                .build());

        assertThat(cryptoRepository.findById(cryptoInfo.getId()).orElseThrow()).isEqualTo(cryptoInfo);
    }

    @Test
    public void shouldFindByName(){
        assertThat(cryptoRepository.findByName("BTC").size()).isEqualTo(100);
    }

    @Test
    public void shouldFindByDateBetween(){
        assertThat(cryptoRepository.findByDateBetween(Date.valueOf("2022-01-01"), Date.valueOf("2022-01-10")).size()).isEqualTo(121);
    }
}
