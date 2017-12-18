FROM debian:8
MAINTAINER Jevgeni Goloborodko <jevgeni@digital-magic.io>

USER root

# Install base packages
RUN apt-get update
RUN apt-get -y upgrade
RUN apt-get --no-install-recommends -y install sysstat netcat net-tools lsof telnet traceroute wget busybox nano less ed vim sudo zip unzip curl locales lynx curl apt-utils syslog-ng ca-certificates apt-transport-https

# Install locales
RUN echo "en_US.UTF-8 UTF-8" >> /etc/locale.gen
RUN locale-gen
ENV LANG en_US.UTF-8
ENV LC_ALL en_US.UTF-8
ENV LANGUAGE en_US.UTF-8

# Install additional stuff
RUN apt-get install -y nginx supervisor


RUN curl -sL https://repos.influxdata.com/influxdb.key | sudo apt-key add -
RUN curl https://www.apache.org/dist/cassandra/KEYS | sudo apt-key add -
RUN echo "deb https://repos.influxdata.com/debian jessie stable" | tee /etc/apt/sources.list.d/influxdb.list
RUN echo "deb http://www.apache.org/dist/cassandra/debian 311x main" | tee -a /etc/apt/sources.list.d/cassandra.sources.list
RUN echo "deb http://http.debian.net/debian jessie-backports main" > /etc/apt/sources.list.d/jessie-backports.list
RUN apt-get update
RUN apt-get install -y -t jessie-backports openjdk-8-jre-headless
RUN apt-get install -y influxdb telegraf cassandra

RUN wget https://dl.influxdata.com/kapacitor/releases/kapacitor_1.4.0_amd64.deb
RUN wget https://dl.influxdata.com/chronograf/releases/chronograf_1.3.10.0_amd64.deb
RUN dpkg -i kapacitor_1.4.0_amd64.deb
RUN dpkg -i chronograf_1.3.10.0_amd64.deb

# Remove default nginx configuration
RUN rm -rf /etc/nginx/sites-enabled/default

COPY assets/telegraf.conf /etc/telegraf/telegraf.conf
COPY assets/system.conf /etc/telegraf/telegraf.d/system.conf
COPY assets/output.conf /etc/telegraf/telegraf.d/output.conf
COPY assets/monitoring-nginx.conf /etc/nginx/conf.d/monitoring.conf

COPY assets/supervisord.conf /etc/supervisord.conf

CMD ["supervisord", "-n", "-c", "/etc/supervisord.conf"]

#EXPOSE 22
#ENTRYPOINT /bin/bash
