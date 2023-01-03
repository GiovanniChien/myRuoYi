FROM openjdk:17

MAINTAINER giovanni

COPY ./ruoyi-web/target/ruoyi-web.jar /ruoyi-web.jar

ENV JAVA_OPTS="$JAVA_OPTS -Xms1g -Xmx1g -Xmn500m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=320m"

EXPOSE 9090

ENTRYPOINT ["java", "-jar", "/ruoyi-web.jar"]

