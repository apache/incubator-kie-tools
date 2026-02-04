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
  BPMN20__tDefinitions,
  BPMN20__tLane,
  BPMN20__tProcess,
  BPMN20__tTextAnnotation,
} from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/types";
import { Unpacked } from "@kie-tools/xyflow-react-kie-diagram/dist/tsExt/tsExt";
import { Normalized } from "../normalization/normalize";
import { visitFlowElementsAndArtifacts } from "./_elementVisitor";
import { addOrGetProcessAndDiagramElements } from "./addOrGetProcessAndDiagramElements";

type NewFlowElement = Partial<
  Normalized<Unpacked<NonNullable<BPMN20__tProcess["flowElement"] | BPMN20__tProcess["artifact"]>>>
>;

export function updateFlowElement({
  definitions,
  newFlowElement,
  id,
}: {
  definitions: Normalized<BPMN20__tDefinitions>;
  newFlowElement: NewFlowElement;
  id: string;
}) {
  const { process } = addOrGetProcessAndDiagramElements({ definitions });

  visitFlowElementsAndArtifacts(process, ({ array, index, owner, element }) => {
    if (element["@_id"] === id) {
      if (array != owner.flowElement) {
        throw new Error(
          `BPMN MUTATION: Element with id ${id} is not a flowElement, but rather a ${element.__$$element}`
        );
      }

      const keysToDelete = Object.keys(newFlowElement).filter(
        (key: keyof NewFlowElement) => newFlowElement[key] === undefined || newFlowElement[key] === ""
      );

      array[index] = { ...element, ...newFlowElement } as typeof element;
      for (const key of keysToDelete) {
        delete array[index][key as keyof NewFlowElement];
      }
      return false; // Will stop visiting.
    }
  });
}

export function updateLane({
  definitions,
  newLane,
  id,
}: {
  definitions: Normalized<BPMN20__tDefinitions>;
  newLane: Partial<Normalized<BPMN20__tLane>>;
  id: string;
}) {
  const { process } = addOrGetProcessAndDiagramElements({ definitions });

  for (let i = 0; i < (process.laneSet ?? []).length; i++) {
    const laneSet = process.laneSet![i];

    for (let j = 0; j < (laneSet.lane ?? []).length; j++) {
      const lane = laneSet.lane![j];
      if (lane["@_id"] === id) {
        laneSet.lane![j] = { ...lane, ...newLane };
        break;
      }

      for (let k = 0; k < (lane.childLaneSet?.lane ?? []).length; k++) {
        const childLane = lane.childLaneSet!.lane![k];
        if (childLane["@_id"] === id) {
          lane.childLaneSet!.lane![k] = { ...childLane, ...newLane };
          break;
        }
      }
    }
  }
}

export function updateTextAnnotation({
  definitions,
  newTextAnnotation,
  id,
}: {
  definitions: Normalized<BPMN20__tDefinitions>;
  newTextAnnotation: Partial<Normalized<BPMN20__tTextAnnotation>>;
  id: string;
}) {
  const { process } = addOrGetProcessAndDiagramElements({ definitions });

  visitFlowElementsAndArtifacts(process, ({ element, array, index }) => {
    if (element["@_id"] === id) {
      if (element.__$$element !== "textAnnotation") {
        throw new Error(
          `BPMN MUTATION: Element with id ${id} is not a textAnnotation, but rather a ${element.__$$element}`
        );
      }

      array[index] = { ...array[index], ...newTextAnnotation };
      return false; // Will stop visiting.
    }
  });
}
