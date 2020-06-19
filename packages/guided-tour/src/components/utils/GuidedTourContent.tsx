/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from "react";
import { useState, useContext, useEffect } from "react";

import {
  Button,
  EmptyState,
  EmptyStateBody,
  EmptyStateIcon,
  EmptyStateSecondaryActions,
  EmptyStateVariant,
  ModalBoxCloseButton,
  Text,
  Title
} from "@patternfly/react-core";
import { TimesCircleIcon, BookIcon } from "@patternfly/react-icons";

import { NavigationControls } from "..";
import { CurrentTutorialContext } from "../../contexts";

function renderContent(
  content: React.ReactNode | ((props: object) => React.ReactNode) | string,
  dismiss?: () => void,
  nextStep?: () => void,
  prevStep?: () => void
) {
  if (typeof content === "function") {
    return content({ dismiss, nextStep, prevStep });
  }
  if (typeof content === "string") {
    return <div dangerouslySetInnerHTML={{ __html: content }} />;
  }
  return content;
}

export const renderStepDialog = (
  content: React.ReactNode | ((props: object) => React.ReactNode) | string,
  onCloseAction: () => void
) => {
  const { currentStep, setCurrentStep } = useContext(CurrentTutorialContext);
  const nextStep = () => setCurrentStep(currentStep + 1);
  const prevStep = () => setCurrentStep(currentStep - 1);
  return () => (
    <>
      <ModalBoxCloseButton data-kgt-close="true" onClose={onCloseAction} />
      {renderContent(content, onCloseAction, nextStep, prevStep)}
      <NavigationControls />
    </>
  );
};

export const renderNegativeReinforcementDialog = (suggestion: string, onCloseAction: () => void) => {
  const { isHighlightLayerEnabled, setIsHighlightLayerEnabled } = useContext(CurrentTutorialContext);
  const [message, setMessage] = useState("");

  useEffect(() => {
    if (isHighlightLayerEnabled) {
      setMessage("");
    }
  }, [isHighlightLayerEnabled]);

  function showSuggestion() {
    setMessage(suggestion);
    setIsHighlightLayerEnabled(false);
  }

  function getNegativeReinforcementBody() {
    if (message.length > 0) {
      return (
        <>
          <Title headingLevel="h4" size="lg">
            Great!
          </Title>
          <EmptyStateBody>
            <Text className="pf-c-content">{message}</Text>
          </EmptyStateBody>
        </>
      );
    } else {
      return (
        <>
          <Title headingLevel="h4" size="lg">
            Do you want to stop the tour?
          </Title>
          <EmptyStateBody>
            <Text className="pf-c-content">
              Seems like you didn't follow the suggested action. Do you want to stop the tour?
            </Text>
            <br />
            <Text className="pf-c-content">
              Click on <b>Dismiss</b> to stop it or <b>Continue</b> to resume your tour :-)
            </Text>
          </EmptyStateBody>
          <Button data-kgt-continue="true" onClick={showSuggestion}>
            Continue
          </Button>
        </>
      );
    }
  }

  return (
    <>
      <ModalBoxCloseButton data-kgt-close="true" onClose={onCloseAction} />
      <EmptyState variant={EmptyStateVariant.small}>
        <EmptyStateIcon icon={BookIcon} />
        {getNegativeReinforcementBody()}
        <EmptyStateSecondaryActions>
          <Button onClick={onCloseAction} variant="link">
            Dismiss
          </Button>
        </EmptyStateSecondaryActions>
      </EmptyState>
    </>
  );
};

export const renderEmptyDialog = (onCloseAction: () => void) => {
  return () => (
    <>
      <EmptyState variant={EmptyStateVariant.small}>
        <EmptyStateIcon icon={TimesCircleIcon} />
        <Title headingLevel="h4" size="lg">
          Oops!
        </Title>
        <EmptyStateBody>Something went wrong and the content could not be loaded.</EmptyStateBody>
        <Button onClick={onCloseAction}>Dismiss</Button>
      </EmptyState>
    </>
  );
};
