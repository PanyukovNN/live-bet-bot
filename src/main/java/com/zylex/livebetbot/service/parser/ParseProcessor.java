package com.zylex.livebetbot.service.parser;

import com.zylex.livebetbot.controller.logger.LogType;
import com.zylex.livebetbot.controller.logger.ParserLogger;
import com.zylex.livebetbot.model.Game;
import org.hibernate.Session;
import org.openqa.selenium.WebDriver;

import javax.persistence.Query;
import java.util.List;

public class ParseProcessor {

    private ParserLogger logger = new ParserLogger();

    private WebDriver driver;

    private Session session;

    public ParseProcessor(WebDriver driver, Session session) {
        this.driver = driver;
        this.session = session;
    }

    public List<Game> process() {
        logger.startLogMessage(LogType.PARSING_START, 0);
        Query query = session.createQuery("FROM Game WHERE finalScore IS NULL OR finalScore = '-1:-1'");
        List<Game> noResultGames = query.getResultList();
        List<Game> breakGames = new CountryParser(driver, noResultGames, logger).parse();
        if (breakGames.isEmpty()) {
            return breakGames;
        }
        GameParser gameParser = new GameParser(driver, logger);
        breakGames.forEach(gameParser::parse);
        return breakGames;
    }
}
