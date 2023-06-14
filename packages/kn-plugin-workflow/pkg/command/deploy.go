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

	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/common"
	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/metadata"
	"github.com/kiegroup/kogito-serverless-operator/workflowproj"
	"github.com/ory/viper"
	"github.com/spf13/cobra"
	"os"
	"path"
	"path/filepath"
)

type DeployCmdConfig struct {
	NameSpace                  string
	KubectlContext             string
	SWFFile                    string
	ManifestPath               string
	TempDir                    string
	ApplicationPropertiesPath  string
	SupportFileFolder          string
	SupportFilesPath           []string
	Delete                     bool
	DeployingDeletingCapsLabel string
	DeployedDeletingLabel      string
}

func NewDeployCommand() *cobra.Command {
	var cmd = &cobra.Command{
		Use:   "deploy",
		Short: "Deploy a Kogito Serverless Workflow file on Kubernetes via Kogito Serverless Workflow Operator",
		Long: `
	Deploy a Kogito Serverless Workflow file in Kubernetes via the Kogito Serverless Workflow Operator. 
	`,
		Example: `
	# Deploy the workflow from the current directory's project. 
	# as a Knative service. You must provide target namespace.
	{{.Name}} deploy --namespace <your_namespace>
	# Regenerate the Manifests and delete the target deployments (undeploy). 
	{{.Name}} deploy --delete <your_namespace>
	# Persist the generated Kubernetes manifests on a given path and deploy the 
	# workflow from the current directory's project. 
	{{.Name}} deploy --manifestPath=<full_directory_path>
    # Specify a custom support files folder. 
	{{.Name}} deploy --supportFiles=<full_directory_path>
		`,

		PreRunE:    common.BindEnv("namespace", "manifestPath", "delete", "supportFilesFolder"),
		SuggestFor: []string{"delpoy", "deplyo"},
	}

	cmd.RunE = func(cmd *cobra.Command, args []string) error {
		return runDeployUndeploy(cmd, args)
	}

	cmd.Flags().StringP("namespace", "n", "", "Target namespace of your deployment.")
	cmd.Flags().StringP("manifestPath", "c", "", "Target directory of your generated Kubernetes manifests.")
	cmd.Flags().StringP("supportFilesFolder", "s", "", "Specify a custom support files folder")
	cmd.Flags().BoolP("delete", "d", false, "Regenerate the Kubernetes manifests and delete the target deployments.")

	cmd.SetHelpFunc(common.DefaultTemplatedHelp)

	return cmd
}

func runDeployUndeploy(cmd *cobra.Command, args []string) error {

	cfg, err := runDeployCmdConfig(cmd)
	//temp dir cleanup
	defer func(cfg *DeployCmdConfig) {
		if cfg.TempDir != "" {
			if err := os.RemoveAll(cfg.TempDir); err != nil {
				fmt.Errorf("❌ ERROR: failed to remove temp dir: %v", err)
			}
		}
	}(&cfg)

	if err != nil {
		return fmt.Errorf("❌ ERROR: initializing deploy config: %w", err)
	}

	fmt.Printf("🛠️️  %s a Kogito Serverless Workflow file on Kubernetes via the Kogito Serverless Workflow Operator...\n", cfg.DeployingDeletingCapsLabel)

	if err := checkDeployEnvironment(&cfg); err != nil {
		return fmt.Errorf("❌ ERROR: checking deploy environment: %w", err)
	}

	if err := generateDeployEnvironment(&cfg); err != nil {
		return fmt.Errorf("❌ ERROR: generating deploy environment: %w", err)
	}

	if cfg.Delete {
		if err = deleteDeploy(&cfg); err != nil {
			return fmt.Errorf("❌ ERROR: deleting deployment: %w", err)
		}
	} else {
		if err = deploy(&cfg); err != nil {
			return fmt.Errorf("❌ ERROR: applying deploy: %w", err)
		}
	}

	fmt.Printf("\n🎉 Kogito Serverless Workflow project successfully %s\n", cfg.DeployedDeletingLabel)

	return nil
}

func deleteDeploy(cfg *DeployCmdConfig) error {
	fmt.Printf("🔨 Deleting your Kogito Serverless project in namespace %s\n", cfg.NameSpace)

	files, err := common.FindServiceFiles(cfg.ManifestPath)
	if err != nil {
		return fmt.Errorf("❌ ERROR: failed to get kubernetes manifest service files: %w", err)
	}
	for _, file := range files {
		if err = common.ExecuteKubectlDelete(file, cfg.NameSpace); err != nil {
			return fmt.Errorf("❌ ERROR: failed to %s manifest %s,  %w", cfg.DeployedDeletingLabel, file, err)
		}
		fmt.Printf(" - ✅ Manifest %s successfully %s in namespace %s\n", path.Base(file), cfg.DeployedDeletingLabel, cfg.NameSpace)

	}
	return nil
}

func deploy(cfg *DeployCmdConfig) error {
	fmt.Printf("🛠 Deploying your Kogito Serverless project in namespace %s\n", cfg.NameSpace)

	manifestExtension := []string{".yaml"}

	files, err := common.FindFilesWithExtensions(cfg.ManifestPath, manifestExtension)
	if err != nil {
		return fmt.Errorf("❌ ERROR: failed to get manifest directory and files: %w", err)
	}
	for _, file := range files {
		if err = common.ExecuteKubectlApply(file, cfg.NameSpace); err != nil {
			return fmt.Errorf("❌ ERROR: failed to %s manifest %s,  %w", cfg.DeployedDeletingLabel, file, err)
		}
		fmt.Printf(" - ✅ Manifest %s successfully %s in namespace %s\n", path.Base(file), cfg.DeployedDeletingLabel, cfg.NameSpace)

	}
	return nil
}

