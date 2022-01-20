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

COPY payments.admin.web.ch.gov.uk.jar /opt/payments.admin.web.ch.gov.uk/
COPY start-ecs /usr/local/bin/

RUN chmod 555 /usr/local/bin/start-ecs

CMD ["start-ecs"]