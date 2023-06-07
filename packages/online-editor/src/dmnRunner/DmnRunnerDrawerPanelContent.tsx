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
import { useCallback, useEffect, useMemo, useState } from "react";
import { Text, TextContent } from "@patternfly/react-core/dist/js/components/Text";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { DrawerCloseButton, DrawerPanelContent } from "@patternfly/react-core/dist/js/components/Drawer";
import { useDmnRunnerDispatch, useDmnRunnerState } from "./DmnRunnerContext";
import { DmnRunnerMode } from "./DmnRunnerStatus";
import { TableIcon } from "@patternfly/react-icons/dist/js/icons/table-icon";
import { useOnlineI18n } from "../i18n";
import { FormDmn, FormDmnOutputs, InputRow } from "@kie-tools/form-dmn";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { Dropdown, DropdownItem, DropdownToggle } from "@patternfly/react-core/dist/js/components/Dropdown";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { PlusIcon } from "@patternfly/react-icons/dist/js/icons/plus-icon";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { CaretDownIcon } from "@patternfly/react-icons/dist/js/icons/caret-down-icon";
import { ToolbarItem } from "@patternfly/react-core/dist/js/components/Toolbar";
import { DmnRunnerLoading } from "./DmnRunnerLoading";
import { DmnRunnerProviderActionType } from "./DmnRunnerTypes";
import { PanelId, useEditorDockContext } from "../editor/EditorPageDockContextProvider";
import { DmnRunnerExtendedServicesError } from "./DmnRunnerContextProvider";

enum ButtonPosition {
  INPUT,
  OUTPUT,
}

const DMN_RUNNER_MIN_WIDTH_TO_ROW_DIRECTION = 711;

interface DmnRunnerStylesConfig {
  contentWidth: "50%" | "100%";
  contentHeight: "50%" | "100%";
  contentFlexDirection: "row" | "column";
  buttonPosition: ButtonPosition;
}

export function DmnRunnerDrawerPanelContent() {
  // STATEs
  const [drawerError, setDrawerError] = useState<boolean>(false);
  const [dmnRunnerStylesConfig, setDmnRunnerStylesConfig] = useState<DmnRunnerStylesConfig>({
    contentWidth: "50%",
    contentHeight: "100%",
    contentFlexDirection: "row",
    buttonPosition: ButtonPosition.OUTPUT,
  });
  const [rowSelectionIsOpen, openRowSelection] = useState<boolean>(false);

  const { i18n, locale } = useOnlineI18n();
  const { currentInputIndex, dmnRunnerKey, extendedServicesError, inputs, jsonSchema, results, resultsDifference } =
    useDmnRunnerState();
  const { setDmnRunnerContextProviderState, onRowAdded, setDmnRunnerInputs, setDmnRunnerMode } = useDmnRunnerDispatch();
  const { notificationsPanel, onOpenPanel } = useEditorDockContext();

  const formInputs: InputRow = useMemo(() => inputs[currentInputIndex], [inputs, currentInputIndex]);

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

  const openValidationTab = useCallback(() => {
    onOpenPanel(PanelId.NOTIFICATIONS_PANEL);
    notificationsPanel?.setActiveTab(i18n.terms.validation);
  }, [i18n.terms.validation, notificationsPanel, onOpenPanel]);

  const openExecutionTab = useCallback(() => {
    onOpenPanel(PanelId.NOTIFICATIONS_PANEL);
    notificationsPanel?.setActiveTab(i18n.terms.execution);
  }, [i18n.terms.execution, notificationsPanel, onOpenPanel]);

  useEffect(() => {
    setDrawerError(false);
  }, [jsonSchema]);

  // changing between rows re-calculate this function;
  const setFormInputs = useCallback(
    (newFormInput: (previousInputRow: InputRow) => InputRow | InputRow) => {
      setDmnRunnerInputs((perviousInputRows) => {
        const newInputRows = [...perviousInputRows];
        if (typeof newFormInput === "function") {
          newInputRows[currentInputIndex] = newFormInput(newInputRows[currentInputIndex]);
          return newInputRows;
        }
        newInputRows[currentInputIndex] = newFormInput;
        return newInputRows;
      });
    },
    [currentInputIndex, setDmnRunnerInputs]
  );

  const onSelectRow = useCallback((event) => {
    openRowSelection(false);
  }, []);

  const getRow = useCallback((index: number) => `Row ${index}`, []);

  const rowOptions = useMemo(
    () =>
      inputs.map((_, rowIndex) => (
        <DropdownItem
          component={"button"}
          key={rowIndex}
          onClick={() => {
            setDmnRunnerContextProviderState({
              type: DmnRunnerProviderActionType.DEFAULT,
              newState: { currentInputIndex: rowIndex },
            });
          }}
        >
          {getRow(rowIndex + 1)}
        </DropdownItem>
      )),
    [inputs, setDmnRunnerContextProviderState, getRow]
  );

  const onAddNewRow = useCallback(() => {
    onRowAdded({ beforeIndex: currentInputIndex + 1 });
  }, [onRowAdded, currentInputIndex]);

  const onChangeToTableView = useCallback(() => {
    setDmnRunnerMode(DmnRunnerMode.TABLE);
    onOpenPanel(PanelId.DMN_RUNNER_TABLE);
  }, [onOpenPanel, setDmnRunnerMode]);

  return (
    <DrawerPanelContent
      id={"kogito-panel-content"}
      className={"kogito--editor__drawer-content-panel"}
      defaultSize={`${DMN_RUNNER_MIN_WIDTH_TO_ROW_DIRECTION}px`}
      onResize={onResize}
      isResizable={true}
      minSize={"361px"}
    >
      {extendedServicesError ? (
        <DmnRunnerExtendedServicesError />
      ) : (
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
                      {inputs.length <= 1 ? (
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
                                style={{ padding: "0px" }}
                                toggleIndicator={null}
                                onToggle={() => openRowSelection((prevState) => !prevState)}
                              >
                                <TextContent>
                                  <Text component={"h3"}>
                                    {i18n.terms.inputs} ({getRow(currentInputIndex + 1)}) <CaretDownIcon />
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
                    <DrawerCloseButton
                      onClick={() =>
                        setDmnRunnerContextProviderState({
                          type: DmnRunnerProviderActionType.DEFAULT,
                          newState: { isExpanded: false },
                        })
                      }
                    />
                  )}
                </PageSection>
                <div key={dmnRunnerKey} className={"kogito--editor__dmn-runner-drawer-content-body"}>
                  <PageSection className={"kogito--editor__dmn-runner-drawer-content-body-input"}>
                    <FormDmn
                      // force a re-render when the row is changed;
                      key={formInputs?.id}
                      formInputs={formInputs}
                      setFormInputs={setFormInputs}
                      formError={drawerError}
                      setFormError={setDrawerError}
                      formSchema={jsonSchema}
                      id={"form"}
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
                    <DrawerCloseButton
                      onClick={() =>
                        setDmnRunnerContextProviderState({
                          type: DmnRunnerProviderActionType.DEFAULT,
                          newState: { isExpanded: false },
                        })
                      }
                    />
                  )}
                </PageSection>
                <div
                  className={"kogito--editor__dmn-runner-drawer-content-body"}
                  data-ouia-component-id={"dmn-runner-results"}
                >
                  <PageSection className={"kogito--editor__dmn-runner-drawer-content-body-output"}>
                    <FormDmnOutputs
                      results={results[currentInputIndex]}
                      differences={resultsDifference[currentInputIndex]}
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
      )}
    </DrawerPanelContent>
  );
}
