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

package steps

import (
	"github.com/DATA-DOG/godog"
	"github.com/kiegroup/kogito-cloud-operator/pkg/controller/kogitodataindex/resource"
	"github.com/kiegroup/kogito-cloud-operator/test/smoke/framework"
)

func registerKogitoDataIndexServiceSteps(s *godog.Suite, data *Data) {
	s.Step(`^Install Kogito Data Index with (\d+) replicas$`, data.deployKogitoDataIndexServiceWithReplicas)
	s.Step(`^Kogito Data Index has (\d+) pods running within (\d+) minutes$`, data.kogitoDataIndexHasPodsRunningWithinMinutes)
}

func (data *Data) deployKogitoDataIndexServiceWithReplicas(replicas int) error {
	return framework.DeployKogitoDataIndexService(data.Namespace, replicas)
}

func (data *Data) kogitoDataIndexHasPodsRunningWithinMinutes(podNb, timeoutInMin int) error {
	return framework.WaitForStatefulSetRunning(data.Namespace, resource.DefaultDataIndexName, podNb, timeoutInMin)
}
