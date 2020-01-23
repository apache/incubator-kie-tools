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
	"github.com/kiegroup/kogito-cloud-operator/pkg/controller/kogitoapp/resource"
	"github.com/kiegroup/kogito-cloud-operator/pkg/logger"
	"github.com/kiegroup/kogito-cloud-operator/pkg/util"
	"github.com/stretchr/testify/assert"

	appsv1 "github.com/openshift/api/apps/v1"
	framework "github.com/operator-framework/operator-sdk/pkg/test"
	"github.com/operator-framework/operator-sdk/pkg/test/e2eutil"

	v1 "github.com/openshift/api/build/v1"

	olmapiv1 "github.com/operator-framework/operator-lifecycle-manager/pkg/api/apis/operators/v1"
	olmapiv1alpha1 "github.com/operator-framework/operator-lifecycle-manager/pkg/api/apis/operators/v1alpha1"

	"k8s.io/apimachinery/pkg/api/errors"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"
)

var (
	retryInterval             = time.Second * 5
	cleanupRetryInterval      = time.Second * 3
	cleanupTimeout            = time.Second * 10
	waitForDeploymentInterval = time.Minute * 2
	log                       = logger.GetLogger("kogito_operator_e2e")
)

func TestKogitoApp(t *testing.T) {

	kogitoAppList := &v1alpha1.KogitoAppList{}
	err := framework.AddToFrameworkScheme(apis.AddToScheme, kogitoAppList)
	if err != nil {
		t.Fatalf("failed to add custom resource scheme to framework: %v", err)
	}
	operatorGroupList := &olmapiv1.OperatorGroup{}
	err = framework.AddToFrameworkScheme(olmapiv1.AddToScheme, operatorGroupList)
	if err != nil {
		t.Fatalf("failed to add OperatorGroup to framework: %v", err)
	}
	subscriptionList := &olmapiv1alpha1.SubscriptionList{}
	err = framework.AddToFrameworkScheme(olmapiv1alpha1.AddToScheme, subscriptionList)
	if err != nil {
		t.Fatalf("failed to add Subscription to framework: %v", err)
	}

	// Specifies what tests should be executed
	tests := util.GetOSEnv("TESTS", "full")
	if tests == "jvm" {
		// Run just JVM tests
		t.Run("kogitoapp", func(t *testing.T) {
			t.Run("QuarkusJvm", testKogitoQuarkusJvmExample)
			t.Run("SpringBootJvm", testKogitoSpringBootJvmExample)
			t.Run("QuarkusJvmPersistent", testKogitoQuarkusJvmPersistentExample)
		})
	} else if tests == "native" {
		// Run just native tests
		t.Run("kogitoapp", func(t *testing.T) {
			t.Run("QuarkusNative", testKogitoQuarkusNativeExample)
		})
	} else if tests == "full" {
		// Run whole test suite
		t.Run("kogitoapp", func(t *testing.T) {
			t.Run("QuarkusJvm", testKogitoQuarkusJvmExample)
			t.Run("QuarkusNative", testKogitoQuarkusNativeExample)
			t.Run("SpringBootJvm", testKogitoSpringBootJvmExample)
			t.Run("QuarkusJvmPersistent", testKogitoQuarkusJvmPersistentExample)
		})
	} else {
		log.Fatalf("wrong value of tests parameter: %v", tests)
	}
}

func testKogitoQuarkusJvmExample(t *testing.T) {
	// initialize framework
	ctx := framework.NewTestCtx(t)
	defer ctx.Cleanup()

	appName := "example-quarkus-jvm"
	namespace := getNamespace(ctx)

	kogitoService := getKogitoServiceStub(appName, namespace)
	kogitoService.Spec.Build.Native = false

	deployAndTestDroolsQuarkusExampleWithKogitoService(t, ctx, kogitoService)
}

func testKogitoQuarkusNativeExample(t *testing.T) {
	// initialize framework
	ctx := framework.NewTestCtx(t)
	defer ctx.Cleanup()

	appName := "example-quarkus-native"
	namespace := getNamespace(ctx)

	kogitoService := getKogitoServiceStub(appName, namespace)
	kogitoService.Spec.Build.Native = true

	deployAndTestDroolsQuarkusExampleWithKogitoService(t, ctx, kogitoService)
}

