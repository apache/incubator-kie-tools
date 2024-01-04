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

package command

import (
	"fmt"
	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/common"
	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/metadata"
	"github.com/ory/viper"
	"github.com/spf13/cobra"
	"os"
)

type CreateCmdConfig struct {
	ProjectName string
	YAML        bool
}

func NewCreateCommand() *cobra.Command {
	var cmd = &cobra.Command{
		Use:   "create",
		Short: "Creates a new SonataFlow project",
		Long: `
	Creates a Workflow file in the specified directory (new-project is the default).

	This SonataFlow project targets use cases requiring a single Serverless
	Workflow file definition.

	Additionally, you can define the configurable parameters of your application in the 
	"application.properties" file (inside the root project directory).
	You can also store your spec files (i.e., Open API files) inside the "specs" folder,
	schemas file inside "schemas" folder and also subflows inside "subflows" folder.

	A SonataFlow project, as the following structure by default:

	Workflow project root
		/specs (optional)
		/schemas (optional)
		/subflows (optional)
		workflow.sw.{json|yaml|yml} (mandatory)

	`,
		Example: `
	# Create a project in the local directory
	# By default the project is named "new-project"
	{{.Name}} create

	# Create a project with an specific name
	{{.Name}} create --name myproject

	# Creates a YAML sample workflow file (JSON is default)
	{{.Name}} create --yaml-workflow
		`,
		SuggestFor: []string{"vreate", "creaet", "craete", "new"}, //nolint:misspell
		PreRunE:    common.BindEnv("name", "yaml-workflow"),
	}

	cmd.RunE = func(cmd *cobra.Command, args []string) error {
		cfg, err := runCreateCmdConfig()
		if err != nil {
			return fmt.Errorf("initializing create config: %w", err)
		}
		return runCreate(cfg)
	}

	cmd.Flags().StringP("name", "n", "new-project", "Project name created in the current directory.")
	cmd.Flags().Bool("yaml-workflow", false, "Create a sample YAML workflow file.")
	cmd.SetHelpFunc(common.DefaultTemplatedHelp)

	return cmd
}

func runCreate(cfg CreateCmdConfig) error {
	fmt.Println("🛠️ Creating SonataFlow project")

	if err := os.Mkdir(cfg.ProjectName, os.ModePerm); err != nil {
		return fmt.Errorf("❌ ERROR: Error creating project directory: %w", err)
	}

	var workflowFormat string
	if cfg.YAML {
		workflowFormat = metadata.WorkflowSwYaml
	} else {
		workflowFormat = metadata.WorkflowSwJson
	}

	workflowPath := fmt.Sprintf("./%s/%s", cfg.ProjectName, workflowFormat)
	if err := common.CreateWorkflow(workflowPath, cfg.YAML); err != nil {
		return fmt.Errorf("❌ ERROR: Error creating workflow file: %w", err)
	}

	fmt.Println("🎉 SonataFlow project successfully created")

	return nil

}

func runCreateCmdConfig() (cfg CreateCmdConfig, err error) {

	cfg = CreateCmdConfig{
		ProjectName: viper.GetString("name"),
		YAML:        viper.GetBool("yaml-workflow"),
	}
	return cfg, nil
}
