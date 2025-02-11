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

package cmd

import (
	"os"
	"os/exec"
	"path/filepath"
	"runtime"
	"testing"

	"github.com/stretchr/testify/assert"
)

func getBinPath() string {
	// Detect OS and architecture for the binary
	binName := "../dist/image-env-to-json-" + runtime.GOOS + "-" + runtime.GOARCH
	if runtime.GOOS == "windows" {
		binName += ".exe" // Add .exe for Windows
	}
	return filepath.Join("../dist", binName)
}

func TestCliBinaryWithJsonWithoutRef(t *testing.T) {
	tempDir := t.TempDir()
	binPath := getBinPath()
	// Run the CLI binary with arguments
	runCmd := exec.Command(binPath, "--help")
	output, err := runCmd.CombinedOutput()
	assert.NoError(t, err, "CLI command failed")
	assert.Contains(t, string(output), "Usage:", "Help text not found in output")

	// Run the CLI with a JSON Schema file
	jsonSchemaPath := "./testdata/schemaWithoutRef.json"
	envJsonPath := filepath.Join(tempDir, "env.json")

	runCmd = exec.Command(binPath, "--directory", tempDir, "--json-schema", jsonSchemaPath)
	runCmd.Env = append(os.Environ(), "MY_ENV=value1", "MY_ENV2=value2")

	_, err = runCmd.CombinedOutput()
	assert.NoError(t, err, "CLI command failed")
	assert.FileExists(t, envJsonPath, "env.json should be created")

	// Validate `env.json` content (Optional)
	content, err := os.ReadFile(envJsonPath)
	assert.NoError(t, err)
	assert.Contains(t, string(content), `"MY_ENV": "value1"`)
	assert.Contains(t, string(content), `"MY_ENV2": "value2"`)
}

func TestCliBinaryWithJsonWithRef(t *testing.T) {
	tempDir := t.TempDir()
	binPath := getBinPath()
	// Run the CLI binary with arguments
	runCmd := exec.Command(binPath, "--help")
	output, err := runCmd.CombinedOutput()
	assert.NoError(t, err, "CLI command failed")
	assert.Contains(t, string(output), "Usage:", "Help text not found in output")

	// Run the CLI with a JSON Schema file
	jsonSchemaPath := "./testdata/schemaWithRef.json"
	envJsonPath := filepath.Join(tempDir, "env.json")

	runCmd = exec.Command(binPath, "--directory", tempDir, "--json-schema", jsonSchemaPath)
	runCmd.Env = append(os.Environ(), "MY_ENV=value1", "MY_ENV2=value2")

	_, err = runCmd.CombinedOutput()
	assert.NoError(t, err, "CLI command failed")
	assert.FileExists(t, envJsonPath, "env.json should be created")

	// Validate `env.json` content (Optional)
	content, err := os.ReadFile(envJsonPath)
	assert.NoError(t, err)
	assert.Contains(t, string(content), `"MY_ENV": "value1"`)
	assert.Contains(t, string(content), `"MY_ENV2": "value2"`)
}
