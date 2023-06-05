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
	"github.com/spf13/cobra"
)

type DeployCmdConfig struct {
	// Deploy options
	Path string // service name
}

func NewDeployCommand() *cobra.Command {
	var cmd = &cobra.Command{
		Use:   "deploy",
		Short: "Deploy a Kogito Serverless Workflow project",
		Long: `
	Deploys a Kogito Serverless Workflow project in the current directory
	in the Kogito operator. 
	`,
		Example: `
	# Deploy the workflow from the current directory's project. 
	# Deploy as Knative service.
	{{.Name}} deploy
	
		`,
		SuggestFor: []string{"delpoy", "deplyo"},
	}

	cmd.RunE = func(cmd *cobra.Command, args []string) error {
		return runDeploy(cmd, args)
	}

	cmd.SetHelpFunc(common.DefaultTemplatedHelp)

	return cmd
}

func runDeploy(cmd *cobra.Command, args []string) error {

	fmt.Println("😮‍💨 the deploy command is not available yet for workflow single file projects.")
	fmt.Println("Meanwhile, you can deploy your project via \"quarkus build\" and \"quarkus deploy\" commands.")
	fmt.Println("To convert it to Quarkus, run \"quarkus convert\" command")
	return nil
}
