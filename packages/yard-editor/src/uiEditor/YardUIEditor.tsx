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
import {
  Button,
  EmptyState,
  EmptyStateBody,
  EmptyStateIcon,
  Tab,
  Tabs,
  TabTitleText,
  Title,
} from "@patternfly/react-core";
import { CubesIcon } from "@patternfly/react-icons";
import { useCallback } from "react";
import { useBoxedExpressionEditorI18n } from "../i18n";

export const YardUIEditor = () => {
  const { i18n } = useBoxedExpressionEditorI18n();

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
    <Tabs isBox={false} aria-label="yard menu tabs">
      <Tab eventKey={0} title={<TabTitleText>{i18n.decisionElementsTab.tabTitle}</TabTitleText>}>
        <div style={{ padding: 10 }}>
          <Button onClick={onNewElementButtonClicked} variant="primary">
            {i18n.decisionElementsTab.addDecisionElementsButton}
          </Button>{" "}
          <Button isDisabled={true} variant="danger">
            {i18n.decisionElementsTab.removeDecisionElementButton}
          </Button>
        </div>
        <div style={{ padding: 10 }}>
          <EmptyStep
            emptyStateTitleText={i18n.decisionElementsTab.emptyStateTitle}
            emptyStateBodyText={i18n.decisionElementsTab.emptyStateBody}
          ></EmptyStep>
        </div>
      </Tab>
      <Tab eventKey={1} title={<TabTitleText>{i18n.decisionInputsTab.tabTitle}</TabTitleText>}></Tab>
      <Tab eventKey={2} title={<TabTitleText>{i18n.generalTab.tabTitle}</TabTitleText>}></Tab>
    </Tabs>
  );
};
