package com.furkanylmz.exchange_rate_api.model;

import lombok.Data;

@Data
public class CurrencyRate {
    private String currencyCode;
    private double forexBuying;
    private double forexSelling;

}
