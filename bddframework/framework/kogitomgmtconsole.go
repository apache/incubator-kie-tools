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
)

// InstallKogitoManagementConsole install the Kogito Management Console component
func InstallKogitoManagementConsole(namespace string, installerType InstallerType, replicas int) error {
	resource := newManagementConsoleResource(namespace, replicas)
	return InstallServiceWithoutCliFlags(&KogitoServiceHolder{KogitoService: resource}, installerType, "mgmt-console")
}

// WaitForKogitoManagementConsoleService wait for Kogito Management Console to be deployed
func WaitForKogitoManagementConsoleService(namespace string, replicas int, timeoutInMin int) error {
	return WaitForService(namespace, getManagementConsoleServiceName(), replicas, timeoutInMin)
}

func getManagementConsoleServiceName() string {
	return infrastructure.DefaultMgmtConsoleName
}

func newManagementConsoleResource(namespace string, replicas int) *v1alpha1.KogitoMgmtConsole {
	return &v1alpha1.KogitoMgmtConsole{
		ObjectMeta: NewObjectMetadata(namespace, getManagementConsoleServiceName()),
		Spec: v1alpha1.KogitoMgmtConsoleSpec{
			KogitoServiceSpec: NewKogitoServiceSpec(int32(replicas), config.GetManagementConsoleImageTag(), infrastructure.DefaultMgmtConsoleImageName),
		},
		Status: v1alpha1.KogitoMgmtConsoleStatus{
			KogitoServiceStatus: NewKogitoServiceStatus(),
		},
	}
}
