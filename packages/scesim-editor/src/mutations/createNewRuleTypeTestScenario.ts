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

import {
  SceSim__FactMappingType,
  SceSim__FactMappingValuesTypes,
  SceSim__settingsType,
} from "@kie-tools/scesim-marshaller/dist/schemas/scesim-1_8/ts-gen/types";
import { pushFactMappings } from "./pushFactMappings";

/**
 * It creates a new RULE-type Test Scenario, adding 2 empty columns (FactMappings) and its related data cells (FactMappingValues)
 */
export function createNewRuleTypeTestScenario({
  factMappingsModel, // The SceSim FactMappings model (which represents Columns) to be mutated when isAutoFillTableEnabled is true
  factMappingValuesModel, // The SceSim FactMappingValue model (which represents a Data Rows) to be mutated when isAutoFillTableEnabled is true
  isStatelessSessionRule,
  isTestSkipped,
  kieSessionRule,
  ruleFlowGroup,
  settingsModel, // The SceSim Setting model to be mutated with the user provided infos.
}: {
  factMappingsModel: SceSim__FactMappingType[];
  factMappingValuesModel: SceSim__FactMappingValuesTypes[];
  isStatelessSessionRule: boolean;
  isTestSkipped: boolean;
  kieSessionRule: string;
  ruleFlowGroup: string;
  settingsModel: SceSim__settingsType;
}) {
  settingsModel.dmoSession = kieSessionRule ? { __$$text: kieSessionRule } : undefined;
  settingsModel.ruleFlowGroup = ruleFlowGroup ? { __$$text: ruleFlowGroup } : undefined;
  settingsModel.skipFromBuild = { __$$text: isTestSkipped };
  settingsModel.stateless = { __$$text: isStatelessSessionRule };
  settingsModel.type = { __$$text: "RULE" };

  const factMappingsToPush = [
    {
      className: "java.lang.Void",
      columnWidth: 300,
      expressionAlias: "PROPERTY-1",
      expressionElements: [],
      expressionIdentifierType: "GIVEN",
      factAlias: "INSTANCE-1",
      factIdentifierName: "INSTANCE-1",
      factIdentifierClassName: "java.lang.Void",
    },
    {
      className: "java.lang.Void",
      columnWidth: 300,
      expressionAlias: "PROPERTY-2",
      expressionElements: [],
      expressionIdentifierType: "EXPECT",
      factAlias: "INSTANCE-2",
      factIdentifierName: "INSTANCE-2",
      factIdentifierClassName: "java.lang.Void",
    },
  ];

  pushFactMappings({ factMappingsModel, factMappingsToPush, factMappingValuesModel });
}
