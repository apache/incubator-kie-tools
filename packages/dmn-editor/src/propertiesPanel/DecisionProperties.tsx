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
import { DMN15__tDecision } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
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

export function DecisionProperties({
  decision,
  namespace,
  index,
}: {
  decision: Normalized<DMN15__tDecision>;
  namespace: string | undefined;
  index: number;
}) {
  const { setState } = useDmnEditorStoreApi();
  const settings = useSettings();

  const thisDmnsNamespace = useDmnEditorStore((s) => s.dmn.model.definitions["@_namespace"]);
  const isReadOnly = settings.isReadOnly || (!!namespace && namespace !== thisDmnsNamespace);

  const { dmnEditorRootElementRef } = useDmnEditor();

  const resolvedTypeRef = useResolvedTypeRef(decision.variable?.["@_typeRef"], namespace);

  const identifierId = useMemo(() => decision["@_id"], [decision]);
  const oldName = useMemo(() => decision["@_label"] ?? decision["@_name"], [decision]);
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
      <FormGroup label="Name">
        <InlineFeelNameInput
          enableAutoFocusing={false}
          isPlain={false}
          id={decision["@_id"]!}
          name={currentName}
          isReadOnly={isReadOnly}
          shouldCommitOnBlur={true}
          className={"pf-v5-c-form-control"}
          onRenamed={setNewIdentifierNameCandidate}
          allUniqueNames={useCallback((s) => s.computed(s).getAllFeelVariableUniqueNames(), [])}
        />
      </FormGroup>

      <FormGroup label="Data type">
        <TypeRefSelector
          heightRef={dmnEditorRootElementRef}
          typeRef={resolvedTypeRef}
          isDisabled={isReadOnly}
          onChange={(newTypeRef) => {
            setState((state) => {
              const drgElement = state.dmn.model.definitions.drgElement![index] as Normalized<DMN15__tDecision>;
              drgElement.variable ??= { "@_id": generateUuid(), "@_name": decision["@_name"] };
              drgElement.variable["@_typeRef"] = newTypeRef;
            });
          }}
        />
      </FormGroup>

      <FormGroup label="Description">
        <TextArea
          aria-label={"Description"}
          type={"text"}
          isDisabled={isReadOnly}
          value={decision.description?.__$$text}
          onChange={(_event, newDescription) => {
            setState((state) => {
              (state.dmn.model.definitions.drgElement![index] as Normalized<DMN15__tDecision>).description = {
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
          {decision["@_id"]}
        </ClipboardCopy>
      </FormGroup>

      <FormGroup label="Question">
        <TextArea
          aria-label={"Question"}
          type={"text"}
          isDisabled={isReadOnly}
          value={decision.question?.__$$text}
          onChange={(_event, newQuestion) => {
            setState((state) => {
              (state.dmn.model.definitions.drgElement![index] as Normalized<DMN15__tDecision>).question = {
                __$$text: newQuestion,
              };
            });
          }}
          placeholder={"Enter a question..."}
          style={{ resize: "vertical", minHeight: "40px" }}
          rows={6}
        />
      </FormGroup>

      <FormGroup label="Allowed answers">
        <TextArea
          aria-label={"Allowed answers"}
          type={"text"}
          isDisabled={isReadOnly}
          value={decision.allowedAnswers?.__$$text}
          onChange={(_event, newAllowedAnswers) => {
            setState((state) => {
              (state.dmn.model.definitions.drgElement![index] as Normalized<DMN15__tDecision>).allowedAnswers = {
                __$$text: newAllowedAnswers,
              };
            });
          }}
          placeholder={"Enter allowed answers..."}
          style={{ resize: "vertical", minHeight: "40px" }}
          rows={3}
        />
      </FormGroup>

      <DocumentationLinksFormGroup
        isReadOnly={isReadOnly}
        values={decision.extensionElements?.["kie:attachment"]}
        onChange={(newExtensionElements) => {
          setState((state) => {
            (state.dmn.model.definitions.drgElement![index] as Normalized<DMN15__tDecision>).extensionElements = {
              "kie:attachment": newExtensionElements,
            };
          });
        }}
      />

      {/*

      What about:

      - supportedObjective
      - impactedPerformanceIndicator
      - decisionMaker
      - decisionOwner
      - usingProcess
      - usingTask

      ?
      */}
    </>
  );
}
