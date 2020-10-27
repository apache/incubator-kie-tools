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

// InstallKogitoTrustyUI install the Kogito Management Console component
func InstallKogitoTrustyUI(installerType InstallerType, trustyUI *bddtypes.KogitoServiceHolder) error {
	return InstallService(trustyUI, installerType, "trusty-ui")
}

// WaitForKogitoTrustyUIService wait for Kogito Management Console to be deployed
func WaitForKogitoTrustyUIService(namespace string, replicas int, timeoutInMin int) error {
	return WaitForService(namespace, getTrustyUIServiceName(), replicas, timeoutInMin)
}

func getTrustyUIServiceName() string {
	return infrastructure.DefaultTrustyUIName
}

// GetKogitoTrustyUIResourceStub Get basic KogitoTrustyUI stub with all needed fields initialized
func GetKogitoTrustyUIResourceStub(namespace string, replicas int) *v1alpha1.KogitoSupportingService {
	return &v1alpha1.KogitoSupportingService{
		ObjectMeta: NewObjectMetadata(namespace, getTrustyUIServiceName()),
		Spec: v1alpha1.KogitoSupportingServiceSpec{
			ServiceType:       v1alpha1.TrustyUI,
			KogitoServiceSpec: NewKogitoServiceSpec(int32(replicas), config.GetTrustyUIImageTag(), infrastructure.DefaultTrustyUIImageName),
		},
		Status: v1alpha1.KogitoSupportingServiceStatus{
			KogitoServiceStatus: NewKogitoServiceStatus(),
		},
	}
}
