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
	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/specs"
	apimetadata "github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/metadata"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/workflowproj"
)

type DeployUndeployCmdConfig struct {
	EmptyNameSpace                   bool
	NameSpace                        string
	KubectlContext                   string
	SonataFlowFile                   string
	CustomGeneratedManifestDir       string
	TempDir                          string
	ApplicationPropertiesPath        string
	ApplicationSecretPropertiesPath  string
	SubflowsDir                      string
	SpecsDir                         string
	SchemasDir                       string
	CustomManifestsFileDir           string
	DefaultDashboardsFolder          string
	Profile                          string
	Image                            string
	SchemasFilesPath                 []string
	SpecsFilesPath                   map[string]string
	SubFlowsFilesPath                []string
	DashboardsPath                   []string
	Minify                           bool
	Wait					   		 bool
}

func checkEnvironment(cfg *DeployUndeployCmdConfig) error {
	fmt.Println("\n🔎 Checking your environment...")

	if ctx, err := common.CheckContext(); err != nil {
		return err
	} else {
		cfg.KubectlContext = ctx
	}

	//setup namespace
	if len(cfg.NameSpace) == 0 {
		if defaultNamespace, err := common.GetCurrentNamespace(); err == nil {
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
	if file, err := common.FindSonataFlowFile(common.WorkflowExtensionsType); err != nil {
		return err
	} else {
		cfg.SonataFlowFile = file
	}
	fmt.Printf(" - ✅ SonataFlow file found: %s\n", cfg.SonataFlowFile)

	fmt.Println("🔍 Looking for your SonataFlow sub flows...")
	files, err := common.FindFilesWithExtensions(cfg.SubflowsDir, common.WorkflowExtensionsType)
	if err != nil {
		return fmt.Errorf("❌ ERROR: failed to get subflows directory: %w", err)
	}
	cfg.SubFlowsFilesPath = files
	for _, file := range cfg.SubFlowsFilesPath {
		fmt.Printf(" - ✅ SonataFlow subflows found: %s\n", file)
	}

	fmt.Println("🔍 Looking for your workflow support files...")

	dir, err := os.Getwd()
	if err != nil {
		return fmt.Errorf("❌ ERROR: failed to get current directory: %w", err)
	}

	fmt.Println("🔍 Looking for properties files...")

	applicationPropertiesPath := findApplicationPropertiesPath(dir)
	if applicationPropertiesPath != "" {
		cfg.ApplicationPropertiesPath = applicationPropertiesPath
		fmt.Printf(" - ✅ Properties file found: %s\n", cfg.ApplicationPropertiesPath)
	}

	applicationSecretPropertiesPath := findApplicationSecretPropertiesPath(dir)
	if applicationSecretPropertiesPath != "" {
		cfg.ApplicationSecretPropertiesPath = applicationSecretPropertiesPath
		fmt.Printf(" - ✅ Secret Properties file found: %s\n", cfg.ApplicationSecretPropertiesPath)
	}

	supportFileExtensions := []string{metadata.JSONExtension, metadata.YAMLExtension, metadata.YMLExtension}

	fmt.Println("🔍 Looking for specs files...")
	if cfg.Minify {
		minifiedfiles, err := specs.NewMinifier(&specs.OpenApiMinifierOpts{
			SpecsDir:    cfg.SpecsDir,
			SubflowsDir: cfg.SubflowsDir,
		}).Minify()
		if err != nil {
			return fmt.Errorf("❌ ERROR: failed to minify specs files: %w", err)
		}
		cfg.SpecsFilesPath = minifiedfiles
	} else {
		files, err = common.FindFilesWithExtensions(cfg.SpecsDir, supportFileExtensions)
		if err != nil {
			return fmt.Errorf("❌ ERROR: failed to get supportFiles directory: %w", err)
		}
		cfg.SpecsFilesPath = map[string]string{}
		for _, file := range files {
			cfg.SpecsFilesPath[file] = file
			fmt.Printf(" - ✅ Specs file found: %s\n", file)
		}
	}

	fmt.Println("🔍 Looking for schema files...")
	files, err = common.FindFilesWithExtensions(cfg.SchemasDir, supportFileExtensions)
	if err != nil {
		return fmt.Errorf("❌ ERROR: failed to get supportFiles directory: %w", err)
	}
	cfg.SchemasFilesPath = files
	for _, file := range cfg.SchemasFilesPath {
		fmt.Printf(" - ✅ Schemas file found: %s\n", file)
	}

	fmt.Println("🔍 Looking for your dashboard files...")

	dashboardExtensions := []string{metadata.YAMLExtension, metadata.YMLExtension}

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

	if cfg.ApplicationSecretPropertiesPath != "" {
		appIO, err := common.MustGetFile(cfg.ApplicationSecretPropertiesPath)
		if err != nil {
			return err
		}
		handler.WithSecretProperties(appIO)
	}

	for _, subflow := range cfg.SubFlowsFilesPath {
		specIO, err := common.MustGetFile(subflow)
		if err != nil {
			return err
		}
		handler.AddResourceAt(filepath.Base(subflow), filepath.Base(cfg.SubflowsDir), specIO)
	}

	for _, supportFile := range cfg.SchemasFilesPath {
		specIO, err := common.MustGetFile(supportFile)
		if err != nil {
			return err
		}
		handler.AddResourceAt(filepath.Base(supportFile), filepath.Base(cfg.SchemasDir), specIO)
	}

	for supportFile, minifiedFile := range cfg.SpecsFilesPath {
		specIO, err := common.MustGetFile(minifiedFile)
		if err != nil {
			return err
		}
		handler.AddResourceAt(filepath.Base(supportFile), filepath.Base(cfg.SpecsDir), specIO)
	}

	for _, dashboardFile := range cfg.DashboardsPath {
		specIO, err := common.MustGetFile(dashboardFile)
		if err != nil {
			return err
		}
		handler.AddResourceAt(filepath.Base(dashboardFile), metadata.DashboardsDefaultDirName, specIO)
	}

	if len(cfg.Profile) > 0 {
		handler.Profile(apimetadata.ProfileType(cfg.Profile))
	}

	_, err = handler.AsObjects()
	if err != nil {
		return err
	}

	if cfg.Image != "" {
		handler.Image(cfg.Image)
	}

	err = handler.SaveAsKubernetesManifests(cfg.CustomGeneratedManifestDir)
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

func findApplicationSecretPropertiesPath(directoryPath string) string {
	filePath := filepath.Join(directoryPath, metadata.ApplicationSecretProperties)

	fileInfo, err := os.Stat(filePath)
	if err != nil || fileInfo.IsDir() {
		return ""
	}

	return filePath
}

func setupConfigManifestPath(cfg *DeployUndeployCmdConfig) error {

	if len(cfg.CustomGeneratedManifestDir) == 0 {
		tempDir, err := os.MkdirTemp("", "manifests")
		if err != nil {
			return fmt.Errorf("❌ ERROR: failed to create temporary directory: %w", err)
		}
		cfg.CustomGeneratedManifestDir = tempDir
		cfg.TempDir = tempDir
	} else {
		_, err := os.Stat(cfg.CustomGeneratedManifestDir)
		if err != nil {
			return fmt.Errorf("❌ ERROR: cannot find or open directory %s : %w", cfg.CustomGeneratedManifestDir, err)
		}
	}
	return nil
}
