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
	"encoding/json"
	"os"
	"path/filepath"
	"testing"

	"github.com/stretchr/testify/assert"
)

func TestGenerateEnvJsonWithInvalidJsonSchema(t *testing.T) {
	tempDir := t.TempDir()
	// Define an invalid JSON Schema
	envJsonSchema := ""

	// Create a temporary JSON schema file
	envSchemaPath := filepath.Join(tempDir, "EnvSchema.json")
	err := os.WriteFile(envSchemaPath, []byte(envJsonSchema), 0644)
	assert.NoError(t, err)

	// Define the expected output file path
	envJsonPath := filepath.Join(tempDir, "env.json")

	// Invoke the generateEnvJson
	err = generateEnvJson(tempDir, envSchemaPath)
	assert.Error(t, err)

	// `env.json` mustn't exist
	_, err = os.Stat(envJsonPath)
	assert.Error(t, err, "env.json file should exist")
}

func TestGenerateEnvJsonWithValidJsonSchema(t *testing.T) {
	tempDir := t.TempDir()
	// Define a sample JSON Schema
	envJsonSchema := `{
		"$id": "EnvJson",
		"$schema": "http://json-schema.org/draft-07/schema#",
		"definitions": {
			"EnvJson": {
				"type": "object",
				"properties": {
					"MY_ENV": { "type": "string" },
					"MY_ENV2": { "type": "string" }
				},
				"required": ["MY_ENV", "MY_ENV2"]
			}
		}
	}`

	// Create a temporary JSON schema file
	envJsonSchemaPath := filepath.Join(tempDir, "EnvSchema.json")
	err := os.WriteFile(envJsonSchemaPath, []byte(envJsonSchema), 0644)
	assert.NoError(t, err)

	// Set environment variables
	os.Setenv("MY_ENV", `"value1"`)
	os.Setenv("MY_ENV2", `"value2"`)
	defer os.Unsetenv("MY_ENV")
	defer os.Unsetenv("MY_ENV2")

	// Define the expected output file path
	envJsonPath := filepath.Join(tempDir, "env.json")

	// Invoke the generateEnvJson
	err = generateEnvJson(tempDir, envJsonSchemaPath)
	assert.NoError(t, err)

	// Verify that the env.json file was created
	_, err = os.Stat(envJsonPath)
	assert.NoError(t, err, "env.json file should exist")

	// Read and validate the contents of env.json
	data, err := os.ReadFile(envJsonPath)
	assert.NoError(t, err)

	var envJson map[string]any
	err = json.Unmarshal(data, &envJson)
	assert.NoError(t, err)

	// Check that the JSON contains the expected values
	assert.Equal(t, "value1", envJson["MY_ENV"])
	assert.Equal(t, "value2", envJson["MY_ENV2"])
}
