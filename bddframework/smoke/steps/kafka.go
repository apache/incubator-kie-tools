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

// To be removed when https://issues.redhat.com/browse/KOGITO-614 is done

import (
	"github.com/DATA-DOG/godog"
	"github.com/kiegroup/kogito-cloud-operator/test/smoke/framework"
)

// registerKafkaSteps register all Kafka steps existing
func registerKafkaSteps(s *godog.Suite, data *Data) {
	s.Step(`^Kafka is installed$`, data.kafkaIsInstalled)
}

func (data *Data) kafkaIsInstalled() error {
	if err := framework.InstallKogitoInfraKafka(data.Namespace); err != nil {
		return err
	}
	return framework.WaitForKogitoInfraKafka(data.Namespace)
}
