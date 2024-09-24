package com.furkanylmz.exchange_rate_api.controller;

import com.furkanylmz.exchange_rate_api.model.CurrencyRate;
import com.furkanylmz.exchange_rate_api.services.CurrencyRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class CurrencyRateController {

    @Autowired
    private CurrencyRateService currencyRateService;

    // Tüm kurları döndür
    @GetMapping("/latest")
    public Map<String, CurrencyRate> getLatestRates() {
        return currencyRateService.getRates();
    }

    // Belirli bir para biriminin kurunu döndür
    @GetMapping("/latest/{currencyCode}")
    public CurrencyRate getRateByCurrencyCode(@PathVariable String currencyCode) {
        return currencyRateService.getRates().get(currencyCode.toUpperCase());
    }

    // Para birimleri arasında dönüşüm yap
    @GetMapping("/convert")
    public Map<String, Object> convertCurrency(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam double amount) {

        Map<String, CurrencyRate> rates = currencyRateService.getRates();
        CurrencyRate fromRate = rates.get(from.toUpperCase());
        CurrencyRate toRate = rates.get(to.toUpperCase());

        if (fromRate == null || toRate == null) {
            throw new RuntimeException("Geçersiz para birimi kodu");
        }

        // TRY üzerinden dönüşüm yapıyoruz
        double amountInTRY = amount * fromRate.getForexSelling();
        double convertedAmount = amountInTRY / toRate.getForexBuying();

        return Map.of("result", convertedAmount);
    }
}

