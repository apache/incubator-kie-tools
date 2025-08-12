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
import { DMN15__tGroup } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { Normalized } from "@kie-tools/dmn-marshaller/dist/normalization/normalize";
import { ClipboardCopy } from "@patternfly/react-core/dist/js/components/ClipboardCopy";
import { FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { TextArea } from "@patternfly/react-core/dist/js/components/TextArea";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { useDmnEditorStoreApi } from "../store/StoreContext";
import { renameGroupNode } from "../mutations/renameNode";
import { useSettings } from "../settings/DmnEditorSettingsContext";
import { useDmnEditorI18n } from "../i18n";

export function GroupProperties({ group, index }: { group: Normalized<DMN15__tGroup>; index: number }) {
  const { i18n } = useDmnEditorI18n();
  const { setState } = useDmnEditorStoreApi();
  const settings = useSettings();

  return (
    <>
      <FormGroup label={i18n.name}>
        <TextInput
          aria-label={"Name"}
          type={"text"}
          isDisabled={settings.isReadOnly}
          onChange={(_event, newName) => {
            setState((state) => {
              renameGroupNode({
                definitions: state.dmn.model.definitions,
                index,
                newName,
              });
            });
          }}
          value={group["@_name"]}
          placeholder={i18n.propertiesPanel.namePlaceholder}
        />
      </FormGroup>

      <FormGroup label={i18n.propertiesPanel.description}>
        <TextArea
          aria-label={"Description"}
          type={"text"}
          isDisabled={settings.isReadOnly}
          value={group.description?.__$$text}
          onChange={(_event, newDescription) => {
            setState((state) => {
              (state.dmn.model.definitions.artifact![index] as Normalized<DMN15__tGroup>).description = {
                __$$text: newDescription,
              };
            });
          }}
          placeholder={i18n.propertiesPanel.descriptionPlaceholder}
          style={{ resize: "vertical", minHeight: "40px" }}
          rows={6}
        />
      </FormGroup>

      <FormGroup label={i18n.propertiesPanel.id}>
        <ClipboardCopy isReadOnly={true} hoverTip={i18n.propertiesPanel.copy} clickTip={i18n.propertiesPanel.copied}>
          {group["@_id"]}
        </ClipboardCopy>
      </FormGroup>
    </>
  );
}
