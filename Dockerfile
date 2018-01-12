FROM quay.io/ukhomeofficedigital/openjdk8

RUN yum update -y \
    && yum install -y \
    unzip \
    sed \
    zip \
    && yum clean all

ENV ALF_HOME /usr/local/alfresco
ENV CATALINA_HOME /usr/local/tomcat

ENV DIST /tmp/alfresco
ENV PATH $CATALINA_HOME/bin:$ALF_HOME/bin:$PATH

ADD assets/tomcat/apache-tomcat-7.0.82.tar.gz $CATALINA_HOME
ADD assets/alfresco/alfresco42.tar $DIST

WORKDIR $ALF_HOME

RUN set -x \
        && ln -s /usr/local/tomcat /usr/local/alfresco/tomcat \
        && mv $DIST/web-server/webapps/alfresco.war tomcat/webapps/alfresco.war \
        && mv $DIST/bin . \
        && rm -rf $CATALINA_HOME/webapps/docs \
        && rm -rf $CATALINA_HOME/webapps/examples \
        && rm -rf $DIST


RUN zip -d tomcat/webapps/alfresco.war "WEB-INF/lib/httpclient-4.1.1.jar"
RUN zip -d tomcat/webapps/alfresco.war "WEB-INF/lib/httpclient-cache-4.1.1.jar"
RUN zip -d tomcat/webapps/alfresco.war "WEB-INF/lib/httpcore-4.1.3.jar"

COPY assets/tomcat/lib/*.jar tomcat/lib/
COPY assets/tomcat/catalina.properties tomcat/conf/catalina.properties
COPY assets/tomcat/setenv.sh tomcat/bin/setenv.sh
COPY assets/tomcat/server.xml tomcat/conf/server.xml
COPY assets/tomcat/context.xml tomcat/conf/context.xml
COPY assets/tomcat/catalina.policy tomcat/conf/catalina.policy
COPY assets/tomcat/tomcat-users.xml tomcat/conf/tomcat-users.xml
COPY assets/tomcat/logging.properties tomcat/conf/logging.properties
COPY assets/tomcat/web.xml tomcat/conf/web.xml

COPY assets/alfresco/alfresco-global.properties tomcat/shared/classes/alfresco-global.properties
COPY assets/alfresco/log4j.properties tomcat/shared/classes/alfresco/extension/log4j.properties
COPY assets/alfresco/caching-content-store-context.xml tomcat/shared/classes/alfresco/extension/caching-content-store-context.xml
COPY assets/alfresco/share-config-custom.xml tomcat/shared/classes/alfresco/web-extension/share-config-custom.xml

# AMPS installation
COPY homeoffice-cts-repo/target/homeoffice-cts-repo.amp amps/homeoffice-cts-repo.amp
COPY assets/alfresco/entrypoint.sh entrypoint.sh

RUN bash ./bin/apply_amps.sh -force -nobackup && chmod +x  entrypoint.sh
ENTRYPOINT ["./entrypoint.sh"]

EXPOSE 8080
CMD ["catalina.sh", "run"]