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

package framework

import (
	"fmt"

	"k8s.io/apimachinery/pkg/api/errors"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"

	"github.com/kiegroup/kogito-cloud-operator/pkg/apis/app/v1alpha1"
	"github.com/kiegroup/kogito-cloud-operator/pkg/client/kubernetes"
)

const (
	mavenArgsAppendEnvVar = "MAVEN_ARGS_APPEND"
	mavenMirrorURLEnvVar  = "MAVEN_MIRROR_URL"
)

// DeployQuarkusExample deploy a Quarkus example
func DeployQuarkusExample(namespace, appName, contextDir string, native, persistence, events bool) error {
	GetLogger(namespace).Infof("Deploy quarkus example %s with name %s, native %v and persistence %v", contextDir, appName, native, persistence)
	return DeployExample(namespace, appName, contextDir, "quarkus", native, persistence, events)
}

// DeploySpringBootExample deploys a Spring boot example
func DeploySpringBootExample(namespace, appName, contextDir string, persistence, events bool) error {
	GetLogger(namespace).Infof("Deploy spring boot example %s with name %s and persistence %v", contextDir, appName, persistence)
	return DeployExample(namespace, appName, contextDir, "springboot", false, persistence, events)
}

// DeployExample deploys an example
func DeployExample(namespace, appName, contextDir, runtime string, native, persistence, events bool) error {
	kogitoApp := getKogitoAppStub(namespace, appName)
	if runtime == "quarkus" {
		kogitoApp.Spec.Runtime = v1alpha1.QuarkusRuntimeType
	} else if runtime == "springboot" {
		kogitoApp.Spec.Runtime = v1alpha1.SpringbootRuntimeType
	}

	gitProjectURI := GetConfigExamplesRepositoryURI()
	kogitoApp.Spec.Build.Native = native
	kogitoApp.Spec.Build.GitSource.URI = &gitProjectURI
	kogitoApp.Spec.Build.GitSource.ContextDir = contextDir
	kogitoApp.Spec.Build.GitSource.Reference = GetConfigExamplesRepositoryRef()

	profiles := ""
	if persistence {
		profiles = profiles + "persistence,"
		kogitoApp.Spec.Infra.InstallInfinispan = v1alpha1.KogitoAppInfraInstallInfinispanAlways
	}
	if events {
		profiles = profiles + "events,"
		kogitoApp.Spec.Infra.InstallKafka = v1alpha1.KogitoAppInfraInstallKafkaAlways
		appendNewEnvToKogitoApp(kogitoApp, "MP_MESSAGING_OUTGOING_KOGITO_PROCESSINSTANCES_EVENTS_BOOTSTRAP_SERVERS", "")
		appendNewEnvToKogitoApp(kogitoApp, "MP_MESSAGING_OUTGOING_KOGITO_USERTASKINSTANCES_EVENTS_BOOTSTRAP_SERVERS", "")
	}

	if len(profiles) > 0 {
		appendNewEnvToKogitoAppBuild(kogitoApp, mavenArgsAppendEnvVar, "-P"+profiles)
	}

	setupBuildImageStreams(kogitoApp)

	if _, err := kubernetes.ResourceC(kubeClient).CreateIfNotExists(kogitoApp); err != nil {
		return fmt.Errorf("Error creating example service %s: %v", appName, err)
	}
	return nil
}

// SetKogitoAppReplicas sets the number of replicas for a Kogito application
func SetKogitoAppReplicas(namespace, name string, nbPods int) error {
	GetLogger(namespace).Infof("Set Kogito application %s replica number to %d", name, nbPods)
	kogitoApp, err := getKogitoApp(namespace, name)
	if err != nil {
		return err
	} else if kogitoApp == nil {
		return fmt.Errorf("No KogitoApp found with name %s in namespace %s", name, namespace)
	}
	replicas := int32(nbPods)
	kogitoApp.Spec.Replicas = &replicas
	return kubernetes.ResourceC(kubeClient).Update(kogitoApp)
}

func getKogitoAppStub(namespace, appName string) *v1alpha1.KogitoApp {
	kogitoApp := &v1alpha1.KogitoApp{
		ObjectMeta: metav1.ObjectMeta{
			Name:      appName,
			Namespace: namespace,
		},
		Status: v1alpha1.KogitoAppStatus{
			ConditionsMeta: v1alpha1.ConditionsMeta{Conditions: []v1alpha1.Condition{}},
			Deployments:    v1alpha1.Deployments{},
		},
		Spec: v1alpha1.KogitoAppSpec{
			Build: &v1alpha1.KogitoAppBuildObject{
				Env:       []v1alpha1.Env{},
				GitSource: &v1alpha1.GitSource{},
			},
		},
	}

	if mavenMirrorURL := GetConfigMavenMirrorURL(); len(mavenMirrorURL) > 0 {
		kogitoApp.Spec.Build.MavenMirrorURL = mavenMirrorURL
	}

	return kogitoApp
}

func getKogitoApp(namespace, name string) (*v1alpha1.KogitoApp, error) {
	kogitoApp := &v1alpha1.KogitoApp{}
	if exists, err := kubernetes.ResourceC(kubeClient).FetchWithKey(types.NamespacedName{Name: name, Namespace: namespace}, kogitoApp); err != nil && !errors.IsNotFound(err) {
		return nil, fmt.Errorf("Error while trying to look for KogitoApp %s: %v ", name, err)
	} else if errors.IsNotFound(err) || !exists {
		return nil, nil
	}
	return kogitoApp, nil
}

func appendNewEnvToKogitoAppBuild(kogitoApp *v1alpha1.KogitoApp, name, value string) {
	env := v1alpha1.Env{
		Name:  name,
		Value: value,
	}
	kogitoApp.Spec.Build.Env = append(kogitoApp.Spec.Build.Env, env)
}

func appendNewEnvToKogitoApp(kogitoApp *v1alpha1.KogitoApp, name, value string) {
	env := v1alpha1.Env{
		Name:  name,
		Value: value,
	}
	kogitoApp.Spec.Env = append(kogitoApp.Spec.Env, env)
}

func setupBuildImageStreams(kogitoApp *v1alpha1.KogitoApp) {
	// If "KOGITO_BUILD_IMAGE_STREAM_TAG" is defined, it is taken into account
	// If not defined then search for specific s2i and runtime tags
	// If none, let the operator manage
	kogitoApp.Spec.Build.ImageS2ITag = GetConfigBuildS2IImageStreamTag()
	kogitoApp.Spec.Build.ImageRuntimeTag = GetConfigBuildRuntimeImageStreamTag()
	// If "KOGITO_BUILD_IMAGE_VERSION" is defined, it's taken into account, otherwise set the current version
	kogitoApp.Spec.Build.ImageVersion = GetConfigBuildImageVersion()
}
