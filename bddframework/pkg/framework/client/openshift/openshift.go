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

package openshift

import (
	"github.com/kiegroup/kogito-cloud-operator/core/client"
	"github.com/kiegroup/kogito-cloud-operator/core/logger"
)

var log = logger.GetLogger("openshift_client")

// ImageStream will call ImageStream OpenShift API
func ImageStream() ImageStreamInterface {
	return newImageStream(&client.Client{})
}

// ImageStreamC will call ImageStream OpenShift API with a given client
func ImageStreamC(c *client.Client) ImageStreamInterface {
	return newImageStream(c)
}

// BuildConfig will call BuildConfig OpenShift API
func BuildConfig() BuildConfigInterface {
	return newBuildConfig(&client.Client{})
}

// BuildConfigC will call BuildConfig OpenShift API with a given client
func BuildConfigC(c *client.Client) BuildConfigInterface {
	return newBuildConfig(c)
}
