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
	"os"
	"time"

	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/common"
	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/metadata"
	"github.com/ory/viper"
	"github.com/spf13/cobra"
)

type CreateCmdConfig struct {
	ProjectName string // Project name
}

func NewCreateCommand() *cobra.Command {
	var cmd = &cobra.Command{
		Use:   "create",
		Short: "Creates a new single file Workflow project",
		Long: `
	Creates a Workflow file in the specified directory (new-project is the default).
	It creates a Dockerfile.workflow in your temporary folder to be used by the "dev" and "build" commands.
	If you wish to delete it, you can use the prune command.
	The generated project has a Hello World workflow.sw.json located on the
	./<project-name>/ directory.
		`,
		Example: `
	# Create a project in the local directory
	# By default the project is named "new-project"
	{{.Name}} create

	# Create a project with an specfic name
	{{.Name}} create --name myproject
		`,
		SuggestFor: []string{"vreate", "creaet", "craete", "new"},
		PreRunE:    common.BindEnv("name"),
	}

	cmd.RunE = func(cmd *cobra.Command, args []string) error {
		return runCreate(cmd, args)
	}

	cmd.Flags().StringP("name", "n", "new-project", "Project name created in the current directory.")
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

	workflowPath := fmt.Sprintf("./%s/%s", name, metadata.WORKFLOW_SW_JSON)
	if err = CreateWorkflow(workflowPath); err != nil {
		fmt.Println("ERROR: creating workflow file")
		return fmt.Errorf("Description: %w", err)
	}

	fmt.Printf("🚀 Create command took: %s \n", time.Since(start))
	return nil
}

func runCreateConfig(cmd *cobra.Command) (cfg CreateCmdConfig, err error) {
	cfg = CreateCmdConfig{
		ProjectName: viper.GetString("name"),
	}
	return
}
