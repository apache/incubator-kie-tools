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

package test

import (
	buildfake "github.com/openshift/client-go/build/clientset/versioned/fake"
	v1 "github.com/openshift/client-go/build/clientset/versioned/typed/build/v1"
	"k8s.io/apimachinery/pkg/runtime"
	"k8s.io/client-go/rest"
)

func newBuildFake(objects ...runtime.Object) v1.BuildV1Interface {
	return &buildFakeWithMockREST{
		innerClient: buildfake.NewSimpleClientset(objects...).BuildV1(),
	}
}

type buildFakeWithMockREST struct {
	innerClient v1.BuildV1Interface
}

func (b *buildFakeWithMockREST) Builds(namespace string) v1.BuildInterface {
	return b.innerClient.Builds(namespace)
}

func (b *buildFakeWithMockREST) BuildConfigs(namespace string) v1.BuildConfigInterface {
	return b.innerClient.BuildConfigs(namespace)
}

func (b *buildFakeWithMockREST) RESTClient() rest.Interface {
	return &rest.RESTClient{}
}