func testKogitoSpringBootJvmExample(t *testing.T) {
	// initialize framework
	ctx := framework.NewTestCtx(t)
	defer ctx.Cleanup()

	appName := "example-springboot-jvm"
	namespace := getNamespace(ctx)

	kogitoService := getKogitoServiceStub(appName, namespace)

	deployAndTestJbpmSpringBootExampleWithKogitoService(t, ctx, kogitoService)
}

func testKogitoQuarkusJvmPersistentExample(t *testing.T) {
	// initialize framework
	ctx := framework.NewTestCtx(t)
	defer ctx.Cleanup()

	appName := "example-quarkus-jvm-persistent"
	namespace := getNamespace(ctx)

	kogitoService := getKogitoServiceStub(appName, namespace)

	deployAndTestJbpmQuarkusExampleWithPersistenceWithKogitoService(t, ctx, kogitoService)
}

func deployAndTestDroolsQuarkusExampleWithKogitoService(t *testing.T, ctx *framework.TestCtx, kogitoService *v1alpha1.KogitoApp) {
	t.Parallel()

	// get global framework variables
	f := framework.Global

	initializeKogitoOperator(t, f, ctx)

	gitProjectURI := "https://github.com/kiegroup/kogito-examples"
	contextDir := "drools-quarkus-example"

	kogitoService.Spec.Build.GitSource.URI = &gitProjectURI
	kogitoService.Spec.Build.GitSource.ContextDir = contextDir

	deployKogitoServiceApp(t, kogitoService, f, ctx)
	verifyDroolsQuarkusExample(t, kogitoService)
}

func deployAndTestJbpmSpringBootExampleWithKogitoService(t *testing.T, ctx *framework.TestCtx, kogitoService *v1alpha1.KogitoApp) {
	t.Parallel()

	// get global framework variables
	f := framework.Global

	initializeKogitoOperator(t, f, ctx)

	gitProjectURI := "https://github.com/kiegroup/kogito-examples"
	contextDir := "jbpm-springboot-example"

	kogitoService.Spec.Build.GitSource.URI = &gitProjectURI
	kogitoService.Spec.Build.GitSource.ContextDir = contextDir
	kogitoService.Spec.Runtime = v1alpha1.SpringbootRuntimeType

	deployKogitoServiceApp(t, kogitoService, f, ctx)
	verifyJbpmSpringBootExample(t, kogitoService)
}

func deployAndTestJbpmQuarkusExampleWithPersistenceWithKogitoService(t *testing.T, ctx *framework.TestCtx, kogitoService *v1alpha1.KogitoApp) {
	t.Parallel()

	// get global framework variables
	f := framework.Global

	initializeKogitoOperator(t, f, ctx)
	initializeInfinispanOperator(t, f, ctx)

	gitProjectURI := "https://github.com/sutaakar/submarine-examples" // temporary change due to workaround, can be reverted to https://github.com/kiegroup/kogito-examples once Quarkus is able to propagate env variables
	contextDir := "jbpm-quarkus-example"

	kogitoService.Spec.Build.GitSource.URI = &gitProjectURI
	kogitoService.Spec.Build.GitSource.ContextDir = contextDir
	kogitoService.Spec.Build.Env = append(kogitoService.Spec.Build.Env, v1alpha1.Env{
		Name:  "MAVEN_ARGS_APPEND",
		Value: "-Ppersistence",
	})
	kogitoService.Spec.Infra.InstallInfinispan = "Always" // Workaround for OCP 4.x, can be removed once https://issues.redhat.com/browse/KOGITO-702 is fixed.

	deployKogitoServiceApp(t, kogitoService, f, ctx)
	verifyJbpmQuarkusPersistentExample(t, f, kogitoService)
}

func initializeKogitoOperator(t *testing.T, f *framework.Framework, ctx *framework.TestCtx) {
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

	// wait for kogito-operator to be ready
	err = e2eutil.WaitForOperatorDeployment(t, f.KubeClient, namespace, "kogito-operator", 1, time.Second*20, time.Second*40)
	if err != nil {
		log.Fatal(err)
	}
}

