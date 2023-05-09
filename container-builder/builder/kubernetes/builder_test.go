package kubernetes

import (
	"context"
	"os"
	"testing"
	"time"

	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"

	"github.com/stretchr/testify/assert"
	v1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/types"

	"github.com/kiegroup/kogito-serverless-operator/container-builder/api"
	"github.com/kiegroup/kogito-serverless-operator/container-builder/util/test"
)

func TestNewBuild(t *testing.T) {
	ns := "test"
	c, err := test.NewFakeClient()
	assert.NoError(t, err)

	dockerFile, err := os.ReadFile("testdata/Dockerfile")
	assert.NoError(t, err)

	workflowDefinition, err := os.ReadFile("testdata/greetings.sw.json")
	assert.NoError(t, err)

	platform := api.PlatformBuild{
		ObjectReference: api.ObjectReference{
			Namespace: ns,
			Name:      "testPlatform",
		},
		Spec: api.PlatformBuildSpec{
			BuildStrategy:   api.BuildStrategyPod,
			PublishStrategy: api.PlatformBuildPublishStrategyKaniko,
			Timeout:         &metav1.Duration{Duration: 5 * time.Minute},
		},
	}
	// create the new build, schedule
	build, err := NewBuild(BuilderInfo{FinalImageName: "quay.io/kiegroup/buildexample:latest", BuildUniqueName: "build1", Platform: platform}).
		WithClient(c).
		WithResource("Dockerfile", dockerFile).
		WithResource("greetings.sw.json", workflowDefinition).
		Schedule()

	assert.NoError(t, err)
	assert.NotNil(t, build)
	assert.Equal(t, api.BuildPhaseScheduling, build.Status.Phase)

	build, err = FromBuild(build).WithClient(c).Reconcile()
	assert.NoError(t, err)
	assert.NotNil(t, build)
	assert.Equal(t, api.BuildPhasePending, build.Status.Phase)

	// The status won't change since FakeClient won't set the status upon creation, since we don't have a controller :)
	build, err = FromBuild(build).WithClient(c).Reconcile()
	assert.NoError(t, err)
	assert.NotNil(t, build)
	assert.Equal(t, api.BuildPhasePending, build.Status.Phase)

	podName := buildPodName(build)
	pod := &v1.Pod{}
	err = c.Get(context.TODO(), types.NamespacedName{Name: podName, Namespace: ns}, pod)
	assert.NoError(t, err)
	assert.NotNil(t, pod)
	assert.Len(t, pod.Spec.Volumes, 1)
}
