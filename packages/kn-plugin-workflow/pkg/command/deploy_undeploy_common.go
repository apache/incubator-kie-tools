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
	"github.com/kiegroup/kogito-serverless-operator/workflowproj"
	"os"
	"path/filepath"
)

type DeployUndeployCmdConfig struct {
	NameSpace                 string
	KubectlContext            string
	SonataFlowFile            string
	ManifestPath              string
	TempDir                   string
	ApplicationPropertiesPath string
	SupportFileFolder         string
	DefaultDashboardsFolder   string
	SupportFilesPath          []string
	DashboardsPath            []string
}

func checkEnvironment(cfg *DeployUndeployCmdConfig) error {
	fmt.Println("\n🔎 Checking your environment...")

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

	// Temporarily disabled due to lack of clarity of operator 'final name'
	// and also how to verify if operator is correctly installed.
	// For more info, please refer to:	KOGITO-9562 and KOGITO-9563
	//fmt.Println("🔎 Checking if the SonataFlow Operator is correctly installed...")
	//if err := common.CheckOperatorInstalled(); err != nil {
	//	return err
	//}

	return nil
}

func generateManifests(cfg *DeployUndeployCmdConfig) error {
	fmt.Println("\n🛠️  Generating your manifests...")
	fmt.Println("🔍 Looking for your SonataFlow files...")
	if file, err := findSonataFlowFile(); err != nil {
		return err
	} else {
		cfg.SonataFlowFile = file
	}
	fmt.Printf(" - ✅ SonataFlow file found: %s\n", cfg.SonataFlowFile)

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

	supportFileExtensions := []string{".json", ".yaml", ".yml"}

	files, err := common.FindFilesWithExtensions(cfg.SupportFileFolder, supportFileExtensions)
	if err != nil {
		return fmt.Errorf("❌ ERROR: failed to get supportFiles directory: %w", err)
	}
	cfg.SupportFilesPath = files
	for _, file := range cfg.SupportFilesPath {
		fmt.Printf(" - ✅ Support file found: %s\n", file)
	}

	fmt.Println("🔍 Looking for your dashboard files...")

	dashboardExtensions := []string{".yaml", ".yml"}

	files, err = common.FindFilesWithExtensions(cfg.DefaultDashboardsFolder, dashboardExtensions)
	if err != nil {
		return fmt.Errorf("❌ ERROR: failed to get dashboards directory: %w", err)
	}
	cfg.DashboardsPath = files
	for _, file := range cfg.DashboardsPath {
		fmt.Printf(" - ✅ Dashboard found: %s\n", file)
	}

	fmt.Println("🚚️ Generating your Kubernetes manifest files...")

	swfFile, err := common.MustGetFile(cfg.SonataFlowFile)
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

	for _, supportFile := range cfg.SupportFilesPath {
		specIO, err := common.MustGetFile(supportFile)
		if err != nil {
			return err
		}
		handler.AddResource(filepath.Base(supportFile), specIO)
	}

	for _, dashboardFile := range cfg.DashboardsPath {
		specIO, err := common.MustGetFile(dashboardFile)
		if err != nil {
			return err
		}
		handler.AddResourceAt(filepath.Base(dashboardFile), metadata.DashboardsDefaultDirName, specIO)
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

func findSonataFlowFile() (string, error) {
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
		return "", fmt.Errorf("❌ ERROR: multiple SonataFlow definition files found")
	}
}

func setupConfigManifestPath(cfg *DeployUndeployCmdConfig) error {

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
