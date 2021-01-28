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
import { useContext, useCallback } from "react";

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
import { Step } from "../../api";
import { useGuidedTourI18n } from "../../i18n";
import { I18nHtml } from "@kogito-tooling/i18n/dist/react-components";

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

export const StepDialog = (
  content: React.ReactNode | ((props: object) => React.ReactNode) | string,
  onCloseAction: () => void
) => {
  const { currentStep, setCurrentStep } = useContext(CurrentTutorialContext);
  const nextStep = useCallback(() => setCurrentStep(currentStep + 1), [currentStep]);
  const prevStep = useCallback(() => setCurrentStep(currentStep - 1), [currentStep]);
  return () => (
    <>
      <ModalBoxCloseButton data-kgt-close="true" onClose={onCloseAction} />
      {renderContent(content, onCloseAction, nextStep, prevStep)}
      <NavigationControls />
    </>
  );
};

export const NegativeReinforcementDialog = (step: Step | undefined, onCloseAction: () => void) => {
  const { isHighlightLayerEnabled, setIsHighlightLayerEnabled } = useContext(CurrentTutorialContext);
  const negativeReinforcementMessage = step?.negativeReinforcementMessage ?? "";
  const showSuggestion = useCallback(() => setIsHighlightLayerEnabled(false), []);
  const { i18n } = useGuidedTourI18n();

  if (!isHighlightLayerEnabled) {
    return () => (
      <>
        <ModalBoxCloseButton data-kgt-close="true" onClose={onCloseAction} />
        <EmptyState variant={EmptyStateVariant.small}>
          <EmptyStateIcon icon={BookIcon} />
          <Title headingLevel="h4" size="lg">
            {`${i18n.great}!`}
          </Title>
          <EmptyStateBody>
            <Text className="pf-c-content">{negativeReinforcementMessage}</Text>
          </EmptyStateBody>
          <EmptyStateSecondaryActions>
            <Button onClick={onCloseAction} variant="link">
              {i18n.skipTour}
            </Button>
          </EmptyStateSecondaryActions>
        </EmptyState>
      </>
    );
  } else {
    return () => (
      <>
        <ModalBoxCloseButton data-kgt-close="true" onClose={onCloseAction} />
        <EmptyState variant={EmptyStateVariant.small}>
          <EmptyStateIcon icon={BookIcon} />
          <Title headingLevel="h4" size="lg">
            {i18n.stop}
          </Title>
          <EmptyStateBody>
            <Text className="pf-c-content">{i18n.notFollowing}</Text>
            <br />
            <Text className="pf-c-content">
              <I18nHtml>{i18n.options} :-)</I18nHtml>
            </Text>
          </EmptyStateBody>
          <Button data-kgt-continue="true" onClick={showSuggestion}>
            {i18n.terms.continue}
          </Button>
          <EmptyStateSecondaryActions>
            <Button onClick={onCloseAction} variant="link">
              {i18n.skipTour}
            </Button>
          </EmptyStateSecondaryActions>
        </EmptyState>
      </>
    );
  }
};

export const EmptyDialog = (onCloseAction: () => void) => {
  const { i18n } = useGuidedTourI18n();
  return () => (
    <>
      <EmptyState variant={EmptyStateVariant.small}>
        <EmptyStateIcon icon={TimesCircleIcon} />
        <Title headingLevel="h4" size="lg">
          {i18n.oops}
        </Title>
        <EmptyStateBody>{i18n.somethingWrong}</EmptyStateBody>
        <Button onClick={onCloseAction}>{i18n.skipTour}</Button>
      </EmptyState>
    </>
  );
};
