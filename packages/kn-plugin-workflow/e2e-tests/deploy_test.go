//go:build e2e_tests

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package e2e_tests

import (
	"fmt"
	"io"
	"os"
	"path/filepath"
	"testing"

	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/command"
	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/common"
	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/metadata"
	"github.com/stretchr/testify/require"
	
	"gopkg.in/yaml.v2"
)

type cfgTestInputDeploy struct {
	input command.DeployUndeployCmdConfig
}

var cfgTestInputDeploy_Success = []cfgTestInputDeploy{
	{input: command.DeployUndeployCmdConfig{}},
	{input: command.DeployUndeployCmdConfig{Image: metadata.DevModeImage}},
}

type Config struct {
	Kind string `yaml:"kind"`
	Spec struct {
		PodTemplate struct {
			Container struct {
				Image string `yaml:"image"`
			} `yaml:"container"`
		} `yaml:"podTemplate"`
	} `yaml:"spec"`
}

func transformDeployCmdCfgToArgs(cfg command.DeployUndeployCmdConfig) []string {
	args := []string{"deploy"}
	return args
}

func TestDeployProjectSuccess(t *testing.T) {
	dir, err := os.Getwd()
	require.NoError(t, err)

	var originalCheckCrds = command.CheckCRDs
	defer func() { command.CheckCRDs = originalCheckCrds }()

	command.CheckCRDs = func(crds []string, typeName string) error {
		return nil
	}

	defer os.Chdir(dir)
	for testIndex := range cfgTestInputDeploy_Success {
		var executeApplyOriginal = common.ExecuteApply
		defer func() { common.ExecuteApply = executeApplyOriginal }()

		common.ExecuteApply = func(path, namespace string) error {
			if cfgTestInputDeploy_Success[testIndex].input.Image != "" {
				file, err := os.Open(path)
				if err != nil {
					return fmt.Errorf("❌ ERROR: Failed to open file: %v", err)
				}
				defer file.Close()
				data, err := io.ReadAll(file)
				if err != nil {
					t.Fatalf("❌ ERROR: Failed to read file: %v", err)
				}

				var cfg Config
				err = yaml.Unmarshal(data, &cfg)
				if err != nil {
					t.Fatalf("❌ ERROR: Failed to unmarshal file: %v", err)
				}

				if cfg.Kind != "SonataFlow" {
					return nil
				}

				if cfg.Spec.PodTemplate.Container.Image != metadata.DevModeImage {
					t.Fatalf("❌ ERROR: Expected image %s, got %s", metadata.DevModeImage, cfg.Spec.PodTemplate.Container.Image)
				}
			}
			return nil
		}

		t.Run(fmt.Sprintf("Test deploy project success index: %d", testIndex), func(t *testing.T) {
			RunCreateTest(t, CfgTestInputCreate_Success[testIndex])
			projectName := GetCreateProjectName(t, CfgTestInputCreate_Success[0])
			projectDir := filepath.Join(TempTestsPath, projectName)
			defer os.RemoveAll(projectDir)

			err = os.Chdir(projectDir)
			require.NoErrorf(t, err, "Expected nil error, got %v", err)

			cmd := command.NewDeployCommand()
			err = cmd.Execute()
			require.NoError(t, err)
		})
	}
}
