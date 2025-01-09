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

package test

import (
	"testing"

	"github.com/stretchr/testify/assert"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/cfg"
)

// RestoreControllersConfig Utility function to restore the controllers global configuration in situations where
// a particular test must populate it with values form a given file. As part of the given test finalization we can
// invoke this function to restore the global configuration.
func RestoreControllersConfig(t *testing.T) {
	_, err := cfg.InitializeControllersCfgAt(getProjectDir() + "/config/manager/controllers_cfg.yaml")
	assert.NoError(t, err)
}
