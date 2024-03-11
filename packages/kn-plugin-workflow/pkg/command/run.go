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

package command

import (
	"fmt"
	"os"
	"sync"
	"time"

	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/common"
	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/metadata"
	"github.com/ory/viper"
	"github.com/spf13/cobra"
)

type RunCmdConfig struct {
	PortMapping string
	OpenDevUI   bool
}

func NewRunCommand() *cobra.Command {
	cmd := &cobra.Command{
		Use:   "run",
		Short: "Run a SonataFlow project in development mode",
		Long: `
	 Run a SonataFlow project in development mode.

	 By default, it runs over ` + metadata.DevModeImage + ` on Docker.
	 Alternatively, you can run the same image with Podman.
		
		 `,
		Example: `
	# Run the workflow inside the current local directory
	{{.Name}} run

	 # Run the current local directory mapping a different host port to the running container port.
	{{.Name}} run --port 8081

 	# Disable automatic browser launch of SonataFlow  Dev UI 
	{{.Name}} run --open-dev-ui=false
		 `,
		SuggestFor: []string{"rnu", "start"}, //nolint:misspell
		PreRunE:    common.BindEnv("port", "open-dev-ui"),
	}

	cmd.RunE = func(cmd *cobra.Command, args []string) error {
		return run()
	}

	cmd.Flags().StringP("port", "p", "8080", "Maps a different host port to the running container port.")
	cmd.Flags().Bool("open-dev-ui", true, "Disable automatic browser launch of SonataFlow  Dev UI")
	cmd.SetHelpFunc(common.DefaultTemplatedHelp)

	return cmd
}

func run() error {
	cfg, err := runDevCmdConfig()
	if err != nil {
		return fmt.Errorf("initializing create config: %w", err)
	}

	if common.IsSonataFlowProject() {
		if err := runSWFProject(cfg); err != nil {
			return err
		}
		return nil
	} else if common.IsQuarkusSonataFlowProject() {
		return fmt.Errorf("looks like you are inside a Quarkus project. If that is the case, you should run it with \"quarkus run\" command")
	} else {
		return fmt.Errorf("cannot find SonataFlow project")
	}
}

func runDevCmdConfig() (cfg RunCmdConfig, err error) {
	cfg = RunCmdConfig{
		PortMapping: viper.GetString("port"),
		OpenDevUI:   viper.GetBool("open-dev-ui"),
	}
	return
}

func runSWFProject(cfg RunCmdConfig) error {

	if errPodman := common.CheckPodman(); errPodman == nil {
		if err := runSWFProjectDevMode(common.Podman, cfg); err != nil {
			return err
		}
	} else if errDocker := common.CheckDocker(); errDocker == nil {
		if err := runSWFProjectDevMode(common.Docker, cfg); err != nil {
			return err
		}
	} else {
		return fmt.Errorf("there is no docker or podman available")
	}
	return nil
}

func runSWFProjectDevMode(containerTool string, cfg RunCmdConfig) (err error) {
	fmt.Println("⏳ Starting your SonataFlow project in dev mode...")
	path, err := os.Getwd()
	if err != nil {
		fmt.Errorf("❌ Error running SonataFlow project: %w", err)
	}

	common.GracefullyStopTheContainerWhenInterrupted(containerTool)

	var wg sync.WaitGroup
	wg.Add(1)

	go func() {
		defer wg.Done()
		if err := common.RunContainerCommand(containerTool, cfg.PortMapping, path); err != nil {
			fmt.Errorf("❌ Error running SonataFlow project: %w", err)
		}
	}()

	readyCheckURL := fmt.Sprintf("http://localhost:%s/q/health/ready", cfg.PortMapping)
	pollInterval := 5 * time.Second
	common.ReadyCheck(readyCheckURL, pollInterval, cfg.PortMapping, cfg.OpenDevUI)

	wg.Wait()
	return err
}
