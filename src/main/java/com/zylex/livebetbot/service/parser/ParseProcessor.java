package com.zylex.livebetbot.service.parser;

import com.zylex.livebetbot.controller.logger.LogType;
import com.zylex.livebetbot.controller.logger.ParserLogger;
import com.zylex.livebetbot.model.Game;
import com.zylex.livebetbot.service.repository.GameRepository;
import org.openqa.selenium.WebDriver;

import java.util.List;

public class ParseProcessor {

    private ParserLogger logger = new ParserLogger();

    private WebDriver driver;

    private GameRepository gameRepository;

    public ParseProcessor(WebDriver driver, GameRepository gameRepository) {
        this.driver = driver;
        this.gameRepository = gameRepository;
    }

    public List<Game> process() {
        logger.startLogMessage(LogType.PARSING_START, 0);
        List<Game> noResultGames = gameRepository.getWithoutResult();
        noResultGames.forEach(game -> game.setRuleNumber(null));
        List<Game> breakGames = new CountryParser(driver, noResultGames, logger).parse();
        if (breakGames.isEmpty()) {
            return breakGames;
        }
        GameParser gameParser = new GameParser(driver, logger);
        breakGames.forEach(gameParser::parse);
        return breakGames;
    }
}
