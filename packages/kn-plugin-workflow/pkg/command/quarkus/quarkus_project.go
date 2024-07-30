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

	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/common"
	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/metadata"
	"github.com/beevik/etree"
)

type CreateQuarkusProjectConfig struct {
	ProjectName         string
	Extensions          string // List of extensions separated by "," to be added to the Quarkus project
	DependenciesVersion metadata.DependenciesVersion
}

type Repository struct {
	Id   string
	Name string
	Url  string
}

func CreateQuarkusProject(cfg CreateQuarkusProjectConfig) error {
	if err := common.CheckProjectName(cfg.ProjectName); err != nil {
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
		"-DnoCode",
		fmt.Sprintf("-DprojectArtifactId=%s", cfg.ProjectName),
		fmt.Sprintf("-Dextensions=%s", cfg.Extensions))

	if err := common.RunCommand(
		create,
		"create",
	); err != nil {
		return err
	}

	//Until we are part of Quarkus 3.x bom we need to manipulate the pom.xml to use the right kogito dependencies
	pomPath := cfg.ProjectName + "/pom.xml"
	if err := manipulatePomToKogito(pomPath, cfg); err != nil {
		return err
	}
	return nil
}

func manipulatePomToKogito(filename string, cfg CreateQuarkusProjectConfig) error {

	if cfg.DependenciesVersion.QuarkusPlatformGroupId == "" || cfg.DependenciesVersion.QuarkusVersion == "" {
		return fmt.Errorf("configuration for Quarkus versions is not complete")
	}

	doc := etree.NewDocument()
	err := doc.ReadFromFile(filename)
	if err != nil {
		return fmt.Errorf("error reading %s: %w", filename, err)
	}

	// Update quarkus.platform.group-id
	properties := doc.FindElement("//properties")
	if properties == nil {
		return fmt.Errorf("error parsing %s: %w", filename, err)
	}
	groupIDElement := properties.FindElement("quarkus.platform.group-id")
	if groupIDElement == nil {
		return fmt.Errorf("error parsing %s: %w", filename, err)
	}
	groupIDElement.SetText(cfg.DependenciesVersion.QuarkusPlatformGroupId)

	// Update quarkus.platform.version
	versionElement := properties.FindElement("quarkus.platform.version")
	if versionElement == nil {
		return fmt.Errorf("error parsing %s: %w", filename, err)
	}
	versionElement.SetText(cfg.DependenciesVersion.QuarkusVersion)

	properties.CreateElement("kie.version").SetText(metadata.KogitoBomDependency.Version)
	properties.CreateElement("kie.tooling.version").SetText(metadata.PluginVersion)

	//Add kogito bom dependency
	depManagement := doc.FindElement("//dependencyManagement")
	if depManagement == nil {
		return fmt.Errorf("error parsing %s: %w", filename, err)
	}

	dependenciesManagendChild := depManagement.FindElement("dependencies")
	if dependenciesManagendChild == nil {
		return fmt.Errorf("error parsing %s: %w", filename, err)
	}

	dependencyElement := dependenciesManagendChild.CreateElement("dependency")
	dependencyElement.CreateElement("groupId").SetText(metadata.KogitoBomDependency.GroupId)
	dependencyElement.CreateElement("artifactId").SetText(metadata.KogitoBomDependency.ArtifactId)
	dependencyElement.CreateElement("version").SetText("${kie.version}")
	dependencyElement.CreateElement("type").SetText(metadata.KogitoBomDependency.Type)
	dependencyElement.CreateElement("scope").SetText(metadata.KogitoBomDependency.Scope)

	// Update kogito pom dependencies
	dependencies := doc.FindElement("//dependencies")
	if dependencies == nil {
		return fmt.Errorf("error parsing %s: %w", filename, err)
	}

	for _, dep := range metadata.KogitoDependencies {
		dependencyElement := dependencies.CreateElement("dependency")
		dependencyElement.CreateElement("groupId").SetText(dep.GroupId)
		dependencyElement.CreateElement("artifactId").SetText(dep.ArtifactId)
		if dep.Version != "" {
			dependencyElement.CreateElement("version").SetText(dep.Version)
		}
	}

	//add apache repository after profiles declaration
	var repositories = []Repository{
		{Id: "apache-public-repository-group", Name: "Apache Public Repository Group", Url: "https://repository.apache.org/content/groups/public/"},
		{Id: "apache-snapshot-repository-group", Name: "Apache Snapshot Repository Group", Url: "https://repository.apache.org/content/groups/snapshots/"},
	}

	var project = doc.FindElement("//project")
	repositoriesElement := project.FindElement("//repositories")
	if repositoriesElement == nil {
		repositoriesElement = project.CreateElement("repositories")
	}

	for _, repo := range repositories {
		var repository = repositoriesElement.CreateElement("repository")
		repository.CreateElement("id").SetText(repo.Id)
		repository.CreateElement("name").SetText(repo.Name)
		repository.CreateElement("url").SetText(repo.Url)
	}

	doc.Indent(4)

	if err := doc.WriteToFile(filename); err != nil {
		return fmt.Errorf("error writing modified content to %s: %w", filename, err)
	}

	return nil

}
