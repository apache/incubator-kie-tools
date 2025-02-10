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

import "@patternfly/react-core/dist/styles/base.css";

import * as React from "react";
import { useCallback, useEffect, useImperativeHandle, useMemo, useRef } from "react";

import { I18nDictionariesProvider } from "@kie-tools-core/i18n/dist/react-components";

import { testScenarioEditorDictionaries, TestScenarioEditorI18nContext, testScenarioEditorI18nDefaults } from "./i18n";

import { DmnLatestModel } from "@kie-tools/dmn-marshaller";
import { Normalized } from "@kie-tools/dmn-marshaller/dist/normalization/normalize";

import { SceSimModel } from "@kie-tools/scesim-marshaller";
import { SceSim__FactMappingType } from "@kie-tools/scesim-marshaller/dist/schemas/scesim-1_8/ts-gen/types";

import { Alert } from "@patternfly/react-core/dist/js/components/Alert";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { Drawer, DrawerContent, DrawerContentBody } from "@patternfly/react-core/dist/js/components/Drawer";
import {
  EmptyState,
  EmptyStateBody,
  EmptyStateIcon,
  EmptyStateHeader,
} from "@patternfly/react-core/dist/js/components/EmptyState";
import { Icon } from "@patternfly/react-core/dist/js/components/Icon";
import { Spinner } from "@patternfly/react-core/dist/js/components/Spinner";
import { Tabs, Tab, TabTitleIcon, TabTitleText } from "@patternfly/react-core/dist/js/components/Tabs";

import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";

import ErrorIcon from "@patternfly/react-icons/dist/esm/icons/error-circle-o-icon";
import TableIcon from "@patternfly/react-icons/dist/esm/icons/table-icon";
import HelpIcon from "@patternfly/react-icons/dist/esm/icons/help-icon";

import { ErrorBoundary, ErrorBoundaryPropsWithFallback } from "react-error-boundary";

import { Commands, CommandsContextProvider, useCommands } from "./commands/CommandsContextProvider";
import TestScenarioCreationPanel from "./creation/TestScenarioCreationPanel";
import TestScenarioDrawerPanel from "./drawer/TestScenarioDrawerPanel";
import TestScenarioSideBarMenu from "./sidebar/TestScenarioSideBarMenu";
import TestScenarioTable from "./table/TestScenarioTable";
import { useTestScenarioEditorI18n } from "./i18n";

import "./TestScenarioEditor.css";
import { ComputedStateCache } from "./store/ComputedStateCache";
import { Computed, createTestScenarioEditorStore, TestScenarioEditorTab } from "./store/TestScenarioEditorStore";
import {
  StoreApiType,
  TestScenarioEditorStoreApiContext,
  useTestScenarioEditorStore,
  useTestScenarioEditorStoreApi,
} from "./store/TestScenarioStoreContext";
import { TestScenarioEditorErrorFallback } from "./TestScenarioEditorErrorFallback";
import { TestScenarioEditorContextProvider, useTestScenarioEditor } from "./TestScenarioEditorContext";
import { TestScenarioEditorExternalModelsContextProvider } from "./externalModels/TestScenarioEditorDependenciesContext";
import { useEffectAfterFirstRender } from "./hook/useEffectAfterFirstRender";
import { INITIAL_COMPUTED_CACHE } from "./store/computed/initial";

/* Constants */

const CURRENT_SUPPORTED_VERSION = "1.8";

/* Enums */

enum TestScenarioFileStatus {
  EMPTY,
  ERROR,
  NEW,
  UNSUPPORTED,
  VALID,
}

export type OnRequestExternalModelsAvailableToInclude = () => Promise<string[]>;

export type OnRequestToJumpToPath = (normalizedPosixPathRelativeToTheOpenFile: string) => void;

export type OnRequestToResolvePath = (normalizedPosixPathRelativeToTheOpenFile: string) => string;

export type OnSceSimModelChange = (model: SceSimModel) => void;

export type OnRequestExternalModelByPath = (
  normalizedPosixPathRelativeToTheOpenFile: string
) => Promise<ExternalDmn | null>;

export type ExternalDmnsIndex = Record<string /** normalizedPosixPathRelativeToTheOpenFile */, ExternalDmn | undefined>;

export type ExternalDmn = {
  model: Normalized<DmnLatestModel>;

  normalizedPosixPathRelativeToTheOpenFile: string;

  svg: string;
};

