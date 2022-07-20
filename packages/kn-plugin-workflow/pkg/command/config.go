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
	"github.com/kiegroup/kie-tools/kn-plugin-workflow/pkg/common"
	"github.com/ory/viper"
	"github.com/spf13/cobra"
)

type ConfigCmdConfig struct {
	// Dependencies versions
	QuarkusVersion string
	KogitoVersion  string

	// Plugin options
	Verbose bool
}

func NewConfigCommand(dependenciesVersion common.DependenciesVersion) *cobra.Command {
	var cmd = &cobra.Command{
		Use:   "config",
		Short: "Updates the config workflow file in the current directory",
		Long: `
	 Updates a config workflow file in the current directory.
		 `,
		Example: `
	 # Update the Quarkus Version
	 {{.Name}} config --quarkus-version 2.10.0.Final
 
	 # Update the Kogito Version
	 {{.Name}} config --kogito-version 1.24.0.Final
		 `,
		SuggestFor: []string{"confgi", "cofngi", "cofnig"},
		PreRunE:    common.BindEnv("verbose", "quarkus-version", "kogito-version"),
	}

	cmd.RunE = func(cmd *cobra.Command, args []string) error {
		return runConfig(cmd, args, dependenciesVersion)
	}

	cmd.Flags().String("quarkus-version", "", "Quarkus version to be set in the config file.")
	cmd.Flags().String("kogito-version", "", "Kogito version to be set in the config file.")
	cmd.SetHelpFunc(common.DefaultTemplatedHelp)

	return cmd
}

func runConfig(cmd *cobra.Command, args []string, dependenciesVersion common.DependenciesVersion) error {
	quarkusVersion, kogitoVersion, err := ReadConfig(dependenciesVersion)
	if err != nil {
		return err
	}

	newQuarkusVersion := viper.GetString("quarkus-version")
	if len(newQuarkusVersion) > 0 {
		quarkusVersion = newQuarkusVersion
	}

	newKogitoVersion := viper.GetString("kogito-version")
	if len(newKogitoVersion) > 0 {
		kogitoVersion = newKogitoVersion
	}

	err = UpdateConfig(quarkusVersion, kogitoVersion)
	if err != nil {
		return err
	}
	return nil
}
