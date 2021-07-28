/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { CheckCircleIcon } from "@patternfly/react-icons/dist/js/icons/check-circle-icon";
import { InfoCircleIcon } from "@patternfly/react-icons/dist/js/icons/info-circle-icon";
import { ExclamationCircleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-circle-icon";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { OutlinedQuestionCircleIcon } from "@patternfly/react-icons/dist/js/icons/outlined-question-circle-icon";
import {
  DescriptionList,
  DescriptionListDescription,
  DescriptionListGroup,
  DescriptionListTerm,
} from "@patternfly/react-core/dist/js/components/DescriptionList";
import { Card, CardBody, CardFooter, CardTitle } from "@patternfly/react-core/dist/js/components/Card";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { Text, TextContent } from "@patternfly/react-core/dist/js/components/Text";
import { NotificationSeverity } from "@kie-tooling-core/notifications/dist/api";
import { dmnFormI18n } from "./i18n";
import { I18nWrapped } from "@kie-tooling-core/i18n/dist/react-components";
import "./styles.scss";
import { ErrorBoundary } from "../common/ErrorBoundary";
import { ExclamationTriangleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-triangle-icon";

const KOGITO_JIRA_LINK = "https://issues.jboss.org/projects/KOGITO";

enum DmnFormResultStatus {
  EMPTY,
  ERROR,
  VALID,
}

const DATE_REGEX = /\d{4}-(?:0[1-9]|1[0-2])-(?:0[1-9]|[1-2]\d|3[0-1])T(?:[0-1]\d|2[0-3]):[0-5]\d:[0-5]\dZ/;

export enum EvaluationStatus {
  SUCCEEDED = "SUCCEEDED",
  SKIPPED = "SKIPPED",
  FAILED = "FAILED",
}

export interface DecisionResultMessage {
  severity: NotificationSeverity;
  message: string;
  messageType: string;
  sourceId: string;
  level: string;
}

export type Result = boolean | number | null | object | object[] | string;

export interface DecisionResult {
  decisionId: string;
  decisionName: string;
  result: Result;
  messages: DecisionResultMessage[];
  evaluationStatus: EvaluationStatus;
}

type DeepPartial<T> = {
  [P in keyof T]?: DeepPartial<T[P]>;
};

export interface DmnFormResultProps {
  results?: DecisionResult[];
  differences?: Array<DeepPartial<DecisionResult>>;
  locale?: string;
  notificationsPanel: false;
}

export interface DmnFormResultWithNotificationsPanelProps {
  results?: DecisionResult[];
  differences?: Array<DeepPartial<DecisionResult>>;
  locale?: string;
  notificationsPanel: true;
  openExecutionTab: () => void;
}

export function DmnFormResult(props: DmnFormResultProps | DmnFormResultWithNotificationsPanelProps) {
  const [formResultStatus, setFormResultStatus] = useState<DmnFormResultStatus>(DmnFormResultStatus.EMPTY);
  const [formResultError, setFormResultError] = useState<boolean>(false);
  const i18n = useMemo(() => {
    dmnFormI18n.setLocale(props.locale ?? navigator.language);
    return dmnFormI18n.getCurrent();
  }, [props.locale]);
  const errorBoundaryRef = useRef<ErrorBoundary>(null);

  useEffect(() => {
    props.differences?.forEach((difference, index) => {
      if (Object.keys(difference).length === 0) {
        return;
      }

      const updatedResult = document.getElementById(`${index}-dmn-result`);
      updatedResult?.classList.add("kogito--editor__dmn-form-result__leaf-updated");
    });
  }, [props.differences]);

  const onAnimationEnd = useCallback((e: React.AnimationEvent<HTMLElement>, index) => {
    e.preventDefault();
    e.stopPropagation();

    const updatedResult = document.getElementById(`${index}-dmn-result`);
    updatedResult?.classList.remove("kogito--editor__dmn-form-result__leaf-updated");
  }, []);

  const openExecutionTab = useCallback(() => {
    if (props.notificationsPanel) {
      props.openExecutionTab?.();
    }
  }, [props.notificationsPanel]);

  const resultStatus = useCallback(
    (evaluationStatus: EvaluationStatus) => {
      switch (evaluationStatus) {
        case EvaluationStatus.SUCCEEDED:
          return (
            <>
              <div className={"kogito-tooling__dmn-form-result__evaluation"}>
                <CheckCircleIcon />
                {props.notificationsPanel ? (
                  <a onClick={openExecutionTab} className={"kogito--editor__dmn-form-result__evaluation-link"}>
                    {i18n.result.evaluation.success}
                  </a>
                ) : (
                  <p className={"kogito--editor__dmn-form-result__evaluation-link"}>{i18n.result.evaluation.success}</p>
                )}
              </div>
            </>
          );
        case EvaluationStatus.SKIPPED:
          return (
            <>
              <div className={"kogito-tooling__dmn-form-result__evaluation"}>
                <InfoCircleIcon />
                {props.notificationsPanel ? (
                  <a onClick={openExecutionTab} className={"kogito--editor__dmn-form-result__evaluation-link"}>
                    {i18n.result.evaluation.skipped}
                  </a>
                ) : (
                  <p className={"kogito--editor__dmn-form-result__evaluation-link"}>{i18n.result.evaluation.skipped}</p>
                )}
              </div>
            </>
          );
        case EvaluationStatus.FAILED:
          return (
            <>
              <div className={"kogito-tooling__dmn-form-result__evaluation"}>
                <ExclamationCircleIcon />
                {props.notificationsPanel ? (
                  <a onClick={openExecutionTab} className={"kogito--editor__dmn-form-result__evaluation-link"}>
                    {i18n.result.evaluation.failed}
                  </a>
                ) : (
                  <p className={"kogito--editor__dmn-form-result__evaluation-link"}>{i18n.result.evaluation.failed}</p>
                )}
              </div>
            </>
          );
      }
    },
    [i18n]
  );

  const result = useCallback(
    (dmnFormResult: Result) => {
      switch (typeof dmnFormResult) {
        case "boolean":
          return dmnFormResult ? <i>true</i> : <i>false</i>;
        case "number":
          return dmnFormResult;
        case "string":
          if (dmnFormResult.match(DATE_REGEX)) {
            const current = new Date(dmnFormResult);
            return (
              <>
                <Tooltip
                  key={`date-tooltip-${dmnFormResult}`}
                  content={
                    <I18nWrapped components={{ date: current.toString() }}>{i18n.result.dateTooltip}</I18nWrapped>
                  }
                >
                  <div className={"kogito--editor__dmn-form-result__results-date"}>
                    <p className={"kogito--editor__dmn-form-result__results-date"}>{dmnFormResult}</p>
                    <OutlinedQuestionCircleIcon />
                  </div>
                </Tooltip>
              </>
            );
          }
          return dmnFormResult;
        case "object":
          if (dmnFormResult) {
            if (Array.isArray(dmnFormResult)) {
              return (
                <DescriptionList>
                  {dmnFormResult.map((dmnResult, index) => (
                    <DescriptionListGroup key={`array-result-${index}`}>
                      <DescriptionListTerm>{index}</DescriptionListTerm>
                      {dmnResult && typeof dmnResult === "object" ? (
                        <DescriptionListDescription>{result(dmnResult)}</DescriptionListDescription>
                      ) : (
                        <DescriptionListDescription>{dmnResult}</DescriptionListDescription>
                      )}
                    </DescriptionListGroup>
                  ))}
                </DescriptionList>
              );
            }
            return (
              <DescriptionList>
                {Object.entries(dmnFormResult).map(([key, value]: [string, object | string]) => (
                  <DescriptionListGroup key={`object-result-${key}-${value}`}>
                    <DescriptionListTerm>{key}</DescriptionListTerm>
                    {value && typeof value === "object" ? (
                      Object.entries(value).map(([key2, value2]: [string, any]) => (
                        <DescriptionListGroup key={`object2-result-${key2}-${value2}`}>
                          <DescriptionListTerm>{key2}</DescriptionListTerm>
                          <DescriptionListDescription>{value2}</DescriptionListDescription>
                        </DescriptionListGroup>
                      ))
                    ) : (
                      <DescriptionListDescription>{result(value)}</DescriptionListDescription>
                    )}
                  </DescriptionListGroup>
                ))}
              </DescriptionList>
            );
          }
          return <i>(null)</i>;
        default:
          return <i>(null)</i>;
      }
    },
    [i18n]
  );

  const resultsToRender = useMemo(
    () =>
      props.results?.map((dmnFormResult, index) => (
        <div key={`${index}-dmn-result`} className={"kogito--editor__dmn-form-result__results"}>
          <Card
            id={`${index}-dmn-result`}
            isFlat={true}
            className={"kogito--editor__dmn-form-result__results-card"}
            onAnimationEnd={(e) => onAnimationEnd(e, index)}
          >
            <CardTitle>
              <Title headingLevel={"h2"}>{dmnFormResult.decisionName}</Title>
            </CardTitle>
            <CardBody isFilled={true}>{result(dmnFormResult.result)}</CardBody>
            <CardFooter>{resultStatus(dmnFormResult.evaluationStatus)}</CardFooter>
          </Card>
        </div>
      )),
    [props.results, resultStatus]
  );

  const formResultErrorMessage = useMemo(
    () => (
      <div>
        <EmptyState>
          <EmptyStateIcon icon={ExclamationTriangleIcon} />
          <TextContent>
            <Text component={"h2"}>{i18n.result.error.title}</Text>
          </TextContent>
          <EmptyStateBody>
            <TextContent>{i18n.result.error.explanation}</TextContent>
            <br />
            <TextContent>
              <I18nWrapped
                components={{
                  jira: (
                    <a href={KOGITO_JIRA_LINK} target={"_blank"}>
                      {KOGITO_JIRA_LINK}
                    </a>
                  ),
                }}
              >
                {i18n.result.error.message}
              </I18nWrapped>
            </TextContent>
          </EmptyStateBody>
        </EmptyState>
      </div>
    ),
    [i18n]
  );

  useEffect(() => {
    if (resultsToRender && resultsToRender.length > 0) {
      setFormResultStatus(DmnFormResultStatus.VALID);
    } else if (formResultError) {
      setFormResultStatus(DmnFormResultStatus.ERROR);
    } else {
      setFormResultStatus(DmnFormResultStatus.EMPTY);
    }
  }, [resultsToRender, formResultError]);

  // Resets the ErrorBoundary everytime the FormSchema is updated
  useEffect(() => {
    errorBoundaryRef.current?.reset();
  }, [props.results]);

  return (
    <>
      {formResultStatus === DmnFormResultStatus.EMPTY && (
        <EmptyState>
          <EmptyStateIcon icon={InfoCircleIcon} />
          <TextContent>
            <Text component={"h2"}>{i18n.result.withoutResponse.title}</Text>
          </TextContent>
          <EmptyStateBody>
            <TextContent>
              <Text>{i18n.result.withoutResponse.explanation}</Text>
            </TextContent>
          </EmptyStateBody>
        </EmptyState>
      )}
      {formResultStatus === DmnFormResultStatus.ERROR && formResultErrorMessage}
      {formResultStatus === DmnFormResultStatus.VALID && (
        <ErrorBoundary ref={errorBoundaryRef} setHasError={setFormResultError} error={formResultErrorMessage}>
          <div data-testid={"dmn-form-result"}>{resultsToRender}</div>
        </ErrorBoundary>
      )}
    </>
  );
}
