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

package monitoring

import (
	"k8s.io/client-go/rest"

	operatorapi "github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/utils"
)

const (
	prometheusGroup = "monitoring.coreos.com"
)

func GetPrometheusAvailability(cfg *rest.Config) (bool, error) {
	cli, err := utils.GetDiscoveryClient(cfg)
	if err != nil {
		return false, err
	}
	apiList, err := cli.ServerGroups()
	if err != nil {
		return false, err
	}
	for _, group := range apiList.Groups {
		if group.Name == prometheusGroup {
			return true, nil
		}

	}
	return false, nil
}

func IsMonitoringEnabled(pl *operatorapi.SonataFlowPlatform) bool {
	return pl != nil && pl.Spec.Monitoring != nil && pl.Spec.Monitoring.Enabled
}
