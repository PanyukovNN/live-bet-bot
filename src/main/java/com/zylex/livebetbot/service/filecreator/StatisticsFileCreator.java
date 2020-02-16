package com.zylex.livebetbot.service.filecreator;

import com.zylex.livebetbot.controller.logger.LogType;
import com.zylex.livebetbot.controller.logger.StatisticsFileCreatorLogger;
import com.zylex.livebetbot.exception.StatisticsFileCreationException;
import com.zylex.livebetbot.model.Game;
import com.zylex.livebetbot.model.OverUnder;
import com.zylex.livebetbot.service.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
//@ComponentScan
public class StatisticsFileCreator {

    private static final StatisticsFileCreatorLogger logger = new StatisticsFileCreatorLogger();

    private GameRepository gameRepository;

    @Autowired
    public StatisticsFileCreator(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Transactional
    public void create() {
        createStatisticsFile(LocalDate.now().minusDays(1));
        logger.log(LogType.OKAY);
    }

    private void createStatisticsFile(LocalDate date) {
        List<Game> games = gameRepository.getByDate(date);
        if (games.isEmpty()) {
            return;
        }
        DateTimeFormatter FILE_NAME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String fileName = String.format("statistics/%s.csv", FILE_NAME_FORMATTER.format(date));
        DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        createFile(fileName);
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName, false), StandardCharsets.UTF_8))) {
            String GAME_BODY_FORMAT = "%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n";
            for (Game game : games) {
                String over10 = findOverUnder(game.getOverUnderList(), OverUnder.Type.OVER, 1);
                String over15 = findOverUnder(game.getOverUnderList(), OverUnder.Type.OVER, 1.5);
                String under10 = findOverUnder(game.getOverUnderList(), OverUnder.Type.UNDER, 1);
                String under15 = findOverUnder(game.getOverUnderList(), OverUnder.Type.UNDER, 1.5);
                String output = String.format(GAME_BODY_FORMAT,
                        DATE_FORMATTER.format(game.getDateTime()),
                        game.getCountry().getName(),
                        game.getLeague().getName(),
                        game.getFirstTeam(), game.getSecondTeam(),
                        game.getScanTimeScore(), game.getFinalScore(),
                        game.getRuleNumber(), over10, over15, under10, under15);
                writer.write(output);
            }
        } catch (IOException e) {
            throw new StatisticsFileCreationException(e.getMessage(), e);
        }
    }

    private String findOverUnder(List<OverUnder> overUnderList, OverUnder.Type type, double size) {
        return overUnderList.stream()
                .filter(overUnder ->
                        overUnder.getType().equals(type.toString()) && Math.abs(overUnder.getSize() - size) < 0.00001)
                .findFirst()
                .map(overUnder -> new DecimalFormat("#0.00")
                        .format(overUnder.getCoefficient())
                        .replace(",", "."))
                .orElse("");
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void createFile(String fileName) {
        try {
            new File("statistics").mkdir();
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            throw new StatisticsFileCreationException(e.getMessage(), e);
        }
    }
}
