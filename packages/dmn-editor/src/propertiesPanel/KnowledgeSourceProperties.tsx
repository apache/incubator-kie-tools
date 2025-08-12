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
import { DMN15__tKnowledgeSource } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { Normalized } from "@kie-tools/dmn-marshaller/dist/normalization/normalize";
import { ClipboardCopy } from "@patternfly/react-core/dist/js/components/ClipboardCopy";
import { FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { TextArea } from "@patternfly/react-core/dist/js/components/TextArea";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { DocumentationLinksFormGroup } from "./DocumentationLinksFormGroup";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../store/StoreContext";

import { InlineFeelNameInput } from "../feel/InlineFeelNameInput";
import { useCallback, useMemo } from "react";
import { useSettings } from "../settings/DmnEditorSettingsContext";
import { useRefactor } from "../refactor/RefactorConfirmationDialog";
import { useDmnEditorI18n } from "../i18n";

export function KnowledgeSourceProperties({
  knowledgeSource,
  namespace,
  index,
}: {
  knowledgeSource: Normalized<DMN15__tKnowledgeSource>;
  namespace: string | undefined;
  index: number;
}) {
  const { i18n } = useDmnEditorI18n();
  const { setState } = useDmnEditorStoreApi();
  const settings = useSettings();

  const thisDmnsNamespace = useDmnEditorStore((s) => s.dmn.model.definitions["@_namespace"]);
  const isReadOnly = settings.isReadOnly || (!!namespace && namespace !== thisDmnsNamespace);
  const identifierId = useMemo(() => knowledgeSource["@_id"], [knowledgeSource]);
  const oldName = useMemo(() => knowledgeSource["@_label"] ?? knowledgeSource["@_name"], [knowledgeSource]);

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
          id={knowledgeSource["@_id"]!}
          name={currentName}
          isReadOnly={isReadOnly}
          shouldCommitOnBlur={true}
          className={"pf-v5-c-form-control"}
          onRenamed={setNewIdentifierNameCandidate}
          allUniqueNames={useCallback((s) => s.computed(s).getAllFeelVariableUniqueNames(), [])}
        />
      </FormGroup>

      <FormGroup label={i18n.propertiesPanel.description}>
        <TextArea
          aria-label={"Description"}
          type={"text"}
          isDisabled={isReadOnly}
          value={knowledgeSource.description?.__$$text}
          onChange={(_event, newDescription) => {
            setState((state) => {
              (state.dmn.model.definitions.drgElement![index] as Normalized<DMN15__tKnowledgeSource>).description = {
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
          {knowledgeSource["@_id"]}
        </ClipboardCopy>
      </FormGroup>

      <FormGroup label={i18n.propertiesPanel.sourceType}>
        <TextInput
          aria-label={"Source type"}
          type={"text"}
          isDisabled={isReadOnly}
          value={knowledgeSource.type?.__$$text}
          onChange={(_event, newType) => {
            setState((state) => {
              (state.dmn.model.definitions.drgElement![index] as Normalized<DMN15__tKnowledgeSource>).type = {
                __$$text: newType,
              };
            });
          }}
          placeholder={i18n.propertiesPanel.sourceTypePlaceHolder}
        />
      </FormGroup>

      <FormGroup label="">
        <TextInput
          aria-label={i18n.propertiesPanel.locationUri}
          type={"text"}
          isDisabled={isReadOnly}
          value={knowledgeSource["@_locationURI"]}
          onChange={(_event, newLocationUri) => {
            setState((state) => {
              (state.dmn.model.definitions.drgElement![index] as Normalized<DMN15__tKnowledgeSource>)["@_locationURI"] =
                newLocationUri;
            });
          }}
          placeholder={i18n.propertiesPanel.locationUriPlaceholder}
        />
      </FormGroup>

      <DocumentationLinksFormGroup
        isReadOnly={isReadOnly}
        values={knowledgeSource.extensionElements?.["kie:attachment"]}
        onChange={(newExtensionElements) => {
          setState((state) => {
            (state.dmn.model.definitions.drgElement![index] as Normalized<DMN15__tKnowledgeSource>).extensionElements =
              {
                "kie:attachment": newExtensionElements,
              };
          });
        }}
      />
    </>
  );
}
