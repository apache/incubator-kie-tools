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
	"fmt"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/bddframework/pkg/config"
)

var verifications = []func() error{
	checkKubernetesAndDomainSuffix,
	checkImageCacheMode,
}

// CheckSetup verifies the configuration is correct
func CheckSetup() error {
	for _, verification := range verifications {
		if err := verification(); err != nil {
			return err
		}
	}

	return nil
}

func checkKubernetesAndDomainSuffix() error {
	if !IsOpenshift() && !config.IsLocalCluster() && len(config.GetDomainSuffix()) <= 0 {
		return fmt.Errorf("The 'domain_suffix' argument is required using Kubernetes cluster")
	}

	return nil
}

func checkImageCacheMode() error {
	imageCacheMode := config.GetImageCacheMode()
	if imageCacheMode.IsValid() {
		return nil
	}
	return (fmt.Errorf("Invalid image cache mode: %s", imageCacheMode))
}
