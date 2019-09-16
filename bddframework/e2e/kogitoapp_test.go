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
	"log"
	"testing"
	"time"

	"github.com/kiegroup/kogito-cloud-operator/pkg/client/meta"
	"github.com/kiegroup/kogito-cloud-operator/pkg/controller/kogitoapp/builder"

	"github.com/stretchr/testify/assert"

	"github.com/kiegroup/kogito-cloud-operator/pkg/apis"
	"github.com/kiegroup/kogito-cloud-operator/pkg/apis/app/v1alpha1"
	appsv1 "github.com/openshift/api/apps/v1"
	framework "github.com/operator-framework/operator-sdk/pkg/test"
	"github.com/operator-framework/operator-sdk/pkg/test/e2eutil"
	"k8s.io/apimachinery/pkg/api/errors"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"
)

var (
	retryInterval             = time.Second * 5
	timeout                   = time.Second * 60
	cleanupRetryInterval      = time.Second * 1
	cleanupTimeout            = time.Second * 5
	waitForDeploymentInterval = time.Minute * 1
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
	err := ctx.InitializeClusterResources(&framework.CleanupOptions{TestContext: ctx, Timeout: cleanupTimeout, RetryInterval: cleanupRetryInterval})
	if err != nil {
		t.Fatalf("failed to initialize cluster resources: %v", err)
	}
	t.Log("Initialized cluster resources")
	// get our namespace
	namespace, err := ctx.GetNamespace()
	if err != nil {
		t.Fatal(err)
	}
	// get global framework variables
	f := framework.Global
	// wait for kogito-operator to be ready
	err = e2eutil.WaitForOperatorDeployment(t, f.KubeClient, namespace, "kogito-cloud-operator", 1, time.Second*5, time.Second*30)
	if err != nil {
		t.Fatal(err)
	}

	// Run our tests
	if err = kogitoOperatorHappyPathTest(t, f, ctx); err != nil {
		t.Fatal(err)
	}
}

func kogitoOperatorHappyPathTest(t *testing.T, f *framework.Framework, ctx *framework.TestCtx) error {
	gitProjectURI := "https://github.com/kiegroup/kogito-examples"
	contextDir := "drools-quarkus-example"
	appName := "example-quarkus"
	namespace, err := ctx.GetNamespace()
	if err != nil {
		return fmt.Errorf("could not get namespace: %v", err)
	}
	exampleKogitoOperator := &v1alpha1.KogitoApp{
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

	err = f.Client.Create(goctx.TODO(), exampleKogitoOperator, &framework.CleanupOptions{TestContext: ctx, Timeout: cleanupTimeout, RetryInterval: cleanupRetryInterval})
	if err != nil {
		return err
	}

	bc, _ := builder.NewBuildConfigS2I(exampleKogitoOperator)
	err = f.Client.Get(goctx.TODO(), types.NamespacedName{Name: bc.Name, Namespace: namespace}, &bc)
	if err != nil {
		log.Fatalf("Impossible to find bc '%s' in namespace '%s'", bc.Name, namespace)
		return err
	}

	dc := appsv1.DeploymentConfig{}
	meta.SetGroupVersionKind(&dc.TypeMeta, meta.KindDeploymentConfig)

	//wait for the build to finish
	for i := 1; i <= 60; i++ {
		if err = f.Client.Get(goctx.TODO(), types.NamespacedName{Name: appName, Namespace: namespace}, &dc); err != nil && errors.IsNotFound(err) {
			log.Printf("Waiting for DeploymentConfig to become available, Time elapsed: %d minutes", time.Duration(i)*waitForDeploymentInterval)
			time.Sleep(waitForDeploymentInterval)
		} else if err != nil {
			log.Fatalf("Error while fetching DC '%s' in namespace '%s'", appName, namespace)
			return err
		} else {
			log.Printf("DeploymentConfig '%s' is available", appName)
			break
		}
	}

	assert.NotNil(t, dc)
	assert.Len(t, dc.Spec.Template.Spec.Containers, 1)

	return nil
}
