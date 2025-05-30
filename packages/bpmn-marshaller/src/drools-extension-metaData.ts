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

import "./drools-extension";
import { WithMetaData } from "./schemas/bpmn-2_0/ts-gen/types";

export type Bpmn20KnownMetaDataKey =
  | "elementname" // Used for any Flow Element.
  | "customTags" // Used for Process Variables.
  | "customDescription" // Used for "Process Instance Description" as a global property.
  | "customSLADueDate" // Used for "SLA Due date" as a global property.
  | "customAbortParent" // Used for "Abort parent" flag on Call Activities.
  | "customAsync" // Used for "Async" flag on Call Activities.
  | "customActivationCondition" // Used for "Activation condition" expression on Ad-hoc sub-processes.
  | "customAutoStart" // Used for "Ad-hoc auto-start" flag on Ad-hoc sub-processes.
  | "customScope"; // Used for "Signal Scope" flag on intermediateThrowEvent and endEvent Signal nodes

export type Bpmn20ProcessVariableTags =
  | "internal" // TODO: Tiago --> Used for?
  | "required" // TODO: Tiago --> Used for?
  | "readonly" // TODO: Tiago --> Used for?
  | "input" // TODO: Tiago --> Used for?
  | "output" // TODO: Tiago --> Used for?
  | "business_relevant" // TODO: Tiago --> Used for?
  | "tracked"; // TODO: Tiago --> Used for?

/**
 * Helps reading drools:metaData entries.
 *
 * @param obj The object to extract drools:metaData from. No-op if undefined.
 * @returns A map containing the metaData entries indexed by their name attribute.
 */
export function parseBpmn20Drools10MetaData(
  obj: undefined | { extensionElements?: WithMetaData }
): Map<Bpmn20KnownMetaDataKey, string> {
  const metadata = new Map<Bpmn20KnownMetaDataKey, string>();

  for (let i = 0; i < (obj?.extensionElements?.["drools:metaData"] ?? []).length; i++) {
    const entry = obj!.extensionElements!["drools:metaData"]![i];
    if (entry["@_name"] !== undefined) {
      metadata.set(entry["@_name"] as Bpmn20KnownMetaDataKey, entry["drools:metaValue"].__$$text);
    }
  }

  return metadata;
}

/**
 * Helps changing drools:metaData entries.
 *
 * @param obj The object to extract drools:metaData from. No-op if undefined.
 * @param keyOrIndex The drools:metaData entry name or entry index.
 * @param value The drools:metaData entry value.
 */
export function setBpmn20Drools10MetaData(
  obj: undefined | { extensionElements?: WithMetaData },
  keyOrIndex: Bpmn20KnownMetaDataKey | number,
  value: string
): void {
  if (obj) {
    obj.extensionElements ??= { "drools:metaData": [] };
    obj.extensionElements["drools:metaData"] ??= [];
  }

  if (typeof keyOrIndex === "number") {
    if (obj?.extensionElements?.["drools:metaData"]?.[keyOrIndex]) {
      obj.extensionElements["drools:metaData"][keyOrIndex]["drools:metaValue"] = { __$$text: value };
    } else {
      // nothing to do.
    }
  } else {
    let updated = false;
    for (let i = 0; i < (obj?.extensionElements?.["drools:metaData"]?.length ?? 0); i++) {
      const entry = obj!.extensionElements!["drools:metaData"]![i]!;
      if (entry["@_name"] === keyOrIndex) {
        if (updated) {
          break;
        }
        entry["drools:metaValue"] = { __$$text: value };
        updated = true;
      }
    }

    if (!updated) {
      obj?.extensionElements?.["drools:metaData"]?.push({
        "@_name": keyOrIndex,
        "drools:metaValue": { __$$text: value },
      });
    }
  }
}

/**
 * Helps adding drools:metaData entries to objects.
 *
 * @param obj The object to extract drools:metaData from. No-op if undefined.
 * @param key The drools:metaData entry name.
 * @param value The drools:metaData entry value.
 */
export function addBpmn20Drools10MetaData(
  obj: undefined | { extensionElements?: WithMetaData },
  key: Bpmn20KnownMetaDataKey,
  value: string
): void {
  if (obj) {
    obj.extensionElements ??= { "drools:metaData": [] };
    obj.extensionElements["drools:metaData"] ??= [];
    obj.extensionElements["drools:metaData"]?.push({
      "@_name": key,
      "drools:metaValue": { __$$text: value },
    });
  }
}

/**
 * Helps renaming drools:metaData entries.
 *
 * @param obj The object to extract drools:metaData from. No-op if undefined.
 * @param index The drools:metaData entry index.
 * @param newKeyName The new drools:metaData entry name.
 */
export function renameBpmn20Drools10MetaDataEntry(
  obj: undefined | { extensionElements?: WithMetaData },
  index: number,
  newKeyName: string
): void {
  if (obj?.extensionElements?.["drools:metaData"]?.[index]) {
    obj.extensionElements["drools:metaData"][index]["@_name"] = newKeyName;
  }
}

/**
 * Helps deleting drools:metaData entries.
 *
 * @param obj The object to extract drools:metaData from. No-op if undefined.
 * @param index The drools:metaData entry name.
 */
export function deleteBpmn20Drools10MetaDataEntry(
  obj: undefined | { extensionElements?: WithMetaData },
  keyOrIndex: string | number
): void {
  if (typeof keyOrIndex === "number") {
    obj?.extensionElements?.["drools:metaData"]?.splice(keyOrIndex, 1);
  } else if (obj?.extensionElements?.["drools:metaData"]) {
    obj.extensionElements!["drools:metaData"] = obj.extensionElements["drools:metaData"].filter(
      (m) => m["@_name"] !== keyOrIndex
    );
  }
}
