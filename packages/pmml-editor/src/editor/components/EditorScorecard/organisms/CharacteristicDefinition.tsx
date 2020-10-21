import * as React from "react";
import { useEffect, useState } from "react";

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
import { Characteristic } from "@kogito-tooling/pmml-editor-marshaller";
import { CharacteristicGeneralForm } from "../molecules/CharacteristicGeneralForm";
import { IndexedCharacteristic } from "./CharacteristicsTable";

interface CharacteristicDefinitionProps {
  characteristic: IndexedCharacteristic | undefined;
  showPanel: boolean;
  hideCharacteristicPanel: () => void;
}

export const CharacteristicDefinition = (props: CharacteristicDefinitionProps) => {
  const { characteristic, showPanel, hideCharacteristicPanel } = props;
  const [activeTabKey, setActiveTabKey] = useState(0);

  const handleTabClick = (event: React.MouseEvent<HTMLElement, MouseEvent>, index: number) => {
    setActiveTabKey(index);
  };

  return (
    <div className={`side-panel side-panel--from-right ${showPanel ? "side-panel--is-visible" : ""}`}>
      <div className="side-panel__container">
        <div className="side-panel__content">
          <Page>
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
                  <CharacteristicGeneralForm characteristic={characteristic} />
                </Tab>
                <Tab eventKey={1} title={<TabTitleText>Attributes</TabTitleText>}>
                  <div>More stuff</div>
                </Tab>
              </Tabs>
            </PageSection>
          </Page>
        </div>
      </div>
    </div>
  );
};
