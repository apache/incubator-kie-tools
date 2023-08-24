import * as React from "react";
import { DMN15__tTextAnnotation } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { ClipboardCopy } from "@patternfly/react-core/dist/js/components/ClipboardCopy";
import { FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { TextArea } from "@patternfly/react-core/dist/js/components/TextArea";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { useDmnEditorStoreApi } from "../store/Store";

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
            setState((dmn) => {
              (dmn.dmn.model.definitions.drgElement![index] as DMN15__tTextAnnotation)["@_textFormat"] = newTextFormat;
            });
          }}
        />
      </FormGroup>

      <FormGroup label="Text">
        <TextArea
          aria-label={"Text"}
          type={"text"}
          isDisabled={false}
          value={textAnnotation.text}
          onChange={(newText) => {
            setState((dmn) => {
              (dmn.dmn.model.definitions.drgElement![index] as DMN15__tTextAnnotation).text = newText;
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
          value={textAnnotation.description}
          onChange={(newDescription) => {
            setState((dmn) => {
              (dmn.dmn.model.definitions.drgElement![index] as DMN15__tTextAnnotation).description = newDescription;
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
