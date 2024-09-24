package com.furkanylmz.exchange_rate_api;

import com.furkanylmz.exchange_rate_api.services.CurrencyRateService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableScheduling
public class ExchangeRateApiApplication {

	@Autowired
	private CurrencyRateService currencyRateService;

	public static void main(String[] args) {
		SpringApplication.run(ExchangeRateApiApplication.class, args);
	}

	@PostConstruct
	public void init() {
		currencyRateService.fetchRates();
	}

	// Her saat başı kurları güncelle
	@Scheduled(cron = "0 0 * * * *")
	public void scheduleRateUpdate() {
		currencyRateService.fetchRates();
	}
}
