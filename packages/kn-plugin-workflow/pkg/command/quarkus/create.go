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
	"bufio"
	"fmt"
	"github.com/beevik/etree"
	"os"
	"path"
	"strings"

	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/common"
	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/metadata"
	apiMetadata "github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/metadata"
	"github.com/ory/viper"
	"github.com/spf13/cobra"
)

func NewCreateCommand() *cobra.Command {
	var cmd = &cobra.Command{
		Use:   "create",
		Short: "Create a Quarkus SonataFlow project",
		Long: `
	Creates a Quarkus SonataFlow project in the current directory.
	It sets up a Quarkus project with minimal extensions to build a workflow
	project.
	The generated project has a "hello world" workflow.sw.json located on the
	./<project-name>/src/main/resources directory.
		`,
		Example: `
	# Create a project in the local directory
	# By default the project is named "new-project"
	{{.Name}} create

	# Create a project with an specific name
	{{.Name}} create --name myproject

	# Create a project with additional extensions
	# You can add multiple extensions by separating them with a comma
	{{.Name}} create --extensions kogito-addons-quarkus-persistence-postgresql,quarkus-core

	# Specify a profile to use for the new Quarkus project (default: dev). Available options: dev, preview, gitops.
	{{.Name}} create --profile=<profile_name>

	# Add persistence support to the project (default: false)
	{{.Name}} create --with-persistence

		`,
		SuggestFor: []string{"vreate", "creaet", "craete", "new"},
		PreRunE:    common.BindEnv("name", "extension", "quarkus-platform-group-id", "quarkus-version", "profile", "with-persistence"),
	}

	cmd.RunE = func(cmd *cobra.Command, args []string) error {
		return runCreate(cmd)
	}

	quarkusDependencies := metadata.ResolveQuarkusDependencies()

	cmd.Flags().StringP("name", "n", "new-project", "Project name created in the current directory.")
	cmd.Flags().StringP("extension", "e", "", "On Quarkus projects, setup project custom Maven extensions, separated with a comma.")
	cmd.Flags().StringP("quarkus-platform-group-id", "G", quarkusDependencies.QuarkusPlatformGroupId, "On Quarkus projects, setup project group id.")
	cmd.Flags().StringP("quarkus-version", "V", quarkusDependencies.QuarkusVersion, "On Quarkus projects, setup the project version.")
	cmd.Flags().StringP("profile", "p", "dev", "Specify a profile to use for the new Quarkus project (default: dev)")
	cmd.Flags().BoolP("with-persistence", "w", false, "Add persistence support to the project (default: false)")

	cmd.SetHelpFunc(common.DefaultTemplatedHelp)

	return cmd
}

type MavenDependency struct {
	GroupId    string
	ArtifactId string
}

var ExtensionPerProfile = map[string][]MavenDependency{
	apiMetadata.GitOpsProfile.String(): {
		MavenDependency{"org.kie","kie-addons-quarkus-persistence-jdbc"},
		MavenDependency{"io.quarkus","quarkus-agroal"},
		MavenDependency{"io.quarkus","quarkus-jdbc-postgresql"},
	},
}

var persistenceVariables = map[string]string {
	"maxYamlCodePoints": "35000000",
	"kogito.persistence.type": "jdbc",
	"kogito.persistence.proto.marshaller": "false",
	"quarkus.datasource.db-kind": "postgresql",
}

