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

package root

import (
	"fmt"
	"os"

	"github.com/kiegroup/kie-tools/kn-plugin-workflow/pkg/command/create"
	"github.com/ory/viper"
	"github.com/spf13/cobra"
)

func NewRootCommand() *cobra.Command {
	var cmd = &cobra.Command{
		Use:   "kn-workflow",
		Short: "Serverless Workflow",
		Long:  "Manage Quarkus workflow projects",
	}

	viper.AutomaticEnv()           // read in environment variables for WORKFLOW_<flag>
	viper.SetEnvPrefix("workflow") // ensure thay all have the prefix

	cmd.PersistentFlags().BoolP("verbose", "v", false, "Print verbose logs")
	if err := viper.BindPFlag("verbose", cmd.PersistentFlags().Lookup("verbose")); err != nil {
		fmt.Fprintf(os.Stderr, "error binding flag: %v\n", err)
	}

	cmd.AddCommand(create.NewCreateCommand())

	return cmd
}
