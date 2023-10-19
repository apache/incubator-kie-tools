import * as React from "react";
import { useState } from "react";
import { FormGroup, FormSection } from "@patternfly/react-core/dist/js/components/Form";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { Grid, GridItem } from "@patternfly/react-core/dist/js/layouts/Grid";
import { CubeIcon } from "@patternfly/react-icons/dist/js/icons/cube-icon";
import { PropertiesPanelHeader } from "./PropertiesPanelHeader";

export function ShapeOptions(props: { startExpanded: boolean }) {
  const [isShapeSectionExpanded, setShapeSectionExpanded] = useState<boolean>(false);

  return (
    <>
      <PropertiesPanelHeader
        icon={<CubeIcon width={16} height={36} style={{ marginLeft: "12px" }} />}
        expands={true}
        fixed={false}
        isSectionExpanded={isShapeSectionExpanded}
        toogleSectionExpanded={() => setShapeSectionExpanded((prev) => !prev)}
        title={"Shape (Work in progress 🔧)"}
      />
      {isShapeSectionExpanded && (
        <FormSection style={{ paddingLeft: "20px", marginTop: "0px" }}>
          <Grid hasGutter={true}>
            <GridItem span={6}>
              <FormGroup label="Width">
                <TextInput
                  aria-label={"Width"}
                  type={"text"}
                  isDisabled={false}
                  value={""}
                  placeholder={"Enter a value..."}
                />
              </FormGroup>
            </GridItem>
            <GridItem span={6}>
              <FormGroup label="Height">
                <TextInput
                  aria-label={"Height"}
                  type={"text"}
                  isDisabled={false}
                  value={""}
                  placeholder={"Enter a value..."}
                />
              </FormGroup>
            </GridItem>
          </Grid>
          <Grid hasGutter={true}>
            <GridItem span={6}>
              <FormGroup label="X">
                <TextInput
                  aria-label={"X"}
                  type={"text"}
                  isDisabled={false}
                  value={""}
                  placeholder={"Enter a value..."}
                />
              </FormGroup>
            </GridItem>
            <GridItem span={6}>
              <FormGroup label="Y">
                <TextInput
                  aria-label={"Y"}
                  type={"text"}
                  isDisabled={false}
                  value={""}
                  placeholder={"Enter a value..."}
                />
              </FormGroup>
            </GridItem>
          </Grid>
        </FormSection>
      )}
    </>
  );
}
