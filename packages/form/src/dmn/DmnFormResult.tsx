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
import { useCallback, useEffect, useMemo } from "react";
import { CheckCircleIcon } from "@patternfly/react-icons/dist/js/icons/check-circle-icon";
import { InfoCircleIcon } from "@patternfly/react-icons/dist/js/icons/info-circle-icon";
import { ExclamationCircleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-circle-icon";
import { Tooltip } from "@patternfly/react-core";
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
import { NotificationSeverity } from "@kogito-tooling/notifications/dist/api";
import { dmnFormI18n } from "./i18n";
import { I18nWrapped } from "@kogito-tooling/i18n/dist/react-components";
import "./styles.scss";

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
  const i18n = useMemo(() => {
    dmnFormI18n.setLocale(props.locale ?? navigator.language);
    return dmnFormI18n.getCurrent();
  }, [props.locale]);

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

  const result = useCallback((dmnFormResult: Result) => {
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
                content={<I18nWrapped components={{ date: current.toString() }}>{i18n.result.dateTooltip}</I18nWrapped>}
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
        return dmnFormResult ? (
          Array.isArray(dmnFormResult) ? (
            <DescriptionList>
              {dmnFormResult.map((dmnResult, index) => (
                <DescriptionListGroup key={`array-result-${index}`}>
                  <DescriptionListTerm>{index}</DescriptionListTerm>
                  <DescriptionListDescription>{dmnResult}</DescriptionListDescription>
                </DescriptionListGroup>
              ))}
            </DescriptionList>
          ) : (
            <DescriptionList>
              {Object.entries(dmnFormResult).map(([key, value]: [string, object | string]) => (
                <DescriptionListGroup key={`object-result-${key}-${value}`}>
                  <DescriptionListTerm>{key}</DescriptionListTerm>
                  {typeof value === "object" && !!value ? (
                    Object.entries(value).map(([key2, value2]: [string, any]) => (
                      <DescriptionListGroup key={`object2-result-${key2}-${value2}`}>
                        <DescriptionListTerm>{key2}</DescriptionListTerm>
                        <DescriptionListDescription>{value2}</DescriptionListDescription>
                      </DescriptionListGroup>
                    ))
                  ) : (
                    <DescriptionListDescription>{value}</DescriptionListDescription>
                  )}
                </DescriptionListGroup>
              ))}
            </DescriptionList>
          )
        ) : (
          <i>(null)</i>
        );
      default:
        return <i>(null)</i>;
    }
  }, []);

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

  return (
    <div data-testid={"dmn-form-result"}>
      {resultsToRender && resultsToRender.length > 0 ? (
        resultsToRender
      ) : (
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
    </div>
  );
}
