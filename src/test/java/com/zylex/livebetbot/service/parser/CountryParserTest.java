package com.zylex.livebetbot.service.parser;

import com.zylex.livebetbot.model.Game;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

class CountryParserTest {

    @Autowired
    private CountryParser countryParser;

    @Test
    void processCountryParser_parse_returnCorrectArray() {
        List<Game> games = countryParser.parse();
        if (!games.isEmpty()) {

        }
    }
}