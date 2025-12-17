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

package common

import (
	"context"
	"github.com/docker/docker/api/types/image"
	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/mock"

	"testing"
)

type MockDockerClient struct {
	mock.Mock
}

func (m *MockDockerClient) ImageList(ctx context.Context, options image.ListOptions) ([]image.Summary, error) {
	args := m.Called(ctx, options)
	return args.Get(0).([]image.Summary), args.Error(1)
}

func TestCheckImageExists(t *testing.T) {

	tests := []struct {
		lookup   string
		images   []string
		expected bool
	}{
		{"docker.io/example/app-image:latest", []string{"docker.io/example/app-image:latest"}, true},
		{"docker.io/demo/service-image:1.0", []string{"demo/service-image:1.0"}, true},

		{"docker.io/testuser/sample-app", []string{"docker.io/testuser/sample-app:latest"}, true},
		{"docker.io/testuser/sample-app", []string{"testuser/sample-app:latest"}, true},

		{"testuser/sample-app:dev", []string{"docker.io/testuser/sample-app:dev"}, true},
		{"testuser/sample-app:dev", []string{"testuser/sample-app:dev"}, true},

		{"docker.io/example/app-image:latest", []string{"app-image:latest"}, false},
		{"docker.io/testuser/sample-app", []string{"sample-app:latest"}, false},
		{"testuser/sample-app:dev", []string{"sample-app:dev"}, false},
	}

	for _, test := range tests {
		ctx := context.Background()
		mockClient := new(MockDockerClient)

		mockClient.On("ImageList", ctx, mock.Anything).Return([]image.Summary{
			{
				RepoTags: test.images,
			},
		}, nil)

		exists, err := CheckImageExists(mockClient, ctx, test.lookup)
		assert.NoError(t, err, "Error should be nil")
		assert.True(t, exists == test.expected, "Expected %t, got %t", test.expected, exists)
	}
}
