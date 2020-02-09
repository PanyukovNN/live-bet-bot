## **Live-Bet-Bot**
Live-bet-bot includes three modules: 
1. Scanning ballchockdee.com every 10 minutes, and finding live football games that appropriate for special rules (Code of rules hide from Git).
1. Collecting games results.
1. Export statistics to file and also displays it to console. 

#### **Used tecnologies:**
- Spring framework
- JPA (Hibernate) 
- PostgreSQL
- Maven
- Selenium
- Logging (log4j)
- Stream API
- Scheduled executor service
- Java 8 Data/Time API
- Properties

------------

###### Output example:
    **************************************************
    Bot started at: 01:19 AM 09.01.2020
    --------------------------------------------------
    Starting chrome driver: complete
    --------------------------------------------------
    Starting chrome driver: complete
    --------------------------------------------------
    Parsing started at 01:19 AM 09.01.2020
    Finding countries: complete
    --------------------------------------------------
    Processing countries: 1/1 (100.0%)
    --------------------------------------------------
    Processing games: 1/1 (100.0%)
    --------------------------------------------------
    Parsing completed in 00 min. 07 sec.
    --------------------------------------------------
    Appropriate games for FIRST_RULE: no games
    Appropriate games for SECOND_RULE: no games
    ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    Result scanning: complete (no games to scan)
    ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
###### 
                            08.01.2020
                 | Total | More one goal | One goal | No goal | N/R
    ----------------------------------------------------------
      FIRST_RULE |   12  |       6       |     3    |    3    |  0
    ----------------------------------------------------------
     SECOND_RULE |    3  |       0       |     2    |    1    |  0
    ----------------------------------------------------------
     Statistics file created successfully (inserted 15 games)