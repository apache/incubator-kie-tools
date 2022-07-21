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
	FROM quay.io/lmotta/kn-workflow:0.0.1 as builder

	WORKDIR /tmp/kn-plugin-workflow/
	ARG WORKFLOW_FILE
	COPY ${WORKFLOW_FILE} ./kogito-workflow/src/main/resources/
	# copy application.properties if exists
	# get project name
	# quarkus version
	# kogito version
	# extension
	# write command in args, and put in run. ./mvnw quarkus:add-extension ...
	
	RUN cd kogito-workflow && ./mvnw package -Dnative -Dquarkus.kubernetes.deployment-target=knative
	
	# docker build -f Dockerfile.workflow --target=kubernetes --output type=local,dest=kubernetes .
	FROM scratch as kubernetes
	COPY --from=builder /tmp/kn-plugin-workflow/kogito-workflow/target/kubernetes .
	
	FROM quay.io/quarkus/quarkus-micro-image:1.0 as runner
	COPY --from=builder /tmp/kn-plugin-workflow/kogito-workflow/target/*-runner /tmp/runner
	EXPOSE 8080
	CMD ["/tmp/runner", "-Dquarkus.http.host=0.0.0.0"]
	
	# change to minimal image
	# docker build -f Dockerfile.workflow --target=dev -t lmotta/abc:dev .
	# docker container run -it --mount type=bind,source="$(pwd)",target=/tmp/kn-plugin-workflow/kogito-workflow/src/main/resources lmotta/abc:dev
	FROM openjdk:11 as dev
	
	WORKDIR /tmp/kn-plugin-workflow/
	COPY --from=builder /root/.m2/ /root/.m2/
	COPY --from=builder /tmp/kn-plugin-workflow/ .
	
	EXPOSE 8080
	# VOLUME
	# EXPOSE 	
	# make a volume between current folder and resources folder
	WORKDIR /tmp/kn-plugin-workflow/kogito-workflow
	CMD ["./mvnw", "quarkus:dev"]
	
`)

	if err != nil {
		return fmt.Errorf("error creating Dockerfile.workflow: %w", err)
	}

	fmt.Printf("Dockerfile.workflow created on %s \n", dockerfilePath)
	return
}
