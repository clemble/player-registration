FROM java:8-jre
MAINTAINER antono@clemble.com

EXPOSE 8080

ADD target/player-registration-0.17.0-SNAPSHOT.jar /data/player-registration.jar

CMD java -jar -Dspring.profiles.active=cloud /data/player-registration.jar
