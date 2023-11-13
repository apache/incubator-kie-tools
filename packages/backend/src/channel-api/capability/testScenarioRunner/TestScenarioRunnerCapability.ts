/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { Capability, CapabilityResponse } from "../../../api";
import { TestResult } from "./TestResult";

/**
 * Capability for running test scenarios.
 */
export interface TestScenarioRunnerCapability extends Capability {
  /**
   * Execute a `mvn clean test` on the given `baseDir` and report back the result.
   * @param baseDir Directory path where the `pom.xml` file is located.
   * @param runnerClass Fully qualified class name of the runner, e.g. `testscenario.KogitoScenarioJunitActivatorTest`.
   * @returns Test result.
   */
  execute(baseDir: string, runnerClass: string): Promise<CapabilityResponse<TestResult>>;

  /**
   * Stop the current active execution, if any.
   */
  stopActiveExecution(): void;
}
