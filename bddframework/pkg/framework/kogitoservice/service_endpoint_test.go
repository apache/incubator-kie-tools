// Copyright 2020 Red Hat, Inc. and/or its affiliates
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package kogitoservice

import (
	"github.com/stretchr/testify/assert"
	"testing"
)

func Test_ServiceEndPoint_String(t *testing.T) {
	serviceEndPoint := ServiceEndpoints{
		HTTPRouteURI: "HTTPRouteURI",
		HTTPRouteEnv: "HTTPRouteEnv",
		WSRouteURI:   "WSRouteURI",
		WSRouteEnv:   "WSRouteEnv",
	}

	assert.Equal(t, serviceEndPoint.HTTPRouteURI, serviceEndPoint.String())
}

func Test_ServiceEndPoint_IsEmpty(t *testing.T) {
	serviceEndPoint := ServiceEndpoints{
		HTTPRouteEnv: "HTTPRouteEnv",
		WSRouteEnv:   "WSRouteEnv",
	}

	assert.True(t, serviceEndPoint.IsEmpty())
}
