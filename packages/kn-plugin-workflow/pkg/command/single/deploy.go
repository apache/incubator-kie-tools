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

package single

import (
	"fmt"
	"os"
	"os/exec"
	"time"

	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/common"
	"github.com/ory/viper"
	"github.com/spf13/cobra"
)

type DeployCmdConfig struct {
	// Deploy options
	Path string // service name
}

func NewDeployCommand() *cobra.Command {
	var cmd = &cobra.Command{
		Use:        "deploy",
		Short:      "Deploy a workflow project",
		Long:       ``,
		Example:    ``,
		SuggestFor: []string{"delpoy", "deplyo"},
		PreRunE:    common.BindEnv("path"),
	}

	cmd.RunE = func(cmd *cobra.Command, args []string) error {
		return runDeploy(cmd, args)
	}

	cmd.Flags().StringP("path", "p", "./kubernetes", fmt.Sprintf("%s path to knative deployment files", cmd.Name()))

	cmd.SetHelpFunc(common.DefaultTemplatedHelp)

	return cmd
}

func runDeploy(cmd *cobra.Command, args []string) error {
	start := time.Now()

	cfg, err := runDeployCmdConfig(cmd)
	if err != nil {
		return fmt.Errorf("initializing deploy config: %w", err)
	}

	if _, err := exec.LookPath("kubectl"); err != nil {
		fmt.Println("ERROR: kubectl is required for deploy")
		fmt.Println("Download from https://kubectl.docs.kubernetes.io/installation/kubectl/")
		os.Exit(1)
	}

	createService := common.ExecCommand("kubectl", "apply", "-f", fmt.Sprintf("%s/knative.yml", cfg.Path))
	if err := common.RunCommand(
		createService,
		"deploy",
	); err != nil {
		return err
	}
	fmt.Println("✅ Knative service sucessufully created")

	// Check if kogito.yml file exists
	if exists, err := checkIfKogitoFileExists(cfg); exists && err == nil {
		deploy := common.ExecCommand("kubectl", "apply", "-f", fmt.Sprintf("%s/kogito.yml", cfg.Path))
		if err := common.RunCommand(
			deploy,
			"deploy",
		); err != nil {
			return err
		}
		fmt.Println("✅ Knative Eventing bindings successfully created")
	}

	finish := time.Since(start)
	fmt.Printf("🚀 Deploy took: %s \n", finish)
	return nil
}

func runDeployCmdConfig(cmd *cobra.Command) (cfg DeployCmdConfig, err error) {
	cfg = DeployCmdConfig{
		Path: viper.GetString("path"),
	}
	return
}

func checkIfKogitoFileExists(cfg DeployCmdConfig) (bool, error) {
	if _, err := os.Stat(fmt.Sprintf("%s/kogito.yml", cfg.Path)); err == nil {
		return true, nil
	} else {
		return false, err
	}
}
