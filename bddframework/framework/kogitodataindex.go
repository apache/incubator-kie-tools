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
)

// InstallKogitoDataIndexService install the Kogito Data Index service
func InstallKogitoDataIndexService(namespace string, installerType InstallerType, dataIndex *KogitoServiceHolder) error {
	// Persistence is already configured internally by the Data Index service, so we don't need to add any additional persistence step here.
	return InstallServiceWithoutCliFlags(dataIndex, installerType, "data-index")
}

// WaitForKogitoDataIndexService wait for Kogito Data Index to be deployed
func WaitForKogitoDataIndexService(namespace string, replicas int, timeoutInMin int) error {
	return WaitForService(namespace, getDataIndexServiceName(), replicas, timeoutInMin)
}

func getDataIndexServiceName() string {
	return infrastructure.DefaultDataIndexName
}

// GetKogitoDataIndexResourceStub Get basic KogitoDataIndex stub with all needed fields initialized
func GetKogitoDataIndexResourceStub(namespace string, replicas int) *v1alpha1.KogitoDataIndex {
	return &v1alpha1.KogitoDataIndex{
		ObjectMeta: NewObjectMetadata(namespace, getDataIndexServiceName()),
		Spec: v1alpha1.KogitoDataIndexSpec{
			KogitoServiceSpec: NewKogitoServiceSpec(int32(replicas), config.GetDataIndexImageTag(), infrastructure.DefaultDataIndexImageName),
		},
		Status: v1alpha1.KogitoDataIndexStatus{
			KogitoServiceStatus: NewKogitoServiceStatus(),
		},
	}
}
