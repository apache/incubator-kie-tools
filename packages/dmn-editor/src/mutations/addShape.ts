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
  DMN15__tDefinitions,
  DMNDI15__DMNDecisionServiceDividerLine,
  DMNDI15__DMNShape,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { NodeType } from "../diagram/connections/graphStructure";
import { NODE_TYPES } from "../diagram/nodes/NodeTypes";
import { Normalized } from "../normalization/normalize";
import { addOrGetDrd } from "./addOrGetDrd";
import { getCentralizedDecisionServiceDividerLine } from "./updateDecisionServiceDividerLine";

export function addShape({
  definitions,
  drdIndex,
  nodeType,
  shape,
  decisionServiceDividerLine,
}: {
  definitions: Normalized<DMN15__tDefinitions>;
  drdIndex: number;
  nodeType: NodeType;
  shape: Normalized<DMNDI15__DMNShape>;
  decisionServiceDividerLine?: Normalized<DMNDI15__DMNDecisionServiceDividerLine>;
}) {
  const { diagramElements } = addOrGetDrd({ definitions, drdIndex });
  diagramElements.push({
    __$$element: "dmndi:DMNShape",
    ...(nodeType === NODE_TYPES.decisionService
      ? {
          "dmndi:DMNDecisionServiceDividerLine": decisionServiceDividerLine
            ? { ...decisionServiceDividerLine }
            : getCentralizedDecisionServiceDividerLine(shape["dc:Bounds"]!),
        }
      : {}),
    ...shape,
  });
}
