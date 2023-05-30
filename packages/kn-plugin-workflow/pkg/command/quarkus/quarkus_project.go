/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

	return nil
}
