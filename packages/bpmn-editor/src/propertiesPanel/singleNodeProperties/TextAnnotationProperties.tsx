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

import { BPMN20__tTextAnnotation } from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/types";
import { FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import * as React from "react";
import { updateTextAnnotation } from "../../mutations/renameNode";
import { Normalized } from "../../normalization/normalize";
import { useBpmnEditorStore, useBpmnEditorStoreApi } from "../../store/StoreContext";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { TextArea } from "@patternfly/react-core/dist/js/components/TextArea";
import { ClipboardCopy } from "@patternfly/react-core/dist/js/components/ClipboardCopy";
import { PropertiesPanelHeaderFormSection } from "./_PropertiesPanelHeaderFormSection";
import { TextAnnotationIcon } from "../../diagram/nodes/NodeIcons";
import { visitFlowElementsAndArtifacts } from "../../mutations/_elementVisitor";
import { addOrGetProcessAndDiagramElements } from "../../mutations/addOrGetProcessAndDiagramElements";
import { generateUuid } from "@kie-tools/xyflow-react-kie-diagram/dist/uuid/uuid";
import { setBpmn20Drools10MetaData } from "@kie-tools/bpmn-marshaller/dist/drools-extension-metaData";
import { useBpmnEditorI18n } from "../../i18n";

export function TextAnnotationProperties({
  textAnnotation,
}: {
  textAnnotation: Normalized<BPMN20__tTextAnnotation> & { __$$element: "textAnnotation" };
}) {
  const { i18n } = useBpmnEditorI18n();
  const bpmnEditorStoreApi = useBpmnEditorStoreApi();
  const settings = useBpmnEditorStore((s) => s.settings);

  return (
    <PropertiesPanelHeaderFormSection title={i18n.singleNodeProperties.textAnnotation} icon={<TextAnnotationIcon />}>
      <FormGroup label="Format">
        <TextInput
          aria-label={i18n.singleNodeProperties.format}
          type={"text"}
          isDisabled={settings.isReadOnly}
          placeholder={i18n.singleNodeProperties.formatPlaceholder}
          value={textAnnotation["@_textFormat"] ?? ""}
          onChange={(e, newTextFormat) => {
            bpmnEditorStoreApi.setState((s) => {
              updateTextAnnotation({
                definitions: s.bpmn.model.definitions,
                newTextAnnotation: { "@_textFormat": newTextFormat },
                id: textAnnotation["@_id"]!,
              });
            });
          }}
        />
      </FormGroup>

      <FormGroup label={i18n.singleNodeProperties.text}>
        <TextArea
          aria-label={"Text"}
          type={"text"}
          isDisabled={settings.isReadOnly}
          value={textAnnotation.text?.__$$text ?? ""}
          onChange={(e, newText) => {
            bpmnEditorStoreApi.setState((s) => {
              updateTextAnnotation({
                definitions: s.bpmn.model.definitions,
                newTextAnnotation: { text: { __$$text: newText } },
                id: textAnnotation["@_id"]!,
              });
              const { process } = addOrGetProcessAndDiagramElements({
                definitions: s.bpmn.model.definitions,
              });
              visitFlowElementsAndArtifacts(process, ({ element: e }) => {
                if (e["@_id"] === textAnnotation?.["@_id"] && e.__$$element === textAnnotation.__$$element) {
                  setBpmn20Drools10MetaData(e, "elementname", e.text?.__$$text || "");
                }
              });
            });
          }}
          placeholder={i18n.singleNodeProperties.textPlaceholder}
          style={{ resize: "vertical", minHeight: "40px" }}
          rows={3}
        />
      </FormGroup>

      <FormGroup label={i18n.propertiesPanel.documentation}>
        <TextArea
          aria-label={"Documentation"}
          type={"text"}
          isDisabled={settings.isReadOnly}
          value={textAnnotation?.documentation?.[0].__$$text || ""}
          onChange={(e, newDocumentation) => {
            bpmnEditorStoreApi.setState((s) => {
              const { process } = addOrGetProcessAndDiagramElements({
                definitions: s.bpmn.model.definitions,
              });
              visitFlowElementsAndArtifacts(process, ({ element: e }) => {
                if (e["@_id"] === textAnnotation["@_id"] && e.__$$element === textAnnotation.__$$element) {
                  e.documentation ??= [];
                  e.documentation[0] = {
                    "@_id": generateUuid(),
                    __$$text: newDocumentation,
                  };
                }
              });
            });
          }}
          placeholder={i18n.propertiesPanel.documentationPlaceholder}
          style={{ resize: "vertical", minHeight: "40px" }}
          rows={3}
        />
      </FormGroup>

      <FormGroup label={i18n.propertiesPanel.id}>
        <ClipboardCopy
          isReadOnly={settings.isReadOnly}
          hoverTip={i18n.propertiesPanel.copy}
          clickTip={i18n.propertiesPanel.copied}
          onChange={(e, newId) => {
            bpmnEditorStoreApi.setState((s) => {
              updateTextAnnotation({
                definitions: s.bpmn.model.definitions,
                newTextAnnotation: { "@_id": newId },
                id: textAnnotation["@_id"]!,
              });
            });
          }}
        >
          {textAnnotation["@_id"]}
        </ClipboardCopy>
      </FormGroup>
    </PropertiesPanelHeaderFormSection>
  );
}
