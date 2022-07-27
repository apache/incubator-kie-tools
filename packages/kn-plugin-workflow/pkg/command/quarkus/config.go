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
	"fmt"
	"os/exec"

	"github.com/kiegroup/kie-tools/kn-plugin-workflow/pkg/common"
	"github.com/ory/viper"
	"github.com/spf13/cobra"
)

type ConfigCmdConfig struct {
	Apply bool

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
	 Updates the config workflow file in the current directory.
		 `,
		Example: `
	 # Update the Quarkus Version
	 {{.Name}} config --quarkus-version 2.10.0.Final
 
	 # Update the Kogito Version
	 {{.Name}} config --kogito-version 1.24.0.Final
		 `,
		SuggestFor: []string{"confgi", "cofngi", "cofnig"},
		PreRunE:    common.BindEnv("verbose", "apply", "quarkus-version", "kogito-version"),
	}

	cmd.RunE = func(cmd *cobra.Command, args []string) error {
		return runConfig(cmd, args, dependenciesVersion)
	}

	cmd.Flags().BoolP("apply", "a", false, "Apply the config file changes.")
	cmd.Flags().String("quarkus-version", "", "Quarkus version to be set in the config file.")
	cmd.Flags().String("kogito-version", "", "Kogito version to be set in the config file.")
	cmd.SetHelpFunc(common.DefaultTemplatedHelp)

	return cmd
}

// Updates the workflow.config.yml and the pom.xml default extensions
func runConfig(cmd *cobra.Command, args []string, dependenciesVersion common.DependenciesVersion) error {
	cfg, err := runConfigCmdConfig(cmd)
	if err != nil {
		return fmt.Errorf("initializing config: %w", err)
	}

	if err := common.CheckJavaDependencies(); err != nil {
		return err
	}

	quarkusVersion, kogitoVersion, err := ReadConfig(dependenciesVersion)
	if err != nil {
		fmt.Printf("ERROR: Ensure that you're in the project directory")
		return err
	}

	if len(cfg.QuarkusVersion) > 0 {
		quarkusVersion = cfg.QuarkusVersion
	}

	if len(cfg.KogitoVersion) > 0 {
		kogitoVersion = cfg.KogitoVersion
	}

	if err := UpdateConfig(quarkusVersion, kogitoVersion); err != nil {
		return err
	}

	updateProjectVersion := exec.Command("mvn",
		"versions:set-property",
		"-Dproperty=quarkus.platform.version",
		fmt.Sprintf("-DnewVersion=%s", quarkusVersion))
	if err := common.RunCommand(
		updateProjectVersion,
		cfg.Verbose,
		"config",
		common.GetFriendlyMessages("updating Quarkus extension"),
	); err != nil {
		fmt.Println("ERROR: Updating project version.")
		fmt.Println("Check the full logs with the -v | --verbose option")
		return err
	}

	if err := common.UpdateProjectExtensionsVersions(
		cfg.Verbose,
		common.GetFriendlyMessages("updating project version"),
		common.GetVersionedExtension(common.QUARKUS_KUBERNETES_EXTENSION, quarkusVersion),
		common.GetVersionedExtension(common.QUARKUS_RESTEASY_REACTIVE_JACKSON_EXTENSION, quarkusVersion),
		common.GetVersionedExtension(common.KOGITO_QUARKUS_SERVERLESS_WORKFLOW_EXTENSION, kogitoVersion),
		common.GetVersionedExtension(common.KOGITO_ADDONS_QUARKUS_KNATIVE_EVENTING_EXTENSION, kogitoVersion),
	); err != nil {
		return err
	}

	fmt.Println("✅ Quarkus extensions were successfully updated in the pom.xml")
	return nil
}

func runConfigCmdConfig(cmd *cobra.Command) (cfg ConfigCmdConfig, err error) {
	cfg = ConfigCmdConfig{
		Apply: viper.GetBool("apply"),

		QuarkusVersion: viper.GetString("quarkus-version"),
		KogitoVersion:  viper.GetString("kogito-version"),

		Verbose: viper.GetBool("verbose"),
	}
	return
}
