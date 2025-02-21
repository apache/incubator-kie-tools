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

import _ from "lodash";
import { v4 as uuid } from "uuid";

import {
  SceSim__FactMappingType,
  SceSim__FactMappingValuesTypes,
} from "@kie-tools/scesim-marshaller/dist/schemas/scesim-1_8/ts-gen/types";

export function pushFactMappings({
  factMappingValuesModel,
  factMappingsModel,
  factMappingsToPush,
}: {
  factMappingValuesModel: SceSim__FactMappingValuesTypes[];
  factMappingsModel: SceSim__FactMappingType[];
  factMappingsToPush: {
    className: string;
    columnWidth?: number;
    expressionAlias: string;
    expressionElements: string[];
    expressionIdentifierType: string;
    factAlias: string;
    factIdentifierName: string;
    factIdentifierClassName: string;
    genericTypes?: string[];
  }[];
}) {
  factMappingsToPush.forEach((factMappingToPush) => {
    const expressionElements = factMappingToPush.expressionElements.map((expressionElement) => ({
      step: {
        __$$text: expressionElement,
      },
    }));
    const genericTypes = factMappingToPush.genericTypes?.map((genericType) => ({
      __$$text: genericType,
    }));

    const newFactMapping = {
      className: { __$$text: factMappingToPush.className },
      columnWidth: { __$$text: factMappingToPush.columnWidth ?? 150 },
      expressionAlias: { __$$text: factMappingToPush.expressionAlias },
      expressionElements: factMappingToPush.expressionElements
        ? {
            ExpressionElement: expressionElements,
          }
        : undefined,
      expressionIdentifier: {
        name: { __$$text: `_${uuid()}`.toLocaleUpperCase() },
        type: { __$$text: factMappingToPush.expressionIdentifierType },
      },
      factAlias: {
        __$$text: factMappingToPush.factAlias,
      },
      factIdentifier: {
        name: {
          __$$text: factMappingToPush.factIdentifierName,
        },
        className: {
          __$$text: factMappingToPush.factIdentifierClassName,
        },
      },
      factMappingValueType: { __$$text: "NOT_EXPRESSION" },
      genericTypes: factMappingToPush.genericTypes
        ? {
            string: genericTypes,
          }
        : undefined,
    };

    factMappingsModel.push(newFactMapping);

    factMappingValuesModel.forEach((scenario) => {
      scenario.factMappingValues.FactMappingValue!.push({
        expressionIdentifier: {
          name: { __$$text: newFactMapping.expressionIdentifier.name.__$$text },
          type: { __$$text: newFactMapping.expressionIdentifier.type.__$$text },
        },
        factIdentifier: {
          name: { __$$text: newFactMapping.factIdentifier.name.__$$text },
          className: { __$$text: newFactMapping.factIdentifier.className.__$$text },
        },
      });
    });
  });
}
