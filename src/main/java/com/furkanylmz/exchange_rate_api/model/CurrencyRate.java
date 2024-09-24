package com.furkanylmz.exchange_rate_api.model;

public class CurrencyRate {
    private String currencyCode;
    private double forexBuying;
    private double forexSelling;

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public double getForexBuying() {
        return forexBuying;
    }

    public void setForexBuying(double forexBuying) {
        this.forexBuying = forexBuying;
    }

    public double getForexSelling() {
        return forexSelling;
    }

    public void setForexSelling(double forexSelling) {
        this.forexSelling = forexSelling;
    }
}
