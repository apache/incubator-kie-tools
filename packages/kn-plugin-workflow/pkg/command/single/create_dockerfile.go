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
	FROM quay.io/lmotta/kn-plugin-workflow:0.0.1

	WORKDIR /tmp/kn-workflow/

	ARG WORKFLOW_FILE

	COPY ${WORKFLOW_FILE} ./serverless-workflow/src/main/resources/

	RUN cd serverless-workflow && ./mvnw package -Dquarkus.kubernetes.deployment-target=knative

	EXPOSE 8080

	ENTRYPOINT [\"/tmp/kn-workflow/serverless-workflow/target/serverless-workflow-0.0.0-runner\"]`)

	if err != nil {
		return fmt.Errorf("error creating Dockerfile.workflow: %w", err)
	}

	fmt.Printf("Dockerfile.workflow created on %s \n", dockerfilePath)
	return
}
