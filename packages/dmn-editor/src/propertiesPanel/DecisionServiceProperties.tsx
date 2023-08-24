import * as React from "react";
import { DMN15__tDecisionService } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { ClipboardCopy } from "@patternfly/react-core/dist/js/components/ClipboardCopy";
import { FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { TextArea } from "@patternfly/react-core/dist/js/components/TextArea";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { DocumentationLinksInput } from "./DocumentationLinksInput";
import { DataTypeSelector } from "../dataTypes/DataTypeSelector";
import { useDmnEditorStoreApi } from "../store/Store";

export function DecisionServiceProperties({
  decisionService,
  index,
}: {
  decisionService: DMN15__tDecisionService;
  index: number;
}) {
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
              (dmn.dmn.model.definitions.drgElement![index] as DMN15__tDecisionService).variable!["@_name"] = newName;
              (dmn.dmn.model.definitions.drgElement![index] as DMN15__tDecisionService)["@_name"] = newName;
            });
          }}
          value={decisionService["@_name"]}
          placeholder={"Enter a name..."}
        />
      </FormGroup>

      <FormGroup label="Data type">
        <DataTypeSelector
          name={decisionService.variable?.["@_typeRef"]}
          onChange={(newTypeRef) => {
            setState((dmn) => {
              (dmn.dmn.model.definitions.drgElement![index] as DMN15__tDecisionService).variable!["@_typeRef"] =
                newTypeRef;
            });
          }}
        />
      </FormGroup>

      <FormGroup label="Description">
        <TextArea
          aria-label={"Description"}
          type={"text"}
          isDisabled={false}
          value={decisionService.description}
          onChange={(newDescription) => {
            setState((dmn) => {
              (dmn.dmn.model.definitions.drgElement![index] as DMN15__tDecisionService).description = newDescription;
            });
          }}
          placeholder={"Enter a description..."}
          style={{ resize: "vertical", minHeight: "40px" }}
          rows={6}
        />
      </FormGroup>

      <FormGroup label="ID">
        <ClipboardCopy isReadOnly={true} hoverTip="Copy" clickTip="Copied">
          {decisionService["@_id"]}
        </ClipboardCopy>
      </FormGroup>

      <FormGroup label="Output decisions"></FormGroup>
      <FormGroup label="Input decisions"></FormGroup>
      <FormGroup label="Input data"></FormGroup>

      <FormGroup label="Documentation links (Work in progress 🔧)">
        <DocumentationLinksInput />
      </FormGroup>
    </>
  );
}
