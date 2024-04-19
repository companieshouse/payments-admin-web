FROM centos:7

RUN yum update -y && \
  yum install -y \
  epel-release-7 \
  zip \
  unzip \
  java-1.8.0-openjdk \
  maven \
  make && \
  yum clean all

COPY payments-admin-web.jar /opt/payments-admin-web/

RUN chmod 555 /usr/local/bin/docker_start.sh

CMD ["./docker_start.sh"]