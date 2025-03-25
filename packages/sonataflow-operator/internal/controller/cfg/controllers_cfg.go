// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

// note: we use `cfg` instead of `config` or `configuration` in order to have a shorter accessor in the codebase.

package cfg

import (
	"bytes"
	"fmt"
	"os"

	"github.com/imdario/mergo"
	"k8s.io/apimachinery/pkg/util/yaml"
	"k8s.io/klog/v2"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/log"
)

const (
	defaultConfigMountPath = "/config/controllers_cfg.yaml"
)

var controllersCfg *ControllersCfg

var defaultControllersCfg = &ControllersCfg{
	HealthFailureThresholdDevMode: 50,
	DefaultPvcKanikoSize:          "1Gi",
	KanikoDefaultWarmerImageTag:   "gcr.io/kaniko-project/warmer:v1.9.0",
	KanikoExecutorImageTag:        "gcr.io/kaniko-project/executor:v1.9.0",
	BuilderConfigMapName:          "sonataflow-operator-builder-config",
}

type GroupArtifactId struct {
	GroupId    string `yaml:"groupId,omitempty"`
	ArtifactId string `yaml:"artifactId,omitempty"`
}

func (g *GroupArtifactId) String() string {
	return fmt.Sprintf("%s:%s", g.GroupId, g.ArtifactId)
}

type ControllersCfg struct {
	DefaultPvcKanikoSize            string            `yaml:"defaultPvcKanikoSize,omitempty"`
	HealthFailureThresholdDevMode   int32             `yaml:"healthFailureThresholdDevMode,omitempty"`
	KanikoDefaultWarmerImageTag     string            `yaml:"kanikoDefaultWarmerImageTag,omitempty"`
	KanikoExecutorImageTag          string            `yaml:"kanikoExecutorImageTag,omitempty"`
	JobsServicePostgreSQLImageTag   string            `yaml:"jobsServicePostgreSQLImageTag,omitempty"`
	JobsServiceEphemeralImageTag    string            `yaml:"jobsServiceEphemeralImageTag,omitempty"`
	DataIndexPostgreSQLImageTag     string            `yaml:"dataIndexPostgreSQLImageTag,omitempty"`
	DataIndexEphemeralImageTag      string            `yaml:"dataIndexEphemeralImageTag,omitempty"`
	DbMigratorToolImageTag          string            `yaml:"dbMigratorToolImageTag,omitempty"`
	SonataFlowBaseBuilderImageTag   string            `yaml:"sonataFlowBaseBuilderImageTag,omitempty"`
	SonataFlowDevModeImageTag       string            `yaml:"sonataFlowDevModeImageTag,omitempty"`
	BuilderConfigMapName            string            `yaml:"builderConfigMapName,omitempty"`
	PostgreSQLPersistenceExtensions []GroupArtifactId `yaml:"postgreSQLPersistenceExtensions,omitempty"`
	KogitoEventsGrouping            bool              `yaml:"kogitoEventsGrouping,omitempty"`
	KogitoEventsGroupingBinary      bool              `yaml:"KogitoEventsGroupingBinary,omitempty"`
	KogitoEventsGroupingCompress    bool              `yaml:"KogitoEventsGroupingCompress,omitempty"`
}

// InitializeControllersCfg initializes the platform configuration for this instance.
// Must be called at the main initialization point.
//
// For the main initialization, the configuration is discarded since the controllers will read the cache instance.
// We keep the pointer return here to make sure we can access it from tests if needed or implement an optional use the defaults if fail to read.
func InitializeControllersCfg() (*ControllersCfg, error) {
	return InitializeControllersCfgAt(defaultConfigMountPath)
}

// InitializeControllersCfgAt same as InitializeControllersCfg receiving a path as input.
func InitializeControllersCfgAt(configFilePath string) (*ControllersCfg, error) {
	if len(configFilePath) == 0 {
		configFilePath = defaultConfigMountPath
	}
	controllersCfg = nil
	yamlFile, err := os.ReadFile(configFilePath)
	if err != nil {
		klog.V(log.E).ErrorS(err, "Failed to read controllers config file", "YAML file location", defaultConfigMountPath)
		return defaultControllersCfg, err
	}
	err = yaml.NewYAMLOrJSONDecoder(bytes.NewReader(yamlFile), 100).Decode(&controllersCfg)
	if err != nil {
		klog.V(log.E).ErrorS(err, "Failed to unmarshal controllers config file", "YAML file location", defaultConfigMountPath)
		return defaultControllersCfg, err
	}
	if err = mergo.Merge(controllersCfg, defaultControllersCfg); err != nil {
		return defaultControllersCfg, err
	}
	return controllersCfg, nil
}

func GetCfg() *ControllersCfg {
	// Guard to use defaults in local tests
	// In runtime, cmd/main.go calls InitializeControllersCfg to set the cache.
	if controllersCfg == nil {
		return defaultControllersCfg
	}
	return controllersCfg
}
