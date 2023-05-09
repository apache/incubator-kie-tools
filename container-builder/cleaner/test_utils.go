/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package cleaner

import (
	"testing"

	"github.com/kiegroup/kogito-serverless-operator/container-builder/common"

	"github.com/stretchr/testify/assert"
)

func CheckRepositoriesSize(t *testing.T, size int, registryContainer common.RegistryContainer) []string {
	repos, err := registryContainer.GetRepositories()
	assert.Nil(t, err, "Error calling GetRepositories()")
	assert.True(t, len(repos) == size)
	return repos
}
