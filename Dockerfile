FROM java:8-jre
MAINTAINER antono@clemble.com

EXPOSE 10000

ADD target/player-registration-*-SNAPSHOT.jar /data/player-registration.jar

CMD java -jar -Dspring.profiles.active=cloud  -Dserver.port=10000 /data/player-registration.jar
