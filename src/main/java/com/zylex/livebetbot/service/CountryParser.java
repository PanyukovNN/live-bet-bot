package com.zylex.livebetbot.service;

import com.zylex.livebetbot.exception.CountryParserException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CountryParser {

    public List<String> parse() {
        try {
            Document document = Jsoup.connect("http://ballchockdee.com/ru-ru/euro/ставки-live/футбол")
                    .userAgent("Chrome/4.0.249.0 Safari/532.5")
                    .referrer("http://www.google.com")
                    .get();
            Elements elements = document.select("ul#ms-live-res-ul-1 > li.Unsel > a");
            List<String> countryLinks = new ArrayList<>();
            elements.forEach(element -> countryLinks.add(element.attr("href")));
            return countryLinks;
        } catch (IOException e) {
            throw new CountryParserException(e.getMessage(), e);
        }
    }
}
