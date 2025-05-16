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

import { DmnDiagramNodeData } from "../../diagram/nodes/Nodes";
import { getDecisionServicePropertiesRelativeToThisDmn } from "../../mutations/addExistingDecisionServiceToDrd";
import { Computed, State } from "../Store";
import * as RF from "reactflow";

export function computeConflictedDecisionServices(
  definitions: State["dmn"]["model"]["definitions"],
  diagramData: ReturnType<Computed["getDiagramData"]>
) {
  const decisionsMap = new Map<string, Array<RF.Node<DmnDiagramNodeData>>>();
  for (const node of diagramData.nodes) {
    if (node.data.dmnObject?.__$$element === "decisionService") {
      const { containedDecisionHrefsRelativeToThisDmn } = getDecisionServicePropertiesRelativeToThisDmn({
        thisDmnsNamespace: definitions["@_namespace"],
        decisionServiceNamespace: node.data.dmnObjectNamespace ?? definitions["@_namespace"],
        decisionService: node.data.dmnObject,
      });
      for (let i = 0; i < containedDecisionHrefsRelativeToThisDmn.length; i++) {
        if (decisionsMap.has(containedDecisionHrefsRelativeToThisDmn[i])) {
          const currentArray = decisionsMap.get(containedDecisionHrefsRelativeToThisDmn[i]);
          currentArray?.push(node);
          if (currentArray) decisionsMap.set(containedDecisionHrefsRelativeToThisDmn[i], [...currentArray]);
        } else {
          decisionsMap.set(containedDecisionHrefsRelativeToThisDmn[i], [node]);
        }
      }
    }
  }

  const containedDecisionNodes: string[] = [];
  for (const node of diagramData.nodes) {
    if (node.data?.dmnObject?.__$$element === "decisionService") {
      containedDecisionNodes.push(
        ...(node.data.dmnObject?.outputDecision ?? []).map((od) => od["@_href"]),
        ...(node.data.dmnObject?.encapsulatedDecision ?? []).map((od) => od["@_href"])
      );
    }
  }

  const conflictedDecisionIds = containedDecisionNodes.filter(
    (item, index) => containedDecisionNodes.indexOf(item) !== index
  );

  return { decisionsMap, conflictedDecisionIds };
}
