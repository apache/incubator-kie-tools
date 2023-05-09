package kubernetes

import (
	"context"
	"os"
	"testing"
	"time"

	"github.com/stretchr/testify/assert"
	v1 "k8s.io/api/core/v1"
	resource2 "k8s.io/apimachinery/pkg/api/resource"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"

	"github.com/kiegroup/kogito-serverless-operator/container-builder/api"
	"github.com/kiegroup/kogito-serverless-operator/container-builder/util"
	"github.com/kiegroup/kogito-serverless-operator/container-builder/util/test"
)

// Test that verify we are able to create a Kaniko build with cache enabled, a specific set of resources and additional flags
func TestNewBuildWithKanikoCustomizations(t *testing.T) {
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

	// Sample CPU and Memory quantities
	cpuQty, _ := resource2.ParseQuantity("1")
	memQty, _ := resource2.ParseQuantity("1Gi")

	// Sample additional flag
	addFlags := make([]string, 1)
	addFlags[0] = "--use-new-run=true"

	// create the new build, schedule with cache enabled, a specific set of resources and additional flags
	build, err := NewBuild(BuilderInfo{FinalImageName: "quay.io/kiegroup/buildexample:latest", BuildUniqueName: "build1", Platform: platform}).
		WithProperty(KanikoCache, api.KanikoTaskCache{Enabled: util.Pbool(true), PersistentVolumeClaim: "kaniko-cache-pv"}).
		WithResourceRequirements(v1.ResourceRequirements{
			Limits: v1.ResourceList{
				v1.ResourceCPU:    cpuQty,
				v1.ResourceMemory: memQty,
			},
			Requests: v1.ResourceList{
				v1.ResourceCPU:    cpuQty,
				v1.ResourceMemory: memQty,
			},
		}).
		WithAdditionalArgs(addFlags).
		WithResource("Dockerfile", dockerFile).
		WithResource("greetings.sw.json", workflowDefinition).
		WithClient(c).
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

	assert.Subset(t, pod.Spec.Containers[0].Args, addFlags)
}
