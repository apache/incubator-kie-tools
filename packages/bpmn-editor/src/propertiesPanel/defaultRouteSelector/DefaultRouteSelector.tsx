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
import { useBpmnEditorStore, useBpmnEditorStoreApi } from "../../store/StoreContext";
import { FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { FormSelect, FormSelectOption } from "@patternfly/react-core/dist/js/components/FormSelect";
import {
  BPMN20__tExclusiveGateway,
  BPMN20__tInclusiveGateway,
  BPMN20__tSequenceFlow,
} from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/types";
import { Normalized } from "../../normalization/normalize";
import { useCallback } from "react";
import { visitFlowElementsAndArtifacts } from "../../mutations/_elementVisitor";
import { addOrGetProcessAndDiagramElements } from "../../mutations/addOrGetProcessAndDiagramElements";
import "./DefaultRouteSelector.css";
import { useBpmnEditorI18n } from "../../i18n";

export function DefaultRouteSelector({
  gateway,
}: {
  gateway:
    | (Normalized<BPMN20__tExclusiveGateway> & { __$$element: "exclusiveGateway" })
    | (Normalized<BPMN20__tInclusiveGateway> & { __$$element: "inclusiveGateway" });
}) {
  const { i18n } = useBpmnEditorI18n();
  const isReadOnly = useBpmnEditorStore((s) => s.settings.isReadOnly);

  const bpmnEditorStoreApi = useBpmnEditorStoreApi();

  const onChange = useCallback(
    (e: React.FormEvent, newDefaultRoute: string) => {
      bpmnEditorStoreApi.setState((s) => {
        const { process } = addOrGetProcessAndDiagramElements({
          definitions: s.bpmn.model.definitions,
        });
        visitFlowElementsAndArtifacts(process, ({ element: e }) => {
          if (e["@_id"] === gateway["@_id"] && e.__$$element === gateway.__$$element) {
            e["@_default"] = newDefaultRoute;
          }
        });
      });
    },
    [bpmnEditorStoreApi, gateway]
  );

  const flowElementsById = useBpmnEditorStore(
    (s) =>
      new Map(
        s.bpmn.model.definitions.rootElement
          ?.find((r) => r.__$$element === "process")
          ?.flowElement?.map((e) => [e["@_id"], e])
      )
  );

  const getLabelForSequenceFlowWithId = useCallback(
    (sequenceFlowRef: string) => {
      const sequenceFlow = flowElementsById.get(sequenceFlowRef) as BPMN20__tSequenceFlow;
      if (!sequenceFlow) {
        return "<Unknown>";
      }

      const target = flowElementsById.get(sequenceFlow["@_targetRef"]);
      if (sequenceFlow?.["@_name"]) {
        return `---( ${sequenceFlow?.["@_name"]} )--->[ ${target?.["@_name"]} ]`;
      } else {
        return `---->[ ${target?.["@_name"]} ]`;
      }
    },
    [flowElementsById]
  );

  return (
    <FormGroup label={i18n.propertiesPanel.defaultRoute}>
      <FormSelect id={"select"} value={gateway["@_default"]} onChange={onChange} isDisabled={isReadOnly}>
        <FormSelectOption id={"none"} isPlaceholder={true} label={i18n.propertiesPanel.none} value={undefined} />
        {gateway.outgoing?.map((o) => (
          <FormSelectOption
            key={o.__$$text}
            id={o.__$$text}
            isPlaceholder={false}
            value={o.__$$text}
            label={getLabelForSequenceFlowWithId(o.__$$text)}
          />
        ))}
      </FormSelect>
    </FormGroup>
  );
}
