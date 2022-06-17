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

package create

import (
	"fmt"
	"io/ioutil"
	"os/exec"
	"time"

	"github.com/kiegroup/kie-tools/kn-plugin-workflow/pkg/common"
	"github.com/ory/viper"
	"github.com/spf13/cobra"
)

const (
	quarkusDefaultWorkflowExtensions = "kogito-quarkus-serverless-workflow,resteasy-reactive-jackson"
)

func NewCreateCommand() *cobra.Command {
	var cmd = &cobra.Command{
		Use:     "create",
		Short:   "Create a Quarkus serverless workflow project",
		PreRunE: common.BindEnv("language", "template", "repository", "confirm"),
	}

	cmd.RunE = func(cmd *cobra.Command, args []string) error {
		return runCreate(cmd, args)
	}

	cmd.Flags().StringP("name", "n", "new-project", fmt.Sprintf("%s project name to be used", cmd.Name()))
	cmd.Flags().StringP("extension", "e", "", fmt.Sprintf("%s project custom extensions, separated with a comma", cmd.Name()))

	cmd.Flags().StringP("image", "i", "quarkus/new-project", fmt.Sprintf("%s project custom extensions, separated with a comma", cmd.Name()))
	cmd.Flags().String("namespace", "default", fmt.Sprintf("%s project custom extensions, separated with a comma", cmd.Name()))

	return cmd
}

func runCreate(cmd *cobra.Command, args []string) error {
	start := time.Now()

	if err := common.CheckPreRequisitions(); err != nil {
		return fmt.Errorf("checking dependencies: %w", err)
	}

	cfg, err := runCreateConfig(cmd)
	if err != nil {
		return fmt.Errorf("create config error %w", err)
	}

	// TODO: extract quarkus version to env
	create := exec.Command(
		"mvn",
		"io.quarkus.platform:quarkus-maven-plugin:2.9.2.Final:create",
		"-DprojectGroupId=org.acme",
		"-DnoCode",
		fmt.Sprintf("-DprojectArtifactId=%s", cfg.ProjectName),
		fmt.Sprintf("-Dextensions=%s", cfg.Extesions))

	if out, err := create.CombinedOutput(); err != nil {
		fmt.Printf("Creating Quarkus workflow project: \n%s\n", string(out))
		return fmt.Errorf("create command failed with error: %w", err)
	}

	generateConfigYaml(cfg)
	generateWorkflow(cfg)

	finish := time.Since(start)
	fmt.Printf("🚀 Create took: %s \n", finish)
	return nil
}

type CreateConfig struct {
	// Quarkus project options
	ProjectName string // Project name
	Extesions   string // List of extensions separated by "," to be add on the Quarkus project

	// Config YAML options
	Image     string // Registry to be add on the config yaml
	Namespace string // Namespace to be add on the config yaml
}

// runCreateConfig returns the configs from the current execution context
func runCreateConfig(cmd *cobra.Command) (cfg CreateConfig, err error) {
	cfg = CreateConfig{
		ProjectName: viper.GetString("name"),
		Extesions:   fmt.Sprintf("%s,%s", quarkusDefaultWorkflowExtensions, viper.GetString("extension")),

		Image:     viper.GetString("image"),
		Namespace: viper.GetString("namespace"),
	}
	return
}

func generateConfigYaml(cfg CreateConfig) (err error) {
	configYamlTemplate, err := GenerateConfigYamlTemplate(cfg)
	if err != nil {
		return fmt.Errorf("error generating config yaml template, %w", err)
	}

	var configYamlFile = fmt.Sprintf("./%s/config.yaml", cfg.ProjectName)
	err = ioutil.WriteFile(configYamlFile, configYamlTemplate, 0644)
	if err != nil {
		return fmt.Errorf("error creating yaml file, %w", err)
	}

	fmt.Printf("Config file created on %s \n", configYamlFile)
	return
}

func generateWorkflow(cfg CreateConfig) (err error) {
	var workflowFilePath = fmt.Sprintf("./%s/src/main/resources/workflow.sw.json", cfg.ProjectName)
	data := []byte("{}")
	err = ioutil.WriteFile(workflowFilePath, data, 0644)
	if err != nil {
		return fmt.Errorf("error creating workflow file")
	}

	fmt.Printf("Workflow file created on %s \n", workflowFilePath)
	return
}
