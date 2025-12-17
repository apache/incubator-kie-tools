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
	"path/filepath"

	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/common"
	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/metadata"
	"github.com/ory/viper"
	"github.com/spf13/cobra"
)

func NewGenManifest() *cobra.Command {
	cmd := &cobra.Command{
		Use:   "gen-manifest",
		Short: "GenerateOperator manifests",
		Long: `
	Generate a list of Operator manifests for a SonataFlow project.
	By default, the manifests are generated in the ./manifests directory,
	but they can be configured by --custom-generated-manifest-dir flag.
		 `,
		Example: `
	# Persist the generated Operator manifests on a default path (default ./manifests)
	{{.Name}} gen-manifest

	# Specify a custom target namespace. (default: kubeclt current namespace; --namespace "" to don't set namespace on your manifest)
	{{.Name}} deploy --namespace <your_namespace>

	# Skip namespace creation
	{{.Name}} gen-manifest --skip-namespace

	# Persist the generated Operator manifests on a specific custom path
	{{.Name}} gen-manifest --custom-generated-manifests-dir=<full_directory_path>

	# Specify a custom subflows files directory. (default: ./subflows)
	{{.Name}} gen-manifest --subflows-dir=<full_directory_path>

	# Specify a custom support specs directory. (default: ./specs)
	{{.Name}} gen-manifest --specs-dir=<full_directory_path>

	# Specify a custom support schemas directory. (default: ./schemas)
	{{.Name}} gen-manifest --schemas-dir=<full_directory_path>

	# Specify a profile to use for the deployment. (default: dev)
	{{.Name}} gen-manifest --profile=<profile_name>

	# Specify a custom image to use for the deployment.
	{{.Name}} gen-manifest --image=<image_name>

			 `,
		PreRunE:    common.BindEnv("namespace", "custom-generated-manifests-dir", "specs-dir", "schemas-dir", "subflows-dir"),
		SuggestFor: []string{"gen-manifests", "generate-manifest"}, //nolint:misspell
	}

	cmd.RunE = func(cmd *cobra.Command, args []string) error {
		return generateManifestsCmd(cmd, args)
	}

	cmd.Flags().StringP("namespace", "n", "", "Target namespace of your deployment. (default: kubeclt current namespace; \"\" to don't set namespace on your manifest)")
	cmd.Flags().BoolP("skip-namespace", "k", false, "Skip namespace creation")
	cmd.Flags().StringP("custom-generated-manifests-dir", "c", "", "Target directory of your generated Operator manifests.")
	cmd.Flags().StringP("specs-dir", "p", "", "Specify a custom specs files directory")
	cmd.Flags().StringP("subflows-dir", "s", "", "Specify a custom subflows files directory")
	cmd.Flags().StringP("schemas-dir", "t", "", "Specify a custom schemas files directory")
	cmd.Flags().StringP("profile", "f", "dev", "Specify a profile to use for the deployment")
	cmd.Flags().StringP("image", "i", "", "Specify a custom image to use for the deployment")

	cmd.SetHelpFunc(common.DefaultTemplatedHelp)

	return cmd
}

func generateManifestsCmd(cmd *cobra.Command, args []string) error {
	cfg, err := runGenManifestCmdConfig(cmd)

	if err != nil {
		return fmt.Errorf("❌ ERROR: initializing deploy config: %w", err)
	}

	fmt.Println("🛠️️ Generating a list of Operator manifests for a SonataFlow project...")
	fmt.Printf("📂 Manifests will be generated in %s\n", cfg.CustomGeneratedManifestDir)

	if err := setupEnvironment(&cfg); err != nil {
		return fmt.Errorf("❌ ERROR: setup environment: %w", err)
	}

	if err := generateManifests(&cfg); err != nil {
		return fmt.Errorf("❌ ERROR: generating manifests: %w", err)
	}

	fmt.Printf("\n🎉 SonataFlow Operator manifests  successfully generated.\n")

	return nil
}

