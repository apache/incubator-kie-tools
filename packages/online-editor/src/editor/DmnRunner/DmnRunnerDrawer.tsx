/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { DecisionResult, DecisionResultMessage, DmnResult, EvaluationStatus, Result } from "./DmnRunnerService";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Card, CardBody, CardFooter, CardTitle } from "@patternfly/react-core/dist/js/components/Card";
import { DrawerCloseButton, DrawerPanelContent } from "@patternfly/react-core/dist/js/components/Drawer";
import {
  DescriptionList,
  DescriptionListDescription,
  DescriptionListGroup,
  DescriptionListTerm,
} from "@patternfly/react-core/dist/js/components/DescriptionList";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { ExclamationCircleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-circle-icon";
import { CheckCircleIcon } from "@patternfly/react-icons/dist/js/icons/check-circle-icon";
import { CubesIcon } from "@patternfly/react-icons/dist/js/icons/cubes-icon";
import { ExclamationIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-icon";
import { InfoCircleIcon } from "@patternfly/react-icons/dist/js/icons/info-circle-icon";
import { diff } from "deep-object-diff";
import { useDmnRunner } from "./DmnRunnerContext";
import { useNotificationsPanel } from "../NotificationsPanel/NotificationsPanelContext";
import { Notification } from "@kogito-tooling/notifications/dist/api";
import { DmnRunnerStatus } from "./DmnRunnerStatus";
import { EmbeddedEditorRef } from "@kogito-tooling/editor/dist/embedded";
import { useOnlineI18n } from "../../common/i18n";
import { I18nWrapped } from "../../../../i18n/src/react-components";
import { Tooltip } from "@patternfly/react-core";
import { OutlinedQuestionCircleIcon } from "@patternfly/react-icons/dist/js/icons/outlined-question-circle-icon";
import { DmnForm } from "../../../../form/src";
import { usePrevious } from "../../common/Hooks";

enum ButtonPosition {
  INPUT,
  OUTPUT,
}

interface Props {
  editor?: EmbeddedEditorRef;
}

const DMN_RUNNER_MIN_WIDTH_TO_ROW_DIRECTION = 711;
const AUTO_SAVE_DELAY = 500;
const DATE_REGEX = /\d{4}-(?:0[1-9]|1[0-2])-(?:0[1-9]|[1-2]\d|3[0-1])T(?:[0-1]\d|2[0-3]):[0-5]\d:[0-5]\dZ/;

interface DmnRunnerStylesConfig {
  contentWidth: "50%" | "100%";
  contentHeight: "50%" | "100%";
  contentFlexDirection: "row" | "column";
  buttonPosition: ButtonPosition;
}

export function DmnRunnerDrawer(props: Props) {
  const notificationsPanel = useNotificationsPanel();
  const { i18n } = useOnlineI18n();
  const formRef = useRef<HTMLFormElement>(null);
  const dmnRunner = useDmnRunner();
  const [dmnRunnerResults, setDmnRunnerResults] = useState<DecisionResult[]>();
  const [dmnRunnerResponseDiffs, setDmnRunnerResponseDiffs] = useState<object[]>();
  const [dmnRunnerStylesConfig, setDmnRunnerStylesConfig] = useState<DmnRunnerStylesConfig>({
    contentWidth: "50%",
    contentHeight: "100%",
    contentFlexDirection: "row",
    buttonPosition: ButtonPosition.OUTPUT,
  });

  const onResize = useCallback((width: number) => {
    // FIXME: Patternfly bug. The first interaction without resizing the splitter will result in width === 0.
    if (width === 0) {
      return;
    }

    if (width > DMN_RUNNER_MIN_WIDTH_TO_ROW_DIRECTION) {
      setDmnRunnerStylesConfig({
        buttonPosition: ButtonPosition.OUTPUT,
        contentWidth: "50%",
        contentHeight: "100%",
        contentFlexDirection: "row",
      });
    } else {
      setDmnRunnerStylesConfig({
        buttonPosition: ButtonPosition.INPUT,
        contentWidth: "100%",
        contentHeight: "50%",
        contentFlexDirection: "column",
      });
    }
  }, []);

  const setExecutionNotifications = useCallback(
    (result: DmnResult) => {
      const decisionNameByDecisionId = result.decisionResults?.reduce(
        (acc, decisionResult) => acc.set(decisionResult.decisionId, decisionResult.decisionName),
        new Map<string, string>()
      );

      const messagesBySourceId = result.messages.reduce((acc, message) => {
        const messageEntry = acc.get(message.sourceId);
        if (!messageEntry) {
          acc.set(message.sourceId, [message]);
        } else {
          acc.set(message.sourceId, [...messageEntry, message]);
        }
        return acc;
      }, new Map<string, DecisionResultMessage[]>());

      const notifications: Notification[] = [...messagesBySourceId.entries()].flatMap(([sourceId, messages]) => {
        const path = decisionNameByDecisionId?.get(sourceId) ?? "";
        return messages.map((message: any) => ({
          type: "PROBLEM",
          path,
          severity: message.severity,
          message: `${message.messageType}: ${message.message}`,
        }));
      });
      notificationsPanel
        .getTabRef(i18n.notificationsPanel.execution)
        ?.kogitoNotifications_setNotifications("", notifications);
    },
    [notificationsPanel.getTabRef, i18n]
  );

  const updateDmnRunnerResults = useCallback(
    (formData: object) => {
      if (!props.editor?.isReady || dmnRunner.status !== DmnRunnerStatus.RUNNING) {
        return;
      }

      return props.editor
        .getContent()
        .then((content) => {
          dmnRunner.service.result({ context: { ...formData }, model: content }).then((result) => {
            if (Object.hasOwnProperty.call(result, "details") && Object.hasOwnProperty.call(result, "stack")) {
              dmnRunner.setFormError(true);
              return;
            }

            setExecutionNotifications(result);

            setDmnRunnerResults((previousDmnRunnerResult) => {
              const differences = result?.decisionResults
                ?.map((decisionResult, index) => diff(previousDmnRunnerResult?.[index] ?? {}, decisionResult ?? {}))
                .map((difference) => {
                  delete (difference as any).messages;
                  return difference;
                });
              if (differences?.length !== 0) {
                setDmnRunnerResponseDiffs(differences);
              }
              return result?.decisionResults;
            });
          });
        })
        .catch(() => {
          setDmnRunnerResults(undefined);
        });
    },
    [props.editor, dmnRunner.service, setExecutionNotifications]
  );

  // Update outputs column on form change
  useEffect(() => {
    updateDmnRunnerResults(dmnRunner.formData);
  }, [dmnRunner.formData, updateDmnRunnerResults]);

  const shouldRenderForm = useMemo(() => {
    return (
      !dmnRunner.formError &&
      dmnRunner.formSchema &&
      Object.keys(dmnRunner.formSchema?.definitions?.InputSet?.properties ?? {}).length !== 0
    );
  }, [dmnRunner.formError, dmnRunner.formSchema]);

  const previousFormError = usePrevious(dmnRunner.formError);
  useEffect(() => {
    if (dmnRunner.formError) {
      // if there is an error generating the form, the last form data is submitted
      updateDmnRunnerResults(dmnRunner.formData);
    } else if (previousFormError) {
      setTimeout(() => {
        formRef.current?.submit();
        Object.keys(dmnRunner.formData ?? {}).forEach((propertyName) => {
          formRef.current?.change(propertyName, dmnRunner.formData?.[propertyName]);
        });
      }, 0);
    }
  }, [dmnRunner.formError, dmnRunner.formData, updateDmnRunnerResults]);

  const openValidationTab = useCallback(() => {
    notificationsPanel.setIsOpen(true);
    notificationsPanel.setActiveTab(i18n.notificationsPanel.validation);
  }, [i18n]);

  const formErrorMessage = useMemo(
    () => (
      <div>
        <EmptyState>
          <EmptyStateIcon icon={ExclamationIcon} />
          <TextContent>
            <Text component={"h2"}>{i18n.dmnRunner.drawer.formError.title}</Text>
          </TextContent>
          <EmptyStateBody>
            <TextContent>{i18n.dmnRunner.drawer.formError.explanation}</TextContent>
            <br />
            <TextContent>
              <I18nWrapped
                components={{ link: <a onClick={openValidationTab}>{i18n.notificationsPanel.validation}</a> }}
              >
                {i18n.dmnRunner.drawer.formError.checkNotificationPanel}
              </I18nWrapped>
            </TextContent>
          </EmptyStateBody>
        </EmptyState>
      </div>
    ),
    [openValidationTab, i18n]
  );

  return (
    <DrawerPanelContent
      id={"kogito-panel-content"}
      className={"kogito--editor__drawer-content-panel"}
      defaultSize={`${DMN_RUNNER_MIN_WIDTH_TO_ROW_DIRECTION}px`}
      onResize={onResize}
      isResizable={true}
      minSize={"361px"}
    >
      <div
        className={"kogito--editor__dmn-runner"}
        style={{ flexDirection: dmnRunnerStylesConfig.contentFlexDirection }}
      >
        <div
          className={"kogito--editor__dmn-runner-content"}
          style={{
            width: dmnRunnerStylesConfig.contentWidth,
            height: dmnRunnerStylesConfig.contentHeight,
          }}
        >
          <Page className={"kogito--editor__dmn-runner-content-page"}>
            <PageSection className={"kogito--editor__dmn-runner-content-header"}>
              <TextContent>
                <Text component={"h2"}>{i18n.terms.inputs}</Text>
              </TextContent>
              {dmnRunnerStylesConfig.buttonPosition === ButtonPosition.INPUT && (
                <DrawerCloseButton onClick={(e: any) => dmnRunner.setDrawerExpanded(false)} />
              )}
            </PageSection>
            <div className={"kogito--editor__dmn-runner-drawer-content-body"}>
              <PageSection className={"kogito--editor__dmn-runner-drawer-content-body-input"}>
                {shouldRenderForm ? (
                  <DmnForm
                    formData={dmnRunner.formData}
                    setFormData={dmnRunner.setFormData}
                    formError={dmnRunner.formError}
                    setFormError={dmnRunner.setFormError}
                    formErrorMessage={formErrorMessage}
                    formSchema={dmnRunner.formSchema}
                    updateDmnRunnerResults={updateDmnRunnerResults}
                    id={"form"}
                    formRef={formRef}
                    showInlineError={true}
                    autosave={true}
                    autosaveDelay={AUTO_SAVE_DELAY}
                    placeholder={true}
                    errorsField={() => <></>}
                    submitField={() => <></>}
                  />
                ) : dmnRunner.formError ? (
                  formErrorMessage
                ) : (
                  <div>
                    <EmptyState>
                      <EmptyStateIcon icon={CubesIcon} />
                      <TextContent>
                        <Text component={"h2"}>{i18n.dmnRunner.drawer.withoutForm.title}</Text>
                      </TextContent>
                      <EmptyStateBody>
                        <TextContent>
                          <Text component={TextVariants.p}>{i18n.dmnRunner.drawer.withoutForm.explanation}</Text>
                        </TextContent>
                      </EmptyStateBody>
                    </EmptyState>
                  </div>
                )}
              </PageSection>
            </div>
          </Page>
        </div>
        <div
          className={"kogito--editor__dmn-runner-content"}
          style={{
            width: dmnRunnerStylesConfig.contentWidth,
            height: dmnRunnerStylesConfig.contentHeight,
          }}
        >
          <Page className={"kogito--editor__dmn-runner-content-page"}>
            <PageSection className={"kogito--editor__dmn-runner-content-header"}>
              <TextContent>
                <Text component={"h2"}>{i18n.terms.outputs}</Text>
              </TextContent>
              {dmnRunnerStylesConfig.buttonPosition === ButtonPosition.OUTPUT && (
                <DrawerCloseButton onClick={(e: any) => dmnRunner.setDrawerExpanded(false)} />
              )}
            </PageSection>
            <div className={"kogito--editor__dmn-runner-drawer-content-body"}>
              <PageSection className={"kogito--editor__dmn-runner-drawer-content-body-output"}>
                <DmnRunnerResult results={dmnRunnerResults!} differences={dmnRunnerResponseDiffs} />
              </PageSection>
            </div>
          </Page>
        </div>
      </div>
    </DrawerPanelContent>
  );
}

type DeepPartial<T> = {
  [P in keyof T]?: DeepPartial<T[P]>;
};

interface DmnRunnerResponseProps {
  results?: DecisionResult[];
  differences?: Array<DeepPartial<DecisionResult>>;
}

function DmnRunnerResult(props: DmnRunnerResponseProps) {
  const notificationsPanel = useNotificationsPanel();
  const { i18n } = useOnlineI18n();

  useEffect(() => {
    props.differences?.forEach((difference, index) => {
      if (Object.keys(difference).length === 0) {
        return;
      }

      const updatedResult = document.getElementById(`${index}-dmn-runner-result`);
      updatedResult?.classList.add("kogito--editor__dmn-runner-drawer-output-leaf-updated");
    });
  }, [props.differences]);

  const onAnimationEnd = useCallback((e: React.AnimationEvent<HTMLElement>, index) => {
    e.preventDefault();
    e.stopPropagation();

    const updatedResult = document.getElementById(`${index}-dmn-runner-result`);
    updatedResult?.classList.remove("kogito--editor__dmn-runner-drawer-output-leaf-updated");
  }, []);

  const openExecutionTab = useCallback(() => {
    notificationsPanel.setIsOpen(true);
    notificationsPanel.setActiveTab("Execution");
  }, [notificationsPanel]);

  const resultStatus = useCallback(
    (evaluationStatus: EvaluationStatus) => {
      switch (evaluationStatus) {
        case EvaluationStatus.SUCCEEDED:
          return (
            <>
              <div className={"kogito--editor__dmn_runner-drawer-evaluation"}>
                <CheckCircleIcon />
                <a onClick={openExecutionTab} className={"kogito--editor__dmn_runner-drawer-evaluation-link"}>
                  {i18n.dmnRunner.drawer.evaluation.success}
                </a>
              </div>
            </>
          );
        case EvaluationStatus.SKIPPED:
          return (
            <>
              <div className={"kogito--editor__dmn_runner-drawer-evaluation"}>
                <InfoCircleIcon />
                <a onClick={openExecutionTab} className={"kogito--editor__dmn_runner-drawer-evaluation-link"}>
                  {i18n.dmnRunner.drawer.evaluation.skipped}
                </a>
              </div>
            </>
          );
        case EvaluationStatus.FAILED:
          return (
            <>
              <div className={"kogito--editor__dmn_runner-drawer-evaluation"}>
                <ExclamationCircleIcon />
                <a onClick={openExecutionTab} className={"kogito--editor__dmn_runner-drawer-evaluation-link"}>
                  {i18n.dmnRunner.drawer.evaluation.failed}
                </a>
              </div>
            </>
          );
      }
    },
    [i18n]
  );

  const result = useCallback((dmnRunnerResult: Result) => {
    switch (typeof dmnRunnerResult) {
      case "boolean":
        return dmnRunnerResult ? <i>true</i> : <i>false</i>;
      case "number":
        return dmnRunnerResult;
      case "string":
        if (dmnRunnerResult.match(DATE_REGEX)) {
          const current = new Date(dmnRunnerResult);
          return (
            <>
              <Tooltip
                key={`date-tooltip-${dmnRunnerResult}`}
                content={<span>This value is in UTC. The value in your current timezone is {current.toString()}</span>}
              >
                <div style={{ display: "flex", alignItems: "center" }}>
                  <p style={{ marginRight: "10px" }}>{dmnRunnerResult}</p>
                  <OutlinedQuestionCircleIcon />
                </div>
              </Tooltip>
            </>
          );
        }
        return dmnRunnerResult;
      case "object":
        return dmnRunnerResult ? (
          Array.isArray(dmnRunnerResult) ? (
            <DescriptionList>
              {dmnRunnerResult.map((dmnResult, index) => (
                <DescriptionListGroup key={`array-result-${index}`}>
                  <DescriptionListTerm>{index}</DescriptionListTerm>
                  <DescriptionListDescription>{dmnResult}</DescriptionListDescription>
                </DescriptionListGroup>
              ))}
            </DescriptionList>
          ) : (
            <DescriptionList>
              {Object.entries(dmnRunnerResult).map(([key, value]: [string, object | string]) => (
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
      props.results?.map((dmnRunnerResult, index) => (
        <div key={`${index}-dmn-runner-result`} className={"kogito--editor__dmn_runner-drawer-results"}>
          <Card
            id={`${index}-dmn-runner-result`}
            isFlat={true}
            className={"kogito--editor__dmn-runner-drawer-content-body-output-card"}
            onAnimationEnd={(e) => onAnimationEnd(e, index)}
          >
            <CardTitle>
              <Title headingLevel={"h2"}>{dmnRunnerResult.decisionName}</Title>
            </CardTitle>
            <CardBody isFilled={true}>{result(dmnRunnerResult.result)}</CardBody>
            <CardFooter>{resultStatus(dmnRunnerResult.evaluationStatus)}</CardFooter>
          </Card>
        </div>
      )),
    [props.results, resultStatus]
  );

  return (
    <div>
      {resultsToRender && resultsToRender.length > 0 ? (
        resultsToRender
      ) : (
        <EmptyState>
          <EmptyStateIcon icon={InfoCircleIcon} />
          <TextContent>
            <Text component={"h2"}>{i18n.dmnRunner.drawer.withoutResponse.title}</Text>
          </TextContent>
          <EmptyStateBody>
            <TextContent>
              <Text>{i18n.dmnRunner.drawer.withoutResponse.explanation}</Text>
            </TextContent>
          </EmptyStateBody>
        </EmptyState>
      )}
    </div>
  );
}
