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
	"errors"
	"fmt"
	"os"
	"os/exec"
	"time"

	"github.com/kiegroup/kie-tools/kn-plugin-workflow/pkg/common"
	"github.com/ory/viper"
	"github.com/spf13/cobra"
)

func NewDeployCommand() *cobra.Command {
	var cmd = &cobra.Command{
		Use:   "deploy",
		Short: "Deploy a Quarkus workflow project",
		Long: `
NAME
	{{.Name}} deploy - Deploy a Quarkus project
	
SYNOPSIS
	{{.Name}} deploy
	
DESCRIPTION
	Deploys a Quarkus workflow project into a cluster.
	
	$ {{.Name}} deploy
	`,
		PreRunE: common.BindEnv("path"),
	}

	cmd.RunE = func(cmd *cobra.Command, args []string) error {
		return runDeploy(cmd, args)
	}

	cmd.Flags().StringP("path", "p", "./target/kubernetes", fmt.Sprintf("%s path to knative deploy files", cmd.Name()))

	cmd.SetHelpFunc(common.DefaultTemplatedHelp)

	return cmd
}

func runDeploy(cmd *cobra.Command, args []string) error {
	start := time.Now()

	cfg, err := runDeployConfig(cmd)
	if err != nil {
		return fmt.Errorf("create config error %w", err)
	}

	if _, err := exec.LookPath("kubectl"); err != nil {
		fmt.Println("ERROR: kubectl is required for deploy")
		fmt.Println("Download from https://kubectl.docs.kubernetes.io/installation/kubectl/")
		os.Exit(1)
	}

	createService := exec.Command("kubectl", "apply", "-f", fmt.Sprintf("%s/knative.yml", cfg.Path))
	if err := common.RunCommand(
		createService,
		cfg.Verbose,
		"creating knative service failed with error",
		getDeployFriendlyMessages(),
	); err != nil {
		fmt.Println("Check the full logs with the [-v | --verbose] flag")
		return fmt.Errorf("%w", err)
	}
	fmt.Println("✅ Knative service sucessufully created")

	if exists, err := checkIfKogitoJsonExists(cfg); exists && err == nil {
		deploy := exec.Command("kubectl", "apply", "-f", fmt.Sprintf("%s/kogito.yml", cfg.Path))
		if err := common.RunCommand(
			deploy,
			cfg.Verbose,
			"creating knative events binding failed with error",
			getDeployFriendlyMessages(),
		); err != nil {
			fmt.Println("Check the full logs with the [-v | --verbose] flag")
			return fmt.Errorf("%w", err)
		}
		fmt.Println("✅ Knative events binding sucessufully created")
	} else if err != nil {
		fmt.Println("Check the full logs with the [-v | --verbose] flag")
		return fmt.Errorf("%w", err)
	}

	finish := time.Since(start)
	fmt.Printf("🚀 Deploy took: %s \n", finish)
	return nil
}

type DeployConfig struct {
	// Deploy options
	Path string // service name

	// Plugin options
	Verbose bool
}

func runDeployConfig(cmd *cobra.Command) (cfg DeployConfig, err error) {
	cfg = DeployConfig{
		Path: viper.GetString("path"),

		Verbose: viper.GetBool("verbose"),
	}
	return
}

func checkIfKogitoJsonExists(cfg DeployConfig) (bool, error) {
	if _, err := os.Stat(fmt.Sprintf("%s/kogito.yml", cfg.Path)); err == nil {
		return true, nil
	} else if errors.Is(err, os.ErrNotExist) {
		return false, nil
	} else {
		return false, fmt.Errorf("%w", err)
	}
}

func getDeployFriendlyMessages() []string {
	return []string{
		" Deploying...",
		" Still deploying",
		" Still deploying",
		" Yes, still deploying",
		" Don't give up on me",
		" Still deploying",
		" This is taking a while",
	}
}
