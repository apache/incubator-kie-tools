# kaas-quarkus-centos-s2i
FROM centos:latest

LABEL maintainer="Submarine <submarine@kiegroup.com>"

ENV MAVEN_VERSION 3.6.0
ENV JAVA_VERSON 1.8.0
ENV SUBMARINE_HOME /home/submarine

LABEL io.openshift.s2i.scripts-url="image:///usr/local/s2i" \
      io.openshift.s2i.destination="/tmp" \
      io.k8s.description="Platform for building KaaS based on SpringBoot" \
      io.k8s.display-name="KaaS based on SpringBoot" \
      io.openshift.expose-services="8080:http" \
      io.openshift.tags="builder,kaas,springboot"

# install s2i files
COPY ./s2i/bin/ /usr/local/s2i

# install required packages
RUN yum install -y --setopt=skip_missing_names_on_install=False \
    tar curl java-$JAVA_VERSON-openjdk-devel && \
    curl -fsSL https://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz | tar xzf - -C /usr/share \
    && mv /usr/share/apache-maven-$MAVEN_VERSION /usr/share/maven && \
    adduser -u 1001 -g 0 submarine --home-dir ${SUBMARINE_HOME} && \
    chown -R 1001:0 ${SUBMARINE_HOME} && \
    chmod -R ug+rwX ${SUBMARINE_HOME} && \
    chmod 755 /usr/local/s2i/*

USER 1001

WORKDIR ${SUBMARINE_HOME}

COPY ./maven/ ${SUBMARINE_HOME}

# Set the default CMD for the image
CMD ["/usr/local/s2i/usage"]
