// Copyright 2020 Red Hat, Inc. and/or its affiliates
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package framework

import (
	"fmt"
	"io/ioutil"
	"strings"

	"github.com/kiegroup/kogito-cloud-operator/test/config"
)

const (
	mavenCommandName = "mvn"
)

// CreateMavenCommand methods initializes the basic data to run maven commands.
func CreateMavenCommand(directory string) MavenCommand {
	return &mavenCommandStruct{directory: directory}
}

// MavenCommand wraps information about the maven command to execute.
type MavenCommand interface {
	// WithLoggerContext method attaches a logger context to trace all the command logs when executing it.
	WithLoggerContext(loggerContext string) MavenCommand
	// Execute command and returns the outputs.
	Execute(targets ...string) (string, error)

	// SkipTests will skip testing automatically
	SkipTests() MavenCommand
	// UpdateArtifacts will force the update of local artifacts
	UpdateArtifacts() MavenCommand
	// Profiles sets the profiles to execute
	Profiles(profiles ...string) MavenCommand
}

type mavenCommandStruct struct {
	directory     string
	loggerContext string

	profiles        []string
	otherOptions    []string
	skipTests       bool
	updateArtifacts bool
}

func (mvnCmd *mavenCommandStruct) WithLoggerContext(loggerContext string) MavenCommand {
	mvnCmd.loggerContext = loggerContext
	return mvnCmd
}

func (mvnCmd *mavenCommandStruct) InDirectory(directory string) MavenCommand {
	mvnCmd.directory = directory
	return mvnCmd
}

func (mvnCmd *mavenCommandStruct) SkipTests() MavenCommand {
	mvnCmd.skipTests = true
	return mvnCmd
}

func (mvnCmd *mavenCommandStruct) UpdateArtifacts() MavenCommand {
	mvnCmd.updateArtifacts = true
	return mvnCmd
}

func (mvnCmd *mavenCommandStruct) Profiles(profiles ...string) MavenCommand {
	mvnCmd.profiles = append(mvnCmd.profiles, profiles...)
	return mvnCmd
}

func (mvnCmd *mavenCommandStruct) Execute(targets ...string) (string, error) {
	var args []string

	// Setup settings.xml
	if err := mvnCmd.setSettingsXML(); err != nil {
		return "", err
	}

	if mvnCmd.skipTests {
		mvnCmd.otherOptions = append(mvnCmd.otherOptions, "-DskipTests")
	}

	if mvnCmd.updateArtifacts {
		mvnCmd.otherOptions = append(mvnCmd.otherOptions, "-U")
	}

	args = append(args, targets...)
	if len(mvnCmd.profiles) > 0 {
		args = append(args, fmt.Sprintf("-P%s", strings.Join(mvnCmd.profiles, ",")))
	}
	if len(mvnCmd.otherOptions) > 0 {
		args = append(args, mvnCmd.otherOptions...)
	}

	cmd := CreateCommand(mavenCommandName, args...).InDirectory(mvnCmd.directory)

	// Set logger context if exists
	if len(mvnCmd.loggerContext) > 0 {
		cmd.WithLoggerContext(mvnCmd.loggerContext)
	}

	return cmd.Execute()
}

// setSettingsXML Creates settings.xml with content based on test configuration
func (mvnCmd *mavenCommandStruct) setSettingsXML() error {
	settingsContent := settingsMainContent

	// Setup custom Maven repository if defined
	if customMavenRepoURL := config.GetCustomMavenRepoURL(); len(customMavenRepoURL) > 0 {
		repository := fmt.Sprintf(repositoryDescription, customMavenRepoURL)
		pluginRepository := fmt.Sprintf(pluginRepositoryDescription, customMavenRepoURL)
		profilesContent := fmt.Sprintf(profilesXMLDescription, repository, pluginRepository)
		settingsContent = strings.ReplaceAll(settingsContent, "<!-- ### profiles ### -->", profilesContent)
	}

	// Setup Maven mirror if defined
	if mavenMirrorURL := config.GetMavenMirrorURL(); len(mavenMirrorURL) > 0 {
		mavenMirrorContent := fmt.Sprintf(mavenMirrorXMLDescription, mavenMirrorURL)
		settingsContent = strings.ReplaceAll(settingsContent, "<!-- ### mirror settings ### -->", mavenMirrorContent)
	}

	// Create settings.xml in directory
	if err := ioutil.WriteFile(fmt.Sprintf("%s/settings.xml", mvnCmd.directory), []byte(settingsContent), 0644); err != nil {
		return err
	}

	// Add option to get copied settings.xml file
	mvnCmd.otherOptions = append(mvnCmd.otherOptions, "-ssettings.xml")

	return nil
}

const (
	repositoryXMLDescription = `
<id>main-repository</id>
<name>main-repository</name>
<url>%s</url>
<layout>default</layout>
<releases>
	<enabled>true</enabled>
	<updatePolicy>always</updatePolicy>
</releases>
<snapshots>
	<enabled>true</enabled>
	<updatePolicy>always</updatePolicy>
</snapshots>`

	profilesXMLDescription = `
	<profiles>
		<profile>
			<id>default</id>
			<repositories>
				%s
			</repositories>
			<pluginRepositories>
				%s
			</pluginRepositories>
		</profile>
	</profiles>
	<activeProfiles>
		<activeProfile>default</activeProfile>
	</activeProfiles>`

	mavenMirrorXMLDescription = `
	<mirrors>
		<mirror>
			<id>mirror-central</id>
			<mirrorOf>external:*</mirrorOf>
			<url>%s</url>
		</mirror>
	</mirrors>`

	settingsMainContent = `
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
https://maven.apache.org/xsd/settings-1.0.0.xsd">

<!-- ### mirror settings ### -->

<!-- ### profiles ### -->
</settings>`
)

var (
	repositoryDescription       = fmt.Sprintf(`<repository>%s</repository><!-- ### configured repositories ### -->`, repositoryXMLDescription)
	pluginRepositoryDescription = fmt.Sprintf(`<pluginRepository>%s</pluginRepository><!-- ### configured plugin repositories ### -->`, repositoryXMLDescription)
)
