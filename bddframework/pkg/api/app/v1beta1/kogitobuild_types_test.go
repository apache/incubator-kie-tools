// Copyright 2021 Red Hat, Inc. and/or its affiliates
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

package v1beta1

import (
	"github.com/kiegroup/kogito-operator/apis"
	"github.com/stretchr/testify/assert"
	corev1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/api/resource"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"testing"
)

func TestKogitoBuildSpec(t *testing.T) {
	instance := &KogitoBuild{
		Spec: KogitoBuildSpec{
			Type:               api.BinaryBuildType,
			DisableIncremental: true,
			Env: []corev1.EnvVar{
				{
					Name: "env1",
				},
				{
					Name: "env2",
				},
			},
			GitSource: GitSource{
				URI: "testURI",
			},
			Runtime: api.QuarkusRuntimeType,
			Native:  true,
			Resources: corev1.ResourceRequirements{
				Limits: corev1.ResourceList{
					"cpu1": resource.MustParse("5"),
					"cpu2": resource.MustParse("10"),
				},
				Requests: corev1.ResourceList{
					"storage1": resource.MustParse("20G"),
					"storage2": resource.MustParse("40G"),
				},
			},
			MavenMirrorURL:      "mavenMirrorURL",
			BuildImage:          "quay.io/test/quarkusBuildImage:latest",
			RuntimeImage:        "quay.io/test/quarkusRuntimeImage:latest",
			TargetKogitoRuntime: "travels",
			Artifact: Artifact{
				GroupID: "com.test",
			},
			EnableMavenDownloadOutput: true,
		},
	}

	spec := instance.GetSpec()
	assert.Equal(t, api.BinaryBuildType, spec.GetType())
	assert.True(t, spec.IsDisableIncremental())
	assert.Equal(t, 2, len(spec.GetEnv()))
	assert.Equal(t, "env1", spec.GetEnv()[0].Name)
	assert.Equal(t, "env2", spec.GetEnv()[1].Name)
	assert.Equal(t, "testURI", spec.GetGitSource().GetURI())
	assert.Equal(t, api.QuarkusRuntimeType, spec.GetRuntime())
	assert.Equal(t, true, spec.IsNative())
	assert.Equal(t, 2, len(spec.GetResources().Limits))
	assert.Equal(t, resource.MustParse("5"), spec.GetResources().Limits["cpu1"])
	assert.Equal(t, resource.MustParse("10"), spec.GetResources().Limits["cpu2"])
	assert.Equal(t, 2, len(spec.GetResources().Requests))
	assert.Equal(t, resource.MustParse("20G"), spec.GetResources().Requests["storage1"])
	assert.Equal(t, resource.MustParse("40G"), spec.GetResources().Requests["storage2"])
	assert.Equal(t, "mavenMirrorURL", spec.GetMavenMirrorURL())
	assert.Equal(t, "quay.io/test/quarkusBuildImage:latest", spec.GetBuildImage())
	assert.Equal(t, "quay.io/test/quarkusRuntimeImage:latest", spec.GetRuntimeImage())
	assert.Equal(t, "travels", spec.GetTargetKogitoRuntime())
	assert.Equal(t, "com.test", spec.GetArtifact().GetGroupID())
	assert.Equal(t, true, spec.IsEnableMavenDownloadOutput())
}

func TestKogitoBuildStatus(t *testing.T) {
	instance := &KogitoBuild{
		Status: KogitoBuildStatus{
			LatestBuild: "build1",
			Conditions: &[]metav1.Condition{
				{
					Type: string(api.KogitoBuildSuccessful),
				},
				{
					Type: string(api.KogitoBuildFailure),
				},
			},
			Builds: Builds{
				New: []string{"new1", "new2"},
			},
		},
	}

	status := instance.GetStatus()
	assert.Equal(t, "build1", status.GetLatestBuild())

	conditions := *status.GetConditions()
	assert.Equal(t, 2, len(conditions))
	assert.Equal(t, string(api.KogitoBuildSuccessful), conditions[0].Type)
	assert.Equal(t, string(api.KogitoBuildFailure), conditions[1].Type)
	assert.Equal(t, 2, len(status.GetBuilds().GetNew()))
	assert.Equal(t, "new1", status.GetBuilds().GetNew()[0])
	assert.Equal(t, "new2", status.GetBuilds().GetNew()[1])
}

func TestKogitoBuild_Builds(t *testing.T) {
	builds := Builds{
		New:       []string{"new1", "new2"},
		Pending:   []string{"pending1", "pending2"},
		Running:   []string{"running1", "running2"},
		Complete:  []string{"complete1", "complete2"},
		Failed:    []string{"failed1", "failed2"},
		Error:     []string{"error1", "error2"},
		Cancelled: []string{"cancelled1", "cancelled2"},
	}
	assert.Equal(t, 2, len(builds.GetNew()))
	assert.Equal(t, "new1", builds.GetNew()[0])
	assert.Equal(t, "new2", builds.GetNew()[1])

	assert.Equal(t, 2, len(builds.GetPending()))
	assert.Equal(t, "pending1", builds.GetPending()[0])
	assert.Equal(t, "pending2", builds.GetPending()[1])

	assert.Equal(t, 2, len(builds.GetRunning()))
	assert.Equal(t, "running1", builds.GetRunning()[0])
	assert.Equal(t, "running2", builds.GetRunning()[1])

	assert.Equal(t, 2, len(builds.GetComplete()))
	assert.Equal(t, "complete1", builds.GetComplete()[0])
	assert.Equal(t, "complete2", builds.GetComplete()[1])

	assert.Equal(t, 2, len(builds.GetFailed()))
	assert.Equal(t, "failed1", builds.GetFailed()[0])
	assert.Equal(t, "failed2", builds.GetFailed()[1])

	assert.Equal(t, 2, len(builds.GetError()))
	assert.Equal(t, "error1", builds.GetError()[0])
	assert.Equal(t, "error2", builds.GetError()[1])

	assert.Equal(t, 2, len(builds.GetCancelled()))
	assert.Equal(t, "cancelled1", builds.GetCancelled()[0])
	assert.Equal(t, "cancelled2", builds.GetCancelled()[1])
}
