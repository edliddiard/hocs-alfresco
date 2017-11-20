FROM quay.io/ukhomeofficedigital/openjdk8

RUN yum update -y
RUN yum install -y \
    apr \
    apr-devel \
    curl \
    cpp \
    gcc \
    ghostscript \
    gpg \
    ImageMagick \
    lsof \
    make \
    tar \
    unzip \
    sed \
    wget
RUN yum clean all

ENV ALF_DOWNLOAD_URL https://download.alfresco.com/release/community/4.2.f-build-00012/alfresco-community-4.2.f.zip
ENV SOLR_DOWNLOAD_URL https://download.alfresco.com/release/community/4.2.f-build-00012/alfresco-community-solr-4.2.f.zip
ENV TOMCAT_TGZ_URL=http://archive.apache.org/dist/tomcat/tomcat-7/v7.0.59/bin/apache-tomcat-7.0.59.tar.gz

ENV ALF_HOME /usr/local/alfresco
ENV CATALINA_HOME /usr/local/tomcat

ENV DIST /tmp/alfresco
ENV SOLR /tmp/solr
ENV PATH $CATALINA_HOME/bin:$ALF_HOME/bin:$PATH

RUN set -x \
	&& gpg --keyserver pgp.mit.edu --recv-key D63011C7 \
	&& mkdir -p $CATALINA_HOME \
	&& curl -fSL "$TOMCAT_TGZ_URL" -o tomcat.tar.gz \
	&& curl -fSL "$TOMCAT_TGZ_URL.asc" -o tomcat.tar.gz.asc \
	&& gpg --verify tomcat.tar.gz.asc \
	&& tar -xvf tomcat.tar.gz --strip-components=1 -C $CATALINA_HOME \
	&& rm tomcat.tar.gz*


RUN set -x \
	&& mkdir -p $ALF_HOME \
	&& mkdir -p $DIST \
	&& wget $ALF_DOWNLOAD_URL \
	&& unzip alfresco-community-4.2.f.zip -d /tmp/alfresco \
	&& rm -f alfresco-community-4.2.f.zip

RUN set -x \
	&& mkdir $SOLR \
	&& wget $SOLR_DOWNLOAD_URL \
	&& unzip alfresco-community-solr-4.2.f.zip -d /tmp/solr \
	&& rm alfresco-community-solr-4.2.f.zip

WORKDIR $ALF_HOME

# basic configuration
RUN set -x \
    	&& ln -s /usr/local/tomcat /usr/local/alfresco/tomcat \
        && mkdir -p $CATALINA_HOME/conf/Catalina/localhost \
        && mv $DIST/web-server/shared tomcat/ \
        && mv $DIST/web-server/lib/*.jar tomcat/lib/ \
        && mv $DIST/web-server/webapps/alfresco.war tomcat/webapps/ \
        && mv $DIST/web-server/webapps/share.war tomcat/webapps/ \
        && mv $DIST/bin . \
        && mv $DIST/licenses . \
        && mv $DIST/README.txt . \
        && rm -rf $CATALINA_HOME/webapps/docs \
        && rm -rf $CATALINA_HOME/webapps/examples \
        && mkdir $CATALINA_HOME/shared/lib $ALF_HOME/amps_share \
        && rm -rf $DIST

COPY assets/tomcat/catalina.properties tomcat/conf/catalina.properties
COPY assets/tomcat/setenv.sh tomcat/bin/setenv.sh
COPY assets/tomcat/server.xml tomcat/conf/server.xml
COPY assets/tomcat/context.xml tomcat/conf/context.xml
COPY assets/tomcat/catalina.policy tomcat/conf/catalina.policy
COPY assets/tomcat/tomcat-users.xml tomcat/conf/tomcat-users.xml
COPY assets/alfresco/alfresco-global.properties tomcat/shared/classes/alfresco-global.properties
COPY assets/alfresco/caching-content-store-context.xml tomcat/shared/classes/alfresco/extension/caching-content-store-context.xml

# Solr installation
RUN set -x \
        && mv $SOLR/alf_data . \
        && mkdir alf_data/solr \
        && mv $SOLR/docs alf_data/solr \
        && mv $SOLR/workspace-SpacesStore alf_data/solr \
        && mv $SOLR/archive-SpacesStore alf_data/solr \
        && mv $SOLR/templates alf_data/solr \
        && mv $SOLR/lib alf_data/solr \
        && mv $SOLR/solr.xml alf_data/solr \
        && mv $SOLR/*.war* alf_data/solr \
        && rm -rf $SOLR

COPY assets/solr/solr-tomcat-context.xml tomcat/conf/Catalina/localhost/solr.xml
COPY assets/solr/workspace-solrcore.properties alf_data/solr/workspace-SpacesStore/conf/solrcore.properties
COPY assets/solr/archive-solrcore.properties alf_data/solr/archive-SpacesStore/conf/solrcore.properties

# AMPS installation
COPY assets/amps amps
COPY assets/amps_share amps_share
RUN bash ./bin/apply_amps.sh -force -nobackup

RUN useradd -ms /bin/bash alfresco
RUN set -x && chown -RL alfresco:alfresco $ALF_HOME
USER alfresco

EXPOSE 8080 8009
CMD ["catalina.sh", "run"]