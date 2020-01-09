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

package e2e

import (
	"github.com/kiegroup/kogito-cloud-operator/pkg/apis/app/v1alpha1"
	"github.com/kiegroup/kogito-cloud-operator/pkg/util"
	olmapiv1 "github.com/operator-framework/operator-lifecycle-manager/pkg/api/apis/operators/v1"
	olmapiv1alpha1 "github.com/operator-framework/operator-lifecycle-manager/pkg/api/apis/operators/v1alpha1"

	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

func getKogitoServiceStub(appName string, namespace string) *v1alpha1.KogitoApp {

	kogitoService := &v1alpha1.KogitoApp{
		ObjectMeta: metav1.ObjectMeta{
			Name:      appName,
			Namespace: namespace,
		},
		Status: v1alpha1.KogitoAppStatus{
			ConditionsMeta: v1alpha1.ConditionsMeta{Conditions: make([]v1alpha1.Condition, 0)},
			Deployments:    v1alpha1.Deployments{},
		},
		Spec: v1alpha1.KogitoAppSpec{
			Build: &v1alpha1.KogitoAppBuildObject{
				Env: []v1alpha1.Env{v1alpha1.Env{
					Name:  "MAVEN_MIRROR_URL",
					Value: util.GetOSEnv("MAVEN_MIRROR_URL", ""),
				}},
				GitSource: &v1alpha1.GitSource{},
			},
		},
	}

	return kogitoService
}

func getOperatorGroup(operatorGroupName string, namespace string) *olmapiv1.OperatorGroup {

	operatorGroup := &olmapiv1.OperatorGroup{
		ObjectMeta: metav1.ObjectMeta{
			Name:      operatorGroupName,
			Namespace: namespace,
		},
		Spec: olmapiv1.OperatorGroupSpec{
			TargetNamespaces: []string{namespace},
		},
	}
	return operatorGroup
}

func getSubscriptionSingleNamespace(subscriptionName string, namespace string, operatorName string, operatorSource string, channel string) *olmapiv1alpha1.Subscription {

	subscription := &olmapiv1alpha1.Subscription{
		ObjectMeta: metav1.ObjectMeta{
			Name:      subscriptionName,
			Namespace: namespace,
		},
		Spec: &olmapiv1alpha1.SubscriptionSpec{
			Package:                operatorName,
			CatalogSource:          operatorSource,
			CatalogSourceNamespace: "openshift-marketplace",
			Channel:                channel,
		},
	}

	return subscription
}