func runGenManifestCmdConfig(cmd *cobra.Command) (cfg DeployUndeployCmdConfig, err error) {

	cfg = DeployUndeployCmdConfig{
		NameSpace:                  viper.GetString("namespace"),
		SpecsDir:                   viper.GetString("specs-dir"),
		SchemasDir:                 viper.GetString("schemas-dir"),
		SubflowsDir:                viper.GetString("subflows-dir"),
		CustomGeneratedManifestDir: viper.GetString("custom-generated-manifests-dir"),
		Profile:                    viper.GetString("profile"),
		Image:                      viper.GetString("image"),
	}

	if cmd.Flags().Changed("namespace") && len(cfg.NameSpace) == 0 {
		// distinguish between a user intentionally setting an empty value
		// and not providing the flag at all
		cfg.EmptyNameSpace = true
	}

	if skipNamespace, _ := cmd.Flags().GetBool("skip-namespace"); skipNamespace {
		cfg.NameSpace = ""
		cfg.EmptyNameSpace = true
	}

	if cmd.Flags().Changed("profile") && len(cfg.Profile) == 0 {
		profile, _ := cmd.Flags().GetString("profile")
		if err := common.IsValidProfile(profile); err != nil {
			return cfg, err
		}
		cfg.Profile = profile
	}

	if cmd.Flags().Changed("image") {
		image, _ := cmd.Flags().GetString("image")
		if image != "" {
			cfg.Image = image
		}
	}

	if len(cfg.SubflowsDir) == 0 {
		dir, err := os.Getwd()
		cfg.SubflowsDir = dir + "/subflows"
		if err != nil {
			return cfg, fmt.Errorf("❌ ERROR: failed to get default subflows workflow files folder: %w", err)
		}
	}

	if len(cfg.SpecsDir) == 0 {
		dir, err := os.Getwd()
		cfg.SpecsDir = dir + "/specs"
		if err != nil {
			return cfg, fmt.Errorf("❌ ERROR: failed to get default support specs files folder: %w", err)
		}
	}

	if len(cfg.SchemasDir) == 0 {
		dir, err := os.Getwd()
		cfg.SchemasDir = dir + "/schemas"
		if err != nil {
			return cfg, fmt.Errorf("❌ ERROR: failed to get default support schemas files folder: %w", err)
		}
	}

	dir, err := os.Getwd()
	cfg.DefaultDashboardsFolder = dir + "/" + metadata.DashboardsDefaultDirName
	if err != nil {
		return cfg, fmt.Errorf("❌ ERROR: failed to get default dashboards files folder: %w", err)
	}

	//setup manifest path
	manifestDir, err := resolveManifestDir(cfg.CustomGeneratedManifestDir)
	if err != nil {
		return cfg, fmt.Errorf("❌ ERROR: failed to get manifest directory: %w", err)
	}
	cfg.CustomGeneratedManifestDir = manifestDir

	return cfg, nil
}

func setupEnvironment(cfg *DeployUndeployCmdConfig) error {
	fmt.Println("\n🔎 Checking your environment...")

	//setup namespace
	if len(cfg.NameSpace) == 0 && !cfg.EmptyNameSpace {
		if defaultNamespace, err := common.GetCurrentNamespace(); err == nil {
			cfg.NameSpace = defaultNamespace
			fmt.Printf(" - ✅  resolved namespace: %s\n", cfg.NameSpace)
		} else {
			cfg.NameSpace = "default"
			fmt.Printf(" - ✅  resolved namespace (default): %s\n", cfg.NameSpace)
		}
	} else if cfg.EmptyNameSpace {
		fmt.Printf(" -  ❗ empty namespace manifest (you will have to setup one later) \n")
	} else {
		fmt.Printf(" - ✅  resolved namespace: %s\n", cfg.NameSpace)
	}

	return nil
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
