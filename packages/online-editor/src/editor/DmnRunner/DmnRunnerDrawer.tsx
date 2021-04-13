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
import { DecisionResult, DecisionResultMessage, EvaluationStatus, Result } from "./DmnRunnerService";
import { AutoForm } from "uniforms-patternfly";
import {
  Card,
  CardBody,
  CardFooter,
  CardTitle,
  DescriptionList,
  DescriptionListDescription,
  DescriptionListGroup,
  DescriptionListTerm,
  DrawerCloseButton,
  DrawerPanelContent,
  EmptyState,
  EmptyStateBody,
  EmptyStateIcon,
  Page,
  PageSection,
  Text,
  TextContent,
  TextVariants,
  Title
} from "@patternfly/react-core";
import {
  CheckCircleIcon,
  CubesIcon,
  ExclamationCircleIcon,
  ExclamationIcon,
  InfoCircleIcon
} from "@patternfly/react-icons";
import { diff } from "deep-object-diff";
import { ErrorBoundary } from "../../common/ErrorBoundry";
import { useDmnRunner } from "./DmnRunnerContext";
import { THROTTLING_TIME } from "./DmnRunnerContextProvider";
import { EditorApi, KogitoEditorChannelApi, KogitoEditorEnvelopeApi } from "@kogito-tooling/editor/dist/api";
import { StateControl } from "@kogito-tooling/editor/dist/channel";
import { EnvelopeServer } from "@kogito-tooling/envelope-bus/dist/channel";
import { usePrevious } from "../../common/Hooks";
import { useNotificationsPanel } from "../NotificationsPanel/NotificationsPanelContext";
import { Notification } from "@kogito-tooling/notifications/dist/api";

enum ButtonPosition {
  INPUT,
  OUTPUT
}

type Editor =
  | (EditorApi & {
      getStateControl(): StateControl;
      getEnvelopeServer(): EnvelopeServer<KogitoEditorChannelApi, KogitoEditorEnvelopeApi>;
    })
  | null;

interface Props {
  editor?: Editor;
}

const DMN_RUNNER_MIN_WIDTH_TO_ROW_DIRECTION = 711;
const AUTO_SAVE_DELAY = 500;

interface DmnRunnerStylesConfig {
  contentWidth: "50%" | "100%";
  contentHeight: "50%" | "100%";
  contentFlexDirection: "row" | "column";
  buttonPosition: ButtonPosition;
}

export function DmnRunnerDrawer(props: Props) {
  const notificationsPanel = useNotificationsPanel();
  const dmnRunner = useDmnRunner();
  const [dmnRunnerResults, setDmnRunnerResults] = useState<DecisionResult[]>();
  const autoFormRef = useRef<HTMLFormElement>();
  const [dmnRunnerResponseDiffs, setDmnRunnerResponseDiffs] = useState<object[]>();
  const errorBoundaryRef = useRef<ErrorBoundary>(null);
  const [dmnRunnerStylesConfig, setDmnRunnerStylesConfig] = useState<DmnRunnerStylesConfig>({
    contentWidth: "50%",
    contentHeight: "100%",
    contentFlexDirection: "row",
    buttonPosition: ButtonPosition.OUTPUT
  });

  const onResize = useCallback((width: number) => {
    const iframe = document.getElementById("kogito-iframe");
    if (iframe) {
      iframe.style.pointerEvents = "visible";
    }

    // FIXME: Patternfly bug. The first interaction without resizing the splitter will result in width === 0.
    if (width === 0) {
      return;
    }

    if (width > DMN_RUNNER_MIN_WIDTH_TO_ROW_DIRECTION) {
      setDmnRunnerStylesConfig({
        buttonPosition: ButtonPosition.OUTPUT,
        contentWidth: "50%",
        contentHeight: "100%",
        contentFlexDirection: "row"
      });
    } else {
      setDmnRunnerStylesConfig({
        buttonPosition: ButtonPosition.INPUT,
        contentWidth: "100%",
        contentHeight: "50%",
        contentFlexDirection: "column"
      });
    }
  }, []);

  // Remove iframe pointer event to enable resize
  useEffect(() => {
    const iframe = document.getElementById("kogito-iframe");
    const drawerResizableSplitter = document.querySelector(".pf-c-drawer__splitter");

    if (iframe && drawerResizableSplitter) {
      const removePointerEvents = () => (iframe.style.pointerEvents = "none");
      drawerResizableSplitter.addEventListener("mousedown", removePointerEvents);

      return () => {
        drawerResizableSplitter.removeEventListener("mousedown", removePointerEvents);
      };
    }
  }, []);

  const updateDmnRunnerResults = useCallback(
    (formData: object) => {
      if (!props.editor) {
        return;
      }
      return props.editor
        .getContent()
        .then(content => {
          dmnRunner.service.result({ context: formData, model: content }).then(result => {
            if (result) {
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

              const notifications: Notification[] = [...messagesBySourceId.entries()].flatMap(
                ([sourceId, messages]) => {
                  const path = decisionNameByDecisionId?.get(sourceId) ?? "";
                  return messages.map(message => ({
                    type: "PROBLEM",
                    path,
                    severity: message.severity,
                    message: `${message.messageType}: ${message.message}`
                  }));
                }
              );
              notificationsPanel.getTabRef("Execution")?.setNotifications("", notifications);
            }
            if (Object.hasOwnProperty.call(result, "details") && Object.hasOwnProperty.call(result, "stack")) {
              // DMN Runner Error
              return;
            }

            setDmnRunnerResults(previousDmnRunnerResult => {
              const differences = result?.decisionResults?.map((decisionResult, index) =>
                diff(previousDmnRunnerResult?.[index] ?? {}, decisionResult ?? {})
              );
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
    [props.editor, dmnRunner.service, notificationsPanel.getTabRef]
  );

  // Update outputs column on form data change
  useEffect(() => {
    updateDmnRunnerResults(dmnRunner.formData);
  }, [dmnRunner.formData, updateDmnRunnerResults]);

  const onSubmit = useCallback(data => {
    dmnRunner.setFormData(data);
  }, []);

  function dataPathToFormFieldPath(path: string) {
    path = path.startsWith("/")
      ? path
          .replace(/\//g, ".")
          .replace(/~0/g, "~")
          .replace(/~1/g, "/")
      : path
          .replace(/\[('|")(.+?)\1\]/g, ".$2")
          .replace(/\[(.+?)\]/g, ".$1")
          .replace(/\\'/g, "'");
    return path.slice(1);
  }

  // Validation occurs on every change and submit.
  const onValidate = useCallback(
    (model, error: any) => {
      if (!error) {
        return;
      }
      // if the form has an error, the error should be displayed and the outputs column should be updated anyway.
      const {
        details,
        changes
      }: { details: object[]; changes: Array<[string, string | number | undefined]> } = error.details.reduce(
        (infos: any, detail: any) => {
          if (detail.keyword === "type") {
            // If it's a type error, it's handled by replacing the current value with a undefined value.
            const formFieldPath = dataPathToFormFieldPath(detail.dataPath);
            autoFormRef.current?.change(formFieldPath, undefined);
            infos.changes = [...infos.changes, [formFieldPath, undefined]];
            return infos;
          } else if (detail.keyword === "enum") {
            // A enum error is caused by a type error.
            const formFieldPath = dataPathToFormFieldPath(detail.dataPath);
            autoFormRef.current?.change(formFieldPath, undefined);
            return infos;
          }
          infos.details = [...infos.details, detail];
          return infos;
        },
        { details: [], changes: [] }
      );
      // Update formData with the current change.
      changes.forEach(([formFieldPath, fieldValue]) => {
        formFieldPath?.split(".")?.reduce((deeper, field, index, array) => {
          if (index === array.length - 1) {
            deeper[field] = fieldValue;
          } else {
            return deeper[field];
          }
        }, model);
      });
      dmnRunner.setFormData(model);
      return { details };
    },
    [dmnRunner.setFormData]
  );

  // Fill the form with the previous data
  const previousIsDrawerOpen = usePrevious(dmnRunner.isDrawerExpanded);
  useEffect(() => {
    if (dmnRunner.isDrawerExpanded && !previousIsDrawerOpen) {
      // The autoFormRef is not available on the useEffect render cycle.
      // Adding this setTimout will make the ref available.
      setTimeout(() => {
        autoFormRef.current?.submit();
        Object.keys(dmnRunner.formData ?? {}).forEach(propertyName => {
          autoFormRef.current?.change(propertyName, dmnRunner.formData?.[propertyName]);
        });
      }, 0);
    }
  }, [dmnRunner.isDrawerExpanded, previousIsDrawerOpen, dmnRunner.formData]);

  // Resets the ErrorBoundary everytime the JsonSchemaBridge is updated
  useEffect(() => {
    errorBoundaryRef.current?.reset();
  }, [dmnRunner.jsonSchemaBridge]);

  const [errorGeneratingForm, setErrorGeneratingForm] = useState<boolean>(false);
  useEffect(() => {
    if (errorGeneratingForm) {
      // if there is an error generating the form, the last form data is submitted
      updateDmnRunnerResults(dmnRunner.formData);
    }
  }, [errorGeneratingForm, dmnRunner.formData, updateDmnRunnerResults]);

  // Subscribe to any change on the DMN Editor and submit the form
  useEffect(() => {
    if (props.editor) {
      let timeout: number | undefined;
      const subscription = props.editor.getStateControl().subscribe(() => {
        if (timeout) {
          clearTimeout(timeout);
        }

        timeout = window.setTimeout(() => {
          updateDmnRunnerResults(dmnRunner.formData);
        }, THROTTLING_TIME);
      });

      return () => {
        props.editor?.getStateControl().unsubscribe(subscription);
      };
    }
  }, [props.editor, dmnRunner.formData, updateDmnRunnerResults]);

  const shouldRenderForm = useMemo(() => {
    return dmnRunner.jsonSchemaBridge && Object.keys(dmnRunner.jsonSchemaBridge?.schema.properties ?? {}).length !== 0;
  }, [dmnRunner.jsonSchemaBridge]);

  return (
    <DrawerPanelContent
      id={"kogito-panel-content"}
      className={"kogito--editor__drawer-content-panel"}
      defaultSize={`${DMN_RUNNER_MIN_WIDTH_TO_ROW_DIRECTION}px`}
      onResize={onResize}
      isResizable={true}
    >
      <div
        className={"kogito--editor__dmn-runner"}
        style={{ flexDirection: dmnRunnerStylesConfig.contentFlexDirection }}
      >
        <div
          className={"kogito--editor__dmn-runner-content"}
          style={{
            width: dmnRunnerStylesConfig.contentWidth,
            height: dmnRunnerStylesConfig.contentHeight
          }}
        >
          <Page className={"kogito--editor__dmn-runner-content-page"}>
            <PageSection className={"kogito--editor__dmn-runner-content-header"}>
              <TextContent>
                <Text component={"h2"}>Inputs</Text>
              </TextContent>
              {dmnRunnerStylesConfig.buttonPosition === ButtonPosition.INPUT && (
                <DrawerCloseButton onClick={(e: any) => dmnRunner.setDrawerExpanded(false)} />
              )}
            </PageSection>
            <div className={"kogito--editor__dmn-runner-drawer-content-body"}>
              <PageSection className={"kogito--editor__dmn-runner-drawer-content-body-input"}>
                {shouldRenderForm ? (
                  <ErrorBoundary
                    ref={errorBoundaryRef}
                    setHasError={setErrorGeneratingForm}
                    error={
                      <div>
                        <EmptyState>
                          <EmptyStateIcon icon={ExclamationIcon} />
                          <TextContent>
                            <Text component={"h2"}>Oops!</Text>
                          </TextContent>
                          <EmptyStateBody>
                            <TextContent>Form cannot be rendered because of an error.</TextContent>
                          </EmptyStateBody>
                        </EmptyState>
                      </div>
                    }
                  >
                    <AutoForm
                      id={"form"}
                      ref={autoFormRef}
                      showInlineError={true}
                      autosave={true}
                      autosaveDelay={AUTO_SAVE_DELAY}
                      schema={dmnRunner.jsonSchemaBridge}
                      onSubmit={onSubmit}
                      placeholder={true}
                      errorsField={() => <></>}
                      submitField={() => <></>}
                      onValidate={onValidate}
                    />
                  </ErrorBoundary>
                ) : (
                  <div>
                    <EmptyState>
                      <EmptyStateIcon icon={CubesIcon} />
                      <TextContent>
                        <Text component={"h2"}>No Form</Text>
                      </TextContent>
                      <EmptyStateBody>
                        <TextContent>
                          <Text component={TextVariants.p}>Associated DMN doesn't have any inputs.</Text>
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
            height: dmnRunnerStylesConfig.contentHeight
          }}
        >
          <Page className={"kogito--editor__dmn-runner-content-page"}>
            <PageSection className={"kogito--editor__dmn-runner-content-header"}>
              <TextContent>
                <Text component={"h2"}>Outputs</Text>
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

  const resultStatus = useCallback((evaluationStatus: EvaluationStatus) => {
    switch (evaluationStatus) {
      case EvaluationStatus.SUCCEEDED:
        return (
          <>
            <div style={{ display: "flex", alignItems: "center" }}>
              <CheckCircleIcon />
              <a onClick={openExecutionTab} style={{ paddingLeft: "5px" }}>
                Evaluated with success
              </a>
            </div>
          </>
        );
      case EvaluationStatus.SKIPPED:
        return (
          <>
            <div style={{ display: "flex", alignItems: "center" }}>
              <InfoCircleIcon />
              <a onClick={openExecutionTab} style={{ paddingLeft: "5px" }}>
                Evaluation skipped
              </a>
            </div>
          </>
        );
      case EvaluationStatus.FAILED:
        return (
          <>
            <div style={{ display: "flex", alignItems: "center" }}>
              <ExclamationCircleIcon />
              <a onClick={openExecutionTab} style={{ paddingLeft: "5px" }}>
                Evaluation failed
              </a>
            </div>
          </>
        );
    }
  }, []);

  const result = useCallback((dmnRunnerResult: Result) => {
    switch (typeof dmnRunnerResult) {
      case "boolean":
        return dmnRunnerResult ? <i>true</i> : <i>false</i>;
      case "number":
      case "string":
        return dmnRunnerResult;
      case "object":
        return dmnRunnerResult !== null ? (
          Array.isArray(dmnRunnerResult) ? (
            <DescriptionList>
              {dmnRunnerResult.map((dmnResult, index) => (
                <DescriptionListGroup key={`array-result-${index}`}>
                  {Object.entries(dmnResult).map(([key, value]) => (
                    <>
                      <DescriptionListTerm>{key}</DescriptionListTerm>
                      <DescriptionListDescription>{value}</DescriptionListDescription>
                    </>
                  ))}
                </DescriptionListGroup>
              ))}
            </DescriptionList>
          ) : (
            <DescriptionList>
              {Object.entries(dmnRunnerResult).map(([key, value]) => (
                <DescriptionListGroup key={`object-result-${key}-${value}`}>
                  <DescriptionListTerm>{key}</DescriptionListTerm>
                  {typeof value === "object" && value !== null ? (
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
        <div key={`${index}-dmn-runner-result`} style={{ padding: "10px" }}>
          <Card
            id={`${index}-dmn-runner-result`}
            isFlat={true}
            className={"kogito--editor__dmn-runner-drawer-content-body-output-card"}
            onAnimationEnd={e => onAnimationEnd(e, index)}
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
            <Text component={"h2"}>No response</Text>
          </TextContent>
          <EmptyStateBody>
            <TextContent>
              <Text>Response appears after decisions are evaluated.</Text>
            </TextContent>
          </EmptyStateBody>
        </EmptyState>
      )}
    </div>
  );
}
