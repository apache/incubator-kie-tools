import * as React from "react";
import { DMN15__tBusinessKnowledgeModel } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { ClipboardCopy } from "@patternfly/react-core/dist/js/components/ClipboardCopy";
import { FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { TextArea } from "@patternfly/react-core/dist/js/components/TextArea";
import { DocumentationLinksFormGroup } from "./DocumentationLinksFormGroup";
import { TypeRefSelector } from "../dataTypes/TypeRefSelector";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../store/Store";
import { renameDrgElement } from "../mutations/renameNode";
import { InlineFeelNameInput } from "../feel/InlineFeelNameInput";
import { useDmnEditorDerivedStore } from "../store/DerivedStore";

export function BkmProperties({
  bkm,
  namespace,
  index,
}: {
  bkm: DMN15__tBusinessKnowledgeModel;
  namespace: string | undefined;
  index: number;
}) {
  const { setState } = useDmnEditorStoreApi();

  const thisDmnsNamespace = useDmnEditorStore((s) => s.dmn.model.definitions["@_namespace"]);
  const isReadonly = !!namespace && namespace !== thisDmnsNamespace;

  const { allFeelVariableUniqueNames } = useDmnEditorDerivedStore();

  bkm.extensionElements?.["kie:attachment"];

  return (
    <>
      <FormGroup label="Name">
        <InlineFeelNameInput
          isPlain={false}
          id={bkm["@_id"]!}
          name={bkm["@_name"]}
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
          typeRef={bkm.variable?.["@_typeRef"]}
          onChange={(newTypeRef) => {
            setState((state) => {
              (state.dmn.model.definitions.drgElement![index] as DMN15__tBusinessKnowledgeModel).variable![
                "@_typeRef"
              ] = newTypeRef;
            });
          }}
        />
      </FormGroup>

      <FormGroup label="Description">
        <TextArea
          aria-label={"Description"}
          type={"text"}
          isDisabled={isReadonly}
          value={bkm.description}
          onChange={(newDescription) => {
            setState((state) => {
              (state.dmn.model.definitions.drgElement![index] as DMN15__tBusinessKnowledgeModel).description =
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

      <DocumentationLinksFormGroup
        isReadonly={isReadonly}
        value={bkm.extensionElements?.["kie:attachment"]}
        onChange={(newExtensionElements) => {
          setState((state) => {
            (state.dmn.model.definitions.drgElement![index] as DMN15__tBusinessKnowledgeModel).extensionElements = {
              "kie:attachment": newExtensionElements,
            };
          });
        }}
      />
    </>
  );
}
