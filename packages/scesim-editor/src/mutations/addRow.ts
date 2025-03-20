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
} from "@kie-tools/scesim-marshaller/dist/schemas/scesim-1_8/ts-gen/types";

export function addRow({
  beforeIndex,
  factMappings,
  factMappingValues,
}: {
  beforeIndex: number;
  factMappings: SceSim__FactMappingType[];
  factMappingValues: SceSim__FactMappingValuesTypes[];
}) {
  /* Creating a new Scenario (Row) composed by a list of FactMappingValues. The list order is not relevant. */
  const factMappingValuesItems = factMappings.map((factMapping) => {
    return {
      expressionIdentifier: {
        name: { __$$text: factMapping.expressionIdentifier.name!.__$$text },
        type: { __$$text: factMapping.expressionIdentifier.type!.__$$text },
      },
      factIdentifier: {
        name: { __$$text: factMapping.factIdentifier.name!.__$$text },
        className: { __$$text: factMapping.factIdentifier.className!.__$$text },
      },
    };
  });

  const newScenario = {
    factMappingValues: {
      FactMappingValue: factMappingValuesItems,
    },
  };

  factMappingValues.splice(beforeIndex, 0, newScenario);
}
