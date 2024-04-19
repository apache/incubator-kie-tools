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
	// DefaultTaskConsoleName ...
	DefaultTaskConsoleName = "task-console"
	// DefaultTaskConsoleImageName ...
	DefaultTaskConsoleImageName = "kogito-task-console"
)

// InstallKogitoTaskConsole install the Kogito Task Console component
func InstallKogitoTaskConsole(installerType InstallerType, taskConsole *bddtypes.KogitoServiceHolder) error {
	return InstallService(taskConsole, installerType, "task-console")
}

// WaitForKogitoTaskConsoleService wait for Kogito Task Console to be deployed
func WaitForKogitoTaskConsoleService(namespace string, replicas int, timeoutInMin int) error {
	return WaitForService(namespace, getTaskConsoleServiceName(), replicas, timeoutInMin)
}

func getTaskConsoleServiceName() string {
	return DefaultTaskConsoleName
}

// GetKogitoTaskConsoleResourceStub Get basic KogitoTaskConsole stub with all needed fields initialized
func GetKogitoTaskConsoleResourceStub(namespace string, replicas int) *v1beta1.KogitoSupportingService {
	return &v1beta1.KogitoSupportingService{
		ObjectMeta: NewObjectMetadata(namespace, getTaskConsoleServiceName()),
		Spec: v1beta1.KogitoSupportingServiceSpec{
			ServiceType:       api.TaskConsole,
			KogitoServiceSpec: NewKogitoServiceSpec(int32(replicas), config.GetServiceImageTag(config.TaskConsoleImageType, config.EphemeralPersistenceType), DefaultTaskConsoleImageName),
		},
	}
}
