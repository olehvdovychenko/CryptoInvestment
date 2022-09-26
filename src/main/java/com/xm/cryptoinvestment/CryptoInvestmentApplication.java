package com.xm.cryptoinvestment;

import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import com.xm.cryptoinvestment.entity.CryptoInfo;
import com.xm.cryptoinvestment.repository.CryptoRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@AllArgsConstructor
public class CryptoInvestmentApplication implements CommandLineRunner {

    private CryptoRepository cryptoRepository;
    private ResourcePatternResolver resourcePatternResolver;

    public static void main(String[] args) {
        SpringApplication.run(CryptoInvestmentApplication.class, args);
    }

    @Override
    public void run(String... args) throws IOException {
        List<CryptoInfo> cryptoList = new ArrayList<>();
        Resource[] resources = resourcePatternResolver.getResources("classpath:data/*.csv");
        for (Resource resource : resources) {
            List<Record> allRecords = getRecordsFromCsv(resource);
            allRecords.forEach(record -> {
                cryptoList.add(CryptoInfo.builder()
                        .date(Date.from(Instant.ofEpochMilli(record.getLong("timestamp"))))
                        .name(record.getString("symbol"))
                        .price(record.getBigDecimal("price"))
                        .build());
            });
        }
        cryptoRepository.saveAll(cryptoList);
    }

    private List<Record> getRecordsFromCsv(Resource resource) throws IOException {
        CsvParserSettings settings = new CsvParserSettings();
        settings.setHeaderExtractionEnabled(true);
        CsvParser parser = new CsvParser(settings);
        return parser.parseAllRecords(resource.getInputStream());
    }
}
