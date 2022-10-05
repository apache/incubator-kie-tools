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

package docker

import (
	"archive/tar"
	"bufio"
	"bytes"
	"context"
	"crypto/sha1"
	"crypto/sha256"
	"encoding/hex"
	"encoding/json"
	"fmt"
	"io"
	"io/ioutil"
	"math/rand"
	"os"
	"path/filepath"
	"strconv"
	"time"

	"github.com/docker/docker/api/types"
	"github.com/docker/docker/client"
	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/common"
	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/metadata"
	"github.com/moby/buildkit/session"
	"github.com/pkg/errors"
	fsutiltypes "github.com/tonistiigi/fsutil/types"
)

func CreateDockerfile(dockerfileDirPath string, quarkusVersion string) (err error) {
	if _, err = os.Stat(dockerfileDirPath); os.IsNotExist(err) {
		if err = os.Mkdir(dockerfileDirPath, 0700); err != nil {
			fmt.Printf("ERROR: creating dir in temp folder %s\n", dockerfileDirPath)
			return
		}
		fmt.Printf("Created dir on %s \n", dockerfileDirPath)
	}
	// create Dockerfile
	dockerfilePath := filepath.Join(dockerfileDirPath, common.WORKFLOW_DOCKERFILE)
	if _, err = os.Stat(dockerfilePath); err == nil {
		fmt.Printf("✅ %s already exists in %s \n", common.WORKFLOW_DOCKERFILE, dockerfileDirPath)
		return
	}

	file, err := os.Create(dockerfilePath)
	if err != nil {
		fmt.Printf("ERROR: creating Dockerfile in temp folder %s\n", dockerfilePath)
		return
	}
	defer file.Close()

	dockerfile := fmt.Sprintf(`
# true or false
ARG extensions

# TODO: quarkus-version and quarkus-platform-group-id
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

CMD ["./mvnw", "quarkus:dev", "-Dquarkus.http.host=0.0.0.0"]	
`, quarkusVersion)
	_, err = file.WriteString(dockerfile)
	if err != nil {
		fmt.Printf("ERROR: writing in %s\n", common.WORKFLOW_DOCKERFILE)
		return
	}

	fmt.Printf("✅ %s created on %s \n", common.WORKFLOW_DOCKERFILE, file.Name())
	return
}

func GetDockerfileDir(dependenciesVersion metadata.DependenciesVersion) string {
	return filepath.Join(os.TempDir(), fmt.Sprintf("%s-%s-%s-%s",
		common.KN_WORKFLOW_NAME,
		metadata.PluginVersion,
		dependenciesVersion.QuarkusPlatformGroupId,
		dependenciesVersion.QuarkusVersion,
	))
}

func GetDockerfilePath(dependenciesVersion metadata.DependenciesVersion) string {
	return filepath.Join(GetDockerfileDir(dependenciesVersion), common.WORKFLOW_DOCKERFILE)
}

func GetDockerBuildArgs(extensions string, registry string, repository string, name string, tag string) map[string]*string {
	var workflowSwJson string = common.WORKFLOW_SW_JSON
	buildArgs := map[string]*string{
		common.DOCKER_BUILD_ARG_WORKFLOW_FILE:            &workflowSwJson,
		common.DOCKER_BUILD_ARG_CONTAINER_IMAGE_REGISTRY: &registry,
		common.DOCKER_BUILD_ARG_CONTAINER_IMAGE_GROUP:    &repository,
		common.DOCKER_BUILD_ARG_CONTAINER_IMAGE_NAME:     &name,
		common.DOCKER_BUILD_ARG_CONTAINER_IMAGE_TAG:      &tag,
		common.DOCKER_BUILD_ARG_WORKFLOW_NAME:            &name,
	}

	existExtensions := strconv.FormatBool(len(extensions) > 0)
	buildArgs[common.DOCKER_BUILD_ARG_EXTENSIONS] = &existExtensions

	if len(extensions) > 0 {
		buildArgs[common.DOCKER_BUILD_ARG_EXTENSIONS_LIST] = &extensions
	}

	return buildArgs
}

