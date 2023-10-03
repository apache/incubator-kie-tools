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
	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/common"
	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/metadata"
	"github.com/ory/viper"
	"github.com/spf13/cobra"
	"os"
	"path/filepath"
)

func NewGenManifest() *cobra.Command {
	cmd := &cobra.Command{
		Use:   "gen-manifest",
		Short: "GenerateOperator manifests",
		Long: `
	 Generate a list of Operator manifests for a SonataFlow project.
	by default, the manifests are generated in the ./manifests directory,
	but they can be configured by --manifestPath flag.
		 `,
		Example: `
	 # Shows the plugin version
	 {{.Name}} gen-manifest
	# Persist the generated Operator manifests on a specific path 
	{{.Name}} gen-manifest --manifestPath=<full_directory_path>
	# Specify a custom support files folder. 
	{{.Name}} gen-manifest --supportFiles=<full_directory_path>
			 `,
		PreRunE:    common.BindEnv("namespace", "manifestPath", "supportFilesFolder"),
		SuggestFor: []string{"gen-manifests", "generate-manifest"}, //nolint:misspell
	}

	cmd.Run = func(cmd *cobra.Command, args []string) {
		generateManifestsCmd(cmd, args)
	}
	cmd.Flags().StringP("namespace", "n", "", "Target namespace of your deployment.")
	cmd.Flags().StringP("manifestPath", "c", "", "Target directory of your generated Operator manifests.")
	cmd.Flags().StringP("supportFilesFolder", "s", "", "Specify a custom support files folder")

	cmd.SetHelpFunc(common.DefaultTemplatedHelp)

	return cmd
}

func generateManifestsCmd(cmd *cobra.Command, args []string) error {
	cfg, err := runGenManifestCmdConfig(cmd)

	if err != nil {
		return fmt.Errorf("❌ ERROR: initializing deploy config: %w", err)
	}

	fmt.Println("🛠️️ Generating a list of Operator manifests for a SonataFlow project...")
	fmt.Printf("📂 Manifests will be generated in %s\n", cfg.ManifestPath)

	if err := checkEnvironment(&cfg); err != nil {
		return fmt.Errorf("❌ ERROR: checking environment: %w", err)
	}

	if err := generateManifests(&cfg); err != nil {
		return fmt.Errorf("❌ ERROR: generating manifests: %w", err)
	}

	fmt.Printf("\n🎉 SonataFlow Operator manifests  successfully generated.\n")

	return nil
}

func runGenManifestCmdConfig(cmd *cobra.Command) (cfg DeployUndeployCmdConfig, err error) {

	cfg = DeployUndeployCmdConfig{
		NameSpace:         viper.GetString("namespace"),
		SupportFileFolder: viper.GetString("supportFilesFolder"),
		ManifestPath:      viper.GetString("manifestPath"),
	}

	if len(cfg.SupportFileFolder) == 0 {
		dir, err := os.Getwd()
		cfg.SupportFileFolder = dir + "/specs"
		if err != nil {
			return cfg, fmt.Errorf("❌ ERROR: failed to get default support files folder: %w", err)
		}
	}
	dir, err := os.Getwd()
	cfg.DefaultDashboardsFolder = dir + "/" + metadata.DashboardsDefaultDirName
	if err != nil {
		return cfg, fmt.Errorf("❌ ERROR: failed to get default dashboards files folder: %w", err)
	}

	//setup manifest path
	manifestDir, err := resolveManifestDir(cfg.ManifestPath)
	if err != nil {
		return cfg, fmt.Errorf("❌ ERROR: failed to get manifest directory: %w", err)
	}
	cfg.ManifestPath = manifestDir

	return cfg, nil
}

func resolveManifestDir(folderName string) (string, error) {
	if folderName == "" {
		folderName = "manifests"
	}

	if _, err := os.Stat(folderName); os.IsNotExist(err) {
		err = os.Mkdir(folderName, 0755)
		if err != nil {
			return "", err
		}
	}

	absPath, err := filepath.Abs(folderName)
	if err != nil {
		return "", err
	}

	return absPath, nil
}
