package com.furkanylmz.exchange_rate_api.controller;

import com.furkanylmz.exchange_rate_api.model.CurrencyRate;
import com.furkanylmz.exchange_rate_api.services.CurrencyRateService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "Currency Rate Controller", description = "Döviz kurları ile ilgili işlemler")
public class CurrencyRateController {



    @Autowired
    private CurrencyRateService currencyRateService;

    // Tüm kurları döndür
    @Operation(summary = "Tüm döviz kurlarını getirir")
    @GetMapping("/latest")
    public Map<String, CurrencyRate> getLatestRates() {
        return currencyRateService.getRates();
    }

    // Belirli bir para biriminin kurunu döndür
    @GetMapping("/latest/{currencyCode}")
    public CurrencyRate getRateByCurrencyCode(@PathVariable String currencyCode) {
        return currencyRateService.getRates().get(currencyCode.toUpperCase());
    }

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

        double convertedAmount;

        if ((from.equalsIgnoreCase("EUR") || from.equalsIgnoreCase("GBP")) && to.equalsIgnoreCase("USD")) {
            convertedAmount = amount * (toRate.getForexSelling() / fromRate.getForexSelling());
        } else {
            throw new RuntimeException("Sadece EUR ve GBP USD'ye dönüştürülebilir.");
        }

        return Map.of(
                "from", from,
                "to", to,
                "amount", amount,
                "convertedAmount", convertedAmount
        );
    }



}

