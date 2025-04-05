/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import * as React from "react";
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { Icon } from "@patternfly/react-core/dist/js/components/Icon";
import { ArrowUpIcon } from "@patternfly/react-icons/dist/js/icons/arrow-up-icon";
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
import { formDmnI18n } from "./i18n";
import { I18nWrapped } from "@kie-tools-core/i18n/dist/react-components";
import "./styles.scss";
import { ErrorBoundary } from "@kie-tools/dmn-runner/dist/ErrorBoundary";
import { ExclamationTriangleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-triangle-icon";
import { DecisionResult, DmnEvaluationStatus, DmnEvaluationResult } from "@kie-tools/extended-services-api";
import { Flex } from "@patternfly/react-core/dist/js/layouts/Flex";
import { Button } from "@patternfly/react-core/dist/js/components/Button";

const ISSUES_URL = "https://github.com/apache/incubator-kie-issues/issues";

const DATE_REGEX = /\d{4}-(?:0[1-9]|1[0-2])-(?:0[1-9]|[1-2]\d|3[0-1])T(?:[0-1]\d|2[0-3]):[0-5]\d:[0-5]\dZ/;

enum FormDmnOutputsStatus {
  EMPTY,
  ERROR,
  VALID,
}

type DeepPartial<T> = {
  [P in keyof T]?: DeepPartial<T[P]>;
};

export interface FormDmnOutputsProps {
  results?: DecisionResult[];
  differences?: Array<DeepPartial<DecisionResult>>;
  locale?: string;
  notificationsPanel: boolean;
  openEvaluationTab?: () => void;
  openBoxedExpressionEditor?: (nodeId: string) => void;
  openedBoxedExpressionEditorNodeId: string | undefined;
}

export function FormDmnOutputs({
  openEvaluationTab,
  openBoxedExpressionEditor,
  openedBoxedExpressionEditorNodeId,
  ...props
}: FormDmnOutputsProps) {
  const [formResultStatus, setFormResultStatus] = useState<FormDmnOutputsStatus>(FormDmnOutputsStatus.EMPTY);
  const [formResultError, setFormResultError] = useState<boolean>(false);

  const i18n = useMemo(() => {
    formDmnI18n.setLocale(props.locale ?? navigator.language);
    return formDmnI18n.getCurrent();
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
  }, [openedBoxedExpressionEditorNodeId, props.differences]);

  const onAnimationEnd = useCallback((e: React.AnimationEvent<HTMLElement>, index) => {
    e.preventDefault();
    e.stopPropagation();

    const updatedResult = document.getElementById(`${index}-dmn-result`);
    updatedResult?.classList.remove("kogito--editor__dmn-form-result__leaf-updated");
  }, []);

  const onOpenEvaluationTab = useCallback(() => {
    if (props.notificationsPanel) {
      openEvaluationTab?.();
    }
  }, [props.notificationsPanel, openEvaluationTab]);

  const resultStatus = useCallback(
    (evaluationStatus: DmnEvaluationStatus) => {
      switch (evaluationStatus) {
        case DmnEvaluationStatus.SUCCEEDED:
          return (
            <>
              <div className={"kie-tools__dmn-form-result__evaluation"}>
                <CheckCircleIcon />
                {props.notificationsPanel ? (
                  <a onClick={onOpenEvaluationTab} className={"kogito--editor__dmn-form-result__evaluation-link"}>
                    {i18n.result.evaluation.succeeded}
                  </a>
                ) : (
                  <p className={"kogito--editor__dmn-form-result__evaluation-link"}>
                    {i18n.result.evaluation.succeeded}
                  </p>
                )}
              </div>
            </>
          );
        case DmnEvaluationStatus.SKIPPED:
          return (
            <>
              <div className={"kie-tools__dmn-form-result__evaluation"}>
                <Icon>&#8631;</Icon>
                {props.notificationsPanel ? (
                  <a onClick={onOpenEvaluationTab} className={"kogito--editor__dmn-form-result__evaluation-link"}>
                    {i18n.result.evaluation.skipped}
                  </a>
                ) : (
                  <p className={"kogito--editor__dmn-form-result__evaluation-link"}>{i18n.result.evaluation.skipped}</p>
                )}
              </div>
            </>
          );
        case DmnEvaluationStatus.FAILED:
          return (
            <>
              <div className={"kie-tools__dmn-form-result__evaluation"}>
                <ExclamationCircleIcon />
                {props.notificationsPanel ? (
                  <a onClick={onOpenEvaluationTab} className={"kogito--editor__dmn-form-result__evaluation-link"}>
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
    [
      i18n.result.evaluation.failed,
      i18n.result.evaluation.skipped,
      i18n.result.evaluation.succeeded,
      onOpenEvaluationTab,
      props.notificationsPanel,
    ]
  );

  const result = useCallback(
    (dmnFormResult: DmnEvaluationResult, parentKey?: string) => {
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
          if (!dmnFormResult) {
            return <i>(null)</i>;
          }

          if (Array.isArray(dmnFormResult)) {
            if (dmnFormResult.length === 0) {
              return (
                <>
                  {parentKey && <DescriptionListTerm>{parentKey}</DescriptionListTerm>}
                  <i>(null)</i>
                </>
              );
            }
            return (
              <DescriptionList>
                <DescriptionListGroup
                  style={{
                    boxShadow: "0 0px 3px rgba(3, 3, 3, 0.15)",
                    padding: "10px",
                  }}
                >
                  {dmnFormResult.map((dmnResult, index) => (
                    <React.Fragment key={`array-result-${index}`}>
                      <DescriptionListTerm>{parentKey ? `${parentKey}-${index}` : index}</DescriptionListTerm>
                      <DescriptionListDescription>{result(dmnResult)}</DescriptionListDescription>
                    </React.Fragment>
                  ))}
                </DescriptionListGroup>
              </DescriptionList>
            );
          }
          return (
            <DescriptionList>
              <DescriptionListGroup
                style={{
                  boxShadow: "0 0px 3px rgba(3, 3, 3, 0.15)",
                  padding: "10px",
                }}
              >
                {Object.entries(dmnFormResult).map(([key, value]: [string, object | string]) => (
                  <React.Fragment key={`object-result-${key}-${value}`}>
                    {value === null && (
                      <DescriptionListTerm>{parentKey ? `${parentKey}-${key}` : key}</DescriptionListTerm>
                    )}
                    {value !== null && typeof value !== "object" && (
                      <DescriptionListTerm>{parentKey ? `${parentKey}-${key}` : key}</DescriptionListTerm>
                    )}
                    <DescriptionListDescription>
                      {result(value, parentKey ? `${parentKey}-${key}` : key)}
                    </DescriptionListDescription>
                  </React.Fragment>
                ))}
              </DescriptionListGroup>
            </DescriptionList>
          );

        default:
          return <i>(null)</i>;
      }
    },
    [i18n]
  );

  const onOpenBoxedExpressionEditor = useCallback(
    (nodeId: string) => {
      return openBoxedExpressionEditor?.(nodeId);
    },
    [openBoxedExpressionEditor]
  );

  const resultsToRender = useMemo(
    () =>
      props.results?.map((dmnFormResult, index) => (
        <div key={`${index}-dmn-result`} className={"kogito--editor__dmn-form-result__results"}>
          <Card
            id={`${index}-dmn-result`}
            isFlat={true}
            className={
              openedBoxedExpressionEditorNodeId === dmnFormResult.decisionId
                ? "kogito--editor__dmn-form-result__results-card-highlight"
                : "kogito--editor__dmn-form-result__results-card"
            }
            onAnimationEnd={(e) => onAnimationEnd(e, index)}
          >
            <CardTitle>
              <Flex justifyContent={{ default: "justifyContentSpaceBetween" }} flexWrap={{ default: "nowrap" }}>
                <Title headingLevel={"h2"}>{dmnFormResult.decisionName}</Title>
                {openBoxedExpressionEditor !== undefined && (
                  <Button
                    variant={"plain"}
                    title={`Open '${dmnFormResult.decisionName}' expression`}
                    icon={<ArrowUpIcon />}
                    onClick={() => onOpenBoxedExpressionEditor?.(dmnFormResult.decisionId)}
                  />
                )}
              </Flex>
            </CardTitle>
            <CardBody isFilled={true}>{result(dmnFormResult.result)}</CardBody>
            <CardFooter>{resultStatus(dmnFormResult.evaluationStatus)}</CardFooter>
          </Card>
        </div>
      )),
    [
      props.results,
      openedBoxedExpressionEditorNodeId,
      openBoxedExpressionEditor,
      result,
      resultStatus,
      onAnimationEnd,
      onOpenBoxedExpressionEditor,
    ]
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
                    <a href={ISSUES_URL} target={"_blank"} rel={"noopener noreferrer"}>
                      {ISSUES_URL}
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
      setFormResultStatus(FormDmnOutputsStatus.VALID);
    } else if (formResultError) {
      setFormResultStatus(FormDmnOutputsStatus.ERROR);
    } else {
      setFormResultStatus(FormDmnOutputsStatus.EMPTY);
    }
  }, [resultsToRender, formResultError]);

  // Resets the ErrorBoundary everytime the FormSchema is updated
  useEffect(() => {
    errorBoundaryRef.current?.reset();
  }, [props.results]);

  return (
    <>
      {formResultStatus === FormDmnOutputsStatus.EMPTY && (
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
      {formResultStatus === FormDmnOutputsStatus.ERROR && formResultErrorMessage}
      {formResultStatus === FormDmnOutputsStatus.VALID && (
        <ErrorBoundary ref={errorBoundaryRef} setHasError={setFormResultError} error={formResultErrorMessage}>
          <div data-testid={"dmn-form-result"}>{resultsToRender}</div>
        </ErrorBoundary>
      )}
    </>
  );
}
