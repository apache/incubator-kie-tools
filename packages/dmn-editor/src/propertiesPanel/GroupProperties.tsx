import * as React from "react";
import { DMN15__tGroup } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { ClipboardCopy } from "@patternfly/react-core/dist/js/components/ClipboardCopy";
import { FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { TextArea } from "@patternfly/react-core/dist/js/components/TextArea";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { useDmnEditorStoreApi } from "../store/Store";
import { renameGroupNode } from "../mutations/renameNode";

export function GroupProperties({ group, index }: { group: DMN15__tGroup; index: number }) {
  const { setState } = useDmnEditorStoreApi();

  return (
    <>
      <FormGroup label="Name">
        <TextInput
          aria-label={"Name"}
          type={"text"}
          isDisabled={false}
          onChange={(newName) => {
            setState((state) => {
              renameGroupNode({
                definitions: state.dmn.model.definitions,
                index,
                newName,
              });
            });
          }}
          value={group["@_name"]}
          placeholder={"Enter a name..."}
        />
      </FormGroup>

      <FormGroup label="Description">
        <TextArea
          aria-label={"Description"}
          type={"text"}
          isDisabled={false}
          value={group.description?.__$$text}
          onChange={(newDescription) => {
            setState((state) => {
              (state.dmn.model.definitions.artifact![index] as DMN15__tGroup).description = {
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
          {group["@_id"]}
        </ClipboardCopy>
      </FormGroup>
    </>
  );
}
