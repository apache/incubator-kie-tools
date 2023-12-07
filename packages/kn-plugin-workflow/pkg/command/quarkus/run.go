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
	"fmt"
	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/common"
	"github.com/ory/viper"
	"github.com/spf13/cobra"
	"sync"
	"time"
)

type RunCmdConfig struct {
	PortMapping string
	OpenDevUI   bool
}

func NewRunCommand() *cobra.Command {
	cmd := &cobra.Command{
		Use:   "run",
		Short: "Run a Quarkus SonataFlow project in development mode",
		Long: `
	Run a Quarkus SonataFlow project based on Quarkus in development mode.
 	 `,
		Example: `
	# Run the local directory
	{{.Name}} quarkus run
       # Run the local directory mapping a different host port to the running container port.
	{{.Name}} run --port 8081
	 `,
		SuggestFor: []string{"rnu", "start"}, //nolint:misspell
		PreRunE:    common.BindEnv("port", "open-dev-ui"),
	}

	cmd.RunE = func(cmd *cobra.Command, args []string) error {
		return run(cmd, args)
	}
	cmd.Flags().StringP("port", "p", "8080", "Maps a different port to Quarkus dev mode.")
	cmd.Flags().Bool("open-dev-ui", true, "If false, disables automatic browser launch of SonataFlow Dev UI")

	cmd.SetHelpFunc(common.DefaultTemplatedHelp)

	return cmd
}

func run(cmd *cobra.Command, args []string) error {
	cfg, err := runDevCmdConfig(cmd)
	if err != nil {
		return fmt.Errorf("initializing create config: %w", err)
	}

	if common.IsQuarkusSonataFlowProject() {
		return runQuarkusSWFProject(cfg)
	}

	return fmt.Errorf("cannot find Quarkus SonataFlow project")
}

func runDevCmdConfig(cmd *cobra.Command) (cfg RunCmdConfig, err error) {
	cfg = RunCmdConfig{
		PortMapping: viper.GetString("port"),
		OpenDevUI:   viper.GetBool("open-dev-ui"),
	}
	return cfg, nil
}

func runQuarkusSWFProject(cfg RunCmdConfig) error {

	if err := common.CheckJavaDependencies(); err != nil {
		return fmt.Errorf("error checking Java dependencies: %w", err)
	}

	return runQuarkusProjectDevMode(cfg)
}

func runQuarkusProjectDevMode(cfg RunCmdConfig) (err error) {
	fmt.Println("🛠️ Starting your Quarkus SonataFlow in dev mode...")
	create := common.ExecCommand(
		"mvn",
		"quarkus:dev",
		"-Dquarkus.http.port="+fmt.Sprintf("%s", cfg.PortMapping),
	)

	var wg sync.WaitGroup
	wg.Add(1)

	go func() {
		defer wg.Done()
		if err := common.RunCommand(create, "mvn quarkus:dev"); err != nil {
			fmt.Printf("❌ ERROR: running Quarkus project: %v", err)
			err = fmt.Errorf("Error running Quarkus project: %w", err)
		}
	}()

	readyCheckURL := fmt.Sprintf("http://localhost:%s/q/health/ready", cfg.PortMapping)
	pollInterval := 5 * time.Second
	common.ReadyCheck(readyCheckURL, pollInterval, cfg.PortMapping, cfg.OpenDevUI)

	wg.Wait()
	return err
}
