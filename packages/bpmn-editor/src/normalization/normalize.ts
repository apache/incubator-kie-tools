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

function remapRef(remap: Map<string, string>, id: string | undefined): string | undefined {
  return id === undefined ? undefined : remap.get(id) ?? id;
}

function remapDataItems(remap: Map<string, string>, items: Array<{ "@_itemSubjectRef"?: string }> | undefined) {
  if (!items) {
    return;
  }
  for (const item of items) {
    if (item["@_itemSubjectRef"] !== undefined) {
      item["@_itemSubjectRef"] = remapRef(remap, item["@_itemSubjectRef"]);
    }
  }
}

function remapProperties(remap: Map<string, string>, properties: Array<{ "@_itemSubjectRef"?: string }> | undefined) {
  if (!properties) {
    return;
  }
  for (const p of properties) {
    p["@_itemSubjectRef"] = remapRef(remap, p["@_itemSubjectRef"]);
  }
}

function remapFlowElements(remap: Map<string, string>, flowElements: NonNullable<BPMN20__tProcess["flowElement"]>) {
  for (const el of flowElements) {
    if (
      el.__$$element === "dataObject" ||
      el.__$$element === "dataObjectReference" ||
      el.__$$element === "dataStoreReference"
    ) {
      el["@_itemSubjectRef"] = remapRef(remap, el["@_itemSubjectRef"]);
    }

    if (
      el.__$$element === "businessRuleTask" ||
      el.__$$element === "callActivity" ||
      el.__$$element === "serviceTask" ||
      el.__$$element === "userTask"
    ) {
      remapDataItems(remap, el.ioSpecification?.dataInput);
      remapDataItems(remap, el.ioSpecification?.dataOutput);
      remapProperties(remap, el.property);
    }

    if (el.__$$element === "endEvent" || el.__$$element === "intermediateThrowEvent") {
      remapDataItems(remap, el.dataInput);
      remapProperties(remap, el.property);
    }

    if (
      el.__$$element === "startEvent" ||
      el.__$$element === "intermediateCatchEvent" ||
      el.__$$element === "boundaryEvent"
    ) {
      remapDataItems(remap, el.dataOutput);
      remapProperties(remap, el.property);
    }

    if (el.__$$element === "subProcess" || el.__$$element === "adHocSubProcess" || el.__$$element === "transaction") {
      remapDataItems(remap, el.ioSpecification?.dataInput);
      remapDataItems(remap, el.ioSpecification?.dataOutput);
      remapProperties(remap, el.property);
      if (el.flowElement) {
        remapFlowElements(remap, el.flowElement);
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
  const itemDefGroups = new Map<string, Array<{ "@_id"?: string }>>();
  for (const rootElement of definitions.rootElement ?? []) {
    if (rootElement.__$$element === "itemDefinition") {
      const key = rootElement["@_structureRef"] ?? "";
      addOrGetGroup(itemDefGroups, key).push(rootElement);
    }
  }

  // Map each duplicate id to the id of the first occurrence we keep.
  const itemDefIdRemap = new Map<string, string>();
  for (const group of itemDefGroups.values()) {
    if (group.length <= 1) {
      continue;
    }
    const survivorId = group[0]["@_id"];
    if (!survivorId) {
      continue;
    }
    for (let i = 1; i < group.length; i++) {
      const duplicateId = group[i]["@_id"];
      if (duplicateId) {
        itemDefIdRemap.set(duplicateId, survivorId);
      }
    }
  }

  if (itemDefIdRemap.size === 0) {
    return;
  }

  // Remove the duplicate itemDefinitions and update every itemSubjectRef.
  const idsToRemove = new Set(itemDefIdRemap.keys());
  definitions.rootElement = definitions.rootElement?.filter(
    (e) => !(e.__$$element === "itemDefinition" && e["@_id"] && idsToRemove.has(e["@_id"]))
  );

  for (const rootElement of definitions.rootElement ?? []) {
    if (rootElement.__$$element === "dataStore") {
      rootElement["@_itemSubjectRef"] = remapRef(itemDefIdRemap, rootElement["@_itemSubjectRef"]);
    }
    if (rootElement.__$$element === "process") {
      remapDataItems(itemDefIdRemap, rootElement.ioSpecification?.dataInput);
      remapDataItems(itemDefIdRemap, rootElement.ioSpecification?.dataOutput);
      remapProperties(itemDefIdRemap, rootElement.property);
      if (rootElement.flowElement) {
        remapFlowElements(itemDefIdRemap, rootElement.flowElement);
      }
    }
  }
}
