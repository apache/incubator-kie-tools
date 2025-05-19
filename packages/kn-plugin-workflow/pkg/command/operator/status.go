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

package operator

import (
	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/common"
	"github.com/ory/viper"
	"github.com/spf13/cobra"
)

func NewStatusOperatorCommand() *cobra.Command {
	var cmd = &cobra.Command{
		Use:   "status",
		Short: "Get the status of deployed SonataFlow Operator.",
		Long: `
	Get the status of deployed SonataFlow Operator.
	`,
		Example: `
	# Check the status of the SonataFlow Operator.
	{{.Name}} operator status --namespace <your_operator_namespace>
		`,
		PreRunE:    common.BindEnv("namespace"),
	}

	cmd.RunE = func(cmd *cobra.Command, args []string) error {
		return runStatusCommand(cmd, args)
	}
	cmd.Flags().StringP("namespace", "n", "", "Target namespace of your Operator deployment.")

	cmd.SetHelpFunc(common.DefaultTemplatedHelp)

	return cmd
}

func runStatusCommand(cmd *cobra.Command, args []string) error {
	namespace := viper.GetString("namespace")

	operator := common.NewOperatorManager(namespace)

	err:= operator.CheckOLMInstalled()
	if err != nil {
		return err
	}

	err = operator.GetSonataflowOperatorStats()
	if err != nil {
		return err
	}
	return nil
}
