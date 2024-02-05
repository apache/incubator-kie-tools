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
import { ClipboardCopy } from "@patternfly/react-core/dist/js/components/ClipboardCopy";
import { FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { TextArea } from "@patternfly/react-core/dist/js/components/TextArea";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { DocumentationLinksFormGroup } from "./DocumentationLinksFormGroup";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../store/StoreContext";
import { renameDrgElement } from "../mutations/renameNode";
import { InlineFeelNameInput } from "../feel/InlineFeelNameInput";
import { useCallback } from "react";

export function KnowledgeSourceProperties({
  knowledgeSource,
  namespace,
  index,
}: {
  knowledgeSource: DMN15__tKnowledgeSource;
  namespace: string | undefined;
  index: number;
}) {
  const { setState } = useDmnEditorStoreApi();

  const thisDmnsNamespace = useDmnEditorStore((s) => s.dmn.model.definitions["@_namespace"]);
  const isReadonly = !!namespace && namespace !== thisDmnsNamespace;

  return (
    <>
      <FormGroup label="Name">
        <InlineFeelNameInput
          enableAutoFocusing={false}
          isPlain={false}
          id={knowledgeSource["@_id"]!}
          name={knowledgeSource["@_name"]}
          isReadonly={isReadonly}
          shouldCommitOnBlur={true}
          className={"pf-c-form-control"}
          onRenamed={(newName) => {
            setState((state) => {
              renameDrgElement({
                definitions: state.dmn.model.definitions,
                index,
                newName,
              });
            });
          }}
          allUniqueNames={useCallback((s) => s.computed(s).getAllFeelVariableUniqueNames(), [])}
        />
      </FormGroup>

      <FormGroup label="Description">
        <TextArea
          aria-label={"Description"}
          type={"text"}
          isDisabled={isReadonly}
          value={knowledgeSource.description?.__$$text}
          onChange={(newDescription) => {
            setState((state) => {
              (state.dmn.model.definitions.drgElement![index] as DMN15__tKnowledgeSource).description = {
                __$$text: newDescription,
              };
            });
          }}
          placeholder={"Enter a description..."}
          style={{ resize: "vertical", minHeight: "40px" }}
          rows={6}
        />
      </FormGroup>

      <FormGroup label="ID">
        <ClipboardCopy isReadOnly={true} hoverTip="Copy" clickTip="Copied">
          {knowledgeSource["@_id"]}
        </ClipboardCopy>
      </FormGroup>

      <FormGroup label="Source type">
        <TextInput
          aria-label={"Source type"}
          type={"text"}
          isDisabled={isReadonly}
          value={knowledgeSource.type?.__$$text}
          onChange={(newType) => {
            setState((state) => {
              (state.dmn.model.definitions.drgElement![index] as DMN15__tKnowledgeSource).type = { __$$text: newType };
            });
          }}
          placeholder={"Enter source type..."}
        />
      </FormGroup>

      <FormGroup label="Location URI">
        <TextInput
          aria-label={"Location URI"}
          type={"text"}
          isDisabled={isReadonly}
          value={knowledgeSource["@_locationURI"]}
          onChange={(newLocationUri) => {
            setState((state) => {
              (state.dmn.model.definitions.drgElement![index] as DMN15__tKnowledgeSource)["@_locationURI"] =
                newLocationUri;
            });
          }}
          placeholder={"Enter location URI..."}
        />
      </FormGroup>

      <DocumentationLinksFormGroup
        isReadonly={isReadonly}
        values={knowledgeSource.extensionElements?.["kie:attachment"]}
        onChange={(newExtensionElements) => {
          setState((state) => {
            (state.dmn.model.definitions.drgElement![index] as DMN15__tKnowledgeSource).extensionElements = {
              "kie:attachment": newExtensionElements,
            };
          });
        }}
      />
    </>
  );
}
