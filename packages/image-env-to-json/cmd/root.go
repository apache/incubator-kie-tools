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

	"github.com/spf13/cobra"
	"github.com/xeipuuv/gojsonschema"
)

type CmdConfig struct {
	Version string
}

const (
	EnvJsonFile = "env.json"
)

func RootCmd(cfg CmdConfig) *cobra.Command {
	var (
		directory  string
		jsonSchema string
	)

	var cmd = &cobra.Command{
		Use:   "image-env-to-json",
		Short: "A CLI tool to create a 'env.json' file from a JSON Schema.",
		Long: `
	This tool uses the "json-schema" JSON Schema file and creates a 'env.json' environment file in the specified "directory".
			`,
		Example: `
	# Run the 
	image-env-to-json --directory /path/to/env.json --json-schema /path/to/jsonSchema.json
		 `,
		Run: func(cmd *cobra.Command, args []string) {
			if err := generateEnvJson(directory, jsonSchema); err != nil {
				fmt.Printf("[image-env-to-json] Error: '%v'.", err)
				os.Exit(1)
			}
		},
	}

	cmd.Version = cfg.Version
	cmd.SetVersionTemplate(`{{printf "%s\n" .Version}}`)

	cmd.PersistentFlags().StringVarP(&directory, "directory", "d", "", "directory to create or update an existing env.json file.")
	cmd.PersistentFlags().StringVar(&jsonSchema, "json-schema", "", "JSON Schema file path to validate.")
	cmd.MarkFlagRequired("directory")
	cmd.MarkFlagRequired("json-schema")

	return cmd
}

func generateEnvJson(directory string, jsonSchema string) error {
	// Check if directory exists
	if _, err := os.Stat(directory); err != nil {
		if os.IsNotExist(err) {
			fmt.Printf("[image-env-to-json] Directory '%s' does not exist. Please provide an existing directory.\n", directory)
			return err
		} else {
			fmt.Printf("[image-env-to-json] Error while reading the '%s'.\n", directory)
			return err
		}
	}

	fmt.Printf("[image-env-to-json] Reading JSON Schema from '%s'...\n", jsonSchema)
	data, err := os.ReadFile(jsonSchema)
	if err != nil {
		fmt.Printf("[image-env-to-json] Error reading file '%s'.\n", jsonSchema)
		return err
	}

	var schema map[string]any
	if err := json.Unmarshal(data, &schema); err != nil {
		fmt.Printf("[image-env-to-json] Error parsing JSON Schema from '%s'.\n", jsonSchema)
		return err
	}

	id := fmt.Sprintf("%v", schema["$id"])
	properties, _ := schema["definitions"].(map[string]any)[id].(map[string]any)["properties"].(map[string]any)
	envVarNames := make([]string, 0, len(properties))
	for key := range properties {
		envVarNames = append(envVarNames, key)
	}
	fmt.Printf("[image-env-to-json] Extracted envVarNames: %s\n", envVarNames)

	fmt.Printf("[image-env-to-json] Looking for environment variables: %v...\n", envVarNames)
	var envJsonPath = fmt.Sprintf("%s/%s", directory, EnvJsonFile)
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

	envData, err := os.ReadFile(envJsonPath)
	if err != nil {
		fmt.Printf("[image-env-to-json] Error reading %s\n", EnvJsonFile)
		return err
	}

	var envJson map[string]any
	if err := json.Unmarshal(envData, &envJson); err != nil {
		fmt.Printf("[image-env-to-json] Error parsing %s\n", EnvJsonFile)
		return err
	}

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

	updatedData, err := json.MarshalIndent(envJson, "", "  ")
	if err != nil {
		fmt.Printf("[image-env-to-json] Error encoding %s\n", EnvJsonFile)
		return err
	}

	if err := os.WriteFile(envJsonPath, updatedData, 0644); err != nil {
		fmt.Printf("[image-env-to-json] Error writing %s\n", EnvJsonFile)
		return err
	}

	if isUpdated {
		fmt.Printf("[image-env-to-json] '%s' file has been updated according to environment variables.\n", envJsonPath)
	} else {
		fmt.Printf("[image-env-to-json] No environment variables overwrites. Using original file.\n")
	}

	// Validate JSON if schema exists
	if schema != nil {
		goSchema := gojsonschema.NewGoLoader(schema)
		goEnvJson := gojsonschema.NewGoLoader(envJson)

		result, err := gojsonschema.Validate(goSchema, goEnvJson)
		if err != nil {
			fmt.Printf("[image-env-to-json] Error while validating '%s'.\n", EnvJsonFile)
			return err
		}

		if !result.Valid() {
			fmt.Printf("[image-env-to-json] Invalid '%s' at '%s'\n", EnvJsonFile, envJsonPath)
			return err
		}
		fmt.Printf("[image-env-to-json] '%s' at '%s' is valid.\n", EnvJsonFile, envJsonPath)
	}

	fmt.Printf("[image-env-to-json] Done.\n")
	return nil
}
