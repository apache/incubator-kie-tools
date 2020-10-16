import * as React from "react";
import { useRef, useState } from "react";

import {
  Button,
  Page,
  PageSection,
  Split,
  SplitItem,
  Tab,
  TabContent,
  Tabs,
  TabTitleText,
  TextContent,
  Title
} from "@patternfly/react-core";
import { CloseIcon } from "@patternfly/react-icons";
import "./CharacteristicDefinition.scss";

interface CharacteristicDefinitionProps {
  showPanel: boolean;
  characteristicsPanelToggle: () => void;
}

export const CharacteristicDefinition = (props: CharacteristicDefinitionProps) => {
  const { showPanel, characteristicsPanelToggle } = props;
  const [activeTab, setActiveTab] = useState<React.ReactText>(0);

  const handleTabClick = (event: React.MouseEvent<HTMLElement, MouseEvent>, tabIndex: React.ReactText) => {
    setActiveTab(tabIndex);
  };

  const testTab = useRef(null);
  const deployTab = useRef(null);

  return (
    <div className={`side-panel side-panel--from-right ${showPanel ? "side-panel--is-visible" : ""}`}>
      <div className="side-panel__container">
        <div className="side-panel__content">
          <Page>
            <PageSection>
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
                    onClick={e => characteristicsPanelToggle()}
                  >
                    <CloseIcon />
                  </Button>
                </SplitItem>
              </Split>
            </PageSection>
            <PageSection>
              <Tabs isFilled={true} activeKey={activeTab} onSelect={handleTabClick} isBox={true}>
                <Tab
                  eventKey={0}
                  id="test-tab"
                  title={
                    <TabTitleText>
                      <span>General</span>
                    </TabTitleText>
                  }
                  tabContentRef={testTab}
                  tabContentId="test-tab-content"
                />
                <Tab
                  eventKey={1}
                  id="deploy-tab"
                  title={<TabTitleText>Attributes</TabTitleText>}
                  tabContentRef={deployTab}
                  tabContentId="deploy-tab-content"
                />
              </Tabs>
              <div className="test-and-deploy__tabs-content">
                <div className="test-and-deploy__tabs-scroll">
                  <TabContent eventKey={0} id="test-tab-content" ref={testTab} aria-label="Test Tab Content">
                    <PageSection variant={"light"}>
                      <div>Hello</div>
                    </PageSection>
                  </TabContent>
                  <TabContent
                    eventKey={1}
                    id="deploy-tab-content"
                    ref={deployTab}
                    aria-label="Deploy Tab Content"
                    hidden={true}
                  >
                    <PageSection variant={"light"}>
                      <div>Goodbye</div>
                    </PageSection>
                  </TabContent>
                </div>
              </div>
            </PageSection>
          </Page>
        </div>
      </div>
    </div>
  );
};
