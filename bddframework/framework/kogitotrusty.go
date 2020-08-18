// Copyright 2020 Red Hat, Inc. and/or its affiliates
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

// InstallKogitoTrustyService install the Kogito Trusty service
func InstallKogitoTrustyService(namespace string, installerType InstallerType, trusty *bddtypes.KogitoServiceHolder) error {
	// Persistence is already configured internally by the Trusty service, so we don't need to add any additional persistence step here.
	return InstallService(trusty, installerType, "trusty")
}

// WaitForKogitoTrustyService wait for Kogito Trusty to be deployed
func WaitForKogitoTrustyService(namespace string, replicas int, timeoutInMin int) error {
	return WaitForService(namespace, getTrustyServiceName(), replicas, timeoutInMin)
}

func getTrustyServiceName() string {
	return infrastructure.DefaultTrustyName
}

// GetKogitoTrustyResourceStub Get basic KogitoTrusty stub with all needed fields initialized
func GetKogitoTrustyResourceStub(namespace string, replicas int) *v1alpha1.KogitoTrusty {
	return &v1alpha1.KogitoTrusty{
		ObjectMeta: NewObjectMetadata(namespace, getTrustyServiceName()),
		Spec: v1alpha1.KogitoTrustySpec{
			KogitoServiceSpec: NewKogitoServiceSpec(int32(replicas), config.GetTrustyImageTag(), infrastructure.DefaultTrustyImageName),
		},
		Status: v1alpha1.KogitoTrustyStatus{
			KogitoServiceStatus: NewKogitoServiceStatus(),
		},
	}
}
