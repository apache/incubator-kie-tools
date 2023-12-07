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
		fmt.Sprintf("-DplatformVersion=%s", cfg.DependenciesVersion.QuarkusVersion),
		fmt.Sprintf("-DprojectArtifactId=%s", cfg.ProjectName),
		fmt.Sprintf("-Dextensions=%s", cfg.Extensions))

	if err := common.RunCommand(
		create,
		"create",
	); err != nil {
		return err
	}

	//Until we are part of Quarkus 3.x bom we need to manipulate the pom.xml to use the right version of kogito-bom
	pomPath := cfg.ProjectName + "/pom.xml"
	if err := manipulatePomToKogito(pomPath); err != nil {
		return err
	}
	return nil
}

func manipulatePomToKogito(filename string) error {

	doc := etree.NewDocument()
	err := doc.ReadFromFile(filename)
	if err != nil {
		return fmt.Errorf("error reading %s: %w", filename, err)
	}

	// Remove the <dependency> block with quarkus-kogito-bom
	dependencyToReplace := doc.FindElement("//dependency[groupId='${quarkus.platform.group-id}'][artifactId='quarkus-kogito-bom']")
	if dependencyToReplace == nil {
		return fmt.Errorf("error parsing %s: %w", filename, err)
	}
	dependencyToReplace.Parent().RemoveChild(dependencyToReplace)

	// Create the new <dependency> block with kogito-bom and add it to the <dependencyManagement> block
	// replacing quarkus-kogito-bom on specific version
	newDependency := doc.CreateElement("dependency")
	newDependency.CreateElement("groupId").SetText("org.kie.kogito")
	newDependency.CreateElement("artifactId").SetText("kogito-bom")
	newDependency.CreateElement("version").SetText(metadata.KogitoVersion)
	newDependency.CreateElement("type").SetText("pom")
	newDependency.CreateElement("scope").SetText("import")

	dependencyManagement := doc.FindElement("//dependencyManagement")
	if dependencyManagement != nil {
		dependencies := dependencyManagement.FindElement("dependencies")
		if dependencies != nil {
			dependencies.AddChild(newDependency)
		} else {
			dependencies = dependencyManagement.CreateElement("dependencies")
			dependencies.AddChild(newDependency)
		}
	}

	doc.Indent(4)

	err = doc.WriteToFile(filename)
	if err != nil {
		fmt.Println("Error writing modified XML:", err)
		return fmt.Errorf("error writing modified content to %s: %w", filename, err)
	}

	return nil

}
