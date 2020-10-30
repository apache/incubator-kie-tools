import * as React from "react";

import {
  Button,
  Page,
  PageSection,
  Split,
  SplitItem,
  Stack,
  StackItem,
  TextContent,
  Title
} from "@patternfly/react-core";
import { CloseIcon } from "@patternfly/react-icons";
import "./CharacteristicPanel.scss";
import { CharacteristicAttributesForm } from "../molecules";
import { IndexedCharacteristic } from "./CharacteristicsTable";

interface CharacteristicPanel {
  characteristic: IndexedCharacteristic | undefined;
}

interface CharacteristicPanelProps extends CharacteristicPanel {
  showCharacteristicPanel: boolean;
  hideCharacteristicPanel: () => void;
  commit: (props: CharacteristicPanel) => void;
}

export const CharacteristicPanel = (props: CharacteristicPanelProps) => {
  const { characteristic, showCharacteristicPanel, hideCharacteristicPanel } = props;

  return (
    <div className={`side-panel side-panel--from-right ${showCharacteristicPanel ? "side-panel--is-visible" : ""}`}>
      <div className="side-panel__container">
        <div className="side-panel__content">
          <Page key={characteristic?.index}>
            <Stack>
              <StackItem>
                <PageSection variant="light">
                  <Split hasGutter={true} style={{ width: "100%" }}>
                    <SplitItem>
                      <TextContent>
                        <Title size="lg" headingLevel={"h1"}>
                          Attributes
                        </Title>
                      </TextContent>
                    </SplitItem>
                    <SplitItem isFilled={true} />
                    <SplitItem>
                      <Button
                        id="close-characteristic-panel-button"
                        data-testid="characteristic-panel__close-panel"
                        variant="link"
                        onClick={e => hideCharacteristicPanel()}
                      >
                        <CloseIcon />
                      </Button>
                    </SplitItem>
                  </Split>
                </PageSection>
              </StackItem>
              <StackItem>
                <PageSection variant="light">
                  <CharacteristicAttributesForm
                    index={characteristic?.index}
                    attributes={characteristic?.characteristic.Attribute ?? []}
                    onAddAttribute={() => window.alert("Add Attribute")}
                    onRowDelete={index => window.alert("Delete Attribute")}
                  />
                </PageSection>
              </StackItem>
            </Stack>
          </Page>
        </div>
      </div>
    </div>
  );
};
