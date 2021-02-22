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

package openshift

import (
	"context"
	"github.com/kiegroup/kogito-cloud-operator/core/test"
	"github.com/kiegroup/kogito-cloud-operator/meta"
	buildv1 "github.com/openshift/api/build/v1"
	"github.com/stretchr/testify/assert"
	v1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"sync"
	"testing"
	"time"
)

func Test_buildConfig_TriggerBuildFromFile_BCNotFound(t *testing.T) {
	cli := test.NewFakeClientBuilder().OnOpenShift().Build()
	buildCLI := newBuildConfigWithBCRetries(cli, 1, 1*time.Second)
	buildOpts := &buildv1.BinaryBuildRequestOptions{AsFile: "myfile.dmn", ObjectMeta: v1.ObjectMeta{Name: "mybuild"}}
	build, err := buildCLI.TriggerBuildFromFile(t.Name(), nil, buildOpts, false, meta.GetRegisteredSchema())
	// buildconfig is not there, raise an error
	assert.Error(t, err)
	assert.Contains(t, err.Error(), "\"mybuild-builder\" not found")
	// we should not have a build
	assert.Nil(t, build)
}

func Test_buildConfig_TriggerBuildFromFile_BCNotFound_Binary(t *testing.T) {
	cli := test.NewFakeClientBuilder().OnOpenShift().Build()
	buildCLI := newBuildConfigWithBCRetries(cli, 1, 1*time.Second)
	buildOpts := &buildv1.BinaryBuildRequestOptions{AsFile: "target.tar.gz", ObjectMeta: v1.ObjectMeta{Name: "mybuild"}}
	build, err := buildCLI.TriggerBuildFromFile(t.Name(), nil, buildOpts, true, meta.GetRegisteredSchema())
	// buildconfig is not there, raise an error
	assert.Error(t, err)
	assert.Contains(t, err.Error(), "\"mybuild\" not found")
	// we should not have a build
	assert.Nil(t, build)
}

func Test_buildConfig_TriggerBuildFromFile_BCNotFoundThenFound(t *testing.T) {
	var wg sync.WaitGroup
	cli := test.NewFakeClientBuilder().OnOpenShift().Build()
	buildCLI := newBuildConfig(cli)
	buildConfig := &buildv1.BuildConfig{
		ObjectMeta: v1.ObjectMeta{Name: "mybuild-builder", Namespace: t.Name()},
		Spec:       buildv1.BuildConfigSpec{},
	}
	buildOpts := &buildv1.BinaryBuildRequestOptions{AsFile: "myfile.dmn", ObjectMeta: v1.ObjectMeta{Name: "mybuild"}}
	wg.Add(2)
	go func() {
		defer wg.Done()
		_, err := buildCLI.TriggerBuildFromFile(t.Name(), nil, buildOpts, false, meta.GetRegisteredSchema())
		// we don't have an actual server to do the rest call, but we can confirm that it was called
		assert.Error(t, err)
		assert.Contains(t, err.Error(), "unknown type used for body")
	}()
	// now we wait for a while and create the the BC
	time.Sleep(3 * time.Second)
	go func() {
		defer wg.Done()
		bc, err := cli.BuildCli.BuildConfigs(t.Name()).Create(context.TODO(), buildConfig, v1.CreateOptions{})
		assert.NotNil(t, bc)
		assert.NoError(t, err)
	}()
	wg.Wait()
}

func Test_buildConfig_TriggerBuildFromFile_BCNotFoundThenFound_Binary(t *testing.T) {
	var wg sync.WaitGroup
	cli := test.NewFakeClientBuilder().OnOpenShift().Build()
	buildCLI := newBuildConfig(cli)
	buildConfig := &buildv1.BuildConfig{
		ObjectMeta: v1.ObjectMeta{Name: "mybuild", Namespace: t.Name()},
		Spec:       buildv1.BuildConfigSpec{},
	}
	buildOpts := &buildv1.BinaryBuildRequestOptions{AsFile: "target.tar.gz", ObjectMeta: v1.ObjectMeta{Name: "mybuild"}}
	wg.Add(2)
	go func() {
		defer wg.Done()
		_, err := buildCLI.TriggerBuildFromFile(t.Name(), nil, buildOpts, true, meta.GetRegisteredSchema())
		// we don't have an actual server to do the rest call, but we can confirm that it was called
		assert.Error(t, err)
		assert.Contains(t, err.Error(), "unknown type used for body")
	}()
	// now we wait for a while and create the the BC
	time.Sleep(3 * time.Second)
	go func() {
		defer wg.Done()
		bc, err := cli.BuildCli.BuildConfigs(t.Name()).Create(context.TODO(), buildConfig, v1.CreateOptions{})
		assert.NotNil(t, bc)
		assert.NoError(t, err)
	}()
	wg.Wait()
}
