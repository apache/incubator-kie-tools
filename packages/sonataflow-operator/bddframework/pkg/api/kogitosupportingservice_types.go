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

package api

import (
	"k8s.io/apimachinery/pkg/runtime"
)

// ServiceType define resource type of supporting service
type ServiceType string

const (
	// DataIndex supporting service resource type
	DataIndex ServiceType = "DataIndex"
	// Explainability supporting service resource type
	Explainability ServiceType = "Explainability"
	// JobsService supporting service resource type
	JobsService ServiceType = "JobsService"
	// MgmtConsole supporting service resource type
	MgmtConsole ServiceType = "MgmtConsole"
	// TaskConsole supporting service resource type
	TaskConsole ServiceType = "TaskConsole"
	// TrustyAI supporting service resource type
	TrustyAI ServiceType = "TrustyAI"
	// TrustyUI supporting service resource type
	TrustyUI ServiceType = "TrustyUI"
)

// KogitoSupportingServiceInterface ...
type KogitoSupportingServiceInterface interface {
	KogitoService
	// GetSpec gets the Kogito Service specification structure.
	GetSupportingServiceSpec() KogitoSupportingServiceSpecInterface
	// GetStatus gets the Kogito Service Status structure.
	GetSupportingServiceStatus() KogitoSupportingServiceStatusInterface
}

// KogitoSupportingServiceSpecInterface ...
type KogitoSupportingServiceSpecInterface interface {
	KogitoServiceSpecInterface
	GetServiceType() ServiceType
	SetServiceType(serviceType ServiceType)
}

// KogitoSupportingServiceStatusInterface ...
type KogitoSupportingServiceStatusInterface interface {
	KogitoServiceStatusInterface
}

// KogitoSupportingServiceListInterface ...
type KogitoSupportingServiceListInterface interface {
	runtime.Object
	// GetItems gets all items
	GetItems() []KogitoSupportingServiceInterface
}
