import * as React from "react";
import { DMN15__tBusinessKnowledgeModel } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { ClipboardCopy } from "@patternfly/react-core/dist/js/components/ClipboardCopy";
import { FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { TextArea } from "@patternfly/react-core/dist/js/components/TextArea";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { DocumentationLinksInput } from "./DocumentationLinksInput";
import { DataTypeSelector } from "../dataTypes/DataTypeSelector";
import { useDmnEditorStoreApi } from "../store/Store";

export function BkmProperties({ bkm, index }: { bkm: DMN15__tBusinessKnowledgeModel; index: number }) {
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
              (dmn.dmn.model.definitions.drgElement![index] as DMN15__tBusinessKnowledgeModel).variable!["@_name"] =
                newName;
              (dmn.dmn.model.definitions.drgElement![index] as DMN15__tBusinessKnowledgeModel)["@_name"] = newName;
            });
          }}
          value={bkm["@_name"]}
          placeholder={"Enter a name..."}
        />
      </FormGroup>

      <FormGroup label="Data type">
        <DataTypeSelector
          name={bkm.variable?.["@_typeRef"]}
          onChange={(newTypeRef) => {
            setState((dmn) => {
              (dmn.dmn.model.definitions.drgElement![index] as DMN15__tBusinessKnowledgeModel).variable!["@_typeRef"] =
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
          value={bkm.description}
          onChange={(newDescription) => {
            setState((dmn) => {
              (dmn.dmn.model.definitions.drgElement![index] as DMN15__tBusinessKnowledgeModel).description =
                newDescription;
            });
          }}
          placeholder={"Enter a description..."}
          style={{ resize: "vertical", minHeight: "40px" }}
          rows={6}
        />
      </FormGroup>

      <FormGroup label="ID">
        <ClipboardCopy isReadOnly={true} hoverTip="Copy" clickTip="Copied">
          {bkm["@_id"]}
        </ClipboardCopy>
      </FormGroup>

      <FormGroup label="Documentation links (Work in progress 🔧)">
        <DocumentationLinksInput />
      </FormGroup>
    </>
  );
}
