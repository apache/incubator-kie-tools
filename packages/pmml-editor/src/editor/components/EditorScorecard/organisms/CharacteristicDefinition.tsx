import * as React from "react";
import { useState } from "react";

import {
  Button,
  Page,
  PageSection,
  Split,
  SplitItem,
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

interface CharacteristicDefinitionProps {
  characteristic: IndexedCharacteristic | undefined;
  showCharacteristicPanel: boolean;
  hideCharacteristicPanel: () => void;
  validateCharacteristicName: (index: number | undefined, name: string | undefined) => boolean;
}

export const CharacteristicDefinition = (props: CharacteristicDefinitionProps) => {
  const { characteristic, showCharacteristicPanel, hideCharacteristicPanel, validateCharacteristicName } = props;
  const [activeTabKey, setActiveTabKey] = useState(0);

  const handleTabClick = (event: React.MouseEvent<HTMLElement, MouseEvent>, index: number) => {
    setActiveTabKey(index);
  };

  return (
    <div className={`side-panel side-panel--from-right ${showCharacteristicPanel ? "side-panel--is-visible" : ""}`}>
      <div className="side-panel__container">
        <div className="side-panel__content">
          <Page key={characteristic?.index}>
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
            <PageSection variant="light">
              <Tabs activeKey={activeTabKey} onSelect={handleTabClick}>
                <Tab eventKey={0} title={<TabTitleText>General</TabTitleText>}>
                  <CharacteristicGeneralForm
                    index={characteristic?.index}
                    name={characteristic?.characteristic.name}
                    reasonCode={characteristic?.characteristic.reasonCode}
                    baselineScore={characteristic?.characteristic.baselineScore}
                    validateCharacteristicName={validateCharacteristicName}
                  />
                </Tab>
                <Tab eventKey={1} title={<TabTitleText>Attributes</TabTitleText>}>
                  <CharacteristicAttributesForm index={characteristic?.index} />
                </Tab>
              </Tabs>
            </PageSection>
          </Page>
        </div>
      </div>
    </div>
  );
};
