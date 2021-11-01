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
import { Text, TextContent } from "@patternfly/react-core/dist/js/components/Text";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { DrawerCloseButton, DrawerPanelContent } from "@patternfly/react-core/dist/js/components/Drawer";
import { useDmnRunner } from "./DmnRunnerContext";
import { Notification } from "@kie-tooling-core/notifications/dist/api";
import { DmnRunnerMode, DmnRunnerStatus } from "./DmnRunnerStatus";
import { TableIcon } from "@patternfly/react-icons/dist/js/icons/table-icon";
import { useOnlineI18n } from "../../common/i18n";
import {
  DecisionResult,
  DecisionResultMessage,
  DmnForm,
  DmnFormResult,
  DmnResult,
  extractDifferences,
} from "@kogito-tooling/form/dist/dmn";
import { usePrevious } from "../../common/Hooks";
import { ErrorBoundary } from "../../common/ErrorBoundary";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { I18nWrapped } from "@kie-tooling-core/i18n/dist/react-components";
import { ExclamationTriangleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-triangle-icon";
import { NotificationsPanelController } from "../NotificationsPanel/NotificationsPanel";
import { WorkspaceFile } from "../../workspace/WorkspacesContext";
import { EditorPageDockDrawerController, PanelId } from "../EditorPageDockDrawer";
import { Button } from "@patternfly/react-core/dist/js/components/Button";

const KOGITO_JIRA_LINK = "https://issues.jboss.org/projects/KOGITO";

enum ButtonPosition {
  INPUT,
  OUTPUT,
}

interface Props {
  workspaceFile: WorkspaceFile;
  editorPageDock: EditorPageDockDrawerController | undefined;
  notificationsPanel: NotificationsPanelController | undefined;
}

const DMN_RUNNER_MIN_WIDTH_TO_ROW_DIRECTION = 711;
const AUTO_SAVE_DELAY = 500;

interface DmnRunnerStylesConfig {
  contentWidth: "50%" | "100%";
  contentHeight: "50%" | "100%";
  contentFlexDirection: "row" | "column";
  buttonPosition: ButtonPosition;
}

export function DmnRunnerDrawerPanelContent(props: Props) {
  const { i18n, locale } = useOnlineI18n();
  const formRef = useRef<HTMLFormElement>(null);
  const dmnRunner = useDmnRunner();
  const [drawerError, setDrawerError] = useState<boolean>(false);
  const errorBoundaryRef = useRef<ErrorBoundary>(null);
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

      const messagesBySourceId = result.messages?.reduce((acc, message) => {
        const messageEntry = acc.get(message.sourceId);
        if (!messageEntry) {
          acc.set(message.sourceId, [message]);
        } else {
          acc.set(message.sourceId, [...messageEntry, message]);
        }
        return acc;
      }, new Map<string, DecisionResultMessage[]>());

      const notifications: Notification[] = [...(messagesBySourceId?.entries() ?? [])].flatMap(
        ([sourceId, messages]) => {
          const path = decisionNameByDecisionId?.get(sourceId) ?? "";
          return messages.map((message: any) => ({
            type: "PROBLEM",
            path,
            severity: message.severity,
            message: `${message.messageType}: ${message.message}`,
          }));
        }
      );
      props.notificationsPanel?.getTab(i18n.terms.execution)?.kogitoNotifications_setNotifications("", notifications);
    },
    [props.notificationsPanel, i18n]
  );

  const updateDmnRunnerResults = useCallback(
    async (formData: object) => {
      if (dmnRunner.status !== DmnRunnerStatus.AVAILABLE) {
        return;
      }

      try {
        const payload = await dmnRunner.preparePayload(formData);
        const result = await dmnRunner.service.result(payload);

        if (Object.hasOwnProperty.call(result, "details") && Object.hasOwnProperty.call(result, "stack")) {
          dmnRunner.setFormError(true);
          return;
        }

        setExecutionNotifications(result);

        setDmnRunnerResults((previousDmnRunnerResult: DecisionResult[]) => {
          if (!result || !result.decisionResults) {
            return;
          }
          const differences = extractDifferences(result.decisionResults, previousDmnRunnerResult);
          if (differences?.length !== 0) {
            setDmnRunnerResponseDiffs(differences);
          }
          return result.decisionResults;
        });
      } catch (e) {
        setDmnRunnerResults(undefined);
      }
    },
    [dmnRunner, setExecutionNotifications]
  );

  // Update outputs column on form change
  useEffect(() => {
    updateDmnRunnerResults(dmnRunner.formData);
  }, [dmnRunner.formData, updateDmnRunnerResults]);

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
  }, [dmnRunner.formError, dmnRunner.formData, updateDmnRunnerResults, previousFormError]);

  const openValidationTab = useCallback(() => {
    props.editorPageDock?.open(PanelId.NOTIFICATIONS_PANEL);
    props.notificationsPanel?.setActiveTab(i18n.terms.validation);
  }, [props.notificationsPanel, i18n]);

  const openExecutionTab = useCallback(() => {
    props.editorPageDock?.open(PanelId.NOTIFICATIONS_PANEL);
    props.notificationsPanel?.setActiveTab(i18n.terms.execution);
  }, [props.notificationsPanel, i18n]);

  const drawerErrorMessage = useMemo(
    () => (
      <div>
        <EmptyState>
          <EmptyStateIcon icon={ExclamationTriangleIcon} />
          <TextContent>
            <Text component={"h2"}>{i18n.dmnRunner.drawer.error.title}</Text>
          </TextContent>
          <EmptyStateBody>
            <TextContent>{i18n.dmnRunner.drawer.error.explanation}</TextContent>
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
                {i18n.dmnRunner.drawer.error.message}
              </I18nWrapped>
            </TextContent>
          </EmptyStateBody>
        </EmptyState>
      </div>
    ),
    [i18n]
  );

  useEffect(() => {
    errorBoundaryRef.current?.reset();
    setDrawerError(false);
  }, [dmnRunner.formSchema]);

  return (
    <DrawerPanelContent
      id={"kogito-panel-content"}
      className={"kogito--editor__drawer-content-panel"}
      defaultSize={`${DMN_RUNNER_MIN_WIDTH_TO_ROW_DIRECTION}px`}
      onResize={onResize}
      isResizable={true}
      minSize={"361px"}
    >
      {drawerError ? (
        drawerErrorMessage
      ) : (
        <ErrorBoundary error={drawerErrorMessage} setHasError={setDrawerError} ref={errorBoundaryRef}>
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
                    <div style={{ display: "flex" }}>
                      <Text component={"h2"}>
                        <>
                          {i18n.terms.inputs}
                          <Button
                            variant={"plain"}
                            style={{ border: 0, marginLeft: "5px" }}
                            onClick={() => {
                              dmnRunner.setMode(DmnRunnerMode.TABULAR);
                              dmnRunner.setDrawerExpanded(false);
                              props.editorPageDock?.open(PanelId.DMN_RUNNER_TABULAR);
                            }}
                          >
                            <TableIcon />
                          </Button>
                        </>
                      </Text>
                    </div>
                  </TextContent>
                  {dmnRunnerStylesConfig.buttonPosition === ButtonPosition.INPUT && (
                    <DrawerCloseButton
                      onClick={(e: any) => {
                        dmnRunner.setDrawerExpanded(false);
                      }}
                    />
                  )}
                </PageSection>
                <div className={"kogito--editor__dmn-runner-drawer-content-body"}>
                  <PageSection className={"kogito--editor__dmn-runner-drawer-content-body-input"}>
                    <DmnForm
                      formData={dmnRunner.formData}
                      setFormData={dmnRunner.setFormData}
                      formError={dmnRunner.formError}
                      setFormError={dmnRunner.setFormError}
                      formSchema={dmnRunner.formSchema}
                      id={"form"}
                      formRef={formRef}
                      showInlineError={true}
                      autosave={true}
                      autosaveDelay={AUTO_SAVE_DELAY}
                      placeholder={true}
                      errorsField={() => <></>}
                      submitField={() => <></>}
                      locale={locale}
                      notificationsPanel={true}
                      openValidationTab={openValidationTab}
                    />
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
                    <DmnFormResult
                      results={dmnRunnerResults}
                      differences={dmnRunnerResponseDiffs}
                      locale={locale}
                      notificationsPanel={true}
                      openExecutionTab={openExecutionTab}
                    />
                  </PageSection>
                </div>
              </Page>
            </div>
          </div>
        </ErrorBoundary>
      )}
    </DrawerPanelContent>
  );
}
