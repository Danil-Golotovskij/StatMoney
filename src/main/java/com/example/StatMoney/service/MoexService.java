package com.example.StatMoney.service;

import com.example.StatMoney.service.CbrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import ru.exdata.moex.IssClient;
import ru.exdata.moex.IssClientBuilder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.*;
import java.io.ByteArrayInputStream;

@Service
public class MoexService {

    private final IssClient client;

    @Autowired
    private CbrService cbrService;

    public MoexService() {
        this.client = IssClientBuilder.builder().build();
    }

    public float getCurrentPrice(String ticker) {
        try {
            // Подготовка запроса к API МосБиржи
            var response = client.iss()
                    .engines()
                    .engine("stock")
                    .markets()
                    .market("shares")
                    .securities()
                    .security(ticker)
                    .format().xml() // Изменяем формат на XML
                    .get(Map.of("limit", "1"));

            // Извлечь данные из раздела 'data' в XML
            return parsePriceFromResponse(response);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public float getCurrentBondPrice(String bondIdentifier) {
        try {
            // Подготовка запроса к API МосБиржи
            var response = client.iss()
                    .engines()
                    .engine("stock")
                    .markets()
                    .market("bonds")  // Рынок облигаций
                    .securities()
                    .security(bondIdentifier)
                    .format().xml()
                    .get(Map.of("limit", "1"));

            // Извлечь данные из раздела 'data' в XML
            return parsePriceFromResponse(response) * 10; // В таблице API все числа, возможно, умножены на 0,1

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private float parsePriceFromResponse(String xmlResponse) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(xmlResponse.getBytes("UTF-8")));
            NodeList rows = doc.getElementsByTagName("row");

            float currentPrice = 0;
            String currency = "SUR"; // Предполагаем, что валюта по умолчанию рубли

            for (int i = 0; i < rows.getLength(); i++) {
                Element row = (Element) rows.item(i);
                // Извлекаем валюту
                String currencyStr = row.getAttribute("CURRENCYID");
                if (currencyStr != null && !currencyStr.isEmpty()) {
                    currency = currencyStr;
                }
                // Извлекаем текущую цену
                String currentPriceStr = row.getAttribute("LCURRENTPRICE");
                if (currentPriceStr != null && !currentPriceStr.isEmpty()) {
                    currentPrice = Float.parseFloat(currentPriceStr);
                }
                // Если значение "LCURRENTPRICE" равно нулю, извлекаем значение из "MARKETPRICE"
                if (currentPrice == 0) {
                    String marketPriceStr = row.getAttribute("MARKETPRICE");
                    if (marketPriceStr != null && !marketPriceStr.isEmpty()) {
                        currentPrice = Float.parseFloat(marketPriceStr);
                    }
                }
            }

            // Если валюта не рубли, переводим в рубли
            if (!currency.equals("SUR")) {
                float currencyRate = cbrService.getCurrentCurrencyRate(currency);
                currentPrice *= currencyRate;
            }

            return currentPrice;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Получить список всех shares - акции, bonds - облигации
    public List<Map<String, String>> getAllSecurities(String market) {
        try {
            var response = client.iss()
                    .engines()
                    .engine("stock")
                    .markets()
                    .market(market)
                    .securities()
                    .format().xml()
                    .get(Map.of());

            return parseSecuritiesFromResponse(response);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private List<Map<String, String>> parseSecuritiesFromResponse(String xmlResponse) {
        Set<String> seenTickers = new HashSet<>();
        List<Map<String, String>> securities = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(xmlResponse.getBytes("UTF-8")));
            NodeList rows = doc.getElementsByTagName("row");

            for (int i = 0; i < rows.getLength(); i++) {
                Element row = (Element) rows.item(i);
                String shortName = row.getAttribute("SHORTNAME");
                String secId = row.getAttribute("SECID");

                if (shortName != null && secId != null && !secId.isEmpty() && !seenTickers.contains(secId)) {
                    securities.add(Map.of("name", shortName, "ticker", secId));
                    seenTickers.add(secId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return securities;
    }
}
