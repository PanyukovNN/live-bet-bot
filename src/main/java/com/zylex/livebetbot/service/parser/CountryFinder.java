package com.zylex.livebetbot.service.parser;

import com.zylex.livebetbot.controller.logger.CountryFinderLogger;
import com.zylex.livebetbot.controller.logger.LogType;
import com.zylex.livebetbot.exception.CountryFinderException;
import com.zylex.livebetbot.model.Country;
import com.zylex.livebetbot.service.repository.CountryRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

@SuppressWarnings("WeakerAccess")
@Service
public class CountryFinder {

    private static final CountryFinderLogger logger = new CountryFinderLogger();

    private CountryRepository countryRepository;

    public CountryFinder(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    public Set<Country> findCountries() {
        try {
            logger.startLogMessage();
            Set<Country> countries = countryRepository.save(extractCountryLinks());
            if (countries.isEmpty()) {
                logger.logCountriesFound(LogType.NO_COUNTRIES);
                return Collections.emptySet();
            }
            logger.logCountriesFound(LogType.OKAY);
            return countries;
        } catch (IOException e) {
            throw new CountryFinderException(e.getMessage(), e);
        }
    }

    private Set<Country> extractCountryLinks() throws IOException {
        Set<Country> countries = new LinkedHashSet<>();
        Document document = Jsoup.connect("http://www.ballchockdee.com/euro/live-betting/football")
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36 OPR/60.0.3255.170")
                .referrer("http://www.google.com")
                .get();
        Elements elements = document.select("ul#ms-live-res-ul-1 > li.Unsel > a");
        for (Element element : elements) {
            String countryName = element.select("div").first().text().replaceAll("\\d+", "");
            String countryLink = element.attr("href");
            Country country = new Country(countryName, countryLink);
            countries.add(country);
        }
        return countries;
    }
}
