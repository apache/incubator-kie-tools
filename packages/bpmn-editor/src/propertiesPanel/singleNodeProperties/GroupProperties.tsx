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
import { BPMN20__tGroup } from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/types";
import { Normalized } from "../../normalization/normalize";
import { GroupIcon } from "../../diagram/nodes/NodeIcons";
import { PropertiesPanelHeaderFormSection } from "./_PropertiesPanelHeaderFormSection";
import { addOrGetCategory } from "../../mutations/addOrGetCategory";
import { useCallback } from "react";
import { useBpmnEditorStore, useBpmnEditorStoreApi } from "../../store/StoreContext";
import { visitFlowElementsAndArtifacts } from "../../mutations/_elementVisitor";
import { generateUuid } from "@kie-tools/xyflow-react-kie-diagram/dist/uuid/uuid";
import { FormSection } from "@patternfly/react-core/dist/js/components/Form/FormSection";
import { FormGroup } from "@patternfly/react-core/dist/js/components/Form/FormGroup";
import { TextArea } from "@patternfly/react-core/dist/js/components/TextArea/TextArea";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput/TextInput";
import { ClipboardCopy } from "@patternfly/react-core/dist/js/components/ClipboardCopy/ClipboardCopy";
import { addOrGetProcessAndDiagramElements } from "../../mutations/addOrGetProcessAndDiagramElements";
import { useBpmnEditorI18n } from "../../i18n";

export function GroupProperties({ group }: { group: Normalized<BPMN20__tGroup> & { __$$element: "group" } }) {
  const { i18n } = useBpmnEditorI18n();
  const bpmnEditorStoreApi = useBpmnEditorStoreApi();
  const settings = useBpmnEditorStore((s) => s.settings);

  const onNameChanged = useCallback(
    (e: React.FormEvent, newName: string) => {
      bpmnEditorStoreApi.setState((s) => {
        const { category } = addOrGetCategory({
          definitions: s.bpmn.model.definitions,
        });
        category.categoryValue ??= [
          {
            "@_id": generateUuid(),
          },
        ];
        category.categoryValue[0]["@_value"] = newName;
        const { process } = addOrGetProcessAndDiagramElements({
          definitions: s.bpmn.model.definitions,
        });
        visitFlowElementsAndArtifacts(process, ({ element: e }) => {
          if (e["@_id"] === group["@_id"] && e.__$$element === group.__$$element) {
            e["@_categoryValueRef"] = newName;
          }
        });
      });
    },
    [bpmnEditorStoreApi, group]
  );

  const onDocumentationChanged = useCallback(
    (newDocumentation) =>
      bpmnEditorStoreApi.setState((s) => {
        const { process } = addOrGetProcessAndDiagramElements({
          definitions: s.bpmn.model.definitions,
        });
        visitFlowElementsAndArtifacts(process, ({ element: e }) => {
          if (e["@_id"] === group["@_id"] && e.__$$element === group.__$$element) {
            e.documentation ??= [];
            e.documentation[0] = {
              "@_id": generateUuid(),
              __$$text: newDocumentation,
            };
          }
        });
      }),
    [bpmnEditorStoreApi, group]
  );

  return (
    <>
      <PropertiesPanelHeaderFormSection title={"Group"} icon={<GroupIcon />}>
        <FormSection>
          <FormGroup label={i18n.propertiesPanel.name}>
            <TextInput
              isDisabled={settings.isReadOnly}
              id={group["@_id"]}
              value={group["@_categoryValueRef"] || ""}
              placeholder={"Enter a name..."}
              onChange={onNameChanged}
            />
          </FormGroup>

          <FormGroup label={i18n.propertiesPanel.documentation}>
            <TextArea
              aria-label={"Documentation"}
              type={"text"}
              isDisabled={settings.isReadOnly}
              value={group?.documentation?.[0].__$$text || ""}
              onChange={onDocumentationChanged}
              placeholder={"Enter documentation..."}
              style={{ resize: "vertical", minHeight: "40px" }}
              rows={3}
            />
          </FormGroup>

          <FormGroup label={i18n.propertiesPanel.id}>
            <ClipboardCopy
              isReadOnly={settings.isReadOnly}
              hoverTip={i18n.propertiesPanel.copy}
              clickTip={i18n.propertiesPanel.copied}
            >
              {" "}
              {group["@_id"]}
            </ClipboardCopy>
          </FormGroup>
        </FormSection>
      </PropertiesPanelHeaderFormSection>
    </>
  );
}
