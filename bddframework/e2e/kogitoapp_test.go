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

// Package for writing e2e tests with the operator.
// References: https://github.com/operator-framework/operator-sdk/blob/master/doc/test-framework/writing-e2e-tests.md
package e2e

import (
	goctx "context"
	"fmt"
	"testing"
	"time"

	"github.com/kiegroup/kogito-cloud-operator/pkg/apis"
	"github.com/kiegroup/kogito-cloud-operator/pkg/apis/app/v1alpha1"
	"github.com/kiegroup/kogito-cloud-operator/pkg/controller/kogitoapp/builder"
	"github.com/kiegroup/kogito-cloud-operator/pkg/logger"
	"github.com/kiegroup/kogito-cloud-operator/pkg/util"

	"github.com/stretchr/testify/assert"

	appsv1 "github.com/openshift/api/apps/v1"
	framework "github.com/operator-framework/operator-sdk/pkg/test"
	"github.com/operator-framework/operator-sdk/pkg/test/e2eutil"

	v1 "github.com/openshift/api/build/v1"
	"k8s.io/apimachinery/pkg/api/errors"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"
)

var (
	retryInterval             = time.Second * 5
	cleanupRetryInterval      = time.Second * 3
	cleanupTimeout            = time.Second * 10
	waitForDeploymentInterval = time.Minute * 1
	log                       = logger.GetLogger("kogito_operator_e2e")
)

func TestKogitoApp(t *testing.T) {
	kogitoAppList := &v1alpha1.KogitoAppList{}
	err := framework.AddToFrameworkScheme(apis.AddToScheme, kogitoAppList)
	if err != nil {
		t.Fatalf("failed to add custom resource scheme to framework: %v", err)
	}
	// more clusters can be added
	t.Run("kogitoapp", func(t *testing.T) {
		t.Run("Cluster1", KogitoAppCluster)
	})
}

func KogitoAppCluster(t *testing.T) {
	t.Parallel()
	// initialize framework
	ctx := framework.NewTestCtx(t)
	defer ctx.Cleanup()
	// initialize cluster resources
	err := ctx.InitializeClusterResources(&framework.CleanupOptions{TestContext: ctx, Timeout: cleanupTimeout, RetryInterval: retryInterval})
	if err != nil {
		log.Fatalf("failed to initialize cluster resources: %v", err)
	}
	log.Info("Initialized cluster resources")
	// get our namespace
	namespace, err := ctx.GetNamespace()
	if err != nil {
		log.Fatal(err)
	}
	// get global framework variables
	f := framework.Global
	// wait for kogito-operator to be ready
	err = e2eutil.WaitForOperatorDeployment(t, f.KubeClient, namespace, "kogito-cloud-operator", 1, time.Second*20, time.Second*40)
	if err != nil {
		log.Fatal(err)
	}

	// Run our tests
	if err = deployKogitoQuarkusExample(t, f, ctx); err != nil {
		log.Fatal(err)
	}
}

func deployKogitoQuarkusExample(t *testing.T, f *framework.Framework, ctx *framework.TestCtx) error {
	gitProjectURI := "https://github.com/kiegroup/kogito-examples"
	contextDir := "drools-quarkus-example"
	appName := "example-quarkus"
	namespace, err := ctx.GetNamespace()
	if err != nil {
		return fmt.Errorf("could not get namespace: %v", err)
	}
	kogitoService := &v1alpha1.KogitoApp{
		ObjectMeta: metav1.ObjectMeta{
			Name:      appName,
			Namespace: namespace,
		},
		Status: v1alpha1.KogitoAppStatus{
			Conditions:  []v1alpha1.Condition{},
			Deployments: v1alpha1.Deployments{},
		},
		Spec: v1alpha1.KogitoAppSpec{
			Build: &v1alpha1.KogitoAppBuildObject{
				GitSource: &v1alpha1.GitSource{
					URI:        &gitProjectURI,
					ContextDir: contextDir,
				},
			},
		},
	}

	// clean up
	defer func() error {
		if err = f.Client.Get(goctx.TODO(), types.NamespacedName{Name: kogitoService.Name, Namespace: namespace}, kogitoService); err == nil {
			f.Client.Delete(goctx.TODO(), kogitoService)
		} else if !errors.IsNotFound(err) {
			return err
		}
		return nil
	}()

	tag := util.GetEnv("KOGITO_IMAGE_TAG", builder.ImageStreamTag)
	log.Infof("Using image tag %s", tag)

	// set tags (used in devel for non-released versions)
	kogitoService.Spec.Build.ImageRuntime.ImageStreamTag = tag
	kogitoService.Spec.Build.ImageS2I.ImageStreamTag = tag

	err = f.Client.Create(goctx.TODO(), kogitoService, &framework.CleanupOptions{TestContext: ctx, Timeout: cleanupTimeout, RetryInterval: cleanupRetryInterval})
	if err != nil {
		return err
	}

	bc := v1.BuildConfig{
		ObjectMeta: metav1.ObjectMeta{
			Name:      fmt.Sprintf("%s%s", kogitoService.Name, builder.BuildS2INameSuffix),
			Namespace: namespace,
		},
	}

	for i := 1; i <= 60; i++ {
		err = f.Client.Get(goctx.TODO(), types.NamespacedName{Name: bc.Name, Namespace: namespace}, &bc)
		if errors.IsNotFound(err) {
			log.Infof("Waiting for BuildConfig to become available, Time elapsed: %d minutes", time.Duration(i)*waitForDeploymentInterval*time.Second)
			time.Sleep(waitForDeploymentInterval)
		} else if err != nil {
			log.Fatalf("Impossible to find bc '%s' in namespace '%s'. Error: %s", bc.Name, namespace, err)
			return err
		}
	}

	assert.NotNil(t, bc)

	dc := appsv1.DeploymentConfig{}

	//wait for the build to finish
	for i := 1; i <= 60; i++ {
		if err = f.Client.Get(goctx.TODO(), types.NamespacedName{Name: appName, Namespace: namespace}, &dc); err != nil && errors.IsNotFound(err) {
			log.Infof("Waiting for DeploymentConfig to become available, Time elapsed: %d minutes", time.Duration(i)*waitForDeploymentInterval)
			time.Sleep(waitForDeploymentInterval)
		} else if err != nil {
			log.Fatalf("Error while fetching DC '%s' in namespace '%s'", appName, namespace)
			return err
		} else {
			log.Infof("DeploymentConfig '%s' is available", appName)
			break
		}
	}

	assert.NotNil(t, dc)
	assert.Len(t, dc.Spec.Template.Spec.Containers, 1)

	return nil
}
