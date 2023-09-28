import * as React from "react";
import { DMN15__tKnowledgeSource } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { ClipboardCopy } from "@patternfly/react-core/dist/js/components/ClipboardCopy";
import { FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { TextArea } from "@patternfly/react-core/dist/js/components/TextArea";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { DocumentationLinksInput } from "./DocumentationLinksInput";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../store/Store";
import { renameDrgElement } from "../mutations/renameNode";
import { InlineFeelNameInput } from "../feel/InlineFeelNameInput";

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
        />
      </FormGroup>

      <FormGroup label="Description">
        <TextArea
          aria-label={"Description"}
          type={"text"}
          isDisabled={isReadonly}
          value={knowledgeSource.description}
          onChange={(newDescription) => {
            setState((state) => {
              (state.dmn.model.definitions.drgElement![index] as DMN15__tKnowledgeSource).description = newDescription;
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
          value={knowledgeSource.type}
          onChange={(newType) => {
            setState((state) => {
              (state.dmn.model.definitions.drgElement![index] as DMN15__tKnowledgeSource).type = newType;
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

      <FormGroup label="Documentation links (Work in progress 🔧)">
        <DocumentationLinksInput />
      </FormGroup>
    </>
  );
}
