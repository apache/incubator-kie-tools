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
	"github.com/kiegroup/kogito-cloud-operator/pkg/apis/app/v1alpha1"
	"github.com/kiegroup/kogito-cloud-operator/pkg/infrastructure"
	"github.com/kiegroup/kogito-cloud-operator/test/config"
	bddtypes "github.com/kiegroup/kogito-cloud-operator/test/types"
)

// InstallKogitoExplainabilityService install the Kogito Explainability service
func InstallKogitoExplainabilityService(namespace string, installerType InstallerType, explainability *bddtypes.KogitoServiceHolder) error {
	// Persistence is already configured internally by the Explainability service, so we don't need to add any additional persistence step here.
	return InstallService(explainability, installerType, "explainability")
}

// WaitForKogitoExplainabilityService wait for Kogito Explainability to be deployed
func WaitForKogitoExplainabilityService(namespace string, replicas int, timeoutInMin int) error {
	return WaitForService(namespace, getExplainabilityServiceName(), replicas, timeoutInMin)
}

func getExplainabilityServiceName() string {
	return infrastructure.DefaultExplainabilityName
}

// GetKogitoExplainabilityResourceStub Get basic KogitoExplainability stub with all needed fields initialized
func GetKogitoExplainabilityResourceStub(namespace string, replicas int) *v1alpha1.KogitoExplainability {
	return &v1alpha1.KogitoExplainability{
		ObjectMeta: NewObjectMetadata(namespace, getExplainabilityServiceName()),
		Spec: v1alpha1.KogitoExplainabilitySpec{
			KogitoServiceSpec: NewKogitoServiceSpec(int32(replicas), config.GetExplainabilityImageTag(), infrastructure.DefaultExplainabilityImageName),
		},
		Status: v1alpha1.KogitoExplainabilityStatus{
			KogitoServiceStatus: NewKogitoServiceStatus(),
		},
	}
}
