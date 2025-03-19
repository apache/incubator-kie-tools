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
	"fmt"
	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/common"
	"github.com/ory/viper"
	"github.com/spf13/cobra"
)

func NewInstallOperatorCommand() *cobra.Command {
	var cmd = &cobra.Command{
		Use:   "install",
		Short: "Install the SonataFlow Operator, which is responsible for deploying SonataFlow projects on Kubernetes.",
		Long: `
	Install the SonataFlow Operator, which is responsible for deploying SonataFlow projects on Kubernetes.
	`,
		Example: `
	# Install the SonataFlow Operator. Usually in Openshift the namespace is "openshift-operators", in case of Minikube or Kind, with
	# default OLM installation, the namespace is "operators".
	{{.Name}} operator install --namespace <your_operator_namespace>
		`,
		PreRunE:    common.BindEnv("namespace"),
	}

	cmd.RunE = func(cmd *cobra.Command, args []string) error {
		return runInstallOperatorCommand(cmd, args)
	}
	cmd.Flags().StringP("namespace", "n", "", "Target namespace of your Operator deployment.")
	cmd.SetHelpFunc(common.DefaultTemplatedHelp)

	return cmd
}

func runInstallOperatorCommand(cmd *cobra.Command, args []string) error {
	fmt.Println("🚀 Installing the SonataFlow Operator...")

	namespace := viper.GetString("namespace")

	operator := common.NewOperatorManager(namespace)

	err := operator.CheckOLMInstalled()
	if err != nil {
		return err
	}

	err = operator.InstallSonataflowOperator()
	if err != nil {
		return err
	}
	fmt.Println("🎉 SonataFlow Operator successfully installed, wait for the operator to be ready.")

	return nil
}
