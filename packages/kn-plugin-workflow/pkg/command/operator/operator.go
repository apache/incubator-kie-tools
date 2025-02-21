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
	"github.com/spf13/cobra"
)

func NewOperatorCommand() *cobra.Command {
	var cmd = &cobra.Command{
		Use:   "operator",
		Short: "Manage the SonataFlow Operator, which is responsible for deploying SonataFlow projects on Kubernetes.",
		Long: `
	Install or uninstall the SonataFlow Operator, which is responsible for deploying SonataFlow projects on Kubernetes.
	`,
		Example: `
	# Install the SonataFlow Operator.
	{{.Name}} operator install

	# Uninstall the SonataFlow Operator.
	{{.Name}} operator uninstall

	# Check the status of the SonataFlow Operator.
	{{.Name}} operator status
		`,
	}

	cmd.AddCommand(NewInstallOperatorCommand())
	cmd.AddCommand(NewUnInstallOperatorCommand())
	cmd.AddCommand(NewStatusOperatorCommand())


	cmd.SetHelpFunc(common.DefaultTemplatedHelp)

	return cmd
}
