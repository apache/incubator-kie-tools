import * as React from "react";
import { useEffect, useState } from "react";

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
import { Characteristic } from "@kogito-tooling/pmml-editor-marshaller";

interface CharacteristicDefinition {
  characteristic: IndexedCharacteristic | undefined;
}

interface CharacteristicDefinitionProps extends CharacteristicDefinition {
  showCharacteristicPanel: boolean;
  hideCharacteristicPanel: () => void;
  validateCharacteristicName: (index: number | undefined, name: string | undefined) => boolean;
  commit: (props: CharacteristicDefinition) => void;
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

  const commitEdit = () => {
    const _characteristic: Characteristic = Object.assign({}, characteristic?.characteristic, {
      name: name.value,
      baselineScore: baselineScore,
      reasonCode: reasonCode
    });
    const _indexedCharacteristic: IndexedCharacteristic = {
      index: characteristic?.index,
      characteristic: _characteristic
    };
    props.commit({ characteristic: _indexedCharacteristic });
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
                        name={name}
                        setName={_name => {
                          setName({
                            value: _name,
                            valid: props.validateCharacteristicName(characteristic?.index, _name)
                          });
                        }}
                        reasonCode={reasonCode}
                        setReasonCode={setReasonCode}
                        baselineScore={baselineScore}
                        setBaselineScore={setBaselineScore}
                      />
                    </Tab>
                    <Tab eventKey={1} title={<TabTitleText>Attributes</TabTitleText>}>
                      <CharacteristicAttributesForm
                        index={characteristic?.index}
                        attributes={characteristic?.characteristic.Attribute ?? []}
                        onAddAttribute={() => window.alert("Add Attribute")}
                        onRowDelete={index => window.alert("Delete Attribute")}
                      />
                    </Tab>
                  </Tabs>
                </PageSection>
              </StackItem>
              <StackItem>
                <PageSection variant="light">
                  <Split hasGutter={true}>
                    <SplitItem>
                      <Button variant={"primary"} isDisabled={!name.valid} onClick={e => commitEdit()}>
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