func checkDeployEnvironment(cfg *DeployCmdConfig) error {
	fmt.Println("\n🔎 Checking your deployment environment...")

	if err := common.CheckKubectl(); err != nil {
		return err
	}

	if ctx, err := common.CheckKubectlContext(); err != nil {
		return err
	} else {
		cfg.KubectlContext = ctx
	}

	//setup namespace
	if len(cfg.NameSpace) == 0 {
		if defaultNamespace, err := common.GetKubectlNamespace(); err == nil {
			cfg.NameSpace = defaultNamespace
		} else {
			return err
		}
	}

	fmt.Println("🔎  Checking if the Kogito Serverless Workflow Operator is correctly installed...")
	if err := common.CheckOperatorInstalled(); err != nil {
		return err
	}

	return nil
}

func generateDeployEnvironment(cfg *DeployCmdConfig) error {
	fmt.Println("\n🛠️ Generating your deployment environment...")
	fmt.Println("🔍 Looking for your Serverless Workflow File...")
	if file, err := findServerlessWorkflowFile(); err != nil {
		return err
	} else {
		cfg.SWFFile = file
	}
	fmt.Printf(" - ✅ Serverless workflow file found: %s\n", cfg.SWFFile)

	fmt.Println("🔍 Looking for your configuration support files...")

	dir, err := os.Getwd()
	if err != nil {
		return fmt.Errorf("❌ ERROR: failed to get current directory: %w", err)
	}

	applicationPropertiesPath := findApplicationPropertiesPath(dir)
	if applicationPropertiesPath != "" {
		cfg.ApplicationPropertiesPath = applicationPropertiesPath
		fmt.Printf(" - ✅ Properties file found: %s\n", cfg.ApplicationPropertiesPath)
	}

	extensions := []string{".json", ".yaml"}

	files, err := common.FindFilesWithExtensions(cfg.SupportFileFolder, extensions)
	if err != nil {
		return fmt.Errorf("❌ ERROR: failed to get current directory: %w", err)
	}
	cfg.SupportFilesPath = files
	for _, file := range cfg.SupportFilesPath {
		fmt.Printf(" - ✅ Support file found: %s\n", file)
	}

	fmt.Println("🚚️ Generating your Kubernetes manifest files..")

	swfFile, err := common.MustGetFile(cfg.SWFFile)
	if err != nil {
		return err
	}

	handler := workflowproj.New(cfg.NameSpace).WithWorkflow(swfFile)
	if cfg.ApplicationPropertiesPath != "" {
		appIO, err := common.MustGetFile(cfg.ApplicationPropertiesPath)
		if err != nil {
			return err
		}
		handler.WithAppProperties(appIO)
	}

	for _, supportfile := range cfg.SupportFilesPath {
		specIO, err := common.MustGetFile(supportfile)
		if err != nil {
			return err
		}
		handler.AddResource(filepath.Base(supportfile), specIO)
	}

	_, err = handler.AsObjects()
	if err != nil {
		return err
	}

	err = handler.SaveAsKubernetesManifests(cfg.ManifestPath)
	if err != nil {
		return err
	}

	return nil
}

func findApplicationPropertiesPath(directoryPath string) string {
	filePath := filepath.Join(directoryPath, metadata.ApplicationProperties)

	fileInfo, err := os.Stat(filePath)
	if err != nil || fileInfo.IsDir() {
		return ""
	}

	return filePath
}

func findServerlessWorkflowFile() (string, error) {
	extensions := []string{metadata.YAMLExtension, metadata.YAMLExtensionShort, metadata.JSONExtension}

	dir, err := os.Getwd()
	if err != nil {
		return "", fmt.Errorf("❌ ERROR: failed to get current directory: %w", err)
	}

	var matchingFiles []string
	for _, ext := range extensions {
		files, _ := filepath.Glob(filepath.Join(dir, "*."+ext))
		matchingFiles = append(matchingFiles, files...)
	}

	switch len(matchingFiles) {
	case 0:
		return "", fmt.Errorf("❌ ERROR: no matching files found")
	case 1:
		return matchingFiles[0], nil
	default:
		return "", fmt.Errorf("❌ ERROR: multiple serverless workflow definition files found")
	}
}

func runDeployCmdConfig(cmd *cobra.Command) (cfg DeployCmdConfig, err error) {

	cfg = DeployCmdConfig{
		NameSpace:         viper.GetString("namespace"),
		Delete:            viper.GetBool("delete"),
		SupportFileFolder: viper.GetString("supportFilesFolder"),
		ManifestPath:      viper.GetString("manifestPath"),
	}
	cfg.DeployingDeletingCapsLabel = "Deploying"
	cfg.DeployedDeletingLabel = "deployed"
	if cfg.Delete {
		cfg.DeployingDeletingCapsLabel = "Deleting (undeploying)"
		cfg.DeployedDeletingLabel = "deleted (undeployed)"
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

func setupConfigManifestPath(cfg *DeployCmdConfig) error {

	if len(cfg.ManifestPath) == 0 {
		tempDir, err := os.MkdirTemp("", "manifests")
		if err != nil {
			return fmt.Errorf("❌ ERROR: failed to create temporary directory: %w", err)
		}
		cfg.ManifestPath = tempDir
		cfg.TempDir = tempDir
	} else {
		_, err := os.Stat(cfg.ManifestPath)
		if err != nil {
			return fmt.Errorf("❌ ERROR: cannot find or open directory %s : %w", cfg.ManifestPath, err)
		}
	}
	return nil
}
