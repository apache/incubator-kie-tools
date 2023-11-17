import * as React from "react";
import { useState, useMemo } from "react";
import { Flex } from "@patternfly/react-core/dist/js/layouts/Flex";
import { Form, FormSection } from "@patternfly/react-core/dist/js/components/Form";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { FontOptions } from "./FontOptions";
import { useDmnEditorStoreApi } from "../store/Store";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { TimesIcon } from "@patternfly/react-icons/dist/js/icons/times-icon";
import { Truncate } from "@patternfly/react-core/dist/js/components/Truncate";
import { PropertiesPanelHeader } from "./PropertiesPanelHeader";
import { ShapeOptions } from "./ShapeOptions";

export function MultipleNodeProperties({ nodeIds }: { nodeIds: string[] }) {
  const [isSectionExpanded, setSectionExpanded] = useState<boolean>(true);
  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const size = useMemo(() => nodeIds.length, [nodeIds.length]);

  return (
    <Form>
      <FormSection>
        <PropertiesPanelHeader
          fixed={true}
          isSectionExpanded={isSectionExpanded}
          toogleSectionExpanded={() => setSectionExpanded((prev) => !prev)}
          title={
            <Flex justifyContent={{ default: "justifyContentCenter" }}>
              <TextContent>
                <Text component={TextVariants.h4}>
                  <Truncate
                    content={`Multiple nodes selected (${size})`}
                    position={"middle"}
                    trailingNumChars={size.toString().length + 2}
                  />
                </Text>
              </TextContent>
            </Flex>
          }
          action={
            <Button
              variant={ButtonVariant.plain}
              onClick={() => {
                dmnEditorStoreApi.setState((state) => {
                  state.diagram.propertiesPanel.isOpen = false;
                });
              }}
            >
              <TimesIcon />
            </Button>
          }
        />
      </FormSection>
      <FormSection>
        <FontOptions startExpanded={false} nodeIds={nodeIds} />
        <ShapeOptions
          startExpanded={false}
          nodeIds={nodeIds}
          isDimensioningEnabled={false}
          isPositioningEnabled={false}
        />
      </FormSection>
    </Form>
  );
}
