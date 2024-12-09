#!/bin/sh

PROFILE="full"

echo "This script requires this example's app to have been compiled with 'mvn clean package -Pcontainer'"
echo "==="

PROJECT_VERSION=$(cd ../ && mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
KOGITO_MANAGEMENT_CONSOLE_IMAGE=$(cd ../ && mvn help:evaluate -Dexpression=kogito.management-console.image -q -DforceStdout)

if [ -n "$1" ]; then
        if [ "$1" = "full" ] || [ "$1" = "infra" ] || [ "$1" = "example-only" ];
  then
    PROFILE="$1"
    echo "Using profile '${PROFILE}'..."
  else
    echo "Unknown docker profile '$1'. The supported profiles are:"
    echo "* 'infra': Use this profile to start only the minimum infrastructure to run the example (postgresql, data-index & jobs-service)."
    echo "* 'example-only': Use this profile to start the example infrastructure and the kogito-example service. Requires the example to have been compiled using the 'container' profile (-Pcontainer)"
    echo "* 'full' (default): Starts full example setup, including infrastructure (database, data-index & jobs-service), the kogito-example-service container, Management Console, and Keycloak. Requires the example to have been compiled using the 'container' profile (-Pcontainer)"
    exit 1;
  fi
fi

echo "PROJECT_VERSION=${PROJECT_VERSION}" > ".env"
echo "KOGITO_MANAGEMENT_CONSOLE_IMAGE=${KOGITO_MANAGEMENT_CONSOLE_IMAGE}" >> ".env"
echo "COMPOSE_PROFILES='${PROFILE}'" >> ".env"

if [ "$(uname)" = "Darwin" ]; then
   echo "DOCKER_GATEWAY_HOST=kubernetes.docker.internal" >> ".env"
elif [ "$(expr substr $(uname -s) 1 5)" = "Linux" ]; then
   echo "DOCKER_GATEWAY_HOST=172.17.0.1" >> ".env";
else
   echo "Unknown uname: $(uname)"
   exit 1
fi

if [ ! -d "./svg" ]
then
    echo "SVG folder does not exist. Have you compiled the project? mvn clean install -DskipTests"
    exit 1
fi

docker compose up