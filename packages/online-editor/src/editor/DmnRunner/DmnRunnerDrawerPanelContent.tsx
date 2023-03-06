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
import { useCallback, useEffect, useMemo, useRef, useState, useLayoutEffect } from "react";
import { Text, TextContent } from "@patternfly/react-core/dist/js/components/Text";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { DrawerCloseButton, DrawerPanelContent } from "@patternfly/react-core/dist/js/components/Drawer";
import { useDmnRunnerDispatch, useDmnRunnerState } from "./DmnRunnerContext";
import { Notification } from "@kie-tools-core/notifications/dist/api";
import { DmnRunnerMode, DmnRunnerStatus } from "./DmnRunnerStatus";
import { TableIcon } from "@patternfly/react-icons/dist/js/icons/table-icon";
import { useOnlineI18n } from "../../i18n";
import {
  DecisionResult,
  DecisionResultMessage,
  DmnForm,
  DmnFormResult,
  DmnResult,
  InputRow,
  extractDifferences,
} from "@kie-tools/form-dmn";
import { Holder } from "@kie-tools-core/react-hooks/dist/Holder";
import { useCancelableEffect } from "@kie-tools-core/react-hooks/dist/useCancelableEffect";
import { usePrevious } from "@kie-tools-core/react-hooks/dist/usePrevious";
import { ErrorBoundary } from "../../reactExt/ErrorBoundary";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { I18nWrapped } from "@kie-tools-core/i18n/dist/react-components";
import { ExclamationTriangleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-triangle-icon";
import { WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { EditorPageDockDrawerRef, PanelId } from "../EditorPageDockDrawer";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { Dropdown, DropdownItem, DropdownToggle } from "@patternfly/react-core/dist/js/components/Dropdown";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { PlusIcon } from "@patternfly/react-icons/dist/js/icons/plus-icon";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { CaretDownIcon } from "@patternfly/react-icons/dist/js/icons/caret-down-icon";
import { ToolbarItem } from "@patternfly/react-core/dist/js/components/Toolbar";
import { DmnRunnerLoading } from "./DmnRunnerLoading";
import { useExtendedServices } from "../../kieSandboxExtendedServices/KieSandboxExtendedServicesContext";
import isEqual from "lodash/isEqual";

const KOGITO_JIRA_LINK = "https://issues.jboss.org/projects/KOGITO";

enum ButtonPosition {
  INPUT,
  OUTPUT,
}

interface Props {
  workspaceFile: WorkspaceFile;
  editorPageDock: EditorPageDockDrawerRef | undefined;
}

const DMN_RUNNER_MIN_WIDTH_TO_ROW_DIRECTION = 711;

interface DmnRunnerStylesConfig {
  contentWidth: "50%" | "100%";
  contentHeight: "50%" | "100%";
  contentFlexDirection: "row" | "column";
  buttonPosition: ButtonPosition;
}

export function DmnRunnerDrawerPanelContent(props: Props) {
  const extendedServices = useExtendedServices();
  const { i18n, locale } = useOnlineI18n();
  const [formRef, setFormRef] = useState<HTMLFormElement | null>();
  const { currentInputRowIndex, inputRows, error, mode, status, isExpanded, jsonSchema } = useDmnRunnerState();
  const { onRowAdded, preparePayload, setError, setExpanded, setInputRows, setCurrentInputRowIndex, setMode } =
    useDmnRunnerDispatch();
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
  const [selectedRow, selectRow] = useState<string>("");
  const [rowSelectionIsOpen, openRowSelection] = useState<boolean>(false);

  const formInputs: InputRow = useMemo(() => {
    return inputRows[currentInputRowIndex];
  }, [inputRows, currentInputRowIndex]);

  const onResize = useCallback((width: number) => {
    // FIXME: PatternFly bug. The first interaction without resizing the splitter will result in width === 0.
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
        (acc: Map<string, string>, decisionResult) => acc.set(decisionResult.decisionId, decisionResult.decisionName),
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
      props.editorPageDock?.setNotifications(i18n.terms.execution, "", notifications);
    },
    [props.editorPageDock, i18n.terms.execution]
  );

  const updateDmnRunnerResults = useCallback(
    async (formInputs: InputRow, canceled: Holder<boolean>) => {
      if (status !== DmnRunnerStatus.AVAILABLE) {
        return;
      }

      try {
        const payload = await preparePayload(formInputs);
        const result = await extendedServices.client.result(payload);
        if (canceled.get()) {
          return;
        }

        if (Object.hasOwnProperty.call(result, "details") && Object.hasOwnProperty.call(result, "stack")) {
          setError(true);
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
    [extendedServices.client, status, preparePayload, setError, setExecutionNotifications]
  );

  // Update outputs column on form change
  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        if (isExpanded && mode === DmnRunnerMode.FORM) {
          updateDmnRunnerResults(formInputs ?? {}, canceled);
        }
      },
      [formInputs, updateDmnRunnerResults, isExpanded, mode]
    )
  );

  const previousFormError = usePrevious(error);
  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        if (error) {
          // if there is an error generating the form, the last form data is submitted
          updateDmnRunnerResults(formInputs ?? {}, canceled);
        } else if (previousFormError) {
          setTimeout(() => {
            formRef?.submit();
            Object.keys(formInputs ?? {}).forEach((propertyName) => {
              formRef?.change(propertyName, formInputs?.[propertyName]);
            });
          }, 0);
        }
      },
      [formRef, error, formInputs, updateDmnRunnerResults, previousFormError]
    )
  );

  const openValidationTab = useCallback(() => {
    props.editorPageDock?.toggle(PanelId.NOTIFICATIONS_PANEL);
    props.editorPageDock?.getNotificationsPanel()?.setActiveTab(i18n.terms.validation);
  }, [props.editorPageDock, i18n]);

  const openExecutionTab = useCallback(() => {
    props.editorPageDock?.toggle(PanelId.NOTIFICATIONS_PANEL);
    props.editorPageDock?.getNotificationsPanel()?.setActiveTab(i18n.terms.execution);
  }, [props.editorPageDock, i18n]);

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
  }, [jsonSchema]);

  // changing between rows re-calculate this function;
  const setFormInputs = useCallback(
    (newFormInputs) => {
      setInputRows((currentInputRows: Array<InputRow>) => {
        const newInputs = [...currentInputRows];
        if (typeof newFormInputs === "function") {
          const formInput = newFormInputs(currentInputRows[currentInputRowIndex]);
          // prevent unnecessary re-render after changing between rows;
          if (!isEqual(formInput, newInputs[currentInputRowIndex])) {
            newInputs[currentInputRowIndex] = formInput;
            return newInputs;
          }
        } else {
          // prevent unnecessary re-render after changing between rows;
          if (!isEqual(newFormInputs, newInputs[currentInputRowIndex])) {
            newInputs[currentInputRowIndex] = newFormInputs;
            return newInputs;
          }
        }
        return currentInputRows;
      });
    },
    [currentInputRowIndex, setInputRows]
  );

  const onSelectRow = useCallback((event) => {
    openRowSelection(false);
  }, []);

  const getRow = useCallback((index: number) => `Row ${index}`, []);

  const rowOptions = useMemo(
    () =>
      inputRows.map((_, rowIndex) => (
        <DropdownItem
          component={"button"}
          key={rowIndex}
          onClick={() => {
            selectRow(getRow(rowIndex + 1));
            setCurrentInputRowIndex(rowIndex);
          }}
        >
          Row {rowIndex + 1}
        </DropdownItem>
      )),
    [inputRows, setCurrentInputRowIndex, selectRow, getRow]
  );

  const onAddNewRow = useCallback(() => {
    setCurrentInputRowIndex((currentInputRowIndex) => {
      const newInputRowIndex = currentInputRowIndex + 1;
      onRowAdded({ beforeIndex: newInputRowIndex });
      selectRow(getRow(newInputRowIndex));
      return newInputRowIndex;
    });
  }, [onRowAdded, setCurrentInputRowIndex, getRow]);

  const onChangeToTableView = useCallback(() => {
    setMode(DmnRunnerMode.TABLE);
    props.editorPageDock?.toggle(PanelId.DMN_RUNNER_TABLE);
  }, [setMode, props.editorPageDock]);

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
          <DmnRunnerLoading>
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
                    <Flex
                      flexWrap={{ default: "nowrap" }}
                      style={{ width: "100%" }}
                      justifyContent={{ default: "justifyContentSpaceBetween" }}
                      alignItems={{ default: "alignItemsCenter" }}
                    >
                      <FlexItem>
                        {inputRows.length <= 1 ? (
                          <Button
                            variant={ButtonVariant.plain}
                            className={"kie-tools--masthead-hoverable"}
                            style={{ cursor: "default" }}
                          >
                            <TextContent>
                              <Text component={"h3"}>{i18n.terms.inputs}</Text>
                            </TextContent>
                          </Button>
                        ) : (
                          <div>
                            <Dropdown
                              className={"kie-tools--masthead-hoverable"}
                              isPlain={true}
                              aria-label="Select Row Input"
                              toggle={
                                <DropdownToggle
                                  toggleIndicator={null}
                                  onToggle={() => openRowSelection((prevState) => !prevState)}
                                >
                                  <TextContent>
                                    <Text component={"h3"}>
                                      {i18n.terms.inputs} (Row {currentInputRowIndex + 1}) <CaretDownIcon />
                                    </Text>
                                  </TextContent>
                                </DropdownToggle>
                              }
                              onSelect={onSelectRow}
                              dropdownItems={rowOptions}
                              isOpen={rowSelectionIsOpen}
                            />
                          </div>
                        )}
                      </FlexItem>
                      <FlexItem>
                        <ToolbarItem>
                          <Tooltip content={"Add new input row"}>
                            <Button
                              className={"kie-tools--masthead-hoverable"}
                              variant={ButtonVariant.plain}
                              onClick={onAddNewRow}
                            >
                              <PlusIcon />
                            </Button>
                          </Tooltip>
                        </ToolbarItem>
                        <ToolbarItem>
                          <Tooltip content={"Switch to table view"}>
                            <Button
                              ouiaId="switch-dmn-runner-to-table-view"
                              className={"kie-tools--masthead-hoverable"}
                              variant={ButtonVariant.plain}
                              onClick={onChangeToTableView}
                            >
                              <TableIcon />
                            </Button>
                          </Tooltip>
                        </ToolbarItem>
                      </FlexItem>
                    </Flex>
                    {dmnRunnerStylesConfig.buttonPosition === ButtonPosition.INPUT && (
                      <DrawerCloseButton onClick={() => setExpanded(false)} />
                    )}
                  </PageSection>
                  <div className={"kogito--editor__dmn-runner-drawer-content-body"}>
                    <PageSection className={"kogito--editor__dmn-runner-drawer-content-body-input"}>
                      <DmnForm
                        // force a re-render when the row is changed;
                        key={selectedRow}
                        formInputs={formInputs}
                        setFormInputs={setFormInputs}
                        formError={error}
                        setFormError={setError}
                        formSchema={jsonSchema}
                        id={"form"}
                        setFormRef={setFormRef}
                        showInlineError={true}
                        autoSave={true}
                        autoSaveDelay={400}
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
                      <Text component={"h3"}>{i18n.terms.outputs}</Text>
                    </TextContent>
                    {dmnRunnerStylesConfig.buttonPosition === ButtonPosition.OUTPUT && (
                      <DrawerCloseButton onClick={() => setExpanded(false)} />
                    )}
                  </PageSection>
                  <div
                    className={"kogito--editor__dmn-runner-drawer-content-body"}
                    data-ouia-component-id={"dmn-runner-results"}
                  >
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
          </DmnRunnerLoading>
        </ErrorBoundary>
      )}
    </DrawerPanelContent>
  );
}
