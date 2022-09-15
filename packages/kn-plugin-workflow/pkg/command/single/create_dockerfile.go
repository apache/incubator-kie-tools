/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package single

import (
	"fmt"
	"os"
)

func GenerateDockerfile(dockerfilePath string) (err error) {
	file, err := os.Create(dockerfilePath)
	if err != nil {
		return fmt.Errorf("error creating Dockerfile: %w", err)
	}

	defer file.Close()
	_, err = file.WriteString(`
# ===============================================================
# BUILDER
# ===============================================================

# docker build -f Dockerfile.workflow --target=builder \
# --build-arg workflow_file=workflow.sw.json \
# --build-arg extensions=quarkus-jsonp,quarkus-smallrye-openapi \
# --build-arg workflow_name=my-project \
# --build-arg container_registry=quay.io \
# --build-arg container_group=lmotta \
# --build-arg container_name=test \
# --build-arg container_tag=0.0.1 \
# .

# tag will change dynamically, each quarkus version will have a tag.
FROM quay.io/lmotta/kn-workflow:2.10.0.Final as builder

WORKDIR /tmp/kn-plugin-workflow

# ARG extensions
# RUN if [[ -z "$extensions" ]]; \
#	then echo "WITHOUT ADDITIONAL EXTENSIONS"; \
#	else ./mvnw quarkus:add-extension -Dextensions=${extensions}; \
#	fi

# copy application.properties if exists
ARG workflow_file
COPY ${workflow_file} application.propertie[s] ./src/main/resources/

# image name
ARG workflow_name
ARG container_registry
ARG container_group
ARG container_name
ARG container_tag
RUN ./mvnw package \
	-Dquarkus.kubernetes.deployment-target=knative \
	-Dquarkus.knative.name=${workflow_name} \
	-Dquarkus.container-image.registry=${container_registry} \
	-Dquarkus.container-image.group=${container_group} \
	-Dquarkus.container-image.name=${container_name} \
	-Dquarkus.container-image.tag=${container_tag}

# ===============================================================
# KUBERNETES
# ===============================================================

# DOCKER_BUILDKIT=1 docker build -f Dockerfile.workflow --target=kubernetes \
# --build-arg workflow_file=workflow.sw.json \
# --build-arg extensions=quarkus-jsonp,quarkus-smallrye-openapi \
# --build-arg workflow_name=my-project \
# --build-arg container_registry=quay.io \
# --build-arg container_group=lmotta \
# --build-arg container_name=test \
# --build-arg container_tag=0.0.1 \
# --output type=local,dest=kubernetes .
FROM scratch as kubernetes
COPY --from=builder /tmp/kn-plugin-workflow/target/kubernetes .

# ===============================================================
# RUNNER
# ===============================================================

# docker build -f Dockerfile.workflow --target=runner \
# --build-arg workflow_file=workflow.sw.json \
# --build-arg extensions=quarkus-jsonp,quarkus-smallrye-openapi \
# --build-arg workflow_name=my-project \
# --build-arg container_registry=quay.io \
# --build-arg container_group=lmotta \
# --build-arg container_name=test \
# --build-arg container_tag=0.0.1 \
# -t quay.io/lmotta/runner .

# TODO: change to minimal image
FROM openjdk:11 as runner

COPY --from=builder /tmp/kn-plugin-workflow/target/quarkus-app/lib/ /runner/lib/
COPY --from=builder /tmp/kn-plugin-workflow/target/quarkus-app/*.jar /runner/
COPY --from=builder /tmp/kn-plugin-workflow/target/quarkus-app/app/ /runner/app/
COPY --from=builder /tmp/kn-plugin-workflow/target/quarkus-app/quarkus/ /runner/quarkus/
EXPOSE 8080

CMD ["java", "-jar", "/deployments/quarkus-run.jar", "-Dquarkus.http.host=0.0.0.0"]

# ===============================================================
# DEV
# ===============================================================

# docker build -f Dockerfile.workflow --target=dev \
# --build-arg workflow_file=workflow.sw.json \
# --build-arg extensions=quarkus-jsonp,quarkus-smallrye-openapi \
# --build-arg workflow_name=my-project \
# --build-arg container_registry=quay.io \
# --build-arg container_group=lmotta \
# --build-arg container_name=test \
# --build-arg container_tag=0.0.1 \
# -t quay.io/lmotta/dev .

# docker container run -it \
# --mount type=bind,source="$(pwd)",target=/tmp/kn-plugin-workflow/src/main/resources \
# -p 8080:8080 quay.io/lmotta/dev

# TODO: change to minimal image
FROM openjdk:11 as dev

COPY --from=builder /root/.m2/ /root/.m2/
COPY --from=builder /tmp/ /tmp/

WORKDIR /tmp/kn-plugin-workflow/
	
EXPOSE 8080

CMD ["./mvnw", "quarkus:dev"]
	
`)

	if err != nil {
		return fmt.Errorf("error creating Dockerfile.workflow: %w", err)
	}

	fmt.Printf("Dockerfile.workflow created on %s \n", dockerfilePath)
	return
}
