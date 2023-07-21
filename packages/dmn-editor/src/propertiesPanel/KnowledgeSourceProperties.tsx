import * as React from "react";
import { DMN14__tKnowledgeSource } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_4/ts-gen/types";
import { ClipboardCopy } from "@patternfly/react-core/dist/js/components/ClipboardCopy";
import { FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { TextArea } from "@patternfly/react-core/dist/js/components/TextArea";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { useCallback } from "react";
import { DocumentationLinksInput } from "./DocumentationLinksInput";

export function KnowledgeSourceProperties({ knowledgeSource }: { knowledgeSource: DMN14__tKnowledgeSource }) {
  const setName = useCallback((dataType: string) => {
    // TODO: Remember to set the variable name here as well.
    console.log(`TIAGO WRITE: Set data type --> ${dataType}`);
  }, []);

  return (
    <>
      <FormGroup label="Name">
        <TextInput
          aria-label={"Name"}
          type={"text"}
          isDisabled={false}
          onChange={setName}
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
          placeholder={"Enter source type..."}
        />
      </FormGroup>

      <FormGroup label="Location URI">
        <TextInput
          aria-label={"Location URI"}
          type={"text"}
          isDisabled={false}
          value={knowledgeSource["@_locationURI"]}
          placeholder={"Enter location URI..."}
        />
      </FormGroup>

      <FormGroup label="Documentation links">
        <DocumentationLinksInput />
      </FormGroup>
    </>
  );
}
