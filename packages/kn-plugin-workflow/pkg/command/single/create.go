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

	"github.com/kiegroup/kie-tools/kn-plugin-workflow/pkg/command"
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
		Use:        "create",
		Short:      "Create a workflow project",
		Long:       ``,
		Example:    ``,
		SuggestFor: []string{"vreate", "creaet", "craete", "new"},
		PreRunE:    common.BindEnv("name", "extensions", "verbose"),
	}

	cmd.RunE = func(cmd *cobra.Command, args []string) error {
		return runCreate(cmd, args)
	}

	cmd.Flags().StringP("name", "n", "new-project", "Project name created in the current directory.")
	cmd.SetHelpFunc(common.DefaultTemplatedHelp)

	return cmd
}

func runCreate(cmd *cobra.Command, args []string) error {
	start := time.Now()

	cfg, err := runCreateConfig(cmd)
	if err != nil {
		return fmt.Errorf("initializing create config: %w", err)
	}

	name := cfg.ProjectName

	if err := os.Mkdir(name, os.ModePerm); err != nil {
		return fmt.Errorf("error creating dir: %w", err)
	}

	workflowPath := fmt.Sprintf("./%s/workflow.sw.json", name)
	command.GenerateWorkflow(workflowPath)

	dockerfilePath := fmt.Sprintf("./%s/Dockerfile.workflow", name)
	GenerateDockerfile(dockerfilePath)

	finish := time.Since(start)
	fmt.Printf("🚀 Create workflow took: %s \n", finish)
	return nil
}

func runCreateConfig(cmd *cobra.Command) (cfg CreateConfig, err error) {
	cfg = CreateConfig{
		ProjectName: viper.GetString("name"),
		Verbose:     viper.GetBool("verbose"),
	}
	return
}
