package com.example.StatMoney.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class CbrService {

    private final RestTemplate restTemplate;

    @Autowired
    public CbrService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public float getCurrentCurrencyRate(String currencyCode) {
        String url = STR."https://www.cbr.ru/scripts/XML_daily.asp?date_req=\{getCurrentDate()}";
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        return parseCurrencyRate(responseEntity.getBody(), currencyCode);
    }

    //Формат даты dd/MM/yyyy
    public float getCurrentCurrencyRate(String currencyCode, String date) {
        String url = STR."https://www.cbr.ru/scripts/XML_daily.asp?date_req=\{date}";
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        return parseCurrencyRate(responseEntity.getBody(), currencyCode);
    }

    private String getCurrentDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return LocalDate.now().format(formatter);
    }

    private float parseCurrencyRate(String xmlResponse, String currencyCode) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new java.io.ByteArrayInputStream(xmlResponse.getBytes("windows-1251")));
            NodeList nodeList = doc.getElementsByTagName("Valute");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Element element = (Element) nodeList.item(i);
                String charCode = element.getElementsByTagName("CharCode").item(0).getTextContent();
                if (charCode.equals(currencyCode)) {
                    String value = element.getElementsByTagName("Value").item(0).getTextContent();
                    return Float.parseFloat(value.replace(',', '.'));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
