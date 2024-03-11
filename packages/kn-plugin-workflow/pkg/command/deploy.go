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
	"path"

	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/metadata"

	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/common"
	"github.com/ory/viper"
	"github.com/spf13/cobra"
)

func NewDeployCommand() *cobra.Command {
	var cmd = &cobra.Command{
		Use:   "deploy",
		Short: "Deploy a SonataFlow project on Kubernetes via SonataFlow Operator",
		Long: `
	Deploy a SonataFlow project in Kubernetes via the SonataFlow Operator.
	By default, the deploy command will generate the Operator manifests and apply them to the cluster.
	You can also provide a custom manifest directory with the --custom-manifests-dir option.
	`,
		Example: `
	# Deploy the workflow project from the current directory's project. 
	# You must provide target namespace.
	{{.Name}} deploy --namespace <your_namespace>

	# Persist the generated Operator manifests on a given path and deploy the 
	# workflow from the current directory's project. 
	{{.Name}} deploy --custom-generated-manifests-dir=<full_directory_path>

	# Specify a custom manifest files directory.
	# This option *will not* automatically generate the manifest files, but will use the existing ones.
	{{.Name}} deploy --custom-manifests-dir=<full_directory_path>

	# Specify a custom subflows files directory. (default: ./subflows)
	{{.Name}} deploy --subflows-dir=<full_directory_path>

	# Specify a custom support specs directory. (default: ./specs)
	{{.Name}} deploy --specs-dir=<full_directory_path>

	# Specify a custom support schemas directory. (default: ./schemas)
	{{.Name}} deploy --schemas-dir=<full_directory_path>

		`,

		PreRunE:    common.BindEnv("namespace", "custom-manifests-dir", "custom-generated-manifests-dir", "specs-dir", "schemas-dir", "subflows-dir"),
		SuggestFor: []string{"delpoy", "deplyo"},
	}

	cmd.RunE = func(cmd *cobra.Command, args []string) error {
		return runDeployUndeploy(cmd, args)
	}

	cmd.Flags().StringP("namespace", "n", "", "Target namespace of your deployment.")
	cmd.Flags().StringP("custom-generated-manifests-dir", "c", "", "Target directory of your generated Operator manifests.")
	cmd.Flags().StringP("custom-manifests-dir", "m", "", "Specify a custom manifest files directory. This option will not automatically generate the manifest files, but will use the existing ones.")
	cmd.Flags().StringP("specs-dir", "p", "", "Specify a custom specs files directory")
	cmd.Flags().StringP("subflows-dir", "s", "", "Specify a custom subflows files directory")
	cmd.Flags().StringP("schemas-dir", "t", "", "Specify a custom schemas files directory")

	cmd.SetHelpFunc(common.DefaultTemplatedHelp)

	return cmd
}

func runDeployUndeploy(cmd *cobra.Command, args []string) error {

	cfg, err := runDeployCmdConfig(cmd)
	//temp dir cleanup
	defer func(cfg *DeployUndeployCmdConfig) {
		if cfg.TempDir != "" {
			if err := os.RemoveAll(cfg.TempDir); err != nil {
				fmt.Errorf("❌ ERROR: failed to remove temp dir: %v", err)
			}
		}
	}(&cfg)

	if err != nil {
		return fmt.Errorf("❌ ERROR: initializing deploy config: %w", err)
	}

	fmt.Println("🛠️️ Deploy a SonataFlow project on Kubernetes via the SonataFlow Operator...")

	if err := checkEnvironment(&cfg); err != nil {
		return fmt.Errorf("❌ ERROR: checking deploy environment: %w", err)
	}

	if len(cfg.CustomManifestsFileDir) == 0 {
		if err := generateManifests(&cfg); err != nil {
			return fmt.Errorf("❌ ERROR: generating deploy environment: %w", err)
		}
	} else {
		fmt.Printf("🛠 Using manifests located at %s\n", cfg.CustomManifestsFileDir)
	}

	if err = deploy(&cfg); err != nil {
		return fmt.Errorf("❌ ERROR: applying deploy: %w", err)
	}

	fmt.Printf("\n🎉 SonataFlow project successfully deployed.\n")

	return nil
}

func deploy(cfg *DeployUndeployCmdConfig) error {
	fmt.Printf("🛠 Deploying your SonataFlow project in namespace %s\n", cfg.NameSpace)

	manifestExtension := []string{metadata.YAMLExtension}

	manifestPath := cfg.CustomGeneratedManifestDir
	if len(cfg.CustomManifestsFileDir) != 0 {
		manifestPath = cfg.CustomManifestsFileDir
	}

	files, err := common.FindFilesWithExtensions(manifestPath, manifestExtension)
	if err != nil {
		return fmt.Errorf("❌ ERROR: failed to get manifest directory and files: %w", err)
	}
	for _, file := range files {
		if err = common.ExecuteKubectlApply(file, cfg.NameSpace); err != nil {
			return fmt.Errorf("❌ ERROR: failed to deploy manifest %s,  %w", file, err)
		}
		fmt.Printf(" - ✅ Manifest %s successfully deployed in namespace %s\n", path.Base(file), cfg.NameSpace)

	}
	return nil
}

func runDeployCmdConfig(cmd *cobra.Command) (cfg DeployUndeployCmdConfig, err error) {

	cfg = DeployUndeployCmdConfig{
		NameSpace:                  viper.GetString("namespace"),
		CustomManifestsFileDir:     viper.GetString("custom-manifests-dir"),
		CustomGeneratedManifestDir: viper.GetString("custom-generated-manifests-dir"),
		SpecsDir:                   viper.GetString("specs-dir"),
		SchemasDir:                 viper.GetString("schemas-dir"),
		SubflowsDir:                viper.GetString("subflows-dir"),
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
			return cfg, fmt.Errorf("❌ ERROR: failed to get default specs files folder: %w", err)
		}
	}

	if len(cfg.SchemasDir) == 0 {
		dir, err := os.Getwd()
		cfg.SchemasDir = dir + "/schemas"
		if err != nil {
			return cfg, fmt.Errorf("❌ ERROR: failed to get default schemas files folder: %w", err)
		}
	}

	dir, err := os.Getwd()
	cfg.DefaultDashboardsFolder = dir + "/" + metadata.DashboardsDefaultDirName
	if err != nil {
		return cfg, fmt.Errorf("❌ ERROR: failed to get default dashboards files folder: %w", err)
	}

	//setup manifest path
	if err := setupConfigManifestPath(&cfg); err != nil {
		return cfg, err
	}

	return cfg, nil
}
