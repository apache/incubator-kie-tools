//go:build e2e_tests

/*
 * Copyright 2024 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package e2e_tests

import (
	"fmt"
	"os"
	"path/filepath"
	"testing"

	"github.com/stretchr/testify/require"

	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/command"
)

type GenManifestTestInputCreate struct {
	args   []string
	checks []func(t *testing.T, content string)
}

var tests = []GenManifestTestInputCreate{
	{args: []string{"gen-manifest", "--image", "my_image"},
		checks: []func(t *testing.T, content string){
			func(t *testing.T, content string) {
				require.Contains(t, content, "      image: my_image", "Expected image to be my_image")
			},
		},
	},
	{args: []string{"gen-manifest", "--profile", "gitops"},
		checks: []func(t *testing.T, content string){
			func(t *testing.T, content string) {
				require.Contains(t, content, "    sonataflow.org/profile: gitops", "Expected profile to be gitops")
			},
		},
	},
	{args: []string{"gen-manifest", "--profile", "dev"},
		checks: []func(t *testing.T, content string){
			func(t *testing.T, content string) {
				require.Contains(t, content, "    sonataflow.org/profile: dev", "Expected profile to be dev")
			},
		},
	},
	{args: []string{"gen-manifest", "--profile", "preview"},
		checks: []func(t *testing.T, content string){
			func(t *testing.T, content string) {
				require.Contains(t, content, "    sonataflow.org/profile: preview", "Expected profile to be preview")
			},
		},
	},
	{args: []string{"gen-manifest", "--namespace", "my_namespace", "--skip-namespace"},
		checks: []func(t *testing.T, content string){
			func(t *testing.T, content string) {
				require.NotContains(t, content, "  namespace: my_namespace", "Unexpected namespace: my_namespace")
			},
		},
	},
	{args: []string{"gen-manifest", "--skip-namespace"},
		checks: []func(t *testing.T, content string){
			func(t *testing.T, content string) {
				require.NotContains(t, content, "  namespace: default", "Unexpected namespace: default")
			},
		},
	},
}

func TestGenManifestProjectSuccess(t *testing.T) {
	var test = CfgTestInputCreate{
		input: command.CreateCmdConfig{ProjectName: "new-project"},
	}
	t.Run(fmt.Sprintf("Test gen-manifest success"), func(t *testing.T) {
		defer CleanUpAndChdirTemp(t)

		RunCreateTest(t, test)

		projectName := GetCreateProjectName(t, CfgTestInputCreate{
			input: command.CreateCmdConfig{ProjectName: "new-project"},
		})
		projectDir := filepath.Join(TempTestsPath, projectName)
		err := os.Chdir(projectDir)
		require.NoErrorf(t, err, "Expected nil error, got %v", err)
		WriteMavenConfigFileWithTailDirs(projectDir)

		for _, run := range tests {
			_, err = ExecuteKnWorkflow(run.args...)
			require.NoErrorf(t, err, "Expected nil error, got %v", err)
			manifestDir := getGenManifestDir(projectDir, t)

			yaml := readFileAsString(t, filepath.Join(manifestDir, "01-sonataflow_hello.yaml"))

			for _, check := range run.checks {
				check(t, yaml)
			}
		}
	})
}

func getGenManifestDir(projectDir string, t *testing.T) string {
	manifestDir := filepath.Join(projectDir, "manifests")
	require.DirExistsf(t, manifestDir, "Expected project directory '%s' to be created", manifestDir)

	expectedFiles := []string{"01-sonataflow_hello.yaml"}
	VerifyFilesExist(t, manifestDir, expectedFiles)

	return manifestDir
}

func readFileAsString(t *testing.T, path string) string {
	content, err := os.ReadFile(path)
	if err != nil {
		t.Fatalf("Failed to read file %s", path)
	}
	return string(content)
}
