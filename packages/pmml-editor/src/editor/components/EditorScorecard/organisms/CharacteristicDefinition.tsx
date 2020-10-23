import * as React from "react";
import { MouseEventHandler, useEffect, useState } from "react";

import {
  Button,
  Page,
  PageSection,
  Split,
  SplitItem,
  Stack,
  StackItem,
  Tab,
  Tabs,
  TabTitleText,
  TextContent,
  Title
} from "@patternfly/react-core";
import { CloseIcon } from "@patternfly/react-icons";
import "./CharacteristicDefinition.scss";
import { CharacteristicAttributesForm, CharacteristicGeneralForm } from "../molecules";
import { IndexedCharacteristic } from "./CharacteristicsTable";
import { ValidatedType } from "../../../types";

interface CharacteristicDefinitionProps {
  characteristic: IndexedCharacteristic | undefined;
  showCharacteristicPanel: boolean;
  hideCharacteristicPanel: () => void;
  validateCharacteristicName: (index: number | undefined, name: string | undefined) => boolean;
}

export const CharacteristicDefinition = (props: CharacteristicDefinitionProps) => {
  const { characteristic, showCharacteristicPanel, hideCharacteristicPanel, validateCharacteristicName } = props;
  const [activeTabKey, setActiveTabKey] = useState(0);

  const [name, setName] = useState<ValidatedType<string | undefined>>({
    value: characteristic?.characteristic.name,
    valid: true
  });
  const [reasonCode, setReasonCode] = useState(characteristic?.characteristic.reasonCode);
  const [baselineScore, setBaselineScore] = useState(characteristic?.characteristic.baselineScore);

  useEffect(() => {
    setName({
      value: characteristic?.characteristic.name,
      valid: props.validateCharacteristicName(characteristic?.index, characteristic?.characteristic.name)
    });
    setReasonCode(characteristic?.characteristic.reasonCode);
    setBaselineScore(characteristic?.characteristic.baselineScore);
  }, [props]);

  const handleTabClick = (event: React.MouseEvent<HTMLElement, MouseEvent>, eventKey: number) => {
    setActiveTabKey(eventKey);
  };

  const handleOKClick: MouseEventHandler<HTMLButtonElement> = event => {
    hideCharacteristicPanel();
  };

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
                          Characteristic Properties
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
                  <Tabs activeKey={activeTabKey} onSelect={handleTabClick}>
                    <Tab eventKey={0} title={<TabTitleText>General</TabTitleText>}>
                      <CharacteristicGeneralForm
                        index={characteristic?.index}
                        name={name.value}
                        isNameValid={name.valid}
                        reasonCode={reasonCode}
                        baselineScore={baselineScore}
                        setName={_name => {
                          setName({
                            value: _name,
                            valid: props.validateCharacteristicName(characteristic?.index, _name)
                          });
                        }}
                        setReasonCode={setReasonCode}
                        setBaselineScore={setBaselineScore}
                      />
                    </Tab>
                    <Tab eventKey={1} title={<TabTitleText>Attributes</TabTitleText>}>
                      <CharacteristicAttributesForm index={characteristic?.index} />
                    </Tab>
                  </Tabs>
                </PageSection>
              </StackItem>
              <StackItem>
                <PageSection variant="light">
                  <Split hasGutter={true}>
                    <SplitItem>
                      <Button variant={"primary"} isDisabled={!name.valid} onClick={handleOKClick}>
                        OK
                      </Button>
                    </SplitItem>
                    <SplitItem>
                      <Button variant={"secondary"} onClick={e => hideCharacteristicPanel()}>
                        Cancel
                      </Button>
                    </SplitItem>
                  </Split>
                </PageSection>
              </StackItem>
            </Stack>
          </Page>
        </div>
      </div>
    </div>
  );
};
