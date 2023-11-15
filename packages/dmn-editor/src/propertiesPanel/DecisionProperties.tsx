import * as React from "react";
import { DMN15__tDecision } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { ClipboardCopy } from "@patternfly/react-core/dist/js/components/ClipboardCopy";
import { FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { TextArea } from "@patternfly/react-core/dist/js/components/TextArea";
import { DocumentationLinksFormGroup } from "./DocumentationLinksFormGroup";
import { TypeRefSelector } from "../dataTypes/TypeRefSelector";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../store/Store";
import { renameDrgElement } from "../mutations/renameNode";
import { InlineFeelNameInput } from "../feel/InlineFeelNameInput";
import { useDmnEditorDerivedStore } from "../store/DerivedStore";
import { useDmnEditor } from "../DmnEditorContext";

export function DecisionProperties({
  decision,
  namespace,
  index,
}: {
  decision: DMN15__tDecision;
  namespace: string | undefined;
  index: number;
}) {
  const { setState } = useDmnEditorStoreApi();

  const thisDmnsNamespace = useDmnEditorStore((s) => s.dmn.model.definitions["@_namespace"]);
  const isReadonly = !!namespace && namespace !== thisDmnsNamespace;

  const { allFeelVariableUniqueNames } = useDmnEditorDerivedStore();

  const { dmnEditorRootElementRef } = useDmnEditor();

  return (
    <>
      <FormGroup label="Name">
        <InlineFeelNameInput
          isPlain={false}
          id={decision["@_id"]!}
          name={decision["@_name"]}
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
          allUniqueNames={allFeelVariableUniqueNames}
        />
      </FormGroup>

      <FormGroup label="Data type">
        <TypeRefSelector
          heightRef={dmnEditorRootElementRef}
          typeRef={decision.variable?.["@_typeRef"]}
          onChange={(newTypeRef) => {
            setState((state) => {
              (state.dmn.model.definitions.drgElement![index] as DMN15__tDecision).variable!["@_typeRef"] = newTypeRef;
            });
          }}
        />
      </FormGroup>

      <FormGroup label="Description">
        <TextArea
          aria-label={"Description"}
          type={"text"}
          isDisabled={isReadonly}
          value={decision.description?.__$$text}
          onChange={(newDescription) => {
            setState((state) => {
              (state.dmn.model.definitions.drgElement![index] as DMN15__tDecision).description = {
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
          isDisabled={isReadonly}
          value={decision.question?.__$$text}
          onChange={(newQuestion) => {
            setState((state) => {
              (state.dmn.model.definitions.drgElement![index] as DMN15__tDecision).question = { __$$text: newQuestion };
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
          isDisabled={isReadonly}
          value={decision.allowedAnswers?.__$$text}
          onChange={(newAllowedAnswers) => {
            setState((state) => {
              (state.dmn.model.definitions.drgElement![index] as DMN15__tDecision).allowedAnswers = {
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
        isReadonly={isReadonly}
        values={decision.extensionElements?.["kie:attachment"]}
        onChange={(newExtensionElements) => {
          setState((state) => {
            (state.dmn.model.definitions.drgElement![index] as DMN15__tDecision).extensionElements = {
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
