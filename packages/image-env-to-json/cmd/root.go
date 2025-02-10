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
	"fmt"
	"os"

	"github.com/santhosh-tekuri/jsonschema/v6"
	"github.com/spf13/cobra"
)

type CmdConfig struct {
	Version string
}

const (
	EnvJsonFile = "env.json"
)

func RootCmd(cfg CmdConfig) *cobra.Command {
	var (
		envJsonDirectory string
		jsonSchemaPath   string
	)

	var cmd = &cobra.Command{
		Use:   "image-env-to-json",
		Short: "A CLI tool to create a 'env.json' file from a JSON Schema.",
		Long: `
	This tool uses the "json-schema" JSON Schema file and creates a 'env.json' environment file in the specified "directory". Paths can be relative or absolute.
			`,
		Example: `
	# Run the 
	image-env-to-json --directory /path/to/env.json --json-schema /path/to/jsonSchema.json
		 `,
		Run: func(cmd *cobra.Command, args []string) {
			if err := generateEnvJson(envJsonDirectory, jsonSchemaPath); err != nil {
				fmt.Printf("[image-env-to-json] Error: '%v'.", err)
				os.Exit(1)
			}
		},
	}

	cmd.Version = cfg.Version
	cmd.SetVersionTemplate(`{{printf "%s\n" .Version}}`)

	cmd.PersistentFlags().StringVarP(&envJsonDirectory, "directory", "d", "", "directory to create or update an existing env.json file.")
	cmd.PersistentFlags().StringVar(&jsonSchemaPath, "json-schema", "", "JSON Schema file path to validate.")
	cmd.MarkFlagRequired("directory")
	cmd.MarkFlagRequired("json-schema")

	return cmd
}

func generateEnvJson(envJsonDirectory string, jsonSchemaPath string) error {
	// Check if directory exists
	if _, err := os.Stat(envJsonDirectory); err != nil {
		if os.IsNotExist(err) {
			fmt.Printf("[image-env-to-json] Directory '%s' does not exist. Please provide an existing directory.\n", envJsonDirectory)
			return err
		} else {
			fmt.Printf("[image-env-to-json] Error while reading the '%s'.\n", envJsonDirectory)
			return err
		}
	}

	// Open the JSON Schema file in the `jsonSchemaPath`
	fmt.Printf("[image-env-to-json] Reading JSON Schema from '%s'...\n", jsonSchemaPath)
	jsonSchemaFile, err := os.Open(jsonSchemaPath)
	if err != nil {
		fmt.Printf("[image-env-to-json] Error reading file '%s'.\n", jsonSchemaPath)
		return err
	}

	// Unmarshal the `jsonSchemaFile`
	jsonSchema, err := jsonschema.UnmarshalJSON(jsonSchemaFile)
	if err != nil {
		fmt.Printf("[image-env-to-json] Failed to unmarshal JSON Schema file: %s.\n", jsonSchemaPath)
		return err
	}

	// Walk though the `jsonSchema` properties, saving the environment variable names
	id := fmt.Sprintf("%v", jsonSchema.(map[string]any)["$id"])
	properties, _ := jsonSchema.(map[string]any)["definitions"].(map[string]any)[id].(map[string]any)["properties"].(map[string]any)
	envVarNames := make([]string, 0, len(properties))
	for key := range properties {
		envVarNames = append(envVarNames, key)
	}
	fmt.Printf("[image-env-to-json] Extracted environment variables: %s\n", envVarNames)

	// Checks if `env.json` exists, or create a new empty one (`{}`)
	var envJsonPath = fmt.Sprintf("%s/%s", envJsonDirectory, EnvJsonFile)
	if _, err := os.Stat(envJsonPath); err != nil {
		if os.IsNotExist(err) {
			// Create emtpy `env.json` file
			err := os.WriteFile(envJsonPath, []byte("{}"), 0644)
			if err != nil {
				fmt.Printf("[image-env-to-json] Error writing to file.\n")
				return err
			}
		} else {
			fmt.Printf("[image-env-to-json] Error while reading the '%s' file.\n", envJsonPath)
			return err
		}
	}

	// Load the `env.json` file
	fmt.Printf("[image-env-to-json] Looking for environment variables: %v...\n", envVarNames)
	envData, err := os.ReadFile(envJsonPath)
	if err != nil {
		fmt.Printf("[image-env-to-json] Error reading %s\n", EnvJsonFile)
		return err
	}

	// Unmarshal the `env.json` file
	var envJson map[string]any
	if err := json.Unmarshal(envData, &envJson); err != nil {
		fmt.Printf("[image-env-to-json] Error parsing %s\n", EnvJsonFile)
		return err
	}

	// Update the `env.json` file with the environment variables
	isUpdated := false
	for _, name := range envVarNames {
		envVarStringValue, exists := os.LookupEnv(name)
		if !exists {
			fmt.Printf("[image-env-to-json] '%s' is not a valid environment variable.\n", name)
		}

		var jsonValue any
		if err := json.Unmarshal([]byte(envVarStringValue), &jsonValue); err != nil {
			fmt.Printf("[image-env-to-json] '%s' is not a valid JSON. Using '%s' as string.\n", name, name)
			isUpdated = true
			envJson[name] = envVarStringValue
		} else {
			fmt.Printf("[image-env-to-json] '%s' is a valid JSON. Using parsed JSON from '%s'.\n", name, name)
			envJson[name] = jsonValue
			isUpdated = true
		}
	}

	// Marshal the `env.json` file
	updatedData, err := json.MarshalIndent(envJson, "", "  ")
	if err != nil {
		fmt.Printf("[image-env-to-json] Error encoding %s\n", EnvJsonFile)
		return err
	}

	// Save the `env.json` file
	if err := os.WriteFile(envJsonPath, updatedData, 0644); err != nil {
		fmt.Printf("[image-env-to-json] Error writing %s\n", EnvJsonFile)
		return err
	}

	if isUpdated {
		fmt.Printf("[image-env-to-json] '%s' file has been updated according to environment variables.\n", envJsonPath)
	} else {
		fmt.Printf("[image-env-to-json] No environment variables overwrites. Using original file.\n")
	}

	// Validates `env.json` using the `jsonSchema` file
	if jsonSchema != nil {
		compiler := jsonschema.NewCompiler()
		compiledSchema, err := compiler.Compile(jsonSchemaPath)
		if err != nil {
			fmt.Printf("[image-env-to-json] Error compiling the JSON Schema: %s.\n", jsonSchemaPath)
			return err
		}

		err = compiledSchema.Validate(envJson)
		if err != nil {
			fmt.Printf("[image-env-to-json] Error while validating '%s'.\n", EnvJsonFile)
			return err
		}
		fmt.Printf("[image-env-to-json] '%s' at '%s' is valid.\n", EnvJsonFile, envJsonPath)
	}

	fmt.Printf("[image-env-to-json] Done.\n")
	return nil
}
