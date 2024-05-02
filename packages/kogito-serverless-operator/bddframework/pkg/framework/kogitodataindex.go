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
	// DataIndexInfinispanImageName is the image name for the Data Index Service with Infinispan
	DataIndexInfinispanImageName = "kogito-data-index-infinispan"
	// DataIndexMongoDBImageName is the image name for the Data Index Service with MongoDB
	DataIndexMongoDBImageName = "kogito-data-index-mongodb"
	// DataIndexPostgresqlImageName is the image name for the Data Index Service with PostgreSQL
	DataIndexPostgresqlImageName = "kogito-data-index-postgresql"
	// DefaultDataIndexImageName is just the image name for the Data Index Service
	DefaultDataIndexImageName = DataIndexInfinispanImageName
	// DefaultDataIndexName is the default name for the Data Index instance service
	DefaultDataIndexName = "data-index"
)

// InstallKogitoDataIndexService install the Kogito Data Index service
func InstallKogitoDataIndexService(namespace string, installerType InstallerType, dataIndex *bddtypes.KogitoServiceHolder) error {
	// Persistence is already configured internally by the Data Index service, so we don't need to add any additional persistence step here.
	return InstallService(dataIndex, installerType, "data-index")
}

// WaitForKogitoDataIndexService wait for Kogito Data Index to be deployed
func WaitForKogitoDataIndexService(namespace string, replicas int, timeoutInMin int) error {
	if err := WaitForDeploymentRunning(namespace, getDataIndexServiceName(), replicas, timeoutInMin); err != nil {
		return err
	}

	// Data Index can be restarted after the deployment of KogitoRuntime, so 2 pods can run in parallel for a while.
	// We need to wait for only one (wait until the old one is deleted)
	return WaitForPodsWithLabel(namespace, LabelAppKey, getDataIndexServiceName(), replicas, timeoutInMin)
}

func getDataIndexServiceName() string {
	return DefaultDataIndexName
}

// GetKogitoDataIndexResourceStub Get basic KogitoDataIndex stub with all needed fields initialized
func GetKogitoDataIndexResourceStub(namespace string, replicas int) *v1beta1.KogitoSupportingService {
	return &v1beta1.KogitoSupportingService{
		ObjectMeta: NewObjectMetadata(namespace, getDataIndexServiceName()),
		Spec: v1beta1.KogitoSupportingServiceSpec{
			ServiceType: api.DataIndex,
			// This should be changed to `ephemeral` once inmemory data-index is available
			KogitoServiceSpec: NewKogitoServiceSpec(int32(replicas), config.GetServiceImageTag(config.DataIndexImageType, config.InfinispanPersistenceType), DefaultDataIndexImageName),
		},
	}
}
