FROM maven:3.5.4-jdk-8

ARG branch=master

ENV APP_NAME portal-song-exporter
ENV DOWNLOAD_URL https://github.com/rtisma/portal-song-exporter/archive/${branch}.tar.gz
ENV APP_HOME /${APP_NAME}
ENV TARBALL /tmp/${APP_NAME}.tar.gz
RUN curl -fsSL -o ${TARBALL} ${DOWNLOAD_URL} \
    && tar zxvf ${TARBALL} -C /tmp \
    && mv /tmp/${APP_NAME}-${branch} ${APP_HOME} \
    && cd ${APP_HOME} \
    && mvn clean package -DskipTests

RUN cp /${APP_HOME}/target/*-jar-with-dependencies.jar /app.jar
COPY ./entrypoint.sh /entrypoint.sh
RUN chmod 755 /entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]
CMD []

