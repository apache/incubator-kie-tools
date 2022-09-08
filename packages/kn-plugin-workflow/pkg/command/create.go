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
	"time"

	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/common"
	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/metadata"
	"github.com/ory/viper"
	"github.com/spf13/cobra"
)

type CreateCmdConfig struct {
	// Quarkus project options
	ProjectName string // Project name
	Extesions   string // List of extensions separated by "," to be add on the Quarkus project

	// Dependencies versions
	DependenciesVersion metadata.DependenciesVersion

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
		PreRunE:    common.BindEnv("name", "extension", "quarkus-platform-group-id", "quarkus-version"),
	}

	cmd.RunE = func(cmd *cobra.Command, args []string) error {
		return runCreate(cmd, args)
	}

	quarkusDepedencies := metadata.ResolveQuarkusDependencies()

	cmd.Flags().StringP("name", "n", "new-project", "Project name created in the current directory.")
	cmd.Flags().StringP("extension", "e", "", "Project custom Maven extensions, separated with a comma.")
	cmd.Flags().StringP("quarkus-platform-group-id", "G", quarkusDepedencies.QuarkusPlatformGroupId, "Quarkus group id to be set in the project.")
	cmd.Flags().StringP("quarkus-version", "V", quarkusDepedencies.QuarkusVersion, "Quarkus version to be set in the project.")
	cmd.SetHelpFunc(common.DefaultTemplatedHelp)

	return cmd
}

func runCreate(cmd *cobra.Command, args []string) error {
	start := time.Now()

	cfg, err := runCreateCmdConfig(cmd)
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

	create := common.ExecCommand(
		"mvn",
		fmt.Sprintf("%s:%s:%s:create", cfg.DependenciesVersion.QuarkusPlatformGroupId, common.QUARKUS_MAVEN_PLUGIN, cfg.DependenciesVersion.QuarkusVersion),
		"-DprojectGroupId=org.acme",
		"-DnoCode",
		fmt.Sprintf("-DplatformVersion=%s", cfg.DependenciesVersion.QuarkusVersion),
		fmt.Sprintf("-DprojectArtifactId=%s", cfg.ProjectName),
		fmt.Sprintf("-Dextensions=%s", cfg.Extesions))

	fmt.Println("Creating a Kogito Serverless Workflow project...")

	if err := common.RunCommand(
		create,
		cfg.Verbose,
		"create",
		common.GetFriendlyMessages("creating"),
	); err != nil {
		fmt.Println("Check the full logs with the -v | --verbose option")
		return err
	}

	workflowFilePath := fmt.Sprintf("./%s/src/main/resources/%s", cfg.ProjectName, common.WORKFLOW_SW_JSON)
	CreateWorkflow(workflowFilePath)

	finish := time.Since(start)
	fmt.Printf("🚀 Project creation took: %s \n", finish)
	return nil
}

// runCreateCmdConfig returns the configs from the current execution context
func runCreateCmdConfig(cmd *cobra.Command) (cfg CreateCmdConfig, err error) {
	quarkusPlatformGroupId := viper.GetString("quarkus-platform-group-id")
	quarkusVersion := viper.GetString("quarkus-version")

	cfg = CreateCmdConfig{
		ProjectName: viper.GetString("name"),
		Extesions: fmt.Sprintf("%s,%s,%s,%s,%s",
			common.KOGITO_QUARKUS_SERVERLESS_WORKFLOW_EXTENSION,
			common.KOGITO_ADDONS_QUARKUS_KNATIVE_EVENTING_EXTENSION,
			common.QUARKUS_KUBERNETES_EXTENSION,
			common.QUARKUS_RESTEASY_REACTIVE_JACKSON_EXTENSION,
			viper.GetString("extension"),
		),

		DependenciesVersion: metadata.DependenciesVersion{
			QuarkusPlatformGroupId: quarkusPlatformGroupId,
			QuarkusVersion:         quarkusVersion,
		},

		Verbose: viper.GetBool("verbose"),
	}
	return
}
