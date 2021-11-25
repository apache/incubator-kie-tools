#!/bin/sh
# Configure module
set -e

SCRIPT_DIR=$(dirname $0)
ARTIFACTS_DIR=${SCRIPT_DIR}/artifacts

chmod -R ug+rwX $SCRIPT_DIR
chmod ug+x ${ARTIFACTS_DIR}/opt/jboss/container/openjdk/jdk/*

pushd ${ARTIFACTS_DIR}
cp -pr * /
popd

alternatives --set java /usr/lib/jvm/ibm-semeru-open-11-jdk/bin/java
alternatives --set javac /usr/lib/jvm/ibm-semeru-open-11-jdk/bin/javac

# Update securerandom.source for quicker starts
JAVA_SECURITY_FILE=/usr/lib/jvm/ibm-semeru-open-11-jdk/conf/security/java.security
SECURERANDOM=securerandom.source
if grep -q "^$SECURERANDOM=.*" $JAVA_SECURITY_FILE; then
    sed -i "s|^$SECURERANDOM=.*|$SECURERANDOM=file:/dev/urandom|" $JAVA_SECURITY_FILE
else
    echo $SECURERANDOM=file:/dev/urandom >> $JAVA_SECURITY_FILE
fi
