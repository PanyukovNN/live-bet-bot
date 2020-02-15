package com.zylex.livebetbot.service.filecreator;

import com.zylex.livebetbot.controller.logger.LeagueFileCreatorLogger;
import com.zylex.livebetbot.controller.logger.LogType;
import com.zylex.livebetbot.exception.LeagueFileCreationException;
import com.zylex.livebetbot.model.League;
import com.zylex.livebetbot.model.LeagueToScan;
import com.zylex.livebetbot.service.repository.LeagueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class LeagueFileCreator {

    private static final LeagueFileCreatorLogger logger = new LeagueFileCreatorLogger();

    private LeagueRepository leagueRepository;

    @Autowired
    public LeagueFileCreator(LeagueRepository leagueRepository) {
        this.leagueRepository = leagueRepository;
    }

    public void create() {
        List<League> leagues = leagueRepository.getNewLeagues();
        leagues.forEach(league -> league.setNew(false));
        leagues.forEach(leagueRepository::update);
        leagues = removeLeaguesToScan(leagues);
        if (leagues.isEmpty()) {
            logger.log(LogType.NO_LEAGUES);
            return;
        }
        DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy_HH.mm");
        String fileName = String.format("leagues/%s.csv", DATE_FORMATTER.format(LocalDateTime.now()));
        createFile(fileName);
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName, false), StandardCharsets.UTF_8))) {
            String LEAGUE_BODY_FORMAT = "%s\n";
            for (League league : leagues) {
                String output = String.format(LEAGUE_BODY_FORMAT,
                        league.getName());
                writer.write(output);
            }
            logger.log(LogType.OKAY);
        } catch (IOException e) {
            logger.log(LogType.ERROR);
            throw new LeagueFileCreationException(e.getMessage(), e);
        }
    }

    private List<League> removeLeaguesToScan(List<League> leagues) {
        List<League> filteredLeagues = new ArrayList<>();
        List<LeagueToScan> leaguesToScan = leagueRepository.getLeaguesToScan();
        for (League league : leagues) {
            if (leaguesToScan.stream().noneMatch(lts -> lts.getName().equals(league.getName()))) {
                filteredLeagues.add(league);
            }
        }
        return filteredLeagues;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void createFile(String fileName) {
        try {
            new File("leagues").mkdir();
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            throw new LeagueFileCreationException(e.getMessage(), e);
        }
    }
}
