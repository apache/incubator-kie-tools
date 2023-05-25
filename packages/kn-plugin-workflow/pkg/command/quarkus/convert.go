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

package quarkus

import (
	"fmt"
	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/common"
	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/metadata"
	"github.com/ory/viper"
	"github.com/spf13/cobra"
	"io"
	"os"
	"path/filepath"
	"strings"
)

func NewConvertCommand() *cobra.Command {
	var cmd = &cobra.Command{
		Use:   "convert",
		Short: "Convert a single-file Kogito Serverless Workflow project to a Quarkus project",
		Long: `
	Convert a Single Kogito Serverless Workflow project to a Quarkus Project.
		`,
		Example: `
	# Run the local directory
	{{.Name}} quarkus convert
		`,
		SuggestFor: []string{"convert-to-quarkus"},
		PreRunE:    common.BindEnv("extension", "quarkus-platform-group-id", "quarkus-version"),
	}

	cmd.RunE = func(cmd *cobra.Command, args []string) error {
		return runConvert()
	}

	quarkusDependencies := metadata.ResolveQuarkusDependencies()

	cmd.Flags().StringP("extension", "e", "", "On Quarkus projects, setup project custom Maven extensions, separated with a comma.")
	cmd.Flags().StringP("quarkus-platform-group-id", "G", quarkusDependencies.QuarkusPlatformGroupId, "On Quarkus projects, setup project group id.")
	cmd.Flags().StringP("quarkus-version", "V", quarkusDependencies.QuarkusVersion, "On Quarkus projects, setup the project version.")

	cmd.SetHelpFunc(common.DefaultTemplatedHelp)

	return cmd
}

func runConvert() error {
	if common.IsSWFProject() {
		return convert()
	} else if common.IsQuarkusSWFProject() {
		return fmt.Errorf("looks like you are already inside a Quarkus project, so no need to convert it")
	} else {
		return fmt.Errorf("cannot find Kogito Serverless Workflow project")
	}
}

func loadConvertCmdConfig() (cfg CreateQuarkusProjectConfig, err error) {
	quarkusPlatformGroupId := viper.GetString("quarkus-platform-group-id")
	quarkusVersion := viper.GetString("quarkus-version")

	cfg = CreateQuarkusProjectConfig{
		Extensions: fmt.Sprintf("%s,%s,%s,%s,%s,%s,%s,%s",
			metadata.KogitoQuarkusServerlessWorkflowExtension,
			metadata.KogitoAddonsQuarkusKnativeEventingExtension,
			metadata.QuarkusKubernetesExtension,
			metadata.QuarkusResteasyJacksonExtension,
			metadata.KogitoQuarkusServerlessWorkflowDevUi,
			metadata.KogitoAddonsQuarkusSourceFiles,
			metadata.SmallryeHealth,
			viper.GetString("extension"),
		),
		DependenciesVersion: metadata.DependenciesVersion{
			QuarkusPlatformGroupId: quarkusPlatformGroupId,
			QuarkusVersion:         quarkusVersion,
		},
	}
	if cfg.ProjectName == "" {
		dir, err := os.Getwd()
		if err != nil {
			dir = "project-name"
		}
		cfg.ProjectName = filepath.Base(dir)
	}
	return
}
func convert() error {

	cfg, err := loadConvertCmdConfig()

	if err != nil {
		return fmt.Errorf("initializing create config: %w", err)
	}

	if err := common.CheckJavaDependencies(); err != nil {
		return err
	}

	if err = runConvertProject(cfg); err != nil {
		return err
	}

	return nil
}

func runConvertProject(cfg CreateQuarkusProjectConfig) (err error) {

	fmt.Println("🔨 Creating a Quarkus Kogito Serverless Workflow project...")
	if err = CreateQuarkusProject(cfg); err != nil {
		fmt.Println("❌ Error creating Quarkus project", err)
		return err
	}

	fmt.Println("🔨 Moving Kogito Serverless Workflow files to Quarkus project...")
	rootFolder, err := os.Getwd()
	if err != nil {
		return err
	}

	if err := moveSWFFilesToQuarkusProject(cfg, rootFolder); err != nil {
		return err
	}

	generatedQuarkusProjectPath := rootFolder + "/" + cfg.ProjectName

	if err := copyDir(generatedQuarkusProjectPath, rootFolder); err != nil {
		fmt.Println("❌ Error migrating Quarkus project files", err)
		return err
	}
	if err := os.RemoveAll(generatedQuarkusProjectPath); err != nil {
		fmt.Println("❌ Error migrating Quarkus project", err)
		return err
	}

	fmt.Println("✅ Quarkus Kogito Serverless Workflow project successfully created")

	return nil
}

func moveSWFFilesToQuarkusProject(cfg CreateQuarkusProjectConfig, rootFolder string) error {
	targetFolder := filepath.Join(rootFolder, cfg.ProjectName+"/src/main/resources")

	// ensure target directory exists
	err := os.MkdirAll(targetFolder, os.ModePerm)
	if err != nil {
		return err
	}

	files, err := os.ReadDir(rootFolder)
	if err != nil {
		return err
	}

	for _, file := range files {
		// Move *.sw.yaml, *.sw.json, application.properties to target
		if strings.HasSuffix(file.Name(), ".sw.yaml") || strings.HasSuffix(file.Name(), ".sw.json") || file.Name() == "application.properties" {
			oldPath := filepath.Join(rootFolder, file.Name())
			newPath := filepath.Join(targetFolder, file.Name())
			if err := os.Rename(oldPath, newPath); err != nil {
				return fmt.Errorf("error moving file %s: %w", oldPath, err)
			}
		}

		// Move /specs directory to target
		if file.IsDir() && file.Name() == "specs" {
			oldPath := filepath.Join(rootFolder, file.Name())
			newPath := filepath.Join(targetFolder, file.Name())
			if err := os.Rename(oldPath, newPath); err != nil {
				return fmt.Errorf("error moving directory %s: %w", oldPath, err)
			}
		}
	}
	return nil
}

func copyFile(src, dst string) error {
	srcFile, err := os.Open(src)
	if err != nil {
		return err
	}
	defer srcFile.Close()

	dstFile, err := os.Create(dst)
	if err != nil {
		return err
	}
	defer dstFile.Close()

	_, err = io.Copy(dstFile, srcFile)
	if err != nil {
		return err
	}

	return nil
}

func copyDir(src, dst string) error {
	err := filepath.Walk(src, func(path string, info os.FileInfo, err error) error {
		if err != nil {
			return err
		}

		dstPath := filepath.Join(dst, path[len(src):])
		if info.IsDir() {
			err = os.MkdirAll(dstPath, info.Mode())
			if err != nil {
				return err
			}
		} else {
			err = copyFile(path, dstPath)
			if err != nil {
				return err
			}
		}

		return nil
	})

	return err
}
