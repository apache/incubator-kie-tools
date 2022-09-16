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

	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/metadata"
)

func GenerateDockerfile(dockerfilePath string) (err error) {
	file, err := os.Create(dockerfilePath)
	if err != nil {
		return fmt.Errorf("error creating Dockerfile: %w", err)
	}

	defer file.Close()

	dockerfile := fmt.Sprintf(`
# true or false
ARG extensions

FROM quay.io/lmotta/kn-workflow:%s as base
WORKDIR /tmp/kn-plugin-workflow

# add additional extensions
FROM base as true-extensions
ARG extensions_list
RUN ./mvnw quarkus:add-extension -Dextensions=${extensions_list}

FROM base as false-extensions
RUN echo "WITHOUT ADDITIONAL EXTENSIONS"

FROM ${extensions}-extensions as builder
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

FROM scratch as output-files
COPY --from=builder /tmp/kn-plugin-workflow/target/kubernetes .

# TODO: change to minimal image
FROM openjdk:11 as runner

COPY --from=builder /tmp/kn-plugin-workflow/target/quarkus-app/lib/ /runner/lib/
COPY --from=builder /tmp/kn-plugin-workflow/target/quarkus-app/*.jar /runner/
COPY --from=builder /tmp/kn-plugin-workflow/target/quarkus-app/app/ /runner/app/
COPY --from=builder /tmp/kn-plugin-workflow/target/quarkus-app/quarkus/ /runner/quarkus/
EXPOSE 8080

CMD ["java", "-jar", "/deployments/quarkus-run.jar", "-Dquarkus.http.host=0.0.0.0"]

# TODO: change to minimal image
FROM openjdk:11 as dev

COPY --from=builder /root/.m2/ /root/.m2/
COPY --from=builder /tmp/ /tmp/

WORKDIR /tmp/kn-plugin-workflow/
	
EXPOSE 8080

CMD ["./mvnw", "quarkus:dev"]	
`, metadata.QuarkusVersion)
	_, err = file.WriteString(dockerfile)

	if err != nil {
		return fmt.Errorf("error creating Dockerfile.workflow: %w", err)
	}

	fmt.Printf("Dockerfile.workflow created on %s \n", dockerfilePath)
	return
}
