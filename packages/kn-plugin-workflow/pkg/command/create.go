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

package command

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
	quarkusDefaultVersion            = "2.9.2.Final"
	quarkusDefaultWorkflowExtensions = "kogito-quarkus-serverless-workflow,kogito-addons-quarkus-knative-eventing,resteasy-reactive-jackson,quarkus-kubernetes"
)

func NewCreateCommand() *cobra.Command {
	var cmd = &cobra.Command{
		Use:   "create",
		Short: "Create a Quarkus serverless workflow project",
		Long: `
NAME
	{{.Name}} create - Create a Quarkus project.

SYNOPSIS
	{{.Name}} create [-n|--name] [-e|--extension] [-i|--image]
					 [--namespace] [-v|--verbose]

DESCRIPTION
	Creates a new Quarkus workflow project with the minimum required deploy to a cluster.

	$ {{.Name}} create -n new-project --namespace
		`,
		PreRunE: common.BindEnv("name", "extension", "image", "namespace"),
	}

	cmd.RunE = func(cmd *cobra.Command, args []string) error {
		return runCreate(cmd, args)
	}

	cmd.Flags().StringP("name", "n", "new-project", fmt.Sprintf("%s project name to be used", cmd.Name()))
	cmd.Flags().StringP("extension", "e", "", fmt.Sprintf("%s project custom extensions, separated with a comma", cmd.Name()))
	cmd.Flags().StringP("image", "i", "quarkus/new-project", fmt.Sprintf("%s registry image to be used on the config.yaml", cmd.Name()))
	cmd.Flags().String("namespace", "default", fmt.Sprintf("%s namespace to be used on the config.yaml", cmd.Name()))

	cmd.SetHelpFunc(common.DefaultTemplatedHelp)

	return cmd
}

func runCreate(cmd *cobra.Command, args []string) error {
	start := time.Now()

	cfg, err := runCreateConfig(cmd)
	if err != nil {
		return fmt.Errorf("create config error %w", err)
	}

	exists, err := common.CheckIfDirExists(cfg.ProjectName)
	if err != nil || exists {
		return fmt.Errorf("directory with name \"%s\" already exists", cfg.ProjectName)
	}

	if err := common.CheckJavaDependencies(); err != nil {
		return fmt.Errorf("checking dependencies: %w", err)
	}

	quarkusVersion := common.GetEnv("QUARKUS_VERSION", quarkusDefaultVersion)
	create := exec.Command(
		"mvn",
		fmt.Sprintf("io.quarkus.platform:quarkus-maven-plugin:%s:create", quarkusVersion),
		"-DprojectGroupId=org.acme",
		"-DnoCode",
		fmt.Sprintf("-DprojectArtifactId=%s", cfg.ProjectName),
		fmt.Sprintf("-Dextensions=%s", cfg.Extesions))

	fmt.Printf("Creating a Quarkus workflow project\n")

	if err := common.RunCommand(
		create,
		cfg.Verbose,
		"create Quarkus project failed with error",
		getCreateFriendlyMessages(),
	); err != nil {
		fmt.Println("Check the full logs with the [-v | --verbose] flag")
		return fmt.Errorf("%w", err)
	}

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

	// Plugin options
	Verbose bool
}

// runCreateConfig returns the configs from the current execution context
func runCreateConfig(cmd *cobra.Command) (cfg CreateConfig, err error) {
	cfg = CreateConfig{
		ProjectName: viper.GetString("name"),
		Extesions:   fmt.Sprintf("%s,%s", quarkusDefaultWorkflowExtensions, viper.GetString("extension")),
		Image:       viper.GetString("image"),
		Namespace:   viper.GetString("namespace"),
		Verbose:     viper.GetBool("verbose"),
	}
	return
}

func generateWorkflow(cfg CreateConfig) (err error) {
	var workflowFilePath = fmt.Sprintf("./%s/src/main/resources/workflow.sw.json", cfg.ProjectName)
	data := []byte(`{
	"id": "hello",
	"specVersion": "0.8.0",
	"name": "Hello World",
	"start": "HelloWorld",
	"states": [
		{
		"name": "HelloWorld",
		"type": "operation",
		"actions": [],
		"end": true
		}
	]
}	  
	`)
	err = ioutil.WriteFile(workflowFilePath, data, 0644)
	if err != nil {
		return fmt.Errorf("error creating workflow file")
	}

	fmt.Printf("Workflow file created on %s \n", workflowFilePath)
	return
}

func getCreateFriendlyMessages() []string {
	return []string{
		" Creating...",
		" Still creating project",
		" Still creating project",
		" Yes, still creating project",
		" Don't give up on me",
		" Still creating project",
		" This is taking a while",
	}
}
