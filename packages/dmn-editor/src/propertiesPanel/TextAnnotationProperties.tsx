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
import { DMN15__tTextAnnotation } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { ClipboardCopy } from "@patternfly/react-core/dist/js/components/ClipboardCopy";
import { FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { TextArea } from "@patternfly/react-core/dist/js/components/TextArea";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { useDmnEditorStoreApi } from "../store/StoreContext";
import { updateTextAnnotation } from "../mutations/renameNode";
import { Normalized } from "../normalization/normalize";
import { useSettings } from "../settings/DmnEditorSettingsContext";

export function TextAnnotationProperties({
  textAnnotation,
  index,
}: {
  textAnnotation: Normalized<DMN15__tTextAnnotation>;
  index: number;
}) {
  const { setState } = useDmnEditorStoreApi();
  const settings = useSettings();

  return (
    <>
      <FormGroup label="Format">
        <TextInput
          aria-label={"Format"}
          type={"text"}
          isDisabled={settings.isReadOnly}
          value={textAnnotation["@_textFormat"]}
          placeholder={"Enter a text format..."}
          onChange={(_event, newTextFormat) => {
            setState((state) => {
              (state.dmn.model.definitions.artifact![index] as Normalized<DMN15__tTextAnnotation>)["@_textFormat"] =
                newTextFormat;
            });
          }}
        />
      </FormGroup>

      <FormGroup label="Text">
        <TextArea
          aria-label={"Text"}
          type={"text"}
          isDisabled={settings.isReadOnly}
          value={textAnnotation.text?.__$$text}
          onChange={(_event, newText) => {
            setState((state) => {
              updateTextAnnotation({
                definitions: state.dmn.model.definitions,
                index,
                newText,
              });
            });
          }}
          placeholder={"Enter text..."}
          style={{ resize: "vertical", minHeight: "40px" }}
          rows={6}
        />
      </FormGroup>

      <FormGroup label="Description">
        <TextArea
          aria-label={"Description"}
          type={"text"}
          isDisabled={settings.isReadOnly}
          value={textAnnotation.description?.__$$text}
          onChange={(_event, newDescription) => {
            setState((state) => {
              (state.dmn.model.definitions.artifact![index] as Normalized<DMN15__tTextAnnotation>).description = {
                __$$text: newDescription,
              };
            });
          }}
          placeholder={"Enter a description..."}
          style={{ resize: "vertical", minHeight: "40px" }}
          rows={2}
        />
      </FormGroup>

      <FormGroup label="ID">
        <ClipboardCopy isReadOnly={true} hoverTip="Copy" clickTip="Copied">
          {textAnnotation["@_id"]}
        </ClipboardCopy>
      </FormGroup>
    </>
  );
}
