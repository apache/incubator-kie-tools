//go:build integration_buildah

/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package builder

import (
	"bytes"
	"fmt"
	"io"
	"log"
	"os"
	"os/exec"
	"testing"
	"time"

	"github.com/sirupsen/logrus"
	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/suite"
)

func TestBuildahBashTestSuite(t *testing.T) {
	suite.Run(t, new(BuildahTestSuite))
}

func (suite *BuildahTestSuite) TestBuildahFromBash() {
	logrus.Info("TestBuildahFromBash")
	registry, err, repos, size := CheckInitialStatePodmanRegistry(suite)

	imageName := "localhost:5000/kiegroup/buildah-bash:latest"
	printSeparateStreamAtEndOfTest := false
	currentDir, _ := currentDir()

	cmd := exec.Command("/bin/sh", currentDir+"/../examples/buildah_build.sh")
	var stdoutBuffer, stderrBuffer bytes.Buffer
	cmd.Stdout = io.MultiWriter(os.Stdout, &stdoutBuffer)
	cmd.Stderr = io.MultiWriter(os.Stderr, &stderrBuffer)

	start := time.Now()
	err = cmd.Run()
	if err != nil {
		log.Fatalf("cmd.Run() failed with %s\n", err)
	}
	timeElapsed := time.Since(start)
	logrus.Infof("The Buildah build took %s", timeElapsed)

	if printSeparateStreamAtEndOfTest {
		outStr, errStr := string(stdoutBuffer.Bytes()), string(stderrBuffer.Bytes())
		fmt.Printf("\nStandard Output:\n%s\n Standard Error:\n%s\n", outStr, errStr)
	}

	reposSize := CheckImageOnPodmanRegistry(suite, imageName, repos, registry)
	assert.True(suite.T(), reposSize == size+1)
}
