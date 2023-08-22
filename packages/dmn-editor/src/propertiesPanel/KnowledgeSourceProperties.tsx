import * as React from "react";
import { DMN14__tKnowledgeSource } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_4/ts-gen/types";
import { ClipboardCopy } from "@patternfly/react-core/dist/js/components/ClipboardCopy";
import { FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { TextArea } from "@patternfly/react-core/dist/js/components/TextArea";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { DocumentationLinksInput } from "./DocumentationLinksInput";
import { useDmnEditorStoreApi } from "../store/Store";

export function KnowledgeSourceProperties({
  knowledgeSource,
  index,
}: {
  knowledgeSource: DMN14__tKnowledgeSource;
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
              (dmn.dmn.model.definitions.drgElement![index] as DMN14__tKnowledgeSource)["@_name"] = newName;
            });
          }}
          value={knowledgeSource["@_name"]}
          placeholder={"Enter a name..."}
        />
      </FormGroup>

      <FormGroup label="Description">
        <TextArea
          aria-label={"Description"}
          type={"text"}
          isDisabled={false}
          value={knowledgeSource.description}
          onChange={(newDescription) => {
            setState((dmn) => {
              (dmn.dmn.model.definitions.drgElement![index] as DMN14__tKnowledgeSource).description = newDescription;
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
          isDisabled={false}
          value={knowledgeSource.type}
          onChange={(newType) => {
            setState((dmn) => {
              (dmn.dmn.model.definitions.drgElement![index] as DMN14__tKnowledgeSource).type = newType;
            });
          }}
          placeholder={"Enter source type..."}
        />
      </FormGroup>

      <FormGroup label="Location URI">
        <TextInput
          aria-label={"Location URI"}
          type={"text"}
          isDisabled={false}
          value={knowledgeSource["@_locationURI"]}
          onChange={(newLocationUri) => {
            setState((dmn) => {
              (dmn.dmn.model.definitions.drgElement![index] as DMN14__tKnowledgeSource)["@_locationURI"] =
                newLocationUri;
            });
          }}
          placeholder={"Enter location URI..."}
        />
      </FormGroup>

      <FormGroup label="Documentation links (Work in progress 🔧)">
        <DocumentationLinksInput />
      </FormGroup>
    </>
  );
}
