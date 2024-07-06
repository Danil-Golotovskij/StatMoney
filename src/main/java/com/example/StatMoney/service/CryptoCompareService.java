package com.example.StatMoney.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class CryptoCompareService {

    private final RestTemplate restTemplate;
    private final String apiKey = "8146bb0e9c1cefc669a28177d4455c1ebed6c99784b26aa0bf5c294a33569bab";
    @Autowired
    private CbrService cbrService;

    @Autowired
    public CryptoCompareService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public float getCryptoPrice(String cryptoSymbol) {
        String url = String.format(
                "https://min-api.cryptocompare.com/data/price?fsym=%s&tsyms=USD&api_key=%s",
                cryptoSymbol.toUpperCase(),
                apiKey
        );

        try {
            String response = restTemplate.getForObject(url, String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response);
            float priceInUSD = root.path("USD").floatValue();
            float usdToRubRate = cbrService.getCurrentCurrencyRate("USD");
            return priceInUSD * usdToRubRate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Map<String, String>> getAllCryptocurrencies() {
        String url = String.format(
                "https://min-api.cryptocompare.com/data/all/coinlist?api_key=%s",
                apiKey
        );

        List<Map<String, String>> cryptocurrencies = new ArrayList<>();

        try {
            String response = restTemplate.getForObject(url, String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response).path("Data");

            Iterator<Map.Entry<String, JsonNode>> fields = root.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                String symbol = field.getKey();
                String fullName = field.getValue().path("FullName").asText();

                cryptocurrencies.add(Map.of("symbol", symbol, "fullName", fullName));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cryptocurrencies;
    }

    public List<Map<String, String>> getAllCryptocurrenciesWithLogos() {
        String url = String.format(
                "https://min-api.cryptocompare.com/data/all/coinlist?api_key=%s",
                apiKey
        );

        List<Map<String, String>> cryptocurrencies = new ArrayList<>();

        try {
            String response = restTemplate.getForObject(url, String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response).path("Data");

            Iterator<Map.Entry<String, JsonNode>> fields = root.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                String symbol = field.getKey();
                JsonNode coinData = field.getValue();
                String fullName = coinData.path("FullName").asText();
                String imageUrl = coinData.path("ImageUrl").asText();
                String fullImageUrl = STR."https://www.cryptocompare.com\{imageUrl}";

                cryptocurrencies.add(Map.of("symbol", symbol, "fullName", fullName, "logo", fullImageUrl));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cryptocurrencies;
    }

    public void downloadCryptoLogos() {
        List<Map<String, String>> cryptocurrencies = getAllCryptocurrenciesWithLogos();

        //Создание директории в домашней папке
        String homeDir = System.getProperty("user.home");
        String folderPath = STR."\{homeDir}\{File.separator}crypto_logos";
        File folder = new File(folderPath);
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                System.err.println(STR."Failed to create directory: \{folderPath}");
                return;
            }
        }

        //Загрузка логотипов
        for (Map<String, String> crypto : cryptocurrencies) {
            String logoUrl = crypto.get("logo");
            String symbol = crypto.get("symbol");

            if (logoUrl != null && !logoUrl.isEmpty()) {
                try (InputStream in = new URL(logoUrl).openStream();
                     FileOutputStream out = new FileOutputStream(STR."\{folderPath}\{File.separator}\{symbol}.png")) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                    System.out.println(STR."Downloaded logo for \{symbol}");
                } catch (Exception e) {
                    System.out.println(STR."Failed to download logo for \{symbol}: \{e.getMessage()}");
                }
            }
        }
    }
}
