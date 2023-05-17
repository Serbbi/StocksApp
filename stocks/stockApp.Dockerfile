FROM maven:latest

COPY . ./

RUN mvn package -Dmaven.test.skip -Dcheckstyle.skip
ENTRYPOINT ["java", "-cp", "stocks-application/target/stocks-application-1.0-SNAPSHOT-jar-with-dependencies.jar", "nl.rug.aoop.stocksapplication.StocksAppMain"]