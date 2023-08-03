import * as React from "react";
import {
  FormFieldGroupExpandable,
  FormFieldGroupHeader,
  FormGroup,
} from "@patternfly/react-core/dist/js/components/Form";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { PencilAltIcon } from "@patternfly/react-icons/dist/js/icons/pencil-alt-icon";

export function StyleOptions(props: { startExpanded: boolean }) {
  return (
    <FormFieldGroupExpandable
      isExpanded={props.startExpanded}
      header={
        <FormFieldGroupHeader
          titleText={{
            text: (
              <TextContent>
                <Text component={TextVariants.h4}>
                  <PencilAltIcon />
                  &nbsp;&nbsp;Style (Work in progress 🔧)
                </Text>
              </TextContent>
            ),
            id: "properties-panel-shape-options",
          }}
        />
      }
    >
      <FormGroup label="Border color">
        <TextInput
          aria-label={"Border color"}
          type={"text"}
          isDisabled={false}
          value={""}
          placeholder={"Enter a color..."}
        />
      </FormGroup>
      <FormGroup label="Font">
        <TextInput
          aria-label={"Font"}
          type={"text"}
          isDisabled={false}
          value={""}
          placeholder={"Enter a font name..."}
        />
      </FormGroup>
      <FormGroup label="Font size">
        <TextInput
          aria-label={"Font size"}
          type={"text"}
          isDisabled={false}
          value={""}
          placeholder={"Enter a font size..."}
        />
      </FormGroup>
      <FormGroup label="Text color">
        <TextInput
          aria-label={"Text color"}
          type={"text"}
          isDisabled={false}
          value={""}
          placeholder={"Enter a color..."}
        />
      </FormGroup>
      <FormGroup label="Fill color">
        <TextInput
          aria-label={"Fill color"}
          type={"text"}
          isDisabled={false}
          value={""}
          placeholder={"Enter a color..."}
        />
      </FormGroup>
    </FormFieldGroupExpandable>
  );
}
