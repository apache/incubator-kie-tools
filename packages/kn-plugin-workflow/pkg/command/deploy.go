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
		PreRunE: common.BindEnv("file"),
	}

	cmd.RunE = func(cmd *cobra.Command, args []string) error {
		return runDeploy(cmd, args)
	}

	cmd.Flags().StringP("file", "f", "config.yaml", fmt.Sprintf("%s config deploy file", cmd.Name()))

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

	deploy := exec.Command("kubectl", "apply", "-f", cfg.FilePath)
	if err := common.RunCommand(
		deploy,
		cfg.Verbose,
		"deploy command failed with error",
		getDeployFriendlyMessages(),
	); err != nil {
		return fmt.Errorf("%w", err)
	}

	finish := time.Since(start)
	fmt.Printf("🚀 Deploy took: %s \n", finish)
	return nil
}

type DeployConfig struct {
	// Deploy options
	FilePath string // service name

	// Plugin options
	Verbose bool
}

func runDeployConfig(cmd *cobra.Command) (cfg DeployConfig, err error) {
	cfg = DeployConfig{
		FilePath: viper.GetString("file"),

		Verbose: viper.GetBool("verbose"),
	}
	return
}

func getDeployFriendlyMessages() []string {
	return []string{
		" Still deploying",
		" Still deploying",
		" Yes, still deploying",
		" Don't give up on me",
		" Still deploying",
		" This is taking a while",
	}
}
