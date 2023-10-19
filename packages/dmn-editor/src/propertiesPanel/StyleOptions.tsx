import * as React from "react";
import { useState } from "react";
import { FormGroup, FormSection } from "@patternfly/react-core/dist/js/components/Form";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { PencilAltIcon } from "@patternfly/react-icons/dist/js/icons/pencil-alt-icon";
import { PropertiesPanelHeader } from "./PropertiesPanelHeader";

export function StyleOptions(props: { startExpanded: boolean }) {
  const [isStyleSectionExpanded, setStyleSectionExpanded] = useState<boolean>(false);

  return (
    <>
      <PropertiesPanelHeader
        icon={<PencilAltIcon width={16} height={36} style={{ marginLeft: "12px" }} />}
        expands={true}
        fixed={false}
        isSectionExpanded={isStyleSectionExpanded}
        toogleSectionExpanded={() => setStyleSectionExpanded((prev) => !prev)}
        title={"Style (Work in progress 🔧)"}
      />
      {isStyleSectionExpanded && (
        <FormSection style={{ paddingLeft: "20px", marginTop: "0px" }}>
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
        </FormSection>
      )}
    </>
  );
}
