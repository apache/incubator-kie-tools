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
	"archive/tar"
	"bufio"
	"bytes"
	"context"
	"crypto/sha256"
	"encoding/hex"
	"encoding/json"
	"fmt"
	"io"
	"os"
	"path/filepath"
	"strconv"

	"github.com/docker/docker/api/types"
	"github.com/docker/docker/client"
	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/common"
	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/metadata"
	"github.com/moby/buildkit/session"
	"github.com/pkg/errors"
	fsutiltypes "github.com/tonistiigi/fsutil/types"
)

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

	// adds dockerfile to tar
	err = addFileToTar(tw, GetDockerfilePath(dependenciesVersion), common.WORKFLOW_DOCKERFILE)
	if err != nil {
		return
	}
	currentPath, err := os.Getwd()
	if err != nil {
		fmt.Println("ERROR: error getting current path")
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