func runCreate(cmd *cobra.Command) error {
	cfg, err := runCreateCmdConfig(cmd)
	if err != nil {
		return fmt.Errorf("initializing create config: %w", err)
	}

	exists, err := common.CheckIfDirExists(cfg.ProjectName)
	if exists {
		return fmt.Errorf("directory with name \"%s\" already exists", cfg.ProjectName)
	}
	if err != nil {
		return fmt.Errorf("directory with name \"%s\" already exists: %w", cfg.ProjectName, err)
	}

	if err := common.CheckJavaDependencies(); err != nil {
		return err
	}

	fmt.Println("🛠️ Creating a Quarkus SonataFlow project...")
	if err = CreateQuarkusProject(cfg); err != nil {
		fmt.Println("❌ ERROR: creating Quarkus SonataFlow project", err)
		return err
	}

	workflowFilePath := fmt.Sprintf("./%s/src/main/resources/%s", cfg.ProjectName, metadata.WorkflowSwJson)
	common.CreateWorkflow(workflowFilePath, false)

	if err := processProfile(cfg); err != nil {
		return fmt.Errorf("adding profile extensions: %w", err)
	}

	if cfg.WithPersistence {
		if err := processPersistenceVariables(cfg); err != nil {
			return err
		}
	}

	fmt.Println("🎉 Quarkus SonataFlow project successfully created")
	return nil
}

func runCreateProject(cfg CreateQuarkusProjectConfig) (err error) {
	if err = common.CheckProjectName(cfg.ProjectName); err != nil {
		return err
	}
	exists, err := common.CheckIfDirExists(cfg.ProjectName)
	if err != nil || exists {
		return fmt.Errorf("directory with name \"%s\" already exists: %w", cfg.ProjectName, err)
	}

	create := common.ExecCommand(
		"mvn",
		fmt.Sprintf("%s:%s:%s:create", cfg.DependenciesVersion.QuarkusPlatformGroupId, metadata.QuarkusMavenPlugin, cfg.DependenciesVersion.QuarkusVersion),
		"-DprojectGroupId=org.acme",
		fmt.Sprintf("-DplatformVersion=%s", cfg.DependenciesVersion.QuarkusVersion),
		fmt.Sprintf("-DprojectArtifactId=%s", cfg.ProjectName),
		fmt.Sprintf("-Dextensions=%s", cfg.Extensions))

	fmt.Println("Creating a Quarkus SonataFlow project...")

	if err := common.RunCommand(
		create,
		"create",
	); err != nil {
		return err
	}

	if err := PostMavenCleanup(cfg); err != nil {
		return err
	}

	if err := processProfile(cfg); err != nil {
		return err
	}

	if cfg.WithPersistence {
		if err := processPersistenceVariables(cfg); err != nil {
			return err
		}
	}
	return
}

func runCreateCmdConfig(cmd *cobra.Command) (cfg CreateQuarkusProjectConfig, err error) {
	quarkusPlatformGroupId := viper.GetString("quarkus-platform-group-id")
	quarkusVersion := viper.GetString("quarkus-version")

	cfg = CreateQuarkusProjectConfig{
		ProjectName: viper.GetString("name"),
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
		WithPersistence: viper.GetBool("with-persistence"),
	}

	if cmd.Flags().Changed("profile") && len(cfg.Profile) == 0 {
		profile, _ := cmd.Flags().GetString("profile")
		if err := common.IsValidProfile(profile); err != nil {
			return cfg, err
		}
		cfg.Profile = profile
	}

	return
}

func processProfile(cfg CreateQuarkusProjectConfig) error {
	if cfg.Profile == apiMetadata.GitOpsProfile.String() {
		if err := addGitOpsProfileExtensions(cfg); err != nil {
			return fmt.Errorf("adding profile extensions: %w", err)
		}
	}
	return nil
}

func addGitOpsProfileExtensions(cfg CreateQuarkusProjectConfig) error {
	filename := path.Join(cfg.ProjectName, "pom.xml")
	doc := etree.NewDocument()
	err := doc.ReadFromFile(filename)
	if err != nil {
		return fmt.Errorf("error reading %s: %w", filename, err)
	}

	dependencies := doc.FindElement("//dependencies")
	if dependencies == nil {
		return fmt.Errorf("error parsing %s: %w", filename, err)
	}

	for _, dep := range ExtensionPerProfile[cfg.Profile] {
		dependencyElement := dependencies.CreateElement("dependency")
		dependencyElement.CreateElement("groupId").SetText(dep.GroupId)
		dependencyElement.CreateElement("artifactId").SetText(dep.ArtifactId)
	}

	doc.Indent(4)

	if err := doc.WriteToFile(filename); err != nil {
		return fmt.Errorf("error writing modified content to %s: %w", filename, err)
	}

	return nil
}

