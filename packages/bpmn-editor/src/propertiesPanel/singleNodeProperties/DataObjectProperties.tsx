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
import { BPMN20__tDataObject } from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/types";
import { Normalized } from "../../normalization/normalize";
import { NameDocumentationAndId } from "../nameDocumentationAndId/NameDocumentationAndId";
import { ItemDefinitionRefSelector } from "../itemDefinitionRefSelector/ItemDefinitionRefSelector";
import { PropertiesPanelHeaderFormSection } from "./_PropertiesPanelHeaderFormSection";
import { DataObjectIcon } from "../../diagram/nodes/NodeIcons";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { useBpmnEditorStoreApi } from "../../store/StoreContext";
import { visitFlowElementsAndArtifacts } from "../../mutations/_elementVisitor";
import { addOrGetProcessAndDiagramElements } from "../../mutations/addOrGetProcessAndDiagramElements";
import { FormGroup } from "@patternfly/react-core/dist/js/components/Form/FormGroup";
import { useBpmnEditorI18n } from "../../i18n";

export function DataObjectProperties({
  dataObject,
}: {
  dataObject: Normalized<BPMN20__tDataObject> & { __$$element: "dataObject" };
}) {
  const { i18n } = useBpmnEditorI18n();
  const bpmnEditorStoreApi = useBpmnEditorStoreApi();
  return (
    <PropertiesPanelHeaderFormSection title={dataObject["@_name"] || "Data object"} icon={<DataObjectIcon />}>
      <NameDocumentationAndId element={dataObject} />

      <Divider inset={{ default: "insetXs" }} />

      <FormGroup label={i18n.propertiesPanel.dataType}>
        <ItemDefinitionRefSelector
          value={dataObject["@_itemSubjectRef"]}
          onChange={(newItemDefinitionRef) => {
            bpmnEditorStoreApi.setState((s) => {
              const { process } = addOrGetProcessAndDiagramElements({ definitions: s.bpmn.model.definitions });
              visitFlowElementsAndArtifacts(process, ({ element }) => {
                if (element["@_id"] === dataObject["@_id"] && element.__$$element === dataObject.__$$element) {
                  element["@_itemSubjectRef"] = newItemDefinitionRef;
                }
              });
            });
          }}
        />
      </FormGroup>
    </PropertiesPanelHeaderFormSection>
  );
}
