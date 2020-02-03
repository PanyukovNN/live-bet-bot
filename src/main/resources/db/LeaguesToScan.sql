DROP TABLE IF EXISTS league_to_scan CASCADE;
CREATE TABLE IF NOT EXISTS league_to_scan (
    id      BIGSERIAL PRIMARY KEY,
    league  VARCHAR(300) NOT NULL
);

INSERT INTO league_to_scan (id, league)
VALUES (default, 'Ставки на фактический исход'),
       (default, 'AUSTRIA BUNDESLIGA'),
       (default, 'АНГЛИЙСКАЯ ПРЕМЬЕР-ЛИГА'),
       (default, 'ЧЕМПИОН-ЛИГА АНГЛИИ'),
       (default, 'Argentina Superliga'),
       (default, 'BELGIUM FIRST DIVISION A'),
       (default, 'ВТОРАЯ БУНДЕСЛИГА ГЕРМАНИИ'),
       (default, 'НЕМЕЦКАЯ БУНДЕСЛИГА'),
       (default, 'Greece Super League'),
       (default, 'Суперлига Дании'),
       (default, 'Egypt Premier League'),
       (default, 'ИСПАНСКАЯ ЛА ЛИГА'),
       (default, 'SPAIN LA LIGA 2'),
       (default, 'ИТАЛЬЯНСКАЯ СЕРИЯ А'),
       (default, 'ИТАЛЬЯНСКАЯ СЕРИЯ В'),
       (default, 'Colombia Primera A'),
       (default, 'Mexico Primera Division'),
       (default, 'Netherlands Eerste Divisie'),
       (default, 'NETHERLANDS EREDIVISIE'),
       (default, 'UAE Arabian Gulf League'),
       (default, 'Oman Professional League'),
       (default, 'Poland Ekstraklasa'),
       (default, 'PORTUGAL LIGA NOS'),
       (default, 'РОССИЙСКАЯ ПРЕМЬЕР-ЛИГА'),
       (default, 'Romania Liga 1'),
       (default, 'Northern Ireland Danske Bank Premiership'),
       (default, 'ФРАНЦУЗСКАЯ ЛИГА 1'),
       (default, 'FRANCE DOMINO''S LIGUE 2'),
       (default, 'Croatia Prva Liga'),
       (default, 'Czech Republic First League'),
       (default, 'Chile Primera Division'),
       (default, 'Чемпионат-лига Шотландии'),
       (default, 'SCOTLAND PREMIERSHIP'),
       (default, 'Israel Premier League'),
       (default, 'India Super League'),
       (default, 'Kenya Premier League'),
       (default, 'Tunisia Professional Ligue 1'),
       (default, 'TURKEY SUPER LEAGUE'),
       (default, 'Portugal Ledman LigaPro'),
       (default, 'Costa Rica Campeonato Primera Division'),
       (default, 'Bolivia Professional Football League'),
       (default, 'Belgium First Division B'),
       (default, 'Guatemala Liga National'),
       (default, 'SWISS RAIFFEISEN SUPER LEAGUE'),
       (default, 'Swiss Challenge League'),
       (default, 'Venezuela Primera Division'),
       (default, 'Qatar QNB Stars League'),
       (default, 'Kuwait Premier League'),
       (default, 'Mexico Liga de Ascenso'),
       (default, 'Saudi Arabia Pro League'),
       (default, 'Jamaica Premier League'),
       (default, 'Austria 2nd Liga'),
       (default, 'Greece Super League 2'),
       (default, 'Israel Liga Leumit'),
       (default, 'India I-League'),
       (default, 'Iran Pro League'),
       (default, 'Colombia Primera B'),
       (default, 'Morocco Botola Pro'),
       (default, 'Paraguay Primera Division'),
       (default, 'Peru Liga 1'),
       (default, 'Turkey 1st Lig'),
       (default, 'South Africa ABSA Premiership');

-- Австралия › Ставки на фактический исход
-- Австрия › AUSTRIA BUNDESLIGA
-- Англия › АНГЛИЙСКАЯ ПРЕМЬЕР-ЛИГА
-- Англия › ЧЕМПИОН-ЛИГА АНГЛИИ
-- Аргентина › Argentina Superliga
--
-- Бельгия › BELGIUM FIRST DIVISION A
-- Германия › ВТОРАЯ БУНДЕСЛИГА ГЕРМАНИИ
-- Германия › НЕМЕЦКАЯ БУНДЕСЛИГА
-- Греция › Greece Super League
-- Дания › Суперлига Дании
--
-- Египет › Egypt Premier League
-- Испания › ИСПАНСКАЯ ЛА ЛИГА
-- Испания › SPAIN LA LIGA 2
-- Италия › ИТАЛЬЯНСКАЯ СЕРИЯ А
-- Италия › ИТАЛЬЯНСКАЯ СЕРИЯ В
--
-- Колумбия › Colombia Primera A
-- Мексика › Mexico Primera Division
-- Нидерланды › Netherlands Eerste Divisie
-- Нидерланды › NETHERLANDS EREDIVISIE
-- Объединённые Арабские Эмираты › UAE Arabian Gulf League
--
-- Оман › Oman Professional League
-- Польша › Poland Ekstraklasa
-- Португалия › PORTUGAL LIGA NOS
-- Россия › РОССИЙСКАЯ ПРЕМЬЕР-ЛИГА
-- Румыния › Romania Liga 1
--
-- Северная Ирландия › Northern Ireland Danske Bank Premiership
-- Франция › ФРАНЦУЗСКАЯ ЛИГА 1
-- Франция › FRANCE DOMINO''S LIGUE 2
-- Хорватия › Croatia Prva Liga
-- Чехия › Czech Republic First League
--
-- Чили › Chile Primera Division
-- Шотландия › Чемпионат-лига Шотландии
-- Шотландия › SCOTLAND PREMIERSHIP
-- Израиль › Israel Premier League
-- Индия › India Super League
--
-- Кения › Kenya Premier League
-- Тунис › Tunisia Professional Ligue 1
-- Турция › TURKEY SUPER LEAGUE
-- Португалия › Portugal Ledman LigaPro
-- Коста-Рика › Costa Rica Campeonato Primera Division
--
-- Боливия › Bolivia Professional Football League
-- Бельгия › Belgium First Division B
-- Гватемала › Guatemala Liga National
-- Швейцария › SWISS RAIFFEISEN SUPER LEAGUE
-- Швейцария › Swiss Challenge League
--
-- Венесуэла › Venezuela Primera Division
-- Катар › Qatar QNB Stars League
-- Кувейт › Kuwait Premier League
-- Мексика › Mexico Liga de Ascenso
-- Саудовская Аравия › Saudi Arabia Pro League
--
-- Ямайка › Jamaica Premier League
-- Австрия › Austria 2nd Liga
-- Греция › Greece Super League 2
-- Израиль › Israel Liga Leumit
-- Индия › India I-League
--
-- Иран › Iran Pro League
-- Колумбия › Colombia Primera B
-- Марокко › Morocco Botola Pro
-- Парагвай › Paraguay Primera Division
-- Перу › Peru Liga 1
-- Турция › Turkey 1st Lig
-- Южно-Африканская Республика › South Africa ABSA Premiership