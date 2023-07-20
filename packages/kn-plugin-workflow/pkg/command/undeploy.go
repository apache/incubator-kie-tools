/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/common"
	"github.com/ory/viper"
	"github.com/spf13/cobra"
	"os"
	"path"
)

func NewUndeployCommand() *cobra.Command {
	var cmd = &cobra.Command{
		Use:   "undeploy",
		Short: "Undeploy a SonataFlow project on Kubernetes via SonataFlow Operator",
		Long: `
	Undeploy a SonataFlow project in Kubernetes via the SonataFlow Operator. 
	`,
		Example: `
	# Undeploy the workflow project from the current directory's project. 
	# You must provide target namespace.
	{{.Name}} undeploy --namespace <your_namespace>
	# Persist the generated Kubernetes manifests on a given path and deploy the 
	# workflow from the current directory's project. 
	{{.Name}} undeploy --manifestPath=<full_directory_path>
		`,

		PreRunE:    common.BindEnv("namespace", "manifestPath"),
		SuggestFor: []string{"undelpoy", "undeplyo"},
	}

	cmd.RunE = func(cmd *cobra.Command, args []string) error {
		return runUndeploy(cmd, args)
	}

	cmd.Flags().StringP("namespace", "n", "", "Target namespace of your deployment.")
	cmd.Flags().StringP("manifestPath", "c", "", "Target directory of your generated Kubernetes manifests.")

	cmd.SetHelpFunc(common.DefaultTemplatedHelp)

	return cmd
}

func runUndeploy(cmd *cobra.Command, args []string) error {

	cfg, err := runUndeployCmdConfig(cmd)
	//temp dir cleanup
	defer func(cfg *DeployUndeployCmdConfig) {
		if cfg.TempDir != "" {
			if err := os.RemoveAll(cfg.TempDir); err != nil {
				fmt.Errorf("❌ ERROR: failed to remove temp dir: %v", err)
			}
		}
	}(&cfg)

	if err != nil {
		return fmt.Errorf("❌ ERROR: initializing undeploy config: %w", err)
	}

	fmt.Println("🛠️️ Undeploy a SonataFlow project on Kubernetes via the SonataFlow Operator...")

	if err := checkEnvironment(&cfg); err != nil {
		return fmt.Errorf("❌ ERROR: checking undeploy environment: %w", err)
	}

	if err := generateManifests(&cfg); err != nil {
		return fmt.Errorf("❌ ERROR: generating undeploy manifests: %w", err)
	}

	if err = undeploy(&cfg); err != nil {
		return fmt.Errorf("❌ ERROR: undeploying: %w", err)
	}

	fmt.Println("\n🎉 SonataFlow project successfully undeployed.")

	return nil
}

func undeploy(cfg *DeployUndeployCmdConfig) error {
	fmt.Printf("🔨 Undeploying your SonataFlow project in namespace %s\n", cfg.NameSpace)

	files, err := common.FindServiceFiles(cfg.ManifestPath)
	if err != nil {
		return fmt.Errorf("❌ ERROR: failed to get kubernetes manifest service files: %w", err)
	}
	for _, file := range files {
		if err = common.ExecuteKubectlDelete(file, cfg.NameSpace); err != nil {
			return fmt.Errorf("❌ ERROR: failed to undeploy manifest %s,  %w", file, err)
		}
		fmt.Printf(" - ✅ Manifest %s successfully undeployed in namespace %s\n", path.Base(file), cfg.NameSpace)

	}
	return nil
}

func runUndeployCmdConfig(cmd *cobra.Command) (cfg DeployUndeployCmdConfig, err error) {

	cfg = DeployUndeployCmdConfig{
		NameSpace:         viper.GetString("namespace"),
		SupportFileFolder: viper.GetString("supportFilesFolder"),
		ManifestPath:      viper.GetString("manifestPath"),
	}

	if len(cfg.SupportFileFolder) == 0 {
		dir, err := os.Getwd()
		cfg.SupportFileFolder = dir + "/specs"
		if err != nil {
			return cfg, fmt.Errorf("❌ ERROR: failed to get current directory: %w", err)
		}
	}
	//setup manifest path
	if err := setupConfigManifestPath(&cfg); err != nil {
		return cfg, err
	}

	return cfg, nil
}
