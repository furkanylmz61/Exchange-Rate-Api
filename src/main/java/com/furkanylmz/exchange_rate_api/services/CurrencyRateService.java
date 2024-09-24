package com.furkanylmz.exchange_rate_api.services;

import com.furkanylmz.exchange_rate_api.model.CurrencyRate;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Service
public class CurrencyRateService {
    private Map<String, CurrencyRate> rates = new HashMap<>();


    public void fetchRates(){
        try {
            String url = "https://www.tcmb.gov.tr/kurlar/today.xml";
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new URL(url).openStream());
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("Currency");

            Map<String, CurrencyRate> tempRates = new HashMap<>();

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                Element element = (Element) node;

                String currencyCode = element.getAttribute("CurrencyCode");
                String forexBuyingStr = element.getElementsByTagName("ForexBuying").item(0).getTextContent();
                String forexSellingStr = element.getElementsByTagName("ForexSelling").item(0).getTextContent();

                double forexBuying = forexBuyingStr.isEmpty() ? 0.0 : Double.parseDouble(forexBuyingStr.replace(',', '.'));
                double forexSelling = forexSellingStr.isEmpty() ? 0.0 : Double.parseDouble(forexSellingStr.replace(',', '.'));

                CurrencyRate rate = new CurrencyRate();
                rate.setCurrencyCode(currencyCode);
                rate.setForexBuying(forexBuying);
                rate.setForexSelling(forexSelling);

                tempRates.put(currencyCode, rate);
            }

            rates = tempRates;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, CurrencyRate> getRates() {
        return rates;
    }

}
