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

package framework

import (
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/api"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/api/app/v1beta1"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/config"
	bddtypes "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/types"
)

const (
	// DefaultMgmtConsoleName ...
	DefaultMgmtConsoleName = "management-console"
	// DefaultMgmtConsoleImageName ...
	DefaultMgmtConsoleImageName = "kogito-management-console"
)

// InstallKogitoManagementConsole install the Kogito Management Console component
func InstallKogitoManagementConsole(installerType InstallerType, managementConsole *bddtypes.KogitoServiceHolder) error {
	return InstallService(managementConsole, installerType, "mgmt-console")
}

// WaitForKogitoManagementConsoleService wait for Kogito Management Console to be deployed
func WaitForKogitoManagementConsoleService(namespace string, replicas int, timeoutInMin int) error {
	return WaitForService(namespace, getManagementConsoleServiceName(), replicas, timeoutInMin)
}

func getManagementConsoleServiceName() string {
	return DefaultMgmtConsoleName
}

// GetKogitoManagementConsoleResourceStub Get basic KogitoManagementConsole stub with all needed fields initialized
func GetKogitoManagementConsoleResourceStub(namespace string, replicas int) *v1beta1.KogitoSupportingService {
	return &v1beta1.KogitoSupportingService{
		ObjectMeta: NewObjectMetadata(namespace, getManagementConsoleServiceName()),
		Spec: v1beta1.KogitoSupportingServiceSpec{
			ServiceType:       api.MgmtConsole,
			KogitoServiceSpec: NewKogitoServiceSpec(int32(replicas), config.GetServiceImageTag(config.ManagementConsoleImageType, config.EphemeralPersistenceType), DefaultMgmtConsoleImageName),
		},
	}
}
