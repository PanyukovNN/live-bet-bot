package com.zylex.livebetbot.config;

import com.zylex.livebetbot.service.DriverManager;
import com.zylex.livebetbot.service.HibernateUtil;
import com.zylex.livebetbot.service.ResultScanner;
import com.zylex.livebetbot.service.Saver;
import com.zylex.livebetbot.service.parser.CountryParser;
import com.zylex.livebetbot.service.parser.GameParser;
import com.zylex.livebetbot.service.parser.ParseProcessor;
import com.zylex.livebetbot.service.repository.GameRepository;
import com.zylex.livebetbot.service.rule.RuleProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class LiveBetBotConfig {

    @Bean
    public GameRepository gameRepository() {
        return new GameRepository(HibernateUtil.getSessionFactory().openSession());
    }

    @Bean
    public DriverManager driverManager() {
        return new DriverManager();
    }

    @Bean
    public CountryParser countryParser(DriverManager driverManager, GameRepository gameRepository) {
        return new CountryParser(driverManager, gameRepository);
    }

    @Bean
    public GameParser gameParser(CountryParser countryParser) {
        return new GameParser(countryParser);
    }

    @Bean
    public ParseProcessor parseProcessor(DriverManager driverManager, GameParser gameParser) {
        return new ParseProcessor(driverManager, gameParser);
    }

    @Bean
    public RuleProcessor ruleProcessor(ParseProcessor parseProcessor) {
        return new RuleProcessor(parseProcessor);
    }

    @Bean
    public Saver saver(RuleProcessor ruleProcessor, GameRepository gameRepository) {
        return new Saver(ruleProcessor, gameRepository);
    }

    @Bean
    public ResultScanner resultScanner(DriverManager driverManager, GameRepository gameRepository) {
        return new ResultScanner(driverManager, gameRepository);
    }
}