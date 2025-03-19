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

func NewUnInstallOperatorCommand() *cobra.Command {
	var cmd = &cobra.Command{
		Use:   "uninstall",
		Short: "Uninstall the SonataFlow Operator.",
		Long: `
	Uninstall the SonataFlow Operator.
	`,
		Example: `
	# Uninstall the SonataFlow Operator.
	{{.Name}} operator uninstall --namespace <your_operator_namespace>
		`,
		PreRunE:    common.BindEnv("namespace"),
	}

	cmd.RunE = func(cmd *cobra.Command, args []string) error {
		return runUnInstallOperatorCommand(cmd, args)
	}
	cmd.Flags().StringP("namespace", "n", "", "Target namespace of your Operator deployment.")

	cmd.SetHelpFunc(common.DefaultTemplatedHelp)

	return cmd
}

func runUnInstallOperatorCommand(cmd *cobra.Command, args []string) error {
	fmt.Println("🚀 Uninstalling the SonataFlow Operator...")

	namespace := viper.GetString("namespace")

	operator := common.NewOperatorManager(namespace)

	err := operator.RemoveCR()
	if err != nil {
		return fmt.Errorf("failed to remove CR: %v", err)
	}

	err = operator.RemoveCSV()
	if err != nil {
		return fmt.Errorf("failed to remove CSV: %v", err)
	}

	err = operator.RemoveCRD()
	if err != nil {
		return fmt.Errorf("failed to remove CRD: %v", err)
	}

	err = operator.RemoveSubscription()
	if err != nil {
		return fmt.Errorf("failed to remove subscription: %v", err)
	}

	fmt.Println("🎉 SonataFlow Operator successfully uninstalled.")

	return nil
}
