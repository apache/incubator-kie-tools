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
import { AttributesTable } from ".";
import { IndexedCharacteristic } from "./CharacteristicsTable";
import { Operation } from "../Operation";
import { Attribute, Characteristic, Model, PMML, Scorecard } from "@kogito-tooling/pmml-editor-marshaller";
import { useSelector } from "react-redux";

interface CharacteristicPanel {
  characteristic: IndexedCharacteristic | undefined;
}

interface CharacteristicPanelProps extends CharacteristicPanel {
  modelIndex: number;
  activeOperation: Operation;
  setActiveOperation: (operation: Operation) => void;
  showCharacteristicPanel: boolean;
  hideCharacteristicPanel: () => void;
  validateText: (text: string | undefined) => boolean;
  addAttribute: () => void;
  deleteAttribute: (index: number) => void;
  commit: (
    index: number | undefined,
    text: string | undefined,
    partialScore: number | undefined,
    reasonCode: string | undefined
  ) => void;
}

export const CharacteristicPanel = (props: CharacteristicPanelProps) => {
  const {
    modelIndex,
    characteristic,
    activeOperation,
    setActiveOperation,
    showCharacteristicPanel,
    hideCharacteristicPanel,
    validateText,
    addAttribute,
    deleteAttribute,
    commit
  } = props;

  const attributes: Attribute[] = useSelector<PMML, Attribute[]>((state: PMML) => {
    const characteristicIndex = characteristic?.index;
    const model: Model | undefined = state.models ? state.models[modelIndex] : undefined;
    if (model !== undefined && characteristicIndex !== undefined && model instanceof Scorecard) {
      const scorecard: Scorecard = model as Scorecard;
      const _characteristic: Characteristic | undefined = scorecard.Characteristics.Characteristic[characteristicIndex];
      if (_characteristic) {
        return _characteristic.Attribute;
      }
    }
    return [];
  });

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
                        onClick={hideCharacteristicPanel}
                      >
                        <CloseIcon />
                      </Button>
                    </SplitItem>
                  </Split>
                </PageSection>
              </StackItem>
              <StackItem>
                <PageSection variant="light">
                  {(attributes.length > 0 || activeOperation === Operation.CREATE_ATTRIBUTE) && (
                    <Button
                      id="add-attribute-button"
                      data-testid="characteristic-panel__add-attribute"
                      variant="primary"
                      onClick={addAttribute}
                      isDisabled={activeOperation !== Operation.NONE}
                    >
                      Add Attribute
                    </Button>
                  )}
                  <AttributesTable
                    modelIndex={modelIndex}
                    attributes={attributes}
                    activeOperation={activeOperation}
                    setActiveOperation={setActiveOperation}
                    validateText={validateText}
                    addAttribute={addAttribute}
                    deleteAttribute={deleteAttribute}
                    commit={commit}
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
