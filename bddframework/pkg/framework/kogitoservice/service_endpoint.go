// Copyright 2019 Red Hat, Inc. and/or its affiliates
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

// ServiceEndpoints represents the endpoints for a deployed Kogito Data Index service
type ServiceEndpoints struct {
	// HTTPRouteURI ...
	HTTPRouteURI string
	// HTTPRouteEnv name of the environment variable that will hold the HTTP URI
	HTTPRouteEnv string
	// WSRouteURI ...
	WSRouteURI string
	// WSRouteEnv name of the environment variable that will hold the HTTP URI
	WSRouteEnv string
}

func (s *ServiceEndpoints) String() string {
	return s.HTTPRouteURI
}

// IsEmpty returns true if route URIs are empty
func (s *ServiceEndpoints) IsEmpty() bool {
	return len(s.HTTPRouteURI) == 0 && len(s.WSRouteURI) == 0
}
