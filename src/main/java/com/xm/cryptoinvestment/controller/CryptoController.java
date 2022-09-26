package com.xm.cryptoinvestment.controller;

import com.xm.cryptoinvestment.entity.CryptoInfo;
import com.xm.cryptoinvestment.service.CryptoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

@RestController
@AllArgsConstructor
public class CryptoController {

    private CryptoService cryptoService;

    @GetMapping("/monthReport/{date}")
    @Tag(name = "Month Report", description = "Get oldest/newest/min/max for each crypto for the whole month")
    public Map<String, Map<String, CryptoInfo>> getMonthReport(@PathVariable @DateTimeFormat(pattern = "yyyy-MM") Date date) {
        return cryptoService.getMonthReport(date);
    }

    @GetMapping("/normalizedRangeReport")
    @Tag(name = "Crypto sorted by Normalized Range", description = "Return a descending sorted list of all the cryptos,\n" +
            "comparing the normalized range")
    public Map<String, BigDecimal> getListOfCryptoInfosSortedByNormalizedRange() {
        return cryptoService.getReportSortedByNormalizedRange();
    }

    @GetMapping("/boundaryReport/{name}")
    @Tag(name = "Boundary report", description = "Return the oldest/newest/min/max values for a requested crypto")
    public Map<String, CryptoInfo> getBoundaryReportByName(@PathVariable String name) {
        return cryptoService.getBoundaryReportByName(name);
    }

    @GetMapping("/highestNormalizedRangeReport/{date}")
    @Tag(name = "Higher Normalized Range", description = "return the crypto with the highest normalized range for a\n" +
            "specific day")
    public Map<String, BigDecimal> getHighestNormalizedRangeByDate(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        return cryptoService.getHighestNormalizedRangeByDate(date);
    }

}
