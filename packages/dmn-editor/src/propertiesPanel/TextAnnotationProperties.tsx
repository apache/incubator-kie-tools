import * as React from "react";
import { DMN15__tTextAnnotation } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { ClipboardCopy } from "@patternfly/react-core/dist/js/components/ClipboardCopy";
import { FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { TextArea } from "@patternfly/react-core/dist/js/components/TextArea";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { useDmnEditorStoreApi } from "../store/Store";
import { updateTextAnnotation } from "../mutations/renameNode";

export function TextAnnotationProperties({
  textAnnotation,
  index,
}: {
  textAnnotation: DMN15__tTextAnnotation;
  index: number;
}) {
  const { setState } = useDmnEditorStoreApi();

  return (
    <>
      <FormGroup label="Format">
        <TextInput
          aria-label={"Format"}
          type={"text"}
          isDisabled={false}
          value={textAnnotation["@_textFormat"]}
          placeholder={"Enter a text format..."}
          onChange={(newTextFormat) => {
            setState((state) => {
              (state.dmn.model.definitions.artifact![index] as DMN15__tTextAnnotation)["@_textFormat"] = newTextFormat;
            });
          }}
        />
      </FormGroup>

      <FormGroup label="Text">
        <TextArea
          aria-label={"Text"}
          type={"text"}
          isDisabled={false}
          value={textAnnotation.text?.__$$text}
          onChange={(newText) => {
            setState((state) => {
              updateTextAnnotation({
                definitions: state.dmn.model.definitions,
                index,
                newText,
              });
            });
          }}
          placeholder={"Enter text..."}
          style={{ resize: "vertical", minHeight: "40px" }}
          rows={6}
        />
      </FormGroup>

      <FormGroup label="Description">
        <TextArea
          aria-label={"Description"}
          type={"text"}
          isDisabled={false}
          value={textAnnotation.description?.__$$text}
          onChange={(newDescription) => {
            setState((state) => {
              (state.dmn.model.definitions.artifact![index] as DMN15__tTextAnnotation).description = {
                __$$text: newDescription,
              };
            });
          }}
          placeholder={"Enter a description..."}
          style={{ resize: "vertical", minHeight: "40px" }}
          rows={2}
        />
      </FormGroup>

      <FormGroup label="ID">
        <ClipboardCopy isReadOnly={true} hoverTip="Copy" clickTip="Copied">
          {textAnnotation["@_id"]}
        </ClipboardCopy>
      </FormGroup>
    </>
  );
}
