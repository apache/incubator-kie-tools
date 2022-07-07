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

type CreateConfig struct {
	// Quarkus project options
	ProjectName string // Project name
	Extesions   string // List of extensions separated by "," to be add on the Quarkus project

	// Plugin options
	Verbose bool
}

func NewCreateCommand() *cobra.Command {
	var cmd = &cobra.Command{
		Use:   "create",
		Short: "Create a Kogito Serverless Workflow project",
		Long: `
	Creates a Kogito Serverless Workflow project in the current directory.
	It sets up a Quarkus project with minimal extensions to build a workflow
	project.
	The generated project has a "hello world" workflow.sw.json located on the
	./<project-name>/src/main/resources directory.
		`,
		Example: `
	# Create a project in the local directory
	# By default the project is named "new-project"
	{{.Name}} create

	# Create a project with an specfic name
	{{.Name}} create --name myproject

	# Create a project with additional extensions
	# You can add multiple extensions by separating them with a comma
	{{.Name}} create --extensions kogito-addons-quarkus-persistence-postgresql,quarkus-core
		`,
		SuggestFor: []string{"vreate", "creaet", "craete", "new"},
		PreRunE:    common.BindEnv("name", "extension"),
	}

	cmd.RunE = func(cmd *cobra.Command, args []string) error {
		return runCreate(cmd, args)
	}

	cmd.Flags().StringP("name", "n", "new-project", "Project name created in the current directory.")
	cmd.Flags().StringP("extension", "e", "", "Project custom Maven extensions, separated with a comma.")
	cmd.SetHelpFunc(common.DefaultTemplatedHelp)

	return cmd
}

func runCreate(cmd *cobra.Command, args []string) error {
	start := time.Now()

	cfg, err := runCreateConfig(cmd)
	if err != nil {
		return fmt.Errorf("initializing create config: %w", err)
	}

	exists, err := common.CheckIfDirExists(cfg.ProjectName)
	if err != nil || exists {
		return fmt.Errorf("directory with name \"%s\" already exists: %w", cfg.ProjectName, err)
	}

	if err := common.CheckJavaDependencies(); err != nil {
		return err
	}

	create := exec.Command(
		"mvn",
		fmt.Sprintf("io.quarkus.platform:quarkus-maven-plugin:%s:create", common.QUARKUS_VERSION),
		"-DprojectGroupId=org.acme",
		"-DnoCode",
		fmt.Sprintf("-DprojectArtifactId=%s", cfg.ProjectName),
		fmt.Sprintf("-Dextensions=%s", cfg.Extesions))

	fmt.Println("Creating a Kogito Serverless Workflow project...")

	if err := common.RunCommand(
		create,
		cfg.Verbose,
		"create",
		getCreateFriendlyMessages(),
	); err != nil {
		fmt.Println("Check the full logs with the -v | --verbose option")
		return err
	}

	generateWorkflow(cfg)

	finish := time.Since(start)
	fmt.Printf("🚀 Project creation took: %s \n", finish)
	return nil
}

// runCreateConfig returns the configs from the current execution context
func runCreateConfig(cmd *cobra.Command) (cfg CreateConfig, err error) {
	cfg = CreateConfig{
		ProjectName: viper.GetString("name"),
		Extesions:   fmt.Sprintf("%s,%s", common.QUARKUS_DEFAULT_EXTENSIONS, viper.GetString("extension")),
		Verbose:     viper.GetBool("verbose"),
	}
	return
}

func generateWorkflow(cfg CreateConfig) (err error) {
	var workflowFilePath = fmt.Sprintf("./%s/src/main/resources/workflow.sw.json", cfg.ProjectName)
	workflowFileData, err := GetWorkflowTemplate()
	if err != nil {
		return err
	}

	err = ioutil.WriteFile(workflowFilePath, workflowFileData, 0644)
	if err != nil {
		fmt.Println("ERROR: writing the workflow json file.")
		return err
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
