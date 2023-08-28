import * as React from "react";
import { DMN15__tDecision } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { ClipboardCopy } from "@patternfly/react-core/dist/js/components/ClipboardCopy";
import { FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { TextArea } from "@patternfly/react-core/dist/js/components/TextArea";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { DocumentationLinksInput } from "./DocumentationLinksInput";
import { DataTypeSelector } from "../dataTypes/DataTypeSelector";
import { useDmnEditorStoreApi } from "../store/Store";

export function DecisionProperties({ decision, index }: { decision: DMN15__tDecision; index: number }) {
  const { setState } = useDmnEditorStoreApi();

  return (
    <>
      <FormGroup label="Name">
        <TextInput
          aria-label={"Name"}
          type={"text"}
          isDisabled={false}
          onChange={(newName) => {
            setState((dmn) => {
              (dmn.dmn.model.definitions.drgElement![index] as DMN15__tDecision).variable!["@_name"] = newName;
              (dmn.dmn.model.definitions.drgElement![index] as DMN15__tDecision)["@_name"] = newName;
            });
          }}
          value={decision["@_name"]}
          placeholder={"Enter a name..."}
        />
      </FormGroup>

      <FormGroup label="Data type">
        <DataTypeSelector
          name={decision.variable?.["@_typeRef"]}
          onChange={(newTypeRef) => {
            setState((dmn) => {
              (dmn.dmn.model.definitions.drgElement![index] as DMN15__tDecision).variable!["@_typeRef"] = newTypeRef;
            });
          }}
        />
      </FormGroup>

      <FormGroup label="Description">
        <TextArea
          aria-label={"Description"}
          type={"text"}
          isDisabled={false}
          value={decision.description}
          onChange={(newDescription) => {
            setState((dmn) => {
              (dmn.dmn.model.definitions.drgElement![index] as DMN15__tDecision).description = newDescription;
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
          isDisabled={false}
          value={decision.question}
          onChange={(newQuestion) => {
            setState((dmn) => {
              (dmn.dmn.model.definitions.drgElement![index] as DMN15__tDecision).question = newQuestion;
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
          isDisabled={false}
          value={decision.allowedAnswers}
          onChange={(newAllowedAnswers) => {
            setState((dmn) => {
              (dmn.dmn.model.definitions.drgElement![index] as DMN15__tDecision).allowedAnswers = newAllowedAnswers;
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
