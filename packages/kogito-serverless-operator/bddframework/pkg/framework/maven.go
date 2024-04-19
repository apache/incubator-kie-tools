/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package framework

import (
	"fmt"
	"io/ioutil"
	"strings"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/config"
)

const (
	mavenCommandName = "mvn"

	defaultRemoteMavenRepository = "https://repository.apache.org/content/groups/public/"
	mainRepositoryID             = "main-repository"
	stagingRepositoryID          = "staging-repository"
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
	// Options adds additional command line options for the Maven command
	Options(options ...string) MavenCommand
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

func (mvnCmd *mavenCommandStruct) Options(options ...string) MavenCommand {
	mvnCmd.otherOptions = append(mvnCmd.otherOptions, options...)
	return mvnCmd
}

func (mvnCmd *mavenCommandStruct) Execute(targets ...string) (string, error) {
	var args []string

	// Setup settings.xml
	if err := mvnCmd.setSettingsXML(); err != nil {
		return "", err
	}

	if !config.IsDisableMavenNativeBuildInContainer() {
		mvnCmd.otherOptions = append(mvnCmd.otherOptions, "-Dquarkus.native.container-build=true", fmt.Sprintf("-Dquarkus.native.container-runtime=%s", config.GetContainerEngine()))
		if len(config.GetNativeBuilderImage()) > 0 {
			mvnCmd.otherOptions = append(mvnCmd.otherOptions, fmt.Sprintf("-Dquarkus.native.builder-image=%s", config.GetNativeBuilderImage()))
		}
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

	// Maven download artifacts configuration
	// Same configuration as in https://github.com/kiegroup/kogito-pipelines/blob/main/.ci/pull-request-config.yaml#L6
	args = append(args, "-Dhttp.keepAlive=false", "-Dmaven.wagon.http.pool=false", "-Dmaven.wagon.httpconnectionManager.ttlSeconds=120", "-Dmaven.wagon.http.retryHandler.count=3")

	cmd := CreateCommand(mavenCommandName, args...).InDirectory(mvnCmd.directory)

	// Set logger context if exists
	if len(mvnCmd.loggerContext) > 0 {
		cmd.WithLoggerContext(mvnCmd.loggerContext)
	}

	return cmd.Execute()
}

// setSettingsXML Creates settings.xml with content based on test configuration
func (mvnCmd *mavenCommandStruct) setSettingsXML() error {
	settings := &mavenSettings{}

	// Setup Maven mirror if defined
	if mavenMirrorURL := config.GetMavenMirrorURL(); len(mavenMirrorURL) > 0 {
		settings.SetMirrorURL(mavenMirrorURL)
		mvnCmd.otherOptions = append(mvnCmd.otherOptions, "-Denforcer.skip")
	}

	// Setup custom Maven repository if defined
	if customMavenRepoURL := config.GetCustomMavenRepoURL(); len(customMavenRepoURL) > 0 {
		mvnCmd.otherOptions = append(mvnCmd.otherOptions, "-Denforcer.skip")

		if !config.IsCustomMavenRepoReplaceDefault() {
			settings.AddRepository(mainRepositoryID, defaultRemoteMavenRepository, false)
		}
		settings.AddRepository(stagingRepositoryID, customMavenRepoURL, true)
	} else {
		settings.AddRepository(mainRepositoryID, defaultRemoteMavenRepository, false)
	}

	// Create settings.xml in directory
	if err := ioutil.WriteFile(fmt.Sprintf("%s/settings.xml", mvnCmd.directory), []byte(settings.Generate()), 0644); err != nil {
		return err
	}

	// Add option to get copied settings.xml file
	mvnCmd.otherOptions = append(mvnCmd.otherOptions, "-ssettings.xml")

	return nil
}

type mavenRepository struct {
	ID             string
	URL            string
	ignoreInMirror bool
}

type mavenSettings struct {
	mirrorURL string

	repositories []mavenRepository
}

func (settings *mavenSettings) SetMirrorURL(mirrorURL string) *mavenSettings {
	settings.mirrorURL = mirrorURL
	return settings
}

func (settings *mavenSettings) AddRepository(repoID, repoURL string, ignoreInMirror bool) *mavenSettings {
	settings.repositories = append(settings.repositories, mavenRepository{
		ID:             repoID,
		URL:            repoURL,
		ignoreInMirror: ignoreInMirror,
	})
	return settings
}

func (settings *mavenSettings) Generate() string {
	settingsContent := settingsMainContent

	if len(settings.mirrorURL) > 0 {
		mavenMirrorContent := fmt.Sprintf(mavenMirrorXMLContentTpl, settings.mirrorURL)
		settingsContent = strings.ReplaceAll(settingsContent, "<!-- ### mirror settings ### -->", mavenMirrorContent)
	}

	if len(settings.repositories) > 0 {
		settingsContent = strings.ReplaceAll(settingsContent, "<!-- ### profiles ### -->", profilesXMLContent)
	}
	for _, repo := range settings.repositories {
		repositoryContent := fmt.Sprintf(repositoryXMLContentTpl, repo.ID, repo.ID, repo.URL)
		settingsContent = strings.ReplaceAll(settingsContent, "</repositories>", fmt.Sprintf("\n<repository>%s</repository>\n</repositories>", repositoryContent))
		settingsContent = strings.ReplaceAll(settingsContent, "</pluginRepositories>", fmt.Sprintf("\n<pluginRepository>%s</pluginRepository>\n</pluginRepositories>", repositoryContent))

		if repo.ignoreInMirror {
			// Ignore repo in mirror if exists
			settingsContent = strings.ReplaceAll(settingsContent, "</mirrorOf>", fmt.Sprintf(",!%s</mirrorOf>", repo.ID))
		}
	}

	return settingsContent
}

const (
	repositoryXMLContentTpl = `
      <id>%s</id>
      <name>%s</name>
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

	mavenMirrorXMLContentTpl = `
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

	profilesXMLContent = `
  <profiles>
    <profile>
      <id>default</id>
      <repositories>
      </repositories>
      <pluginRepositories>
      </pluginRepositories>
    </profile>
  </profiles>
  <activeProfiles>
    <activeProfile>default</activeProfile>
  </activeProfiles>`
)
