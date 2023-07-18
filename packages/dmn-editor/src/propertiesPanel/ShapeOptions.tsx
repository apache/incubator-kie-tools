import * as React from "react";
import {
  FormFieldGroupExpandable,
  FormFieldGroupHeader,
  FormGroup,
} from "@patternfly/react-core/dist/js/components/Form";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { Grid, GridItem } from "@patternfly/react-core/dist/js/layouts/Grid";
import { CubeIcon } from "@patternfly/react-icons/dist/js/icons/cube-icon";

export function ShapeOptions(props: { startExpanded: boolean }) {
  return (
    <FormFieldGroupExpandable
      isExpanded={props.startExpanded}
      header={
        <FormFieldGroupHeader
          titleText={{
            text: (
              <TextContent>
                <Text component={TextVariants.h4}>
                  <CubeIcon />
                  &nbsp;&nbsp;Shape
                </Text>
              </TextContent>
            ),
            id: "properties-panel-shape-options",
          }}
        />
      }
    >
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
            <TextInput aria-label={"X"} type={"text"} isDisabled={false} value={""} placeholder={"Enter a value..."} />
          </FormGroup>
        </GridItem>
        <GridItem span={6}>
          <FormGroup label="Y">
            <TextInput aria-label={"Y"} type={"text"} isDisabled={false} value={""} placeholder={"Enter a value..."} />
          </FormGroup>
        </GridItem>
      </Grid>
    </FormFieldGroupExpandable>
  );
}
