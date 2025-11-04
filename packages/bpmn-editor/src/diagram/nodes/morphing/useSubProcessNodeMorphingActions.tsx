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

import * as React from "react";
import { BPMN20__tProcess } from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/types";
import { ElementFilter } from "@kie-tools/xml-parser-ts/dist/elementFilter";
import { Unpacked } from "@kie-tools/xyflow-react-kie-diagram/dist/tsExt/tsExt";
import { useCallback, useMemo } from "react";
import { visitFlowElementsAndArtifacts } from "../../../mutations/_elementVisitor";
import { addOrGetProcessAndDiagramElements } from "../../../mutations/addOrGetProcessAndDiagramElements";
import { Normalized } from "../../../normalization/normalize";
import { useBpmnEditorStoreApi } from "../../../store/StoreContext";
import { SubProcessIcon } from "../NodeIcons";
import { generateUuid } from "@kie-tools/xyflow-react-kie-diagram/dist/uuid/uuid";
import { AdHocSubProcessIconSvg, MultiInstanceParallelIconSvg } from "../NodeSvgs";
import { keepIntersection } from "./keepIntersection";

export type SubProcess = Normalized<
  ElementFilter<Unpacked<NonNullable<BPMN20__tProcess["flowElement"]>>, "adHocSubProcess" | "subProcess">
>;

export function useSubProcessNodeMorphingActions(subProcess: SubProcess) {
  const bpmnEditorStoreApi = useBpmnEditorStoreApi();

  const morphSubProcess = useCallback(
    (subProcessElement: SubProcess["__$$element"] | "eventSubProcess" | "multiInstanceSubProcess") => {
      // 1 - Sub process
      // 2 - Event sub process
      // 3 - Ad-hoc sub-process
      bpmnEditorStoreApi.setState((s) => {
        const { process } = addOrGetProcessAndDiagramElements({
          definitions: s.bpmn.model.definitions,
        });
        visitFlowElementsAndArtifacts(process, ({ array, index, owner, element }) => {
          if (element["@_id"] === subProcess["@_id"] && array[index].__$$element === subProcess.__$$element) {
            if (subProcessElement === "eventSubProcess") {
              keepIntersection({
                fromElement: element.__$$element,
                toElement: "subProcess",
                srcObj: element,
                targetObj: array[index],
              });
              array[index].__$$element = "subProcess";
              array[index]["@_triggeredByEvent"] = true;
              array[index].loopCharacteristics = undefined;
            } else if (subProcessElement === "multiInstanceSubProcess") {
              keepIntersection({
                fromElement: element.__$$element,
                toElement: "subProcess",
                srcObj: element,
                targetObj: array[index],
              });

              array[index].__$$element = "subProcess";
              array[index]["@_triggeredByEvent"] = false;
              array[index].loopCharacteristics = {
                "@_id": generateUuid(),
                __$$element: "multiInstanceLoopCharacteristics",
              };
            } else {
              keepIntersection({
                fromElement: element.__$$element,
                toElement: subProcessElement,
                srcObj: element,
                targetObj: array[index],
              });
              array[index].__$$element = subProcessElement;
              array[index]["@_triggeredByEvent"] = false;
              array[index].loopCharacteristics = undefined;
            }

            return false; // Will stop visiting.
          }
        });
      });
    },
    [bpmnEditorStoreApi, subProcess]
  );

  const morphingActions = useMemo(() => {
    return [
      {
        icon: <SubProcessIcon variant={"other"} />,
        key: "1",
        title: "Sub-process",
        id: "subProcess",
        action: () => morphSubProcess("subProcess"),
      } as const,
      {
        icon: <SubProcessIcon variant={"event"} />,
        key: "2",
        title: "Event",
        id: "eventSubProcess",
        action: () => morphSubProcess("eventSubProcess"),
      } as const,
      {
        icon: <MultiInstanceParallelIconSvg stroke={"black"} strokeWidth={2} size={28} isIcon={true} />,
        key: "3",
        title: "Multi-instance",
        id: "multiInstanceSubProcess",
        action: () => morphSubProcess("multiInstanceSubProcess"),
      } as const,
      {
        icon: <AdHocSubProcessIconSvg stroke={"black"} size={20} isIcon={true} />,
        key: "4",
        title: "Ad-hoc",
        id: "adHocSubProcess",
        action: () => morphSubProcess("adHocSubProcess"),
      } as const,
    ];
  }, [morphSubProcess]);

  return morphingActions;
}
