package com.zylex.livebetbot.controller.logger;

import com.zylex.livebetbot.service.rule.RuleNumber;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StatisticsCollectorLogger {

    private DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public synchronized void startLogMessage(LocalDate startDate, LocalDate endDate) {
        String periodMessage;
        if (startDate.equals(endDate)) {
            periodMessage = String.format("%s",
                    DATE_FORMATTER.format(startDate));
        } else {
            periodMessage = String.format("%s - %s",
                    DATE_FORMATTER.format(startDate),
                    DATE_FORMATTER.format(endDate));
        }
        String header = "\n             | Total | Over goal | Refund | No goal | N/R";
        int indent = (header.length() - periodMessage.length()) / 2;
        System.out.print(StringUtils.repeat(" ", indent) + periodMessage);
        System.out.print("\n" + StringUtils.repeat("-", header.length()));
        System.out.print(header);
        System.out.print("\n" + StringUtils.repeat("-", header.length()));
    }

    public void logStatistics(RuleNumber ruleNumber, int overGoal, int refund, int noGoal, int noResult) {
        int totalGames = overGoal + refund + noGoal + noResult;
        String output = String.format("\n%12s | %4d  | %5d     | %4d   | %4d    | %2d", ruleNumber, totalGames, overGoal, refund, noGoal, noResult);
        System.out.print(output);
        System.out.print("\n" + StringUtils.repeat("-", output.length() + 1));
    }

    public void fileCreatedSuccessful(int insertedGames) {
        System.out.print(String.format("\nStatistics file created successful (inserted %d games)", insertedGames));
    }
}
