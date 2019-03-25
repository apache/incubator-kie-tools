# kaas-springboot-centos
FROM centos:latest

ENV JAVA_VERSON 1.8.0
ENV SUBMARINE_HOME /home/submarine

LABEL maintainer="Submarine <submarine@kiegroup.com>"

#Set labels used in OpenShift to describe the builder image
LABEL io.openshift.s2i.scripts-url="image:///usr/local/s2i" \
      io.openshift.s2i.destination="/tmp" \
      io.k8s.description="Runtime image for KaaS based on SpringBoot native image" \
      io.k8s.display-name="KaaS based on SpringBoot native image" \
      io.openshift.expose-services="8080:http" \
      io.openshift.tags="builder,runtime,kaas,springboot"

# S2I scripts
COPY s2i/bin/ /usr/local/s2i

RUN yum install -y java-$JAVA_VERSON-openjdk j && \
    yum clean all && \
    chmod 755 /usr/local/s2i/* && \
    adduser -u 1001 -g 0 submarine --home-dir ${SUBMARINE_HOME} && \
    chown -R 1001:0 ${SUBMARINE_HOME} && \
    chmod -R ug+rwX ${SUBMARINE_HOME}

USER 1001

WORKDIR ${SUBMARINE_HOME}

# Set the default port for applications built using this image
EXPOSE 8080

CMD ["/usr/local/s2i/run"]
