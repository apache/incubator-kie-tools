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

package root

import (
	"fmt"
	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/command/operator"
	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/command/specs"

	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/command/quarkus"

	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/command"
	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/common"
	"github.com/ory/viper"
	"github.com/spf13/cobra"
)

type RootCmdConfig struct {
	Name    string
	Version string
}

func NewRootCommand(cfg RootCmdConfig) *cobra.Command {
	var cmd = &cobra.Command{
		Use:   cfg.Name,
		Short: "SonataFlow",
		Long: `
	Manage SonataFlow projects
	==========================

	Currently, SonataFlow targets use cases with a single Serverless Workflow main
	file definition (i.e. workflow.sw.{json|yaml|yml}).

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
		Aliases: []string{"kn-workflow"},
	}

	viper.AutomaticEnv()           // read in environment variables for WORKFLOW_<flag>
	viper.SetEnvPrefix("workflow") // ensure that all have the prefix

	cmd.Version = cfg.Version
	cmd.SetVersionTemplate(`{{printf "%s\n" .Version}}`)

	cmd.AddCommand(command.NewCreateCommand())
	cmd.AddCommand(command.NewRunCommand())
	cmd.AddCommand(command.NewDeployCommand())
	cmd.AddCommand(command.NewUndeployCommand())
	cmd.AddCommand(command.NewGenManifest())
	cmd.AddCommand(quarkus.NewQuarkusCommand())
	cmd.AddCommand(command.NewVersionCommand(cfg.Version))
	cmd.AddCommand(specs.SpecsCommand())
	cmd.AddCommand(operator.NewOperatorCommand())

	cmd.SetHelpFunc(func(cmd *cobra.Command, args []string) {
		runRootHelp(cmd, args)
	})

	return cmd
}

func runRootHelp(cmd *cobra.Command, args []string) {
	tpl := common.GetTemplate(cmd, "root")
	var data = struct {
		Name string
	}{
		Name: cmd.Root().Use,
	}

	if err := tpl.Execute(cmd.OutOrStdout(), data); err != nil {
		fmt.Fprintf(cmd.ErrOrStderr(), "unable to display help text: %v", err)
	}
}
