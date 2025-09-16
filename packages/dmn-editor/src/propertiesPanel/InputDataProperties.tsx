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
import { DMN_LATEST__tInputData } from "@kie-tools/dmn-marshaller";
import { Normalized } from "@kie-tools/dmn-marshaller/dist/normalization/normalize";
import { ClipboardCopy } from "@patternfly/react-core/dist/js/components/ClipboardCopy";
import { FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { TextArea } from "@patternfly/react-core/dist/js/components/TextArea";
import { DocumentationLinksFormGroup } from "./DocumentationLinksFormGroup";
import { TypeRefSelector } from "../dataTypes/TypeRefSelector";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../store/StoreContext";
import { InlineFeelNameInput } from "../feel/InlineFeelNameInput";
import { useDmnEditor } from "../DmnEditorContext";
import { useResolvedTypeRef } from "../dataTypes/useResolvedTypeRef";
import { useCallback, useMemo } from "react";
import { generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import { useSettings } from "../settings/DmnEditorSettingsContext";
import { useRefactor } from "../refactor/RefactorConfirmationDialog";
import { useDmnEditorI18n } from "../i18n";

export function InputDataProperties({
  inputData,
  namespace,
  index,
}: {
  inputData: Normalized<DMN_LATEST__tInputData>;
  namespace: string | undefined;
  index: number;
}) {
  const { i18n } = useDmnEditorI18n();
  const { setState } = useDmnEditorStoreApi();
  const settings = useSettings();
  const thisDmnsNamespace = useDmnEditorStore((s) => s.dmn.model.definitions["@_namespace"]);
  const isReadOnly = settings.isReadOnly || (!!namespace && namespace !== thisDmnsNamespace);

  const { dmnEditorRootElementRef } = useDmnEditor();

  const resolvedTypeRef = useResolvedTypeRef(inputData.variable?.["@_typeRef"], namespace);

  const identifierId = useMemo(() => inputData["@_id"], [inputData]);
  const oldName = useMemo(() => inputData["@_label"] ?? inputData["@_name"], [inputData]);

  const { setNewIdentifierNameCandidate, refactorConfirmationDialog, newName } = useRefactor({
    index,
    identifierId,
    oldName,
  });

  const currentName = useMemo(() => {
    return newName === "" ? oldName : newName;
  }, [newName, oldName]);

  return (
    <>
      {refactorConfirmationDialog}
      <FormGroup label={i18n.name}>
        <InlineFeelNameInput
          enableAutoFocusing={false}
          isPlain={false}
          id={inputData["@_id"]!}
          name={currentName}
          isReadOnly={isReadOnly}
          shouldCommitOnBlur={true}
          className={"pf-v5-c-form-control"}
          onRenamed={setNewIdentifierNameCandidate}
          allUniqueNames={useCallback((s) => s.computed(s).getAllFeelVariableUniqueNames(), [])}
        />
      </FormGroup>
      <FormGroup label={i18n.propertiesPanel.dataType}>
        <TypeRefSelector
          heightRef={dmnEditorRootElementRef}
          typeRef={resolvedTypeRef}
          isDisabled={isReadOnly}
          onChange={(newTypeRef) => {
            setState((state) => {
              const drgElement = state.dmn.model.definitions.drgElement![index] as Normalized<DMN_LATEST__tInputData>;
              drgElement.variable ??= { "@_id": generateUuid(), "@_name": inputData["@_name"] };
              drgElement.variable["@_typeRef"] = newTypeRef;
            });
          }}
        />
      </FormGroup>
      <FormGroup label={i18n.propertiesPanel.description}>
        <TextArea
          aria-label={"Description"}
          type={"text"}
          isDisabled={isReadOnly}
          value={inputData.description?.__$$text ?? ""}
          onChange={(_event, newDescription) => {
            setState((state) => {
              (state.dmn.model.definitions.drgElement![index] as Normalized<DMN_LATEST__tInputData>).description = {
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
          {inputData["@_id"]}
        </ClipboardCopy>
      </FormGroup>

      <DocumentationLinksFormGroup
        isReadOnly={isReadOnly}
        values={inputData.extensionElements?.["kie:attachment"]}
        onChange={(newExtensionElements) => {
          setState((state) => {
            (state.dmn.model.definitions.drgElement![index] as Normalized<DMN_LATEST__tInputData>).extensionElements = {
              "kie:attachment": newExtensionElements,
            };
          });
        }}
      />
    </>
  );
}
