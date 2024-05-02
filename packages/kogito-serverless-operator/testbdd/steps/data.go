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

package steps

import (
	"strconv"
	"time"

	"github.com/cucumber/godog"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework"
	kogitoSteps "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/steps"
)

// Data contains all data needed by Gherkin steps to run
type Data struct {
	*kogitoSteps.Data
}

// RegisterAllSteps register all steps available to the test suite
func (data *Data) RegisterAllSteps(ctx *godog.ScenarioContext) {
	registerOperatorSteps(ctx, data)
	registerPlatformSteps(ctx, data)
	registerSonataFlowSteps(ctx, data)
	registerKubernetesSteps(ctx, data)

	// Used for debugging
	ctx.Step(`^Wait (\d+) seconds?$`, data.waitSeconds)
}

func (data *Data) waitSeconds(seconds int) error {
	framework.GetMainLogger().Info("Waiting for " + strconv.Itoa(seconds) + " s")
	_ = <-time.After(time.Duration(seconds) * time.Second)
	return nil
}
