# Use OpenJDK 21 as base image
FROM openjdk:21-jdk-slim

# Install necessary utilities
RUN apt-get update && apt-get install -y curl tar

# Download and install Tomcat
RUN curl -fsSL https://archive.apache.org/dist/tomcat/tomcat-9/v9.0.78/bin/apache-tomcat-9.0.78.tar.gz -o /tmp/tomcat.tar.gz
RUN mkdir /opt/tomcat 
RUN tar -xvzf /tmp/tomcat.tar.gz -C /opt/tomcat --strip-components=1 
RUN rm /tmp/tomcat.tar.gz
RUN mkdir -p /opt/jboss/wildfly/logs

ENV TZ="Asia/Kolkata"


# RUN mkdir /opt/tomcat/webappsBKUP

RUN rm -rf /opt/tomcat/webapps/*


RUN rm -rf /opt/tomcat/conf/server.xml
RUN rm -rf /opt/tomcat/conf/logback.xml

COPY logback.xml /opt/tomcat/conf/logback.xml

COPY server.xml /opt/tomcat/conf/server.xml

COPY ./target/ptsl-generichttpapiweb-1.0.0.war /opt/tomcat/webapps/genericapi.war


COPY ../properties/common/log4j2-common.xml /log4j2-common.xml
COPY ../properties/common/custom_hosts_production.properties /custom_hosts_production.properties
COPY ../properties/common/smpp-interface.properties /smpp-interface.properties
COPY ../properties/common/encrypt.properties /encrypt.properties
COPY ../properties/common/dnd.properties /dnd.properties
COPY ../properties/common/dlttemplateconfig.properties /dlttemplateconfig.properties
COPY ../properties/common/common.properties /common.properties
COPY ../properties/common/callback-params.properties /callback-params.properties
COPY ../properties/common/payload-params.properties /payload-params.properties
COPY ../properties/common/midconfig.properties /midconfig.properties
COPY ../properties/common/messageremove.properties /messageremove.properties
COPY ../properties/common/kafka-custom.properties /kafka-custom.properties
COPY ../properties/common/jndi.properties /jndi.properties
COPY ../properties/common/internal-ui.properties /internal-ui.properties
COPY ../properties/common/inmem-refresh-interval.properties /inmem-refresh-interval.properties
COPY ../properties/common/http-connector.properties /http-connector.properties
COPY ../properties/common/global.properties /global.properties
COPY ../properties/common/genericapi.properties /genericapi.properties
COPY ../properties/common/exception-case.properties /exception-case.properties
COPY ../properties/common/wallet-history.properties /wallet-history.properties
COPY ../properties/common/wallet-balance-reminder.properties /wallet-balance-reminder.properties
COPY ../properties/common/url_hitter.properties /url_hitter.properties
COPY ../properties/common/timezone.properties /timezone.properties
COPY ../properties/common/timer-procesor-interval.properties /timer-procesor-interval.properties
COPY ../properties/common/sms_request.xsd /sms_request.xsd
COPY ../properties/common/shortcode-provider.properties /shortcode-provider.properties
COPY ../properties/common/replace-iso.properties /replace-iso.properties
COPY ../properties/common/r3c_addinfo.properties /r3c_addinfo.properties
COPY ../properties/common/prometheus.control.properties /prometheus.control.properties
COPY ../properties/common/mcc-mnc-list.json /mcc-mnc-list.json


COPY ../properties/profile/digitalocean2/kafka-producer.properties_do2 /kafka-producer.properties_do2
COPY ../properties/profile/digitalocean2/kafka-consumer.properties_do2 /kafka-consumer.properties_do2
COPY ../properties/profile/digitalocean2/common-db.properties_do2 /common-db.properties_do2
COPY ../properties/profile/digitalocean2/elasticsearch.properties_do2 /elasticsearch.properties_do2


#VOLUME ["/logs"]
#
## Copy the log rotation script into the container
#COPY rotate_logs.sh /opt/tomcat/rotate_logs.sh
#
## Make the script executable
#RUN chmod +x /opt/tomcat/rotate_logs.sh
#
## Install cron
#RUN apt-get update && apt-get install -y cron
#
## Add the cron job to the crontab
#RUN echo "0 * * * * /opt/tomcat/rotate_logs.sh" | crontab -


WORKDIR /opt/tomcat
# Set the entrypoint to catalina.sh and pass the logback configuration
ENTRYPOINT ["bin/catalina.sh"]

# Set the default command to run with the logback configuration
CMD ["run", "-Dlogback.configurationFile=/opt/tomcat/conf/logback.xml"]

