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
import { Normalized } from "../../normalization/normalize";
import { BPMN20__tProcess } from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/types";
import { ElementFilter } from "@kie-tools/xml-parser-ts/dist/elementFilter";
import { Unpacked } from "@kie-tools/xyflow-react-kie-diagram/dist/tsExt/tsExt";
import { generateUuid } from "@kie-tools/xyflow-react-kie-diagram/dist/uuid/uuid";
import { Checkbox } from "@patternfly/react-core/dist/js/components/Checkbox";
import { visitFlowElementsAndArtifacts } from "../../mutations/_elementVisitor";
import { addOrGetProcessAndDiagramElements } from "../../mutations/addOrGetProcessAndDiagramElements";
import "./MultiInstanceCheckbox.css";
import {
  updateDataMappingWithVariableRef,
  setDataMappingForElement,
  setOutputDataMapping,
  getDataMapping,
} from "../../mutations/_dataMapping";
import { useBpmnEditorI18n } from "../../i18n";

export type WithMultipleInstance = Normalized<
  ElementFilter<
    Unpacked<NonNullable<BPMN20__tProcess["flowElement"]>>,
    "callActivity" | "userTask" | "serviceTask" | "businessRuleTask"
  >
>;

export function MultiInstanceCheckbox({ element }: { element: WithMultipleInstance }) {
  const { i18n } = useBpmnEditorI18n();
  const isReadOnly = useBpmnEditorStore((s) => s.settings.isReadOnly);

  const bpmnEditorStoreApi = useBpmnEditorStoreApi();

  return (
    <FormGroup
      fieldId="kie-bpmn-editor--properties-panel--multi-instance-checkbox"
      // helperText={"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod."} // FIXME: Tiago -> Description
    >
      <Checkbox
        label={i18n.propertiesPanel.multiInstance}
        id="kie-bpmn-editor--properties-panel--multi-instance-checkbox"
        name="is-multi-instance"
        aria-label="Multi-instance"
        isDisabled={isReadOnly}
        isChecked={element.loopCharacteristics?.__$$element === "multiInstanceLoopCharacteristics"}
        onChange={(e, checked) => {
          bpmnEditorStoreApi.setState((s) => {
            const { process } = addOrGetProcessAndDiagramElements({
              definitions: s.bpmn.model.definitions,
            });
            visitFlowElementsAndArtifacts(process, ({ element: e }) => {
              if (e["@_id"] === element["@_id"] && e.__$$element === element.__$$element) {
                if (checked) {
                  e.loopCharacteristics = {
                    "@_id": generateUuid(),
                    __$$element: "multiInstanceLoopCharacteristics",
                  };
                } else {
                  if (e.loopCharacteristics?.__$$element === "multiInstanceLoopCharacteristics") {
                    const { inputDataMapping, outputDataMapping } = getDataMapping(element);
                    if (e.loopCharacteristics.inputDataItem?.["@_name"] !== undefined) {
                      updateDataMappingWithVariableRef(
                        inputDataMapping,
                        undefined,
                        e.loopCharacteristics.inputDataItem["@_name"]
                      );
                    }
                    if (e.loopCharacteristics.outputDataItem?.["@_name"] !== undefined) {
                      updateDataMappingWithVariableRef(
                        outputDataMapping,
                        undefined,
                        e.loopCharacteristics.outputDataItem["@_name"]
                      );
                    }

                    setDataMappingForElement({
                      definitions: s.bpmn.model.definitions,
                      element: element.__$$element,
                      elementId: element["@_id"],
                      inputDataMapping,
                      outputDataMapping,
                    });
                  }
                  e.loopCharacteristics = undefined;
                }
              }
            });
          });
        }}
      />
    </FormGroup>
  );
}
