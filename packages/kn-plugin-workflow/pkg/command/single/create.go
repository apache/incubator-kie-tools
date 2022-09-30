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

package single

import (
	"fmt"
	"os"
	"time"

	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/command"
	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/common"
	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/metadata"
	"github.com/ory/viper"
	"github.com/spf13/cobra"
)

type CreateConfig struct {
	// Quarkus project options
	ProjectName         string // Project name
	DependenciesVersion metadata.DependenciesVersion
}

func NewCreateCommand() *cobra.Command {
	var cmd = &cobra.Command{
		Use:        "create",
		Short:      "Create a workflow project",
		Long:       ``,
		Example:    ``,
		SuggestFor: []string{"vreate", "creaet", "craete", "new"},
		PreRunE:    common.BindEnv("name", "quarkus-platform-group-id", "quarkus-version"),
	}

	cmd.RunE = func(cmd *cobra.Command, args []string) error {
		return runCreate(cmd, args)
	}

	quarkusDepedencies := metadata.ResolveQuarkusDependencies()
	cmd.Flags().StringP("name", "n", "new-project", "Project name created in the current directory.")
	cmd.Flags().StringP("quarkus-platform-group-id", "G", quarkusDepedencies.QuarkusPlatformGroupId, "Quarkus group id to be set in the project.")
	cmd.Flags().StringP("quarkus-version", "V", quarkusDepedencies.QuarkusVersion, "Quarkus version to be set in the project.")
	cmd.SetHelpFunc(common.DefaultTemplatedHelp)

	return cmd
}

func runCreate(cmd *cobra.Command, args []string) (err error) {
	start := time.Now()
	fmt.Println("🔨 Creating workflow project")

	cfg, err := runCreateConfig(cmd)
	if err != nil {
		fmt.Println("ERROR: parsing flags")
		return fmt.Errorf("Description: %w", err)
	}

	name := cfg.ProjectName
	if err = os.Mkdir(name, os.ModePerm); err != nil {
		fmt.Println("ERROR: creating project with name", cfg.ProjectName)
		return fmt.Errorf("Description: %w", err)
	}

	workflowPath := fmt.Sprintf("./%s/%s", name, common.WORKFLOW_SW_JSON)
	if err = command.CreateWorkflow(workflowPath); err != nil {
		fmt.Println("ERROR: creating workflow file")
		return fmt.Errorf("Description: %w", err)
	}

	dockerfilePath := GetDockerfileDir(cfg.DependenciesVersion)
	if err = CreateDockerfile(dockerfilePath, cfg.DependenciesVersion.QuarkusVersion); err != nil {
		fmt.Println("ERROR: creating Dockerfile in temp folder")
		return fmt.Errorf("Description: %w", err)
	}

	fmt.Printf("🚀 Build took: %s \n", time.Since(start))
	return nil
}

func runCreateConfig(cmd *cobra.Command) (cfg CreateConfig, err error) {
	cfg = CreateConfig{
		ProjectName: viper.GetString("name"),
		DependenciesVersion: metadata.DependenciesVersion{
			QuarkusPlatformGroupId: viper.GetString("quarkus-platform-group-id"),
			QuarkusVersion:         viper.GetString("quarkus-version"),
		},
	}
	return
}
