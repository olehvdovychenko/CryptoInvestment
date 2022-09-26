package com.xm.cryptoinvestment.service;

import com.xm.cryptoinvestment.entity.CryptoInfo;
import com.xm.cryptoinvestment.repository.CryptoRepository;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CryptoService {

    private CryptoRepository cryptoRepository;

    public Map<String, Map<String, CryptoInfo>> getMonthReport(Date date) {
        Date startMonth = DateUtils.truncate(date, Calendar.MONTH);
        Date endMonth = DateUtils.addMilliseconds(DateUtils.ceiling(date, Calendar.MONTH), -1);

        Map<String, List<CryptoInfo>> map = cryptoRepository.findByDateBetween(startMonth, endMonth).stream()
                .collect(Collectors.groupingBy(CryptoInfo::getName));

        Map<String, Map<String, CryptoInfo>> result = new HashMap<>();
        for (Map.Entry<String, List<CryptoInfo>> entry : map.entrySet()) {
            Map<String, CryptoInfo> internalMap = new HashMap<>();
            internalMap.put("MAX PRICE", entry.getValue().stream()
                    .max(Comparator.comparing(CryptoInfo::getPrice))
                    .orElseThrow());
            internalMap.put("MIN PRICE", entry.getValue().stream()
                    .min(Comparator.comparing(CryptoInfo::getPrice))
                    .orElseThrow());
            internalMap.put("OLDEST REPORT", entry.getValue().stream()
                    .min(Comparator.comparing(CryptoInfo::getDate))
                    .orElseThrow());
            internalMap.put("NEWEST REPORT", entry.getValue().stream()
                    .max(Comparator.comparing(CryptoInfo::getDate))
                    .orElseThrow());
            result.put(entry.getKey(), internalMap);
        }
        return result;
    }

    public Map<String, BigDecimal> getReportSortedByNormalizedRange() {
        Map<String, List<CryptoInfo>> map = cryptoRepository.findAll().stream()
                .collect(Collectors.groupingBy(CryptoInfo::getName));

        return collectMapWithNormalizedRange(map).entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    public Map<String, CryptoInfo> getBoundaryReportByName(String name) {
        List<CryptoInfo> cryptoInfos = cryptoRepository.findByName(name);
        Map<String, CryptoInfo> report = new HashMap<>();
        report.put("MAX PRICE", cryptoInfos.stream()
                .max(Comparator.comparing(CryptoInfo::getPrice))
                .orElseThrow());
        report.put("MIN PRICE", cryptoInfos.stream()
                .min(Comparator.comparing(CryptoInfo::getPrice))
                .orElseThrow());
        report.put("OLDEST REPORT", cryptoInfos.stream()
                .min(Comparator.comparing(CryptoInfo::getDate))
                .orElseThrow());
        report.put("NEWEST REPORT", cryptoInfos.stream()
                .max(Comparator.comparing(CryptoInfo::getDate))
                .orElseThrow());
        return report;
    }

    public Map<String, BigDecimal> getHighestNormalizedRangeByDate(Date date) {
        Date startDay = DateUtils.truncate(date, Calendar.DATE);
        Date endDay = DateUtils.addMilliseconds(DateUtils.ceiling(date, Calendar.DATE), -1);

        Map<String, List<CryptoInfo>> map = cryptoRepository.findByDateBetween(startDay, endDay).stream()
                .collect(Collectors.groupingBy(CryptoInfo::getName));

        Map.Entry<String, BigDecimal> maxEntry = collectMapWithNormalizedRange(map).entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .orElseThrow();

        return Map.of("Highest normalized range today have " + maxEntry.getKey(), maxEntry.getValue());
    }

    private Map<String, BigDecimal> collectMapWithNormalizedRange(Map<String, List<CryptoInfo>> map) {
        Map<String, BigDecimal> result = new HashMap<>();
        for (Map.Entry<String, List<CryptoInfo>> entry : map.entrySet()) {
            result.put(entry.getKey(), calculateNormalizedRange(entry.getValue()));
        }
        return result;
    }

    private BigDecimal calculateNormalizedRange(List<CryptoInfo> cryptoInfos) {
        BigDecimal maxPrice = cryptoInfos.stream()
                .max(Comparator.comparing(CryptoInfo::getPrice))
                .orElseThrow().getPrice();
        BigDecimal minPrice = cryptoInfos.stream()
                .min(Comparator.comparing(CryptoInfo::getPrice))
                .orElseThrow().getPrice();

        return (maxPrice.subtract(minPrice)).divide(minPrice, RoundingMode.CEILING);
    }
}
