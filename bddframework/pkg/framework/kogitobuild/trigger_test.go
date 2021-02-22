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

package kogitobuild

import (
	"github.com/kiegroup/kogito-cloud-operator/core/client/openshift"
	"github.com/kiegroup/kogito-cloud-operator/core/operator"
	"github.com/kiegroup/kogito-cloud-operator/core/test"
	"github.com/kiegroup/kogito-cloud-operator/meta"
	buildv1 "github.com/openshift/api/build/v1"
	"github.com/stretchr/testify/assert"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"strings"
	"testing"
)

func TestStartNewBuild(t *testing.T) {
	bc := &buildv1.BuildConfig{
		ObjectMeta: metav1.ObjectMeta{Name: "mybuildconfig", Namespace: t.Name()},
		Spec: buildv1.BuildConfigSpec{
			CommonSpec: buildv1.CommonSpec{},
		},
	}
	runningBuild := &buildv1.Build{
		ObjectMeta: metav1.ObjectMeta{
			Name:      "mybuildconfig-1",
			Namespace: t.Name(),
			Labels:    map[string]string{openshift.BuildConfigLabelSelector: "mybuildconfig"}},
		Spec: buildv1.BuildSpec{},
		Status: buildv1.BuildStatus{
			Phase:     buildv1.BuildPhaseRunning,
			Cancelled: false,
		},
	}
	cli := test.NewFakeClientBuilder().OnOpenShift().AddBuildObjects(bc, runningBuild).Build()
	context := &operator.Context{
		Client: cli,
		Log:    test.TestLogger,
		Scheme: meta.GetRegisteredSchema(),
	}
	triggerHandler := NewTriggerHandler(context)
	err := triggerHandler.StartNewBuild(bc)
	// we reach an error state since the FakeCli can't update the status for our build.
	// and thus the go routine that waits for this status will fail as well :)
	assert.Error(t, err)

	builds, err := openshift.BuildConfigC(cli).GetBuildsStatusByLabel(
		t.Name(), strings.Join([]string{openshift.BuildConfigLabelSelector, "mybuildconfig"}, "="))
	assert.NoError(t, err)
	assert.NotNil(t, builds)
	assert.Len(t, builds.Running, 1)
}