func BuildDockerImage(
	ctx context.Context,
	dependenciesVersion metadata.DependenciesVersion,
	dockerCli client.CommonAPIClient,
	imageBuildOptions types.ImageBuildOptions,
) (err error) {
	// creates a tar with the Dockerfile and the workflow.sw.json
	buf := new(bytes.Buffer)
	tw := tar.NewWriter(buf)
	defer tw.Close()

	dockerfilePath := GetDockerfilePath(dependenciesVersion)
	if _, err = os.Stat(dockerfilePath); err != nil {
		fmt.Printf(" - Couldn't find %s in tmp folder \n", common.WORKFLOW_DOCKERFILE)
		fmt.Printf(" - Creating a new %s \n", common.WORKFLOW_DOCKERFILE)
		if err = CreateDockerfile(GetDockerfileDir(dependenciesVersion), dependenciesVersion.QuarkusVersion); err != nil {
			fmt.Println("ERROR: creating Dockerfile in temp folder")
			return fmt.Errorf("Description: %w", err)
		}
	}

	// adds dockerfile to tar
	err = addFileToTar(tw, dockerfilePath, common.WORKFLOW_DOCKERFILE)
	if err != nil {
		return
	}

	currentPath, err := common.GetCurrentPath()
	if err != nil {
		return
	}

	// adds workflow.sw.json
	err = addFileToTar(tw, filepath.Join(currentPath, common.WORKFLOW_SW_JSON), common.WORKFLOW_SW_JSON)
	if err != nil {
		return
	}
	dockerTar := bytes.NewReader(buf.Bytes())

	// builds using options
	res, err := dockerCli.ImageBuild(ctx, dockerTar, imageBuildOptions)
	if err != nil {
		fmt.Println("ERROR: error building, image options: %s", imageBuildOptions)
		return
	}
	defer res.Body.Close()

	return dockerLog(res.Body)
}

func addFileToTar(tw *tar.Writer, path string, fileName string) (err error) {
	fileReader, err := os.Open(path)
	if err != nil {
		fmt.Printf("ERROR: opening %s\n", path)
		return
	}
	readFile, err := ioutil.ReadAll(fileReader)
	if err != nil {
		fmt.Printf("ERROR: reading %s\n", path)
		return
	}
	tarHeader := &tar.Header{
		Name: fileName,
		Size: int64(len(readFile)),
	}
	err = tw.WriteHeader(tarHeader)
	if err != nil {
		fmt.Printf("ERROR: unable to write tar header %s\n", path)
		return
	}
	_, err = tw.Write(readFile)
	if err != nil {
		fmt.Printf("ERROR: unable to write tar body %s\n", path)
		return
	}
	return
}

// Creates a new session
// A session is a grpc server that enables Docker sdk to have a longer connection with the daemon
func CreateSession(contextDir string, forStream bool) (*session.Session, error) {
	sessionHash := sha256.Sum256([]byte(fmt.Sprintf("%s", contextDir)))
	sharedKey := hex.EncodeToString(sessionHash[:])
	session, err := session.NewSession(context.Background(), filepath.Base(contextDir), sharedKey)
	if err != nil {
		return nil, errors.Wrap(err, "failed to create session")
	}
	return session, nil
}

func ResetUIDAndGID(_ string, s *fsutiltypes.Stat) bool {
	s.Uid = 0
	s.Gid = 0
	return true
}

type ErrorDetail struct {
	Message string `json:"message"`
}

type ErrorLine struct {
	Error       string      `json:"error"`
	ErrorDetail ErrorDetail `json:"errorDetail"`
}

func dockerLog(rd io.Reader) error {
	var lastLine string

	scanner := bufio.NewScanner(rd)
	for scanner.Scan() {
		lastLine = scanner.Text()
		fmt.Println(scanner.Text())
	}

	errLine := &ErrorLine{}
	json.Unmarshal([]byte(lastLine), errLine)
	if errLine.Error != "" {
		return errors.New(errLine.Error)
	}

	if err := scanner.Err(); err != nil {
		return err
	}

	return nil
}

func RandString() string {
	var src = rand.NewSource(time.Now().UnixNano())
	string := strconv.FormatInt(src.Int63(), 10)
	h := sha1.New()
	h.Write([]byte(string))
	hash := hex.EncodeToString(h.Sum(nil))
	return hash[:6]
}
