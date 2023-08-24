import * as React from "react";
import { DMN15__tInputData } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { ClipboardCopy } from "@patternfly/react-core/dist/js/components/ClipboardCopy";
import { FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { TextArea } from "@patternfly/react-core/dist/js/components/TextArea";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { DocumentationLinksInput } from "./DocumentationLinksInput";
import { DataTypeSelector } from "../dataTypes/DataTypeSelector";
import { useDmnEditorStoreApi } from "../store/Store";

export function InputDataProperties({ inputData, index }: { inputData: DMN15__tInputData; index: number }) {
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
              (dmn.dmn.model.definitions.drgElement![index] as DMN15__tInputData).variable!["@_name"] = newName;
              (dmn.dmn.model.definitions.drgElement![index] as DMN15__tInputData)["@_name"] = newName;
            });
          }}
          value={inputData["@_name"]}
          placeholder={"Enter a name..."}
        />
      </FormGroup>

      <FormGroup label="Data type">
        <DataTypeSelector
          name={inputData.variable?.["@_typeRef"]}
          onChange={(newTypeRef) => {
            setState((dmn) => {
              (dmn.dmn.model.definitions.drgElement![index] as DMN15__tInputData).variable!["@_typeRef"] = newTypeRef;
            });
          }}
        />
      </FormGroup>

      <FormGroup label="Description">
        <TextArea
          aria-label={"Description"}
          type={"text"}
          isDisabled={false}
          value={inputData.description}
          onChange={(newDescription) => {
            setState((dmn) => {
              (dmn.dmn.model.definitions.drgElement![index] as DMN15__tInputData).description = newDescription;
            });
          }}
          placeholder={"Enter a description..."}
          style={{ resize: "vertical", minHeight: "40px" }}
          rows={6}
        />
      </FormGroup>

      <FormGroup label="ID">
        <ClipboardCopy isReadOnly={true} hoverTip="Copy" clickTip="Copied">
          {inputData["@_id"]}
        </ClipboardCopy>
      </FormGroup>

      <FormGroup label="Documentation links (Work in progress 🔧)">
        <DocumentationLinksInput />
      </FormGroup>
    </>
  );
}
