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

package utils

import (
	"fmt"

	"k8s.io/client-go/discovery"
	"k8s.io/client-go/rest"
)

var isOpenShift = false

// IsOpenShift is a global flag that can be safely called across reconciliation cycles, defined at the controller manager start.
func IsOpenShift() bool {
	return isOpenShift
}

// SetIsOpenShift sets the global flag isOpenShift by the controller manager.
// We don't need to keep fetching the API every reconciliation cycle that we need to know about the platform.
func SetIsOpenShift(cfg *rest.Config) {
	if cfg == nil {
		panic("Rest Config struct is nil, impossible to get cluster information")
	}
	// Adapted from https://github.com/RHsyseng/operator-utils/blob/main/internal/platform/platform_versioner.go#L95
	client, err := discovery.NewDiscoveryClientForConfig(cfg)
	if err != nil {
		panic(fmt.Sprintf("Impossible to get new client for config when fetching cluster information: %s", err))
	}
	apiList, err := client.ServerGroups()
	if err != nil {
		panic(fmt.Sprintf("issue occurred while fetching ServerGroups: %s", err))
	}

	for _, v := range apiList.Groups {
		if v.Name == "route.openshift.io" {
			isOpenShift = true
			break
		}
	}
}
