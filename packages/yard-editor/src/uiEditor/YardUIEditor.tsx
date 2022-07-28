/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from "react";
import { useCallback, useState } from "react";
import {
  Button,
  EmptyState,
  EmptyStateBody,
  EmptyStateIcon,
  Tab,
  Tabs,
  TabTitleText,
  TextInput,
  Title,
  TitleSizes,
} from "@patternfly/react-core";
import { CubesIcon } from "@patternfly/react-icons";
import { useBoxedExpressionEditorI18n } from "../i18n";
import { YardData, YardFile } from "../types";
import * as yaml from "js-yaml";
import "./YardUIEditor.css";

interface Props {
  file: YardFile | undefined;
  isReadOnly: boolean;
}

export const YardUIEditor = ({ file, isReadOnly }: Props) => {
  const { i18n } = useBoxedExpressionEditorI18n();
  const [activeTabIndex, setActiveTabIndex] = useState(0);
  const handleTabClick = useCallback((_event, tabIndex) => setActiveTabIndex(tabIndex), []);
  console.log("++++++++++++++++++++++++++++");
  console.log(file);
  console.log(file?.content);
  console.log(file?.path);
  console.log(file?.type.toString());

  const parseYardContent = useCallback((file: YardFile) => {
    if (file?.content) {
      return yaml.load(file.content) as YardData;
    }
    return null;
  }, []);

  const yardData = file?.content ? parseYardContent(file) : null;
  console.log(yardData);

  const EmptyStep = ({
    emptyStateBodyText,
    emptyStateTitleText,
  }: {
    emptyStateBodyText: string;
    emptyStateTitleText: string;
  }) => {
    return (
      <EmptyState>
        <EmptyStateIcon icon={CubesIcon} />
        <Title headingLevel={"h6"} size={"md"}>
          {emptyStateTitleText}
        </Title>
        <EmptyStateBody>{emptyStateBodyText}</EmptyStateBody>
      </EmptyState>
    );
  };

  const onNewElementButtonClicked = useCallback(() => {
    window.alert("Not yet implemented");
  }, []);

  return (
    <div className={"yard-ui-editor"}>
      <Tabs activeKey={activeTabIndex} aria-label="yard menu tabs" isBox={false} onSelect={handleTabClick}>
        <Tab eventKey={0} title={<TabTitleText>{i18n.generalTab.tabTitle}</TabTitleText>}>
          <div className={"general-body"}>
            <Title headingLevel="h6" size={TitleSizes.md}>
              {i18n.generalTab.name}
            </Title>
            <TextInput
              id={"name-text-input"}
              isReadOnly={isReadOnly}
              value={yardData?.name ? yardData.name : ""}
            ></TextInput>
            <div className={"divider"}></div>
            <Title headingLevel="h6" size={TitleSizes.md}>
              {i18n.generalTab.kind}
            </Title>
            <TextInput
              id={"kind-text-input"}
              isReadOnly={isReadOnly}
              value={yardData?.kind ? yardData.kind : ""}
            ></TextInput>
            <div className={"divider"}></div>
            <Title headingLevel="h6" size={TitleSizes.md}>
              {i18n.generalTab.expressionLang}
            </Title>
            <TextInput
              id={"expression-lang-text-input"}
              isReadOnly={isReadOnly}
              value={yardData?.expressionLang ? yardData.expressionLang : ""}
            ></TextInput>
            <div className={"divider"}></div>
            <Title headingLevel="h6" size={TitleSizes.md}>
              {i18n.generalTab.specVersion}
            </Title>
            <TextInput
              id={"specVersion-text-input"}
              isReadOnly={isReadOnly}
              value={yardData?.specVersion ? yardData.specVersion : ""}
            ></TextInput>
          </div>
        </Tab>
        <Tab eventKey={1} title={<TabTitleText>{i18n.decisionInputsTab.tabTitle}</TabTitleText>}>
          <div className={"decision-input-body"}>
            <EmptyStep
              emptyStateTitleText={i18n.decisionInputsTab.emptyStateTitle}
              emptyStateBodyText={i18n.decisionInputsTab.emptyStateBody}
            />
          </div>
        </Tab>
        <Tab eventKey={2} title={<TabTitleText>{i18n.decisionElementsTab.tabTitle}</TabTitleText>}>
          <div className={"decision-element-header"}>
            <Button onClick={onNewElementButtonClicked} variant="primary">
              {i18n.decisionElementsTab.addDecisionElementsButton}
            </Button>
            <div className={"divider"} />
            <Button isDisabled={true} variant="danger">
              {i18n.decisionElementsTab.removeDecisionElementButton}
            </Button>
          </div>
          <div className={"decision-element-body"}>
            <EmptyStep
              emptyStateTitleText={i18n.decisionElementsTab.emptyStateTitle}
              emptyStateBodyText={i18n.decisionElementsTab.emptyStateBody}
            />
          </div>
        </Tab>
      </Tabs>
    </div>
  );
};