export type TestScenarioEditorProps = {
  /**
   * When the SceSim represented by `model` ("This SceSim") is DMN-type Test Scenario this prop needs to map their contents by namespace.
   * The SceSim model won't be correctly rendered if an included model is not found on this object.
   */
  externalModelsByNamespace?: ExternalDmnsIndex;
  /**
   * A link that will take users to an issue tracker so they can report problems they find on the Test Scenario Editor.
   * This is shown on the ErrorBoundary fallback component, when an uncaught error happens.
   */
  issueTrackerHref?: string;
  /**
   * The Test Scenario itself.
   */
  model: SceSimModel;
  /**
   * Called when a change occurs on `model`, so the controlled flow of the component can be done.
   */
  onModelChange?: OnSceSimModelChange;
  /**
   * Notifies the caller when the Test Scenario Editor performs a new edit after the debounce time.
   */
  onModelDebounceStateChanged?: (changed: boolean) => void;
  /**
   * Called when the contents of a specific available DMN model is necessary.
   */
  onRequestExternalModelByPath?: OnRequestExternalModelByPath;
  /**
   * Called when the list of paths of available DMN models
   */
  onRequestExternalModelsAvailableToInclude?: OnRequestExternalModelsAvailableToInclude;
  /**
   * When users want to jump to another file, this method is called, allowing the controller of this component decide what to do.
   * Links are only rendered if this is provided. Otherwise, paths will be rendered as text.
   */
  onRequestToJumpToPath?: OnRequestToJumpToPath;
  /**
   * All paths inside the Test Scenario Editor are relative. To be able to resolve them and display them as absolute paths, this function is called.
   * If undefined, the relative paths will be displayed.
   */
  onRequestToResolvePath?: OnRequestToResolvePath;
  /**
   * The file path of the current opened Test Scenario scesim file
   */
  openFileNormalizedPosixPathRelativeToTheWorkspaceRoot: string | undefined;
};

export type TestScenarioEditorRef = {
  reset: (mode: SceSimModel) => void;
  getCommands: () => Commands;
  getDiagramSvg: () => Promise<string | undefined>;
};

export type TestScenarioSelectedColumnMetaData = {
  factMapping: SceSim__FactMappingType;
  index: number;
  isBackground: boolean;
};

function TestScenarioMainPanel() {
  const { i18n } = useTestScenarioEditorI18n();
  const { commandsRef } = useCommands();
  const testScenarioEditorStoreApi = useTestScenarioEditorStoreApi();
  const navigation = useTestScenarioEditorStore((s) => s.navigation);
  const scesimModel = useTestScenarioEditorStore((s) => s.scesim.model);
  const isAlertEnabled = true; // Will be managed in kie-issue#970
  const testScenarioType = scesimModel.ScenarioSimulationModel.settings.type?.__$$text.toUpperCase();

  const scenarioTableScrollableElementRef = useRef<HTMLDivElement | null>(null);
  const backgroundTableScrollableElementRef = useRef<HTMLDivElement | null>(null);

  const onTabChanged = useCallback(
    (_event, tab) => {
      testScenarioEditorStoreApi.setState((state) => {
        state.navigation.tab = tab;
      });
    },
    [testScenarioEditorStoreApi]
  );

  const showDockPanel = useCallback(
    (show: boolean) => {
      testScenarioEditorStoreApi.setState((state) => {
        state.navigation.dock.isOpen = show;
      });
    },
    [testScenarioEditorStoreApi]
  );

  // Show Properties panel
  useEffect(() => {
    if (!commandsRef.current) {
      return;
    }

    commandsRef.current.toggleTestScenarioDock = async () => {
      console.trace("Test Scenario Editor: COMMANDS: Toggle dock panel...");

      testScenarioEditorStoreApi.setState((state) => {
        state.navigation.dock.isOpen = !state.navigation.dock.isOpen;
      });
    };
  }, [testScenarioEditorStoreApi, commandsRef]);

  return (
    <>
      <div className="kie-scesim-editor--content">
        <Drawer isExpanded={navigation.dock.isOpen} isInline={true} position={"right"}>
          <DrawerContent panelContent={<TestScenarioDrawerPanel onDrawerClose={() => showDockPanel(false)} />}>
            <DrawerContentBody>
              {isAlertEnabled && (
                <div className="kie-scesim-editor--content-alert">
                  <Alert
                    variant={testScenarioType === "DMN" ? "warning" : "danger"}
                    title={
                      testScenarioType === "DMN"
                        ? i18n.alerts.dmnDataRetrievedFromScesim
                        : i18n.alerts.ruleDataRetrievedFromScesim
                    }
                  />
                </div>
              )}
              <div className="kie-scesim-editor--content-tabs">
                <Tabs isFilled={true} activeKey={navigation.tab} onSelect={onTabChanged} role="region">
                  <Tab
                    eventKey={TestScenarioEditorTab.SIMULATION}
                    title={
                      <>
                        <TabTitleIcon>
                          <TableIcon />
                        </TabTitleIcon>
                        <TabTitleText>{i18n.tab.scenarioTabTitle}</TabTitleText>
                        <Tooltip content={i18n.tab.scenarioTabInfo}>
                          <Icon size="sm" status="info">
                            <HelpIcon />
                          </Icon>
                        </Tooltip>
                      </>
                    }
                  >
                    <div
                      className="kie-scesim-editor--scenario-table-container"
                      ref={scenarioTableScrollableElementRef}
                    >
                      <TestScenarioTable
                        tableData={scesimModel.ScenarioSimulationModel.simulation}
                        scrollableParentRef={scenarioTableScrollableElementRef}
                      />
                    </div>
                  </Tab>
                  <Tab
                    eventKey={TestScenarioEditorTab.BACKGROUND}
                    title={
                      <>
                        <TabTitleIcon>
                          <TableIcon />
                        </TabTitleIcon>
                        <TabTitleText>{i18n.tab.backgroundTabTitle}</TabTitleText>
                        <Tooltip content={i18n.tab.backgroundTabInfo}>
                          <Icon size="sm" status="info">
                            <HelpIcon />
                          </Icon>
                        </Tooltip>
                      </>
                    }
                  >
                    <div
                      className="kie-scesim-editor--background-table-container"
                      ref={backgroundTableScrollableElementRef}
                    >
                      <TestScenarioTable
                        tableData={scesimModel.ScenarioSimulationModel.background}
                        scrollableParentRef={backgroundTableScrollableElementRef}
                      />
                    </div>
                  </Tab>
                </Tabs>
              </div>
            </DrawerContentBody>
          </DrawerContent>
        </Drawer>
      </div>
      <TestScenarioSideBarMenu />
    </>
  );
}

