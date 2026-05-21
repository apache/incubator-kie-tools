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

import { BpmnLatestModel } from "@kie-tools/bpmn-marshaller";
import { BPMN20__tProcess } from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/types";
import { getNewBpmnIdRandomizer } from "../idRandomizer/bpmnIdRandomizer";
import { State } from "../store/Store";

export type Normalized<T> = WithRequiredDeep<T, "@_id">;

type WithRequiredDeep<T, K extends keyof any> = T extends undefined
  ? T
  : T extends Array<infer U>
    ? Array<WithRequiredDeep<U, K>>
    : { [P in keyof T]: WithRequiredDeep<T[P], K> } & (K extends keyof T
        ? { [P in K]-?: NonNullable<WithRequiredDeep<T[P], K>> }
        : T);

// Get the array for `key`, or create an empty one if missing.
function addOrGetGroup<K, V>(map: Map<K, V[]>, key: K): V[] {
  let group = map.get(key);
  if (!group) {
    group = [];
    map.set(key, group);
  }
  return group;
}

function getRefToRemap(
  itemDefinitionIdsToRemap: Map<string, string>,
  id: string | undefined
): string | undefined {
  return id === undefined ? undefined : itemDefinitionIdsToRemap.get(id) ?? id;
}

function remapDataItems(
  itemDefinitionIdsToRemap: Map<string, string>,
  items: Array<{ "@_itemSubjectRef"?: string }> | undefined
) {
  if (!items) {
    return;
  }
  for (const item of items) {
    if (item["@_itemSubjectRef"] !== undefined) {
      item["@_itemSubjectRef"] = getRefToRemap(itemDefinitionIdsToRemap, item["@_itemSubjectRef"]);
    }
  }
}

function remapProperties(
  itemDefinitionIdsToRemap: Map<string, string>,
  properties: Array<{ "@_itemSubjectRef"?: string }> | undefined
) {
  if (!properties) {
    return;
  }
  for (const p of properties) {
    p["@_itemSubjectRef"] = getRefToRemap(itemDefinitionIdsToRemap, p["@_itemSubjectRef"]);
  }
}

function remapFlowElements(
  itemDefinitionIdsToRemap: Map<string, string>,
  flowElements: NonNullable<BPMN20__tProcess["flowElement"]>
) {
  for (const el of flowElements) {
    if (
      el.__$$element === "dataObject" ||
      el.__$$element === "dataObjectReference" ||
      el.__$$element === "dataStoreReference"
    ) {
      el["@_itemSubjectRef"] = getRefToRemap(itemDefinitionIdsToRemap, el["@_itemSubjectRef"]);
    }

    if (
      el.__$$element === "businessRuleTask" ||
      el.__$$element === "callActivity" ||
      el.__$$element === "serviceTask" ||
      el.__$$element === "userTask"
    ) {
      remapDataItems(itemDefinitionIdsToRemap, el.ioSpecification?.dataInput);
      remapDataItems(itemDefinitionIdsToRemap, el.ioSpecification?.dataOutput);
      remapProperties(itemDefinitionIdsToRemap, el.property);
    }

    if (el.__$$element === "endEvent" || el.__$$element === "intermediateThrowEvent") {
      remapDataItems(itemDefinitionIdsToRemap, el.dataInput);
      remapProperties(itemDefinitionIdsToRemap, el.property);
    }

    if (
      el.__$$element === "startEvent" ||
      el.__$$element === "intermediateCatchEvent" ||
      el.__$$element === "boundaryEvent"
    ) {
      remapDataItems(itemDefinitionIdsToRemap, el.dataOutput);
      remapProperties(itemDefinitionIdsToRemap, el.property);
    }

    if (el.__$$element === "subProcess" || el.__$$element === "adHocSubProcess" || el.__$$element === "transaction") {
      remapDataItems(itemDefinitionIdsToRemap, el.ioSpecification?.dataInput);
      remapDataItems(itemDefinitionIdsToRemap, el.ioSpecification?.dataOutput);
      remapProperties(itemDefinitionIdsToRemap, el.property);
      if (el.flowElement) {
        remapFlowElements(itemDefinitionIdsToRemap, el.flowElement);
      }
    }
  }
}

