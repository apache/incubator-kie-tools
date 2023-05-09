/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package builder

import (
	"context"
	"time"

	"github.com/containers/buildah/define"
	"github.com/containers/podman/v4/pkg/bindings/images"
	"github.com/containers/podman/v4/pkg/domain/entities"
	"github.com/sirupsen/logrus"
)

type BuildahVanillaConfig struct {
	DockerFilePath     string
	DockerFileName     string
	Output             string
	Ulimit             string
	Tags               []string
	SeccompProfilePath string
	AddCapabilities    []string
}

func BuildahBuild(connection context.Context, config BuildahVanillaConfig) (string, error) {
	dockerfiles := []string{config.DockerFilePath + config.DockerFileName}
	buildOptions := define.BuildOptions{
		AddCapabilities: config.AddCapabilities,
		AdditionalTags:  config.Tags,
		Output:          config.Output,
		CommonBuildOpts: &define.CommonBuildOptions{
			Ulimit:             []string{config.Ulimit},
			SeccompProfilePath: config.SeccompProfilePath,
		},
	}
	start := time.Now()
	report, err := images.Build(connection, dockerfiles, entities.BuildOptions{BuildOptions: buildOptions})
	timeElapsed := time.Since(start)
	logrus.Infof("The Buildah build took %s", timeElapsed)
	return report.ID, err
}
