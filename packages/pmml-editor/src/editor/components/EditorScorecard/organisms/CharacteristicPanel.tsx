import * as React from "react";
import { useCallback } from "react";

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
import { AttributesTable } from ".";
import { IndexedCharacteristic } from "./CharacteristicsTable";
import { Operation } from "../Operation";

interface CharacteristicPanel {
  characteristic: IndexedCharacteristic | undefined;
}

interface CharacteristicPanelProps extends CharacteristicPanel {
  activeOperation: Operation;
  setActiveOperation: (operation: Operation) => void;
  showCharacteristicPanel: boolean;
  hideCharacteristicPanel: () => void;
}

export const CharacteristicPanel = (props: CharacteristicPanelProps) => {
  const {
    characteristic,
    activeOperation,
    setActiveOperation,
    showCharacteristicPanel,
    hideCharacteristicPanel
  } = props;

  const onAddAttribute = useCallback(() => {
    setActiveOperation(Operation.CREATE_ATTRIBUTE);
  }, [characteristic]);

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
                  <Button
                    id="add-attribute-button"
                    data-testid="characteristic-panel__add-attribute"
                    variant="primary"
                    onClick={e => onAddAttribute()}
                    isDisabled={activeOperation !== Operation.NONE}
                  >
                    Add Attribute
                  </Button>
                  <AttributesTable
                    index={characteristic?.index}
                    activeOperation={activeOperation}
                    setActiveOperation={setActiveOperation}
                    attributes={characteristic?.characteristic.Attribute ?? []}
                    onAddAttribute={() => window.alert("Add Attribute")}
                    onRowDelete={index => window.alert("Delete Attribute")}
                    commit={index => null}
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
