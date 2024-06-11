// Copyright 2024 Apache Software Foundation (ASF)
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package cfg

import (
	"os"
	"testing"

	"github.com/stretchr/testify/assert"
)

func TestInitializeControllersCfgAt_ValidFile(t *testing.T) {
	cfg, err := InitializeControllersCfgAt("./testdata/controllers-cfg-test.yaml")
	assert.NoError(t, err)
	assert.NotNil(t, cfg)

	assert.Equal(t, int32(555), cfg.HealthFailureThresholdDevMode)
	assert.Equal(t, "2Gi", cfg.DefaultPvcKanikoSize)
	assert.Equal(t, "local/jobs-service:1.0.0", cfg.JobsServicePostgreSQLImageTag)
	assert.Equal(t, "local/data-index:1.0.0", cfg.DataIndexPostgreSQLImageTag)
	assert.Equal(t, "local/sonataflow-builder:1.0.0", cfg.SonataFlowBaseBuilderImageTag)
	assert.Equal(t, "local/sonataflow-devmode:1.0.0", cfg.SonataFlowDevModeImageTag)
}

func TestInitializeControllersCfgAt_FileNotFound(t *testing.T) {
	cfg, err := InitializeControllersCfgAt("./whatever.yaml")
	assert.Error(t, err)
	assert.NotNil(t, cfg) //get the default
	assert.True(t, os.IsNotExist(err))
	// defaults
	assert.Equal(t, defaultControllersCfg, cfg)
}

func TestInitializeControllersCfgAt_NotValidYaml(t *testing.T) {
	cfg, err := InitializeControllersCfgAt("./testdata/controllers-cfg-invalid.yaml")
	assert.NoError(t, err)
	assert.NotNil(t, cfg)
	// defaults
	assert.Equal(t, defaultControllersCfg, cfg)
}
