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

package quarkus

import (
	"fmt"
	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/common"
	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/metadata"
	"github.com/ory/viper"
	"github.com/spf13/cobra"
)

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

	# Create a project with an specific name
	{{.Name}} create --name myproject

	# Create a project with additional extensions
	# You can add multiple extensions by separating them with a comma
	{{.Name}} create --extensions kogito-addons-quarkus-persistence-postgresql,quarkus-core
		`,
		SuggestFor: []string{"vreate", "creaet", "craete", "new"},
		PreRunE:    common.BindEnv("name", "extension", "quarkus-platform-group-id", "quarkus-version"),
	}

	cmd.RunE = func(cmd *cobra.Command, args []string) error {
		return runCreate()
	}

	quarkusDependencies := metadata.ResolveQuarkusDependencies()

	cmd.Flags().StringP("name", "n", "new-project", "Project name created in the current directory.")
	cmd.Flags().StringP("extension", "e", "", "On Quarkus projects, setup project custom Maven extensions, separated with a comma.")
	cmd.Flags().StringP("quarkus-platform-group-id", "G", quarkusDependencies.QuarkusPlatformGroupId, "On Quarkus projects, setup project group id.")
	cmd.Flags().StringP("quarkus-version", "V", quarkusDependencies.QuarkusVersion, "On Quarkus projects, setup the project version.")
	cmd.SetHelpFunc(common.DefaultTemplatedHelp)

	return cmd
}

func runCreate() error {
	cfg, err := runCreateCmdConfig()
	if err != nil {
		return fmt.Errorf("initializing create config: %w", err)
	}

	exists, err := common.CheckIfDirExists(cfg.ProjectName)
	if exists {
		return fmt.Errorf("directory with name \"%s\" already exists", cfg.ProjectName)
	}
	if err != nil {
		return fmt.Errorf("directory with name \"%s\" already exists: %w", cfg.ProjectName, err)
	}

	if err := common.CheckJavaDependencies(); err != nil {
		return err
	}

	fmt.Println("🔨 Creating a Quarkus Kogito Serverless Workflow project...")
	if err = CreateQuarkusProject(cfg); err != nil {
		fmt.Println("❌  Error creating Quarkus project", err)
		return err
	}

	workflowFilePath := fmt.Sprintf("./%s/src/main/resources/%s", cfg.ProjectName, metadata.WorkflowSwJson)
	common.CreateWorkflow(workflowFilePath)

	fmt.Println("✅ Quarkus Kogito Serverless Workflow project successfully created")
	return nil
}

func runCreateProject(cfg CreateQuarkusProjectConfig) (err error) {
	if err = common.CheckProjectName(cfg.ProjectName); err != nil {
		return err
	}
	exists, err := common.CheckIfDirExists(cfg.ProjectName)
	if err != nil || exists {
		return fmt.Errorf("directory with name \"%s\" already exists: %w", cfg.ProjectName, err)
	}

	create := common.ExecCommand(
		"mvn",
		fmt.Sprintf("%s:%s:%s:create", cfg.DependenciesVersion.QuarkusPlatformGroupId, metadata.QuarkusMavenPlugin, cfg.DependenciesVersion.QuarkusVersion),
		"-DprojectGroupId=org.acme",
		"-DnoCode",
		fmt.Sprintf("-DplatformVersion=%s", cfg.DependenciesVersion.QuarkusVersion),
		fmt.Sprintf("-DprojectArtifactId=%s", cfg.ProjectName),
		fmt.Sprintf("-Dextensions=%s", cfg.Extensions))

	fmt.Println("Creating a Quarkus Kogito Serverless Workflow project...")

	if err := common.RunCommand(
		create,
		"create",
	); err != nil {
		return err
	}
	return
}

func runCreateCmdConfig() (cfg CreateQuarkusProjectConfig, err error) {
	quarkusPlatformGroupId := viper.GetString("quarkus-platform-group-id")
	quarkusVersion := viper.GetString("quarkus-version")

	cfg = CreateQuarkusProjectConfig{
		ProjectName: viper.GetString("name"),
		Extensions: fmt.Sprintf("%s,%s,%s,%s,%s,%s,%s,%s",
			metadata.KogitoQuarkusServerlessWorkflowExtension,
			metadata.KogitoAddonsQuarkusKnativeEventingExtension,
			metadata.QuarkusKubernetesExtension,
			metadata.QuarkusResteasyJacksonExtension,
			metadata.KogitoQuarkusServerlessWorkflowDevUi,
			metadata.KogitoAddonsQuarkusSourceFiles,
			metadata.SmallryeHealth,
			viper.GetString("extension"),
		),
		DependenciesVersion: metadata.DependenciesVersion{
			QuarkusPlatformGroupId: quarkusPlatformGroupId,
			QuarkusVersion:         quarkusVersion,
		},
	}
	return
}