function TestScenarioParserErrorPanel({
  parserErrorTitle,
  parserErrorMessage,
}: {
  parserErrorTitle: string;
  parserErrorMessage: string;
}) {
  return (
    <EmptyState>
      <EmptyStateHeader
        titleText={<>{parserErrorTitle}</>}
        icon={<EmptyStateIcon icon={ErrorIcon} />}
        headingLevel="h4"
      />
      <EmptyStateBody>{parserErrorMessage}</EmptyStateBody>
    </EmptyState>
  );
}

export const TestScenarioEditorInternal = ({
  forwardRef,
  model,
  onModelChange,
  onModelDebounceStateChanged,
}: TestScenarioEditorProps & { forwardRef?: React.Ref<TestScenarioEditorRef> }) => {
  console.trace("[TestScenarioEditorInternal] Component creation ...");

  const scesim = useTestScenarioEditorStore((s) => s.scesim);
  const testScenarioEditorStoreApi = useTestScenarioEditorStoreApi();
  const { testScenarioEditorModelBeforeEditingRef, testScenarioEditorRootElementRef } = useTestScenarioEditor();
  const { commandsRef } = useCommands();

  /** Implementing Editor APIs */

  // Allow imperativelly controlling the Editor.
  useImperativeHandle(
    forwardRef,
    () => ({
      reset: () => {
        console.trace("[TestScenarioEditorInternal: Reset called!");
        const state = testScenarioEditorStoreApi.getState();
        state.dispatch(state).scesim.reset();
      },
      getCommands: () => commandsRef.current,
      getDiagramSvg: async () => undefined,
    }),
    [commandsRef, testScenarioEditorStoreApi]
  );

  // Make sure the Test Scenario Editor reacts to props changing.
  useEffectAfterFirstRender(() => {
    testScenarioEditorStoreApi.setState((state) => {
      // Avoid unecessary state updates
      if (model === state.scesim.model) {
        console.trace("[TestScenarioEditorInternal]: useEffectAfterFirstRender called, but the models are the same!");
        return;
      }

      console.trace("[TestScenarioEditorInternal]: Model updated!");

      state.scesim.model = model;
      testScenarioEditorModelBeforeEditingRef.current = model;
      //state.dispatch(state).scesim.reset();
    });
  }, [testScenarioEditorStoreApi, model]);

  // Only notify changes when dragging/resizing operations are not happening.
  useEffectAfterFirstRender(() => {
    onModelDebounceStateChanged?.(false);

    const timeout = setTimeout(() => {
      // Ignore changes made outside... If the controller of the component
      // changed its props, it knows it already, we don't need to call "onModelChange" again.
      if (model === scesim.model) {
        return;
      }

      onModelDebounceStateChanged?.(true);
      console.trace("[TestScenarioEditorInternal: Debounce State changed!");
      console.trace(scesim.model);
      onModelChange?.(scesim.model);
    }, 500);

    return () => {
      clearTimeout(timeout);
    };
  }, [onModelChange, scesim.model]);

  const scesimFileStatus = useMemo(() => {
    if (scesim.model.ScenarioSimulationModel) {
      const parserErrorField = "parsererror" as keyof typeof scesim.model.ScenarioSimulationModel;
      if (scesim.model.ScenarioSimulationModel[parserErrorField]) {
        return TestScenarioFileStatus.ERROR;
      }
      if (scesim.model.ScenarioSimulationModel["@_version"] != CURRENT_SUPPORTED_VERSION) {
        return TestScenarioFileStatus.UNSUPPORTED;
      } else if (scesim.model.ScenarioSimulationModel.settings?.type) {
        return TestScenarioFileStatus.VALID;
      } else {
        return TestScenarioFileStatus.NEW;
      }
    } else {
      return TestScenarioFileStatus.EMPTY;
    }
  }, [scesim]);

  console.trace("[TestScenarioEditorInternal] File Status: " + TestScenarioFileStatus[scesimFileStatus]);

  return (
    <div ref={testScenarioEditorRootElementRef}>
      {(() => {
        switch (scesimFileStatus) {
          case TestScenarioFileStatus.EMPTY:
            return (
              <Bullseye>
                <Spinner aria-label="SCESIM Data loading .." />
              </Bullseye>
            );
          case TestScenarioFileStatus.ERROR:
            return (
              <TestScenarioParserErrorPanel
                parserErrorTitle={"File parsing error"}
                parserErrorMessage={
                  "Impossibile to correctly parse the provided scesim file. Most likely, the XML structure of the file " +
                  "is invalid."
                }
              />
            );
          case TestScenarioFileStatus.NEW:
            return <TestScenarioCreationPanel />;
          case TestScenarioFileStatus.UNSUPPORTED:
            return (
              <TestScenarioParserErrorPanel
                parserErrorTitle={
                  "This file holds a Test Scenario asset version (" +
                  scesim.model.ScenarioSimulationModel["@_version"] +
                  ") not supported"
                }
                parserErrorMessage={
                  "Most likely, this file has been generated with a very old Business Central version (< 7.30.0.Final). " +
                  "Please update your Business Central instance and download again this scesim file, it will be automatically updated to the supported version (" +
                  CURRENT_SUPPORTED_VERSION +
                  ")."
                }
              />
            );
          case TestScenarioFileStatus.VALID:
            return <TestScenarioMainPanel />;
        }
      })()}
    </div>
  );
};

