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
import { useDmnRunnerDispatch, useDmnRunnerState } from "./DmnRunnerContext";
import { DmnRunnerMode } from "./DmnRunnerStatus";
import { useElementsThatStopKeyboardEventsPropagation } from "@kie-tools-core/keyboard-shortcuts/dist/channel";
import { DmnRunnerLoading } from "./DmnRunnerLoading";
import { Drawer, DrawerContent, DrawerPanelContent } from "@patternfly/react-core/dist/js/components/Drawer";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { ExclamationIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-icon";
import { CubeIcon } from "@patternfly/react-icons/dist/js/icons/cube-icon";
import { Text, TextContent } from "@patternfly/react-core/dist/js/components/Text";
import { ErrorBoundary } from "../reactExt/ErrorBoundary";
import { useOnlineI18n } from "../i18n";
import { UnitablesWrapper } from "@kie-tools/unitables/dist/UnitablesWrapper";
import { DmnRunnerOutputsTable } from "@kie-tools/unitables-dmn/dist/DmnRunnerOutputsTable";
import { DmnUnitablesValidator } from "@kie-tools/unitables-dmn/dist/DmnUnitablesValidator";
import "./DmnRunnerTable.css";
import setObjectValueByPath from "lodash/set";
import cloneDeep from "lodash/cloneDeep";
import { DmnRunnerProviderActionType } from "./DmnRunnerProvider";

export function DmnRunnerTable() {
  // STATEs
  const [dmnRunnerTableError, setDmnRunnerTableError] = useState<boolean>(false);

  // REFs
  const dmnRunnerTableErrorBoundaryRef = useRef<ErrorBoundary>(null);
  const inputsContainerRef = useRef<HTMLDivElement>(null);
  const outputsContainerRef = useRef<HTMLDivElement>(null);
  const inputsScrollableElementRef = useRef<{ current: HTMLDivElement | null }>({ current: null });
  const outputsScrollableElementRef = useRef<{ current: HTMLDivElement | null }>({ current: null });

  // CUSTOM HOOKs
  const { i18n } = useOnlineI18n();
  const { configs, error, inputs, jsonSchema, results } = useDmnRunnerState();
  const {
    dmnRunnerDispatcher,
    onRowAdded,
    onRowDuplicated,
    onRowReset,
    onRowDeleted,
    setDmnRunnerInputs,
    setDmnRunnerMode,
    setDmnRunnerConfigInputs,
  } = useDmnRunnerDispatch();
  const { drawerPanelDefaultSize, drawerPanelMinSize, drawerPanelMaxSize, forceDrawerPanelRefresh } =
    useAnchoredUnitablesDrawerPanel({
      inputsContainerRef,
      outputsContainerRef,
    });

  // MEMOs
  const rowCount = useMemo(() => inputs?.length ?? 1, [inputs?.length]);
  const hasInputs = useMemo(() => !!jsonSchema?.definitions?.InputSet?.properties, [jsonSchema]);
  const jsonSchemaBridge = useMemo(
    () => new DmnUnitablesValidator(i18n.dmnRunner.table).getBridge(jsonSchema ?? {}),
    [i18n, jsonSchema]
  );

  useEffect(() => {
    dmnRunnerTableErrorBoundaryRef.current?.reset();
  }, [jsonSchema]);

  useEffect(() => {
    forceDrawerPanelRefresh();
  }, [forceDrawerPanelRefresh, jsonSchema]);

  const openRow = useCallback(
    (rowIndex: number) => {
      setDmnRunnerMode(DmnRunnerMode.FORM);
      dmnRunnerDispatcher({ type: DmnRunnerProviderActionType.DEFAULT, newState: { currentInputIndex: rowIndex } });
    },
    [setDmnRunnerMode, dmnRunnerDispatcher]
  );

  // FIXME: Prevent shortcuts when editing on dmn runner table;
  // useElementsThatStopKeyboardEventsPropagation(
  //   window,
  //   useMemo(() => [".kie-tools--dmn-runner-table--drawer"], [])
  // );

  useEffect(() => {
    inputsScrollableElementRef.current.current =
      document.querySelector(".kie-tools--dmn-runner-table--drawer")?.querySelector(".pf-c-drawer__content") ?? null;
    outputsScrollableElementRef.current.current =
      document.querySelector(".kie-tools--dmn-runner-table--drawer")?.querySelector(".pf-c-drawer__panel-main") ?? null;
  }, []);

  const setWidth = useCallback(
    (newWidth: number, fieldName: string) => {
      setDmnRunnerConfigInputs((previousDmnRunnerConfigInputs) => {
        const newDmnRunnerConfigInputs = cloneDeep(previousDmnRunnerConfigInputs);
        setObjectValueByPath(newDmnRunnerConfigInputs, `${fieldName}.width`, newWidth);
        return newDmnRunnerConfigInputs;
      });
    },
    [setDmnRunnerConfigInputs]
  );

  return (
    <div style={{ height: "100%" }}>
      <DmnRunnerLoading>
        {jsonSchema &&
          (dmnRunnerTableError ? (
            dmnRunnerTableError
          ) : (
            <ErrorBoundary
              ref={dmnRunnerTableErrorBoundaryRef}
              setHasError={setDmnRunnerTableError}
              error={<DmnRunnerTableError />}
            >
              <Drawer isInline={true} isExpanded={true} className={"kie-tools--dmn-runner-table--drawer"}>
                {/* DMN Runner Outputs */}
                <DrawerContent
                  panelContent={
                    <>
                      <DrawerPanelContent
                        isResizable={true}
                        minSize={rowCount > 0 ? drawerPanelMinSize : "30%"}
                        maxSize={drawerPanelMaxSize}
                        defaultSize={drawerPanelDefaultSize}
                      >
                        <div ref={outputsContainerRef}>
                          <DmnRunnerOutputsTable
                            scrollableParentRef={outputsScrollableElementRef.current}
                            i18n={i18n.dmnRunner.table}
                            jsonSchemaBridge={jsonSchemaBridge}
                            results={results}
                          />
                        </div>
                      </DrawerPanelContent>
                    </>
                  }
                >
                  {/* DMN Runner Inputs */}
                  {hasInputs ? (
                    <UnitablesWrapper
                      scrollableParentRef={inputsScrollableElementRef.current}
                      i18n={i18n.dmnRunner.table}
                      jsonSchema={jsonSchema}
                      openRow={openRow}
                      rows={inputs}
                      setRows={setDmnRunnerInputs}
                      error={error}
                      setError={(error: boolean) =>
                        dmnRunnerDispatcher({ type: DmnRunnerProviderActionType.DEFAULT, newState: { error } })
                      }
                      jsonSchemaBridge={jsonSchemaBridge}
                      containerRef={inputsContainerRef}
                      onRowAdded={onRowAdded}
                      onRowDuplicated={onRowDuplicated}
                      onRowReset={onRowReset}
                      onRowDeleted={onRowDeleted}
                      configs={configs}
                      setWidth={setWidth}
                    />
                  ) : (
                    <DmnRunnerTableEmpty />
                  )}
                </DrawerContent>
              </Drawer>
            </ErrorBoundary>
          ))}
      </DmnRunnerLoading>
    </div>
  );
}

function DmnRunnerTableEmpty() {
  return (
    <EmptyState>
      <EmptyStateIcon icon={CubeIcon} />
      <TextContent>
        <Text component={"h2"}>No inputs node yet...</Text>
      </TextContent>
      <EmptyStateBody>
        <TextContent>Add an input node and see a custom table here.</TextContent>
      </EmptyStateBody>
    </EmptyState>
  );
}

function DmnRunnerTableError() {
  return (
    <div>
      <EmptyState>
        <EmptyStateIcon icon={ExclamationIcon} />
        <TextContent>
          <Text component={"h2"}>Error</Text>
        </TextContent>
        <EmptyStateBody>
          <p>An error has happened on the DMN Runner Table</p>
        </EmptyStateBody>
      </EmptyState>
    </div>
  );
}

function useIntervalUntil(callback: () => Promise<{ shouldStop: boolean; cleanup?: () => void }>, ms: number) {
  useEffect(() => {
    let canceled = false;
    let effectCleanup = () => {};
    const interval = setInterval(() => {
      if (canceled) {
        return;
      }

      callback().then(({ cleanup, shouldStop }) => {
        if (canceled) {
          return;
        }

        if (shouldStop) {
          effectCleanup = cleanup ?? effectCleanup;
          clearInterval(interval);
        }
      });
    }, ms);

    return () => {
      canceled = true;
      clearInterval(interval);
      effectCleanup();
    };
  }, [ms, callback]);
}

function useAnchoredUnitablesDrawerPanel(args: {
  inputsContainerRef: React.RefObject<HTMLDivElement>;
  outputsContainerRef: React.RefObject<HTMLDivElement>;
}) {
  const [scrollbarWidth, setScrollbarWidth] = useState(0); // Default size on Chrome.
  const [drawerPanelMinSize, setDrawerPanelMinSize] = useState<string>();
  const [drawerPanelDefaultSize, setDrawerPanelDefaultSize] = useState<string>();

  const refreshDrawerPanelDefaultSize = useCallback(() => {
    if (!args.inputsContainerRef.current) {
      return { didRefresh: false };
    }

    const children = Object.values(args.inputsContainerRef.current.childNodes);
    const newWidth = children.reduce((acc, child: HTMLElement) => acc + child.offsetWidth, 1);
    const newDefaultSize = `calc(100vw - ${newWidth + scrollbarWidth}px)`;

    setDrawerPanelDefaultSize((prev) => {
      // This is a nasty trick to force refreshing even when the value is the same.
      // Alternate with a space at the end of the state.
      return prev?.endsWith(" ") ? newDefaultSize : newDefaultSize + " ";
    });

    return { didRefresh: true };
  }, [args.inputsContainerRef, scrollbarWidth]);

  const refreshDrawerPanelMinSize = useCallback(() => {
    const outputsTable = args.outputsContainerRef.current?.querySelector(".expression-container-box");
    if (!outputsTable) {
      return { didRefresh: false };
    }

    const ADJUSTMENT_TO_HIDE_OUTPUTS_LINE_NUMBERS_IN_PX =
      10 + // 10px for TODO
      5; // 5px for paddingRight

    const newTotalWidth = (outputsTable as HTMLElement).offsetWidth + ADJUSTMENT_TO_HIDE_OUTPUTS_LINE_NUMBERS_IN_PX;
    const newDrawerPanelMinSize = `min(50%, ${newTotalWidth + scrollbarWidth}px)`;
    setDrawerPanelMinSize((prev) => {
      // This is a nasty trick to force refreshing even when the value is the same.
      // Alternate with a space at the end of the state.
      return prev?.endsWith(" ") ? newDrawerPanelMinSize : newDrawerPanelMinSize + " ";
    });
    return { didRefresh: true };
  }, [args.outputsContainerRef, scrollbarWidth]);

  // Keep panel minimally "glued"
  useIntervalUntil(
    useCallback(async () => {
      const { didRefresh } = refreshDrawerPanelDefaultSize();
      return { shouldStop: didRefresh };
    }, [refreshDrawerPanelDefaultSize]),
    100
  );
  useIntervalUntil(
    useCallback(async () => {
      const { didRefresh } = refreshDrawerPanelMinSize();
      return { shouldStop: didRefresh };
    }, [refreshDrawerPanelMinSize]),
    100
  );

  // Recalculate panels position when double-clicking on the resize handle.
  useIntervalUntil(
    useCallback(async () => {
      const resizer = document.querySelector(
        ".kie-tools--dmn-runner-table--drawer .pf-c-drawer__panel .pf-c-drawer__splitter.pf-m-vertical"
      ) as HTMLElement | undefined;

      if (!resizer) {
        return { shouldStop: false };
      }

      resizer.addEventListener("dblclick", refreshDrawerPanelDefaultSize);
      resizer.addEventListener("dblclick", refreshDrawerPanelMinSize);

      return {
        shouldStop: true,
        cleanup: () => {
          resizer.removeEventListener("dblclick", refreshDrawerPanelDefaultSize);
          resizer.removeEventListener("dblclick", refreshDrawerPanelMinSize);
        },
      };
    }, [refreshDrawerPanelDefaultSize, refreshDrawerPanelMinSize]),
    100
  );

  // Keep scrolls in sync and set scrollbarWidth
  useEffect(() => {
    const content = document.querySelector(".kie-tools--dmn-runner-table--drawer .pf-c-drawer__content") as
      | HTMLElement
      | undefined;

    const panel = document.querySelector(".kie-tools--dmn-runner-table--drawer .pf-c-drawer__panel-main") as
      | HTMLElement
      | undefined;

    if (!panel || !content) {
      return;
    }

    setScrollbarWidth(content.offsetWidth - content.clientWidth);

    const syncContentScroll = () => (panel.scrollTop = content.scrollTop);
    const syncPanelScroll = () => (content.scrollTop = panel.scrollTop);

    content.addEventListener("scroll", syncContentScroll);
    panel.addEventListener("scroll", syncPanelScroll);

    return () => {
      content.removeEventListener("scroll", syncContentScroll);
      panel.removeEventListener("scroll", syncPanelScroll);
    };
  }, [drawerPanelDefaultSize, drawerPanelMinSize]);

  const drawerPanelMaxSize = useMemo(() => {
    return `max(50%, ${drawerPanelDefaultSize})`;
  }, [drawerPanelDefaultSize]);

  const forceRefresh = useCallback(() => {
    refreshDrawerPanelMinSize();
    refreshDrawerPanelDefaultSize();
  }, [refreshDrawerPanelMinSize, refreshDrawerPanelDefaultSize]);

  return {
    drawerPanelDefaultSize,
    drawerPanelMinSize,
    drawerPanelMaxSize,
    forceDrawerPanelRefresh: forceRefresh,
  };
}