export function normalize(model: BpmnLatestModel): State["bpmn"]["model"] {
  getNewBpmnIdRandomizer()
    .ack({
      json: model.definitions.import,
      type: "BPMN20__tDefinitions",
      attr: "import",
    })
    .ack({
      json: model.definitions["bpmndi:BPMNDiagram"],
      type: "BPMN20__tDefinitions",
      attr: "bpmndi:BPMNDiagram",
    })
    .randomize({ skipAlreadyAttributedIds: true });

  // Normalize property elements: populate name attribute from id if missing for process variables
  model.definitions.rootElement?.forEach((rootElement) => {
    if (rootElement.__$$element === "process") {
      rootElement.property?.forEach((property) => {
        if (!property["@_name"] && property["@_id"]) {
          property["@_name"] = property["@_id"];
        }
        if (property["@_itemSubjectRef"] === "") {
          delete property["@_itemSubjectRef"];
        }
      });
      // Normalize properties in subprocesses
      rootElement.flowElement?.forEach((flowElement) => {
        if (
          flowElement.__$$element === "subProcess" ||
          flowElement.__$$element === "adHocSubProcess" ||
          flowElement.__$$element === "transaction"
        ) {
          flowElement.property?.forEach((property) => {
            if (!property["@_name"] && property["@_id"]) {
              property["@_name"] = property["@_id"];
            }
            if (property["@_itemSubjectRef"] === "") {
              delete property["@_itemSubjectRef"];
            }
          });
        }
      });
    }
    // Normalize error elements: populate name attribute from errorCode if missing
    if (rootElement.__$$element === "error") {
      if (!rootElement["@_name"] && rootElement["@_errorCode"]) {
        rootElement["@_name"] = rootElement["@_errorCode"];
      }
    }

    // Normalize escalation elements: populate name attribute from escalationCode if missing
    if (rootElement.__$$element === "escalation") {
      if (!rootElement["@_name"] && rootElement["@_escalationCode"]) {
        rootElement["@_name"] = rootElement["@_escalationCode"];
      }
    }
  });

  // Merge itemDefinitions that share the same structureRef.
  deduplicateItemDefinitions(model.definitions);

  const normalizedModel = model as Normalized<BpmnLatestModel>;

  return normalizedModel;
}

// Keep one itemDefinition per structureRef and update references to point to it.
export function deduplicateItemDefinitions(definitions: BpmnLatestModel["definitions"]): void {
  const itemDefinitionsGroupedStructureRef = new Map<string, Array<{ "@_id"?: string }>>();
  for (const rootElement of definitions.rootElement ?? []) {
    if (rootElement.__$$element === "itemDefinition") {
      const itemDefinitionStructureRef = rootElement["@_structureRef"] ?? "";
      addOrGetGroup(itemDefinitionsGroupedStructureRef, itemDefinitionStructureRef).push(rootElement);
    }
  }

  // Map each duplicate id to the id of the first occurrence we keep.
  const itemDefinitionIdsToRemap = new Map<string, string>();
  for (const itemDefinitionsGroup of itemDefinitionsGroupedStructureRef.values()) {
    if (itemDefinitionsGroup.length <= 1) {
      continue;
    }
    const survivorId = itemDefinitionsGroup[0]["@_id"];
    if (!survivorId) {
      continue;
    }
    for (let i = 1; i < itemDefinitionsGroup.length; i++) {
      const duplicateId = itemDefinitionsGroup[i]["@_id"];
      if (duplicateId) {
        itemDefinitionIdsToRemap.set(duplicateId, survivorId);
      }
    }
  }

  if (itemDefinitionIdsToRemap.size === 0) {
    return;
  }

  // Remove the duplicate itemDefinitions and update every itemSubjectRef.
  const itemDefinitionIdsToRemove = new Set(itemDefinitionIdsToRemap.keys());
  definitions.rootElement = definitions.rootElement?.filter(
    (e) => !(e.__$$element === "itemDefinition" && e["@_id"] && itemDefinitionIdsToRemove.has(e["@_id"]))
  );

  for (const rootElement of definitions.rootElement ?? []) {
    if (rootElement.__$$element === "dataStore") {
      rootElement["@_itemSubjectRef"] = getRefToRemap(itemDefinitionIdsToRemap, rootElement["@_itemSubjectRef"]);
    }
    if (rootElement.__$$element === "process") {
      remapDataItems(itemDefinitionIdsToRemap, rootElement.ioSpecification?.dataInput);
      remapDataItems(itemDefinitionIdsToRemap, rootElement.ioSpecification?.dataOutput);
      remapProperties(itemDefinitionIdsToRemap, rootElement.property);
      if (rootElement.flowElement) {
        remapFlowElements(itemDefinitionIdsToRemap, rootElement.flowElement);
      }
    }
  }
}
