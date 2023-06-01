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
	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/common"
	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/metadata"
	"github.com/ory/viper"
	"github.com/spf13/cobra"
	"os"
)

type CreateCmdConfig struct {
	ProjectName string
}

func NewCreateCommand() *cobra.Command {
	var cmd = &cobra.Command{
		Use:   "create",
		Short: "Creates a new Workflow project",
		Long: `
	Creates a Workflow file in the specified directory (new-project is the default).

	This plain Serverless Workflow project targets use cases requiring a single Serverless
	Workflow file definition.

	Additionally, you can define the configurable parameters of your application in the 
	"application.properties" file (inside the root directory). 
	You can also store your spec files (i.e., Open API files)inside the "specs" folder.
	`,
		Example: `
	# Create a project in the local directory
	# By default the project is named "new-project"
	{{.Name}} create

	# Create a project with an specific name
	{{.Name}} create --name myproject
		`,
		SuggestFor: []string{"vreate", "creaet", "craete", "new"},
		PreRunE:    common.BindEnv("name"),
	}

	cmd.RunE = func(cmd *cobra.Command, args []string) error {
		cfg, err := runCreateCmdConfig()
		if err != nil {
			return fmt.Errorf("initializing create config: %w", err)
		}
		return runCreate(cfg)
	}

	cmd.Flags().StringP("name", "n", "new-project", "Project name created in the current directory.")
	cmd.SetHelpFunc(common.DefaultTemplatedHelp)

	return cmd
}

func runCreate(cfg CreateCmdConfig) error {
	fmt.Println("🔨 Creating workflow project")

	if err := os.Mkdir(cfg.ProjectName, os.ModePerm); err != nil {
		return fmt.Errorf("❌ Error creating project directory: %w", err)
	}

	workflowPath := fmt.Sprintf("./%s/%s", cfg.ProjectName, metadata.WorkflowSwJson)
	if err := common.CreateWorkflow(workflowPath); err != nil {
		return fmt.Errorf("❌ Error creating workflow file: %w", err)
	}

	fmt.Println("✅ Kogito Serverless Workflow project successfully created")

	return nil

}

func runCreateCmdConfig() (cfg CreateCmdConfig, err error) {

	cfg = CreateCmdConfig{
		ProjectName: viper.GetString("name"),
	}
	return cfg, nil
}