func processPersistenceVariables(cfg CreateQuarkusProjectConfig) error {
	err := addPersistenceVariablesToDockerFiles(cfg)
	if err != nil {
		return err
	}

	err = addPersistenceVariables(cfg)
	if err != nil {
		return err
	}
	return nil
}

func addPersistenceVariables(cfg CreateQuarkusProjectConfig) error {
	filename := path.Join(cfg.ProjectName, "pom.xml")
	doc := etree.NewDocument()
	err := doc.ReadFromFile(filename)
	if err != nil {
		return fmt.Errorf("error reading %s: %w", filename, err)
	}

	pluginElems := doc.FindElements("//plugins/plugin")
	for _, plugin := range pluginElems {
		if aid := plugin.FindElement("artifactId"); aid != nil {
			if aid.Text() == "quarkus-maven-plugin" {
				configElem := plugin.FindElement("configuration")
				if configElem == nil {
					configElem = plugin.CreateElement("configuration")
				}
				var systemPropertyVariables = configElem.CreateElement("environmentVariables");
				for key, val := range persistenceVariables {
					propertyElem := systemPropertyVariables.CreateElement(key)
					propertyElem.SetText(val)
				}
				break
			}
		}
	}

	doc.Indent(4)

	if err := doc.WriteToFile(filename); err != nil {
		return fmt.Errorf("error writing modified content to %s: %w", filename, err)
	}

	return nil
}

func addPersistenceVariablesToDockerFiles(cfg CreateQuarkusProjectConfig) error {
	extensions := []string{"jvm", "legacy-jar", "native", "native-micro"}

	for _, extension := range extensions {
		dockerfilePath := path.Join(cfg.ProjectName, "src/main/docker", "Dockerfile." + extension)
		if err := addPersistenceVariablesToDockerFile(dockerfilePath); err != nil {
			return err
		}
	}
	return nil
}

func addPersistenceVariablesToDockerFile(filename string) error {
	var text = GenerateEnvLine()

	file, err := os.Open(filename)
	defer file.Close()
	if err != nil {
		return fmt.Errorf("error opening %s: %w", filename, err)
	}

	appended := false
	scanner := bufio.NewScanner(file)

	var lines []string

	for scanner.Scan() {
		line := scanner.Text()
		if strings.HasPrefix(line, "EXPOSE") && !appended {
			lines = append(lines, text)
			appended = true
		}
		lines = append(lines, line)
	}

	if err := scanner.Err(); err != nil {
		return fmt.Errorf("error reading from %s: %w", filename, err)
	}

	file, err = os.OpenFile(filename, os.O_WRONLY|os.O_TRUNC, 0644)
	if err != nil {
		return fmt.Errorf("error opening %s for writing: %w", filename, err)
	}
	defer file.Close()

	writer := bufio.NewWriter(file)
	for _, line := range lines {
		_, err := writer.WriteString(line + "\n")
		if err != nil {
			return fmt.Errorf("error writing to %s: %w", filename, err)
		}
	}

	err = writer.Flush()
	if err != nil {
		return fmt.Errorf("error flushing to %s: %w", filename, err)
	}

	return nil
}

func GenerateEnvLine() string {
	var sb strings.Builder
	sb.WriteString("ENV ")
	first := true
	for key, val := range persistenceVariables {
		if !first {
			sb.WriteString(" \\\n    ")
		}
		sb.WriteString(fmt.Sprintf("%s=%s", key, val))
		first = false
	}
	sb.WriteString("\n")
	return sb.String()
}
