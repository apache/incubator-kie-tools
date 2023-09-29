import * as React from "react";
import { DMN15__tDecision } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { ClipboardCopy } from "@patternfly/react-core/dist/js/components/ClipboardCopy";
import { FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { TextArea } from "@patternfly/react-core/dist/js/components/TextArea";
import { DocumentationLinksInput } from "./DocumentationLinksInput";
import { TypeRefSelector } from "../dataTypes/TypeRefSelector";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../store/Store";
import { renameDrgElement } from "../mutations/renameNode";
import { InlineFeelNameInput } from "../feel/InlineFeelNameInput";
import { useDmnEditorDerivedStore } from "../store/DerivedStore";

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
          value={decision.description}
          onChange={(newDescription) => {
            setState((state) => {
              (state.dmn.model.definitions.drgElement![index] as DMN15__tDecision).description = newDescription;
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
          value={decision.question}
          onChange={(newQuestion) => {
            setState((state) => {
              (state.dmn.model.definitions.drgElement![index] as DMN15__tDecision).question = newQuestion;
            });
          }}
          placeholder={"Enter a question..."}
          style={{ resize: "vertical", minHeight: "40px" }}
          rows={3}
        />
      </FormGroup>

      <FormGroup label="Allowed answers">
        <TextArea
          aria-label={"Allowed answers"}
          type={"text"}
          isDisabled={isReadonly}
          value={decision.allowedAnswers}
          onChange={(newAllowedAnswers) => {
            setState((state) => {
              (state.dmn.model.definitions.drgElement![index] as DMN15__tDecision).allowedAnswers = newAllowedAnswers;
            });
          }}
          placeholder={"Enter allowed answers..."}
          style={{ resize: "vertical", minHeight: "40px" }}
          rows={6}
        />
      </FormGroup>

      <FormGroup label="Documentation links (Work in progress 🔧)">
        <DocumentationLinksInput />
      </FormGroup>

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