export const TestScenarioEditor = React.forwardRef(
  (props: TestScenarioEditorProps, ref: React.Ref<TestScenarioEditorRef>) => {
    console.trace("[TestScenarioEditor] Component creation ... ");
    console.trace(props.model);

    const store = useMemo(
      () => createTestScenarioEditorStore(props.model, new ComputedStateCache<Computed>(INITIAL_COMPUTED_CACHE)),
      // Purposefully empty. This memoizes the initial value of the store
      // eslint-disable-next-line react-hooks/exhaustive-deps
      []
    );
    const storeRef = React.useRef<StoreApiType>(store);

    const resetState: ErrorBoundaryPropsWithFallback["onReset"] = useCallback(({ args }) => {
      storeRef.current?.setState((state) => {
        state.scesim.model = args[0];
      });
    }, []);

    return (
      <I18nDictionariesProvider
        defaults={testScenarioEditorI18nDefaults}
        dictionaries={testScenarioEditorDictionaries}
        initialLocale={navigator.language}
        ctx={TestScenarioEditorI18nContext}
      >
        <TestScenarioEditorContextProvider {...props}>
          <ErrorBoundary FallbackComponent={TestScenarioEditorErrorFallback} onReset={resetState}>
            <TestScenarioEditorExternalModelsContextProvider {...props}>
              <TestScenarioEditorStoreApiContext.Provider value={storeRef.current}>
                <CommandsContextProvider>
                  <TestScenarioEditorInternal forwardRef={ref} {...props} />
                </CommandsContextProvider>
              </TestScenarioEditorStoreApiContext.Provider>
            </TestScenarioEditorExternalModelsContextProvider>
          </ErrorBoundary>
        </TestScenarioEditorContextProvider>
      </I18nDictionariesProvider>
    );
  }
);
