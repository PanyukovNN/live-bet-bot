package com.zylex.livebetbot.controller.logger;

import com.zylex.livebetbot.service.rule.RuleNumber;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StatisticsCollectorLogger {

    private DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public synchronized void startLogMessage() {
        String periodMessage = String.format("%s", DATE_FORMATTER.format(LocalDate.now().minusDays(1)));
        String header = "\n             | Total | Two more goal | One goal | No goal | N/R";
        int indent = (header.length() - periodMessage.length()) / 2;
        System.out.print(StringUtils.repeat(" ", indent) + periodMessage);
        System.out.print("\n" + StringUtils.repeat("-", header.length()));
        System.out.print(header);
        System.out.print("\n" + StringUtils.repeat("-", header.length()));
    }

    public void logStatistics(RuleNumber ruleNumber, int twoMoreGoal, int oneGoal, int noGoal, int noResult) {
        int totalGames = twoMoreGoal + oneGoal + noGoal + noResult;
        String output = String.format("\n%12s | %4d  | %7d       | %5d    | %4d    | %2d", ruleNumber, totalGames, twoMoreGoal, oneGoal, noGoal, noResult);
        System.out.print(output);
        System.out.print("\n" + StringUtils.repeat("-", output.length() + 1));
    }
}
