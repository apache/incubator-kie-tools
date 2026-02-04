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
import {
  deleteBpmn20Drools10MetaDataEntry,
  parseBpmn20Drools10MetaData,
  setBpmn20Drools10MetaData,
} from "@kie-tools/bpmn-marshaller/dist/drools-extension-metaData";
import { FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { addOrGetProcessAndDiagramElements } from "../../mutations/addOrGetProcessAndDiagramElements";
import { useBpmnEditorStore, useBpmnEditorStoreApi } from "../../store/StoreContext";
import {
  BPMN20__tDefinitions,
  BPMN20__tProcess,
  WithMetaData,
} from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/types";
import { ElementFilter } from "@kie-tools/xml-parser-ts/dist/elementFilter";
import { Unpacked } from "@kie-tools/xyflow-react-kie-diagram/dist/tsExt/tsExt";
import { Normalized } from "../../normalization/normalize";
import { visitFlowElementsAndArtifacts } from "../../mutations/_elementVisitor";
import { useBpmnEditorI18n } from "../../i18n";

export type WithSlaDueDate =
  | undefined
  | Normalized<
      | ElementFilter<Unpacked<NonNullable<BPMN20__tDefinitions["rootElement"]>>, "process">
      | ElementFilter<
          Unpacked<NonNullable<BPMN20__tProcess["flowElement"]>>,
          | "startEvent"
          | "intermediateCatchEvent"
          | "boundaryEvent"
          | "callActivity"
          | "subProcess"
          | "adHocSubProcess"
          | "transaction"
          | "userTask"
          | "task"
          | "serviceTask"
          | "businessRuleTask"
        >
    >;

export function SlaDueDateInput({ element }: { element: WithSlaDueDate }) {
  const { i18n } = useBpmnEditorI18n();
  const bpmnEditorStoreApi = useBpmnEditorStoreApi();

  const isReadOnly = useBpmnEditorStore((s) => s.settings.isReadOnly);

  return (
    <>
      <FormGroup
        label={i18n.propertiesPanel.slaDuedate}
        // helperText={"E.g.,: 2024-09-19 19:22:42"} // FIXME: Tiago -> Description
      >
        <TextInput
          aria-label={"SLA Due Date"}
          type={"text"}
          isDisabled={isReadOnly}
          placeholder={i18n.propertiesPanel.datePlaceholder}
          value={parseBpmn20Drools10MetaData(element).get("customSLADueDate") || ""}
          onChange={(e, newSlaDueDate) =>
            bpmnEditorStoreApi.setState((s) => {
              const { process } = addOrGetProcessAndDiagramElements({
                definitions: s.bpmn.model.definitions,
              });
              if (!element || element["@_id"] === process["@_id"]) {
                if (newSlaDueDate) {
                  setBpmn20Drools10MetaData(process, "customSLADueDate", newSlaDueDate);
                } else {
                  deleteBpmn20Drools10MetaDataEntry(process, "customSLADueDate");
                }
              } else {
                visitFlowElementsAndArtifacts(process, ({ element: e }) => {
                  if (e["@_id"] === element["@_id"]) {
                    if (newSlaDueDate) {
                      setBpmn20Drools10MetaData(
                        e as { extensionElements?: WithMetaData },
                        "customSLADueDate",
                        newSlaDueDate
                      );
                    } else {
                      deleteBpmn20Drools10MetaDataEntry(e as { extensionElements?: WithMetaData }, "customSLADueDate");
                    }
                  }
                });
              }
            })
          }
        />
      </FormGroup>
    </>
  );
}
