FROM java:8-jre
MAINTAINER antono@clemble.com

EXPOSE 10000

ADD ./buildoutput/player-registration.jar /data/player-registration.jar

CMD java -jar -Dspring.profiles.active=cloud -Dlogging.config=classpath:logback.cloud.xml -Dserver.port=10000 /data/player-registration.jar
