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

package quarkus

import (
	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/common"
	"github.com/spf13/cobra"
)

const (
	Build  = "build"
	Create = "create"
	Deploy = "deploy"
)

func NewQuarkusCommand(dependenciesVersion common.DependenciesVersion) *cobra.Command {
	var cmd = &cobra.Command{
		Use:        "quarkus",
		Short:      "Manage Kogito Serverless Workflow projects",
		Long:       ``,
		Example:    ``,
		SuggestFor: []string{"quaks", "qarkus"},
		PreRunE:    common.BindEnv(""),
	}

	cmd.RunE = func(cmd *cobra.Command, args []string) error {
		return nil
	}

	cmd.AddCommand(NewBuildCommand())
	cmd.AddCommand(NewCreateCommand(dependenciesVersion))
	cmd.AddCommand(NewDeployCommand())

	cmd.SetHelpFunc(common.DefaultTemplatedHelp)
	return cmd
}
