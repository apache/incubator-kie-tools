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
import { GatewayIcon } from "../NodeIcons";
import { keepIntersection } from "./keepIntersection";

export type Gateway = Normalized<
  ElementFilter<
    Unpacked<NonNullable<BPMN20__tProcess["flowElement"]>>,
    "exclusiveGateway" | "inclusiveGateway" | "parallelGateway" | "eventBasedGateway" | "complexGateway"
  >
>;

export function useGatewayNodeMorphingActions(gateway: Gateway) {
  const bpmnEditorStoreApi = useBpmnEditorStoreApi();

  const morphGateway = useCallback(
    (newGatewayElement: Gateway["__$$element"]) => {
      // 1 - Parallel
      // 2 - Exclusive
      // 3 - Inclusive
      // 4 - Event
      // 5 - Complex
      bpmnEditorStoreApi.setState((s) => {
        const { process } = addOrGetProcessAndDiagramElements({
          definitions: s.bpmn.model.definitions,
        });
        visitFlowElementsAndArtifacts(process, ({ array, index, owner, element }) => {
          if (element["@_id"] === gateway["@_id"] && element.__$$element === gateway.__$$element) {
            keepIntersection({
              fromElement: element.__$$element,
              toElement: newGatewayElement,
              srcObj: element,
              targetObj: array[index],
            });

            array[index].__$$element = newGatewayElement;
            return false; // Will stop visiting.
          }
        });
      });
    },
    [bpmnEditorStoreApi, gateway]
  );

  const morphingActions = useMemo(() => {
    return [
      {
        icon: <GatewayIcon variant={"parallelGateway"} isIcon={true} />,
        key: "1",
        title: "Parallel",
        id: "parallelGateway",
        action: () => morphGateway("parallelGateway"),
      } as const,
      {
        icon: <GatewayIcon variant={"exclusiveGateway"} isIcon={true} />,
        key: "2",
        title: "Exclusive",
        id: "exclusiveGateway",
        action: () => morphGateway("exclusiveGateway"),
      } as const,
      {
        icon: <GatewayIcon variant={"inclusiveGateway"} isIcon={true} />,
        key: "3",
        title: "Inclusive",
        id: "inclusiveGateway",
        action: () => morphGateway("inclusiveGateway"),
      } as const,
      {
        icon: <GatewayIcon variant={"eventBasedGateway"} isIcon={true} />,
        key: "4",
        title: "Event",
        id: "eventBasedGateway",
        action: () => morphGateway("eventBasedGateway"),
      } as const,
      {
        icon: <GatewayIcon variant={"complexGateway"} isIcon={true} />,
        key: "5",
        title: "Complex",
        id: "complexGateway",
        action: () => morphGateway("complexGateway"),
      } as const,
    ];
  }, [morphGateway]);

  return morphingActions;
}
