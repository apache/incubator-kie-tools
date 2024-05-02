/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package meta

import (
	appsv1 "github.com/openshift/api/apps/v1"
	buildv1 "github.com/openshift/api/build/v1"
	imgv1 "github.com/openshift/api/image/v1"
	routev1 "github.com/openshift/api/route/v1"
	coreappsv1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	rbac "k8s.io/api/rbac/v1"
	eventing "knative.dev/eventing/pkg/apis/eventing/v1"
	sources "knative.dev/eventing/pkg/apis/sources/v1"

	hyperfoil "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/api/hyperfoil/v1alpha2"
	grafana "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework/infrastructure/grafana/v1alpha1"
	infinispan "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework/infrastructure/infinispan/v1"
	kafka "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework/infrastructure/kafka/v1beta2"
	keycloak "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework/infrastructure/keycloak/v1alpha1"
	mongodb "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework/infrastructure/mongodb/v1"

	apiextensionsv1 "k8s.io/apiextensions-apiserver/pkg/apis/apiextensions/v1"
	apiextensionsv1beta1 "k8s.io/apiextensions-apiserver/pkg/apis/apiextensions/v1beta1"

	olmapiv1 "github.com/operator-framework/operator-lifecycle-manager/pkg/api/apis/operators/v1"
	olmapiv1alpha1 "github.com/operator-framework/operator-lifecycle-manager/pkg/api/apis/operators/v1alpha1"
	olmv1 "github.com/operator-framework/operator-lifecycle-manager/pkg/package-server/apis/operators/v1"
	prometheus "github.com/prometheus-operator/prometheus-operator/pkg/apis/monitoring/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/runtime"
	clientgoscheme "k8s.io/client-go/kubernetes/scheme"
)

// GetRegisteredSchema gets all schema and types registered for use with CLI, unit tests, custom clients and so on
func GetRegisteredSchema() *runtime.Scheme {
	s := runtime.NewScheme()
	schemes := getRegisteredSchemeBuilder()
	err := schemes.AddToScheme(s)
	if err != nil {
		panic(err)
	}

	// https://issues.jboss.org/browse/KOGITO-617
	metav1.AddToGroupVersion(s, apiextensionsv1.SchemeGroupVersion)
	metav1.AddToGroupVersion(s, appsv1.GroupVersion)
	metav1.AddToGroupVersion(s, prometheus.SchemeGroupVersion)
	metav1.AddToGroupVersion(s, routev1.GroupVersion)
	metav1.AddToGroupVersion(s, infinispan.SchemeGroupVersion)
	metav1.AddToGroupVersion(s, mongodb.SchemeBuilder.GroupVersion)
	metav1.AddToGroupVersion(s, kafka.SchemeGroupVersion)
	metav1.AddToGroupVersion(s, grafana.GroupVersion)
	metav1.AddToGroupVersion(s, eventing.SchemeGroupVersion)
	metav1.AddToGroupVersion(s, sources.SchemeGroupVersion)
	metav1.AddToGroupVersion(s, hyperfoil.GroupVersion)
	metav1.AddToGroupVersion(s, olmapiv1.SchemeGroupVersion)
	metav1.AddToGroupVersion(s, olmapiv1alpha1.SchemeGroupVersion)
	metav1.AddToGroupVersion(s, olmv1.SchemeGroupVersion)
	return s
}

// getRegisteredSchemeBuilder gets the SchemeBuilder with all the desired APIs registered
func getRegisteredSchemeBuilder() runtime.SchemeBuilder {
	return runtime.NewSchemeBuilder(
		clientgoscheme.AddToScheme,
		corev1.AddToScheme,
		coreappsv1.AddToScheme,
		buildv1.Install,
		rbac.AddToScheme,
		appsv1.Install,
		coreappsv1.AddToScheme,
		routev1.Install,
		imgv1.Install,
		apiextensionsv1.AddToScheme,
		kafka.SchemeBuilder.AddToScheme,
		mongodb.SchemeBuilder.AddToScheme,
		infinispan.AddToScheme,
		keycloak.SchemeBuilder.AddToScheme,
		prometheus.SchemeBuilder.AddToScheme,
		eventing.AddToScheme, sources.AddToScheme,
		grafana.AddToScheme,
		hyperfoil.AddToScheme,
		olmapiv1alpha1.AddToScheme,
		olmapiv1.AddToScheme,
		olmv1.AddToScheme,
		// Required for MogoDB, can be removed once we start using newer MongoDB operator version
		apiextensionsv1beta1.AddToScheme)
}
