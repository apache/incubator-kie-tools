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

package quarkus

import (
	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/common"
	"github.com/spf13/cobra"
)

func NewQuarkusCommand() *cobra.Command {
	var cmd = &cobra.Command{
		Use:        "quarkus",
		Short:      "Manage SonataFlow projects built in Quarkus",
		Long:       `Manage SonataFlow projects built in Quarkus`,
		SuggestFor: []string{"quaks", "qarkus"},
	}

	cmd.AddCommand(NewCreateCommand())
	cmd.AddCommand(NewBuildCommand())
	cmd.AddCommand(NewRunCommand())
	cmd.AddCommand(NewDeployCommand())
	cmd.AddCommand(NewConvertCommand())

	cmd.SetHelpFunc(common.DefaultTemplatedHelp)
	return cmd
}
