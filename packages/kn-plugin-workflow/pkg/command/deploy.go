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
		PreRunE: common.BindEnv("image", "name", "openshift"),
	}

	cmd.RunE = func(cmd *cobra.Command, args []string) error {
		return runDeploy(cmd, args)
	}

	cmd.Flags().StringP("image", "i", "quarkus/new-project", fmt.Sprintf("%s image URL", cmd.Name()))
	cmd.Flags().StringP("name", "n", "new-project", fmt.Sprintf("%s service name", cmd.Name()))
	cmd.Flags().BoolP("openshift", "o", false, fmt.Sprintf("%s deploy to a openshift cluster", cmd.Name()))

	return cmd
}

func runDeploy(cmd *cobra.Command, args []string) error {
	start := time.Now()

	cfg, err := runDeployConfig(cmd)
	if err != nil {
		return fmt.Errorf("create config error %w", err)
	}

	// use stdin to pass config.yaml ?
	// login?
	// use knative or file
	// file path?
	var deploy *exec.Cmd
	if cfg.Openshift {
		deploy = exec.Command("kn", "service", "create", cfg.ServiceName, fmt.Sprintf("--image=%s", cfg.Image), "--port=8080")
	} else {
		deploy = exec.Command("kn", "service", "create", cfg.ServiceName, fmt.Sprintf("--image=%s", cfg.Image), "--port=8080")
	}

	if err := common.RunCommand(deploy, cfg.Verbose, "deploy command failed with error"); err != nil {
		return fmt.Errorf("%w", err)
	}

	finish := time.Since(start)
	fmt.Printf("🚀 Deploy took: %s \n", finish)
	return nil
}

type DeployConfig struct {
	// Deploy options
	Image       string // registry image
	ServiceName string // service name

	// Deploy strategy
	Openshift bool // registry to be uploaded

	// Plugin options
	Verbose bool
}

func runDeployConfig(cmd *cobra.Command) (cfg DeployConfig, err error) {
	cfg = DeployConfig{
		Image:       viper.GetString("registry"),
		ServiceName: viper.GetString("group"),

		Openshift: viper.GetBool("openshift"),

		Verbose: viper.GetBool("verbose"),
	}
	return
}
