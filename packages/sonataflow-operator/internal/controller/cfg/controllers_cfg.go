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
	BuilderConfigMapName            string            `yaml:"builderConfigMapName,omitempty"`
	PostgreSQLPersistenceExtensions []GroupArtifactId `yaml:"postgreSQLPersistenceExtensions,omitempty"`
	KogitoEventsGrouping            bool              `yaml:"kogitoEventsGrouping,omitempty"`
	KogitoEventsGroupingBinary      bool              `yaml:"KogitoEventsGroupingBinary,omitempty"`
	KogitoEventsGroupingCompress    bool              `yaml:"KogitoEventsGroupingCompress,omitempty"`
	// Image fields overridden by environment variables if present
	JobsServicePostgreSQLImageTag string `yaml:"-"`
	JobsServiceEphemeralImageTag  string `yaml:"-"`
	DataIndexPostgreSQLImageTag   string `yaml:"-"`
	DataIndexEphemeralImageTag    string `yaml:"-"`
	DbMigratorToolImageTag        string `yaml:"-"`
	SonataFlowBaseBuilderImageTag string `yaml:"-"`
	SonataFlowDevModeImageTag     string `yaml:"-"`
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
		controllersCfg = defaultControllersCfg
	} else {
		err = yaml.NewYAMLOrJSONDecoder(bytes.NewReader(yamlFile), 100).Decode(&controllersCfg)
		if err != nil {
			klog.V(log.E).ErrorS(err, "Failed to unmarshal controllers config file", "YAML file location", defaultConfigMountPath)
			controllersCfg = defaultControllersCfg
		}
	}

	_ = mergo.Merge(controllersCfg, defaultControllersCfg)
	injectEnvOverrides(controllersCfg)

	return controllersCfg, nil
}

func injectEnvOverrides(cfg *ControllersCfg) {
	if val := os.Getenv("RELATED_IMAGE_JOBS_SERVICE_POSTGRESQL"); val != "" {
		cfg.JobsServicePostgreSQLImageTag = val
	}
	if val := os.Getenv("RELATED_IMAGE_JOBS_SERVICE_EPHEMERAL"); val != "" {
		cfg.JobsServiceEphemeralImageTag = val
	}
	if val := os.Getenv("RELATED_IMAGE_DATA_INDEX_POSTGRESQL"); val != "" {
		cfg.DataIndexPostgreSQLImageTag = val
	}
	if val := os.Getenv("RELATED_IMAGE_DATA_INDEX_EPHEMERAL"); val != "" {
		cfg.DataIndexEphemeralImageTag = val
	}
	if val := os.Getenv("RELATED_IMAGE_DB_MIGRATOR_TOOL"); val != "" {
		cfg.DbMigratorToolImageTag = val
	}
	if val := os.Getenv("RELATED_IMAGE_SONATAFLOW_BUILDER"); val != "" {
		cfg.SonataFlowBaseBuilderImageTag = val
	}
	if val := os.Getenv("RELATED_IMAGE_SONATAFLOW_DEV_MODE"); val != "" {
		cfg.SonataFlowDevModeImageTag = val
	}
}

func GetCfg() *ControllersCfg {
	// Guard to use defaults in local tests
	// In runtime, cmd/main.go calls InitializeControllersCfg to set the cache.
	if controllersCfg == nil {
		return defaultControllersCfg
	}
	return controllersCfg
}