func initializeInfinispanOperator(t *testing.T, f *framework.Framework, ctx *framework.TestCtx) {
	// get our namespace
	namespace, err := ctx.GetNamespace()
	if err != nil {
		log.Fatal(err)
	}

	log.Info("Installing Infinispan operator")

	operatorGroup := getOperatorGroup(namespace, namespace)
	err = f.Client.Create(goctx.TODO(), operatorGroup, &framework.CleanupOptions{TestContext: ctx, Timeout: cleanupTimeout, RetryInterval: cleanupRetryInterval})
	if err != nil {
		log.Fatalf("could not create operator group: %v", err)
	}

	subscription := getSubscriptionSingleNamespace("infinispan", namespace, "infinispan", "community-operators", "stable")
	err = f.Client.Create(goctx.TODO(), subscription, &framework.CleanupOptions{TestContext: ctx, Timeout: cleanupTimeout, RetryInterval: cleanupRetryInterval})
	if err != nil {
		log.Fatalf("could not create subscription: %v", err)
	}

	// wait for infinispan operator to be ready
	err = e2eutil.WaitForDeployment(t, f.KubeClient, namespace, "infinispan-operator", 1, time.Second*5, time.Minute*3)
	if err != nil {
		log.Fatal(err)
	}
}

func deployKogitoServiceApp(t *testing.T, kogitoService *v1alpha1.KogitoApp, f *framework.Framework, ctx *framework.TestCtx) {
	namespace, err := ctx.GetNamespace()
	if err != nil {
		log.Fatal(err)
	}
	appName := kogitoService.Name
	tag := util.GetOSEnv("KOGITO_IMAGE_TAG", resource.ImageStreamTag)
	log.Infof("Using image tag %s", tag)

	// set tags (used in devel for non-released versions)
	kogitoService.Spec.Build.ImageRuntimeTag = tag
	kogitoService.Spec.Build.ImageS2ITag = tag

	err = f.Client.Create(goctx.TODO(), kogitoService, &framework.CleanupOptions{TestContext: ctx, Timeout: cleanupTimeout, RetryInterval: cleanupRetryInterval})
	if err != nil {
		log.Fatalf("could not create kogito service CR: %v", err)
	}

	bc := v1.BuildConfig{
		ObjectMeta: metav1.ObjectMeta{
			Name:      fmt.Sprintf("%s%s", kogitoService.Name, resource.BuildS2INameSuffix),
			Namespace: namespace,
		},
	}

	// wait for the buildconfig to be available
	for i := 1; i <= 60; i++ {
		err = f.Client.Get(goctx.TODO(), types.NamespacedName{Name: bc.Name, Namespace: namespace}, &bc)
		if errors.IsNotFound(err) {
			log.Infof("Waiting for BuildConfig to become available, Time elapsed: %d minutes", time.Duration(i)*waitForDeploymentInterval/time.Minute)
			time.Sleep(waitForDeploymentInterval)
		} else if err != nil {
			log.Fatalf("Impossible to find bc '%s' in namespace '%s'. Error: %s", bc.Name, namespace, err)
		}
	}

	assert.NotNil(t, bc)

	dc := appsv1.DeploymentConfig{}

	//wait for the build to finish
	for i := 1; i <= 60; i++ {
		if err = f.Client.Get(goctx.TODO(), types.NamespacedName{Name: appName, Namespace: namespace}, &dc); err != nil && errors.IsNotFound(err) {
			log.Infof("Waiting for DeploymentConfig to become available, Time elapsed: %d minutes", time.Duration(i)*waitForDeploymentInterval/time.Minute)
			time.Sleep(waitForDeploymentInterval)
		} else if err != nil {
			log.Fatalf("Error while fetching DC '%s' in namespace '%s'", appName, namespace)
		} else {
			log.Infof("DeploymentConfig '%s' is available", appName)
			break
		}
	}

	//wait for the app to be deployed
	for i := 1; i <= 60; i++ {
		if err = f.Client.Get(goctx.TODO(), types.NamespacedName{Name: appName, Namespace: namespace}, &dc); err != nil {
			log.Fatalf("could not get deployment config: %v", err)
		}

		if dc.Status.AvailableReplicas > 0 {
			break
		} else {
			log.Infof("Waiting for replica pods to become available, Time elapsed: %d minutes", time.Duration(i)*waitForDeploymentInterval/time.Minute)
			time.Sleep(waitForDeploymentInterval)
		}
	}

	assert.NotNil(t, dc)
	assert.Len(t, dc.Spec.Template.Spec.Containers, 1)

	f.Client.Get(goctx.TODO(), types.NamespacedName{Name: kogitoService.Name, Namespace: namespace}, kogitoService)
	assert.NotEmpty(t, kogitoService.Status.Route)
}

func getNamespace(ctx *framework.TestCtx) string {
	namespace, err := ctx.GetNamespace()
	if err != nil {
		log.Fatalf("could not get namespace: %v", err)
	}
	return namespace
}
