package com.zylex.livebetbot;

import com.zylex.livebetbot.controller.logger.ConsoleLogger;
import com.zylex.livebetbot.controller.logger.LogType;
import com.zylex.livebetbot.exception.LiveBetBotException;
import com.zylex.livebetbot.service.DriverManager;
import com.zylex.livebetbot.service.HibernateUtil;
import org.hibernate.Session;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LiveBetBotApplication {

    public static void main(String[] args) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1, r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });
        try (Session session = HibernateUtil.getSessionFactory().openSession();
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            ScheduledParsingTask parsingTask = new ScheduledParsingTask(
                    new DriverManager(),
                    session
            );
            scheduler.scheduleAtFixedRate(parsingTask, 0, 10, TimeUnit.MINUTES);
            //noinspection StatementWithEmptyBody
            while (!reader.readLine().equalsIgnoreCase("exit")) {
            }
        } catch (IOException e) {
            throw new LiveBetBotException(e.getMessage(), e);
        } finally {
            scheduler.shutdown();
            ConsoleLogger.endMessage(LogType.BOT_END);
        }
    }

//    private static Connection getConnection() {
//        try(InputStream inputStream = LiveBetBotApplication.class.getClassLoader().getResourceAsStream("dataBase.properties")) {
//            Properties property = new Properties();
//            property.load(inputStream);
//            final String login = property.getProperty("db.login");
//            final String password = property.getProperty("db.password");
//            final String url = property.getProperty("db.url");
//            Class.forName("org.postgresql.Driver");
//            return java.sql.DriverManager.getConnection(url, login, password);
//        } catch(SQLException | IOException | ClassNotFoundException e) {
//            throw new LiveBetBotException(e.getMessage(), e);
//        }
//    }
}
