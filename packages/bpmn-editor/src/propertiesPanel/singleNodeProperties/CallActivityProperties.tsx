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
import { BPMN20__tCallActivity } from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/types";
import { FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { Normalized } from "../../normalization/normalize";
import { useBpmnEditorStoreApi } from "../../store/StoreContext";
import { NameDocumentationAndId } from "../nameDocumentationAndId/NameDocumentationAndId";
import { BidirectionalDataMappingFormSection } from "../dataMapping/DataMappingFormSection";
import { OnEntryAndExitScriptsFormSection } from "../onEntryAndExitScripts/OnEntryAndExitScriptsFormSection";
import { CallActivityIcon } from "../../diagram/nodes/NodeIcons";
import { PropertiesPanelHeaderFormSection } from "./_PropertiesPanelHeaderFormSection";
import { CalledElementSelector } from "../calledElementSelector/CalledElementSelector";
import { MultiInstanceProperties } from "../multiInstance/MultiInstanceProperties";
import { Checkbox } from "@patternfly/react-core/dist/js/components/Checkbox";
import { visitFlowElementsAndArtifacts } from "../../mutations/_elementVisitor";
import { addOrGetProcessAndDiagramElements } from "../../mutations/addOrGetProcessAndDiagramElements";
import {
  parseBpmn20Drools10MetaData,
  setBpmn20Drools10MetaData,
} from "@kie-tools/bpmn-marshaller/dist/drools-extension-metaData";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { AsyncCheckbox } from "../asyncCheckbox/AsyncCheckbox";
import { SlaDueDateInput } from "../slaDueDate/SlaDueDateInput";
import { MultiInstanceCheckbox } from "../multiInstanceCheckbox/MultiInstanceCheckbox";
import { useBpmnEditorI18n } from "../../i18n";

export function CallActivityProperties({
  callActivity,
}: {
  callActivity: Normalized<BPMN20__tCallActivity> & { __$$element: "callActivity" };
}) {
  const { i18n } = useBpmnEditorI18n();
  const bpmnEditorStoreApi = useBpmnEditorStoreApi();

  return (
    <>
      <PropertiesPanelHeaderFormSection
        title={callActivity["@_name"] || i18n.singleNodeProperties.callActivity}
        icon={<CallActivityIcon />}
      >
        <NameDocumentationAndId element={callActivity} />

        <Divider inset={{ default: "insetXs" }} />

        <CalledElementSelector element={callActivity} />

        <FormGroup
          fieldId="kie-bpmn-editor--properties-panel--call-activity--independent"
          // helperText={"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod."} // FIXME: Tiago -> Description
        >
          <Checkbox
            label={i18n.singleNodeProperties.independent}
            id="kie-bpmn-editor--properties-panel--call-activity--independent"
            name="is-independent"
            aria-label="Independent"
            isChecked={callActivity["@_drools:independent"] ?? false}
            onChange={(e, checked) => {
              bpmnEditorStoreApi.setState((s) => {
                const { process } = addOrGetProcessAndDiagramElements({
                  definitions: s.bpmn.model.definitions,
                });
                visitFlowElementsAndArtifacts(process, ({ element }) => {
                  if (element["@_id"] === callActivity["@_id"] && element.__$$element === callActivity.__$$element) {
                    element["@_drools:independent"] = checked;
                  }
                });
              });
            }}
          />
        </FormGroup>

        {!(callActivity["@_drools:independent"] === true) && (
          <FormGroup
            fieldId="kie-bpmn-editor--properties-panel--call-activity--abort-parent"
            // helperText={"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod."} // FIXME: Tiago -> Description
          >
            <Checkbox
              label={i18n.singleNodeProperties.abortParent}
              id="kie-bpmn-editor--properties-panel--call-activity--abort-parent"
              name="should-abort-parent"
              aria-label="Abort parent"
              isChecked={(parseBpmn20Drools10MetaData(callActivity).get("customAbortParent") ?? "true") === "true"}
              onChange={(e, checked) => {
                bpmnEditorStoreApi.setState((s) => {
                  const { process } = addOrGetProcessAndDiagramElements({
                    definitions: s.bpmn.model.definitions,
                  });
                  visitFlowElementsAndArtifacts(process, ({ element: e }) => {
                    if (e["@_id"] === callActivity["@_id"] && e.__$$element === callActivity.__$$element) {
                      setBpmn20Drools10MetaData(e, "customAbortParent", `${checked}`);
                    }
                  });
                });
              }}
            />
          </FormGroup>
        )}

        <FormGroup
          fieldId="kie-bpmn-editor--properties-panel--call-activity--wait-for-completion"
          // helperText={"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod."} // FIXME: Tiago -> Description
        >
          <Checkbox
            label={i18n.singleNodeProperties.waitForCompletion}
            id="kie-bpmn-editor--properties-panel--call-activity--wait-for-completion"
            name="should-wait-for-completion"
            aria-label="Wait for completion"
            isChecked={callActivity["@_drools:waitForCompletion"] ?? true}
            onChange={(e, checked) => {
              bpmnEditorStoreApi.setState((s) => {
                const { process } = addOrGetProcessAndDiagramElements({
                  definitions: s.bpmn.model.definitions,
                });
                visitFlowElementsAndArtifacts(process, ({ element: e }) => {
                  if (e["@_id"] === callActivity["@_id"] && e.__$$element === callActivity.__$$element) {
                    e["@_drools:waitForCompletion"] = checked;
                  }
                });
              });
            }}
          />
        </FormGroup>

        <Divider inset={{ default: "insetXs" }} />

        <SlaDueDateInput element={callActivity} />

        <AsyncCheckbox element={callActivity} />

        <Divider inset={{ default: "insetXs" }} />

        <MultiInstanceCheckbox element={callActivity} />

        {callActivity.loopCharacteristics?.__$$element === "multiInstanceLoopCharacteristics" && (
          <MultiInstanceProperties element={callActivity} />
        )}
      </PropertiesPanelHeaderFormSection>

      <BidirectionalDataMappingFormSection element={callActivity} />

      <OnEntryAndExitScriptsFormSection element={callActivity} />
    </>
  );
}
