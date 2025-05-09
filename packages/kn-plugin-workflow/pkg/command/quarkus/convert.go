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
	fsutils "github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/common/fs"
	"io"
	"os"
	"path/filepath"

	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/common"
	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/metadata"
	"github.com/ory/viper"
	"github.com/spf13/cobra"
)

func NewConvertCommand() *cobra.Command {
	var cmd = &cobra.Command{
		Use:   "convert",
		Short: "Convert a SonataFlow project to a Quarkus SonataFlow project",
		Long: `
	Convert a SonataFlow project to a Quarkus SonataFlow Project.
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
	if common.IsSonataFlowProject() {
		return convert()
	} else if common.IsQuarkusSonataFlowProject() {
		return fmt.Errorf("looks like you are already inside a Quarkus project, so no need to convert it")
	} else {
		return fmt.Errorf("cannot find SonataFlow project")
	}
}

func loadConvertCmdConfig() (cfg CreateQuarkusProjectConfig, err error) {
	quarkusPlatformGroupId := viper.GetString("quarkus-platform-group-id")
	quarkusVersion := viper.GetString("quarkus-version")

	cfg = CreateQuarkusProjectConfig{
		Extensions: fmt.Sprintf("%s,%s,%s,%s",
			metadata.QuarkusKubernetesExtension,
			metadata.QuarkusResteasyJacksonExtension,
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

	fmt.Println("🔨 Creating a Quarkus SonataFlow project...")
	if err = CreateQuarkusProject(cfg); err != nil {
		fmt.Println("❌ Error creating Quarkus project", err)
		return err
	}

	fmt.Println("🔨 Moving SonataFlow files to Quarkus SonataFlow project...")
	rootFolder, err := os.Getwd()
	if err != nil {
		return err
	}

	if err := moveSWFFilesToQuarkusProject(cfg, rootFolder); err != nil {
		return err
	}

	generatedQuarkusProjectPath := rootFolder + "/" + cfg.ProjectName

	if err := copyDir(generatedQuarkusProjectPath, rootFolder); err != nil {
		fmt.Println("❌ Error migrating Quarkus SonataFlow project files", err)
		return err
	}
	if err := os.RemoveAll(generatedQuarkusProjectPath); err != nil {
		fmt.Println("❌ Error migrating Quarkus SonataFlow project", err)
		return err
	}

	fmt.Println("✅ Quarkus SonataFlow project successfully created")

	return nil
}

func moveSWFFilesToQuarkusProject(cfg CreateQuarkusProjectConfig, rootFolder string) error {
	targetFolder := filepath.Join(rootFolder, cfg.ProjectName+"/src/main/resources")

	err := os.MkdirAll(targetFolder, os.ModePerm)
	if err != nil {
		return err
	}

	items, err := os.ReadDir(rootFolder)
	if err != nil {
		return err
	}

	for _, item := range items {
		if item.IsDir() && item.Name() == cfg.ProjectName {
			continue
		}

		info, err := item.Info()
		if err != nil {
			return err
		}
		if fsutils.IsHidden(info, item.Name()) && info.IsDir() {
			continue
		}

		srcPath := filepath.Join(rootFolder, item.Name())
		dstPath := filepath.Join(targetFolder, item.Name())

		if err := os.Rename(srcPath, dstPath); err != nil {
			return fmt.Errorf("error moving %s: %w", srcPath, err)
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

		if fsutils.IsHidden(info, path) && info.IsDir() {
			return nil
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
