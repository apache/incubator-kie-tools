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
	"bytes"
	_ "embed"
	"fmt"
	"os"
	"path"
	"text/template"

	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/common"
	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/metadata"
	"github.com/ory/viper"
	"github.com/spf13/cobra"
)

type CreateCmdConfig struct {
	ProjectName     string
	YAML            bool
	WithPersistence bool
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
	
	# Add Dockerfile with persistence support to the project (default: false)
	{{.Name}} create --with-persistence

		`,
		SuggestFor: []string{"vreate", "creaet", "craete", "new"}, //nolint:misspell
		PreRunE:    common.BindEnv("name", "yaml-workflow", "with-persistence"),
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
	cmd.Flags().BoolP("with-persistence", "w", false, "Add persistence support to the project (default: false)")
	cmd.SetHelpFunc(common.DefaultTemplatedHelp)

	return cmd
}

//go:embed template/SonataFlow-Builder.containerfile
var SonataFlowBuilderContainerFile string

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
	if cfg.WithPersistence {
		err := addGitOpsDockerFile(cfg)
		if err != nil {
			return fmt.Errorf("❌ ERROR: Error creating Dockerfile for gitops profile: %w", err)
		}
	}
	fmt.Println("🎉 SonataFlow project successfully created")

	return nil

}

func runCreateCmdConfig() (cfg CreateCmdConfig, err error) {

	cfg = CreateCmdConfig{
		ProjectName:     viper.GetString("name"),
		YAML:            viper.GetBool("yaml-workflow"),
		WithPersistence: viper.GetBool("with-persistence"),
	}
	return cfg, nil
}

func addGitOpsDockerFile(cfg CreateCmdConfig) error {
	data := struct {
		BuildImage string
	}{
		BuildImage: metadata.BuilderImage,
	}

	tmpl, err := template.New("dockerfile").Parse(SonataFlowBuilderContainerFile)
	if err != nil {
		return fmt.Errorf("error parsing Dockerfile template: %w", err)
	}

	var rendered bytes.Buffer
	if err := tmpl.Execute(&rendered, data); err != nil {
		return fmt.Errorf("error executing Dockerfile template: %w", err)
	}

	dockerfilePath := path.Join(cfg.ProjectName, "Dockerfile.gitops")
	file, err := os.Create(dockerfilePath)
	if err != nil {
		return fmt.Errorf("error creating Dockerfile %s: %w", dockerfilePath, err)
	}
	defer file.Close()
	if _, err := file.WriteString(rendered.String()); err != nil {
		return fmt.Errorf("error writing to Dockerfile %s: %w", dockerfilePath, err)
	}
	return nil
}
