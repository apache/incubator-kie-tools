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
import { DecisionResult, InputRow } from "@kie-tools/form-dmn";
import { PanelId } from "../EditorPageDockDrawer";
import { useElementsThatStopKeyboardEventsPropagation } from "@kie-tools-core/keyboard-shortcuts/dist/channel";
import { WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { DmnRunnerLoading } from "./DmnRunnerLoading";
import { Holder } from "@kie-tools-core/react-hooks/dist/Holder";
import { useCancelableEffect } from "@kie-tools-core/react-hooks/dist/useCancelableEffect";
import { Drawer, DrawerContent, DrawerPanelContent } from "@patternfly/react-core/dist/js/components/Drawer";
import { TableOperation } from "@kie-tools/boxed-expression-component/dist/api";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { ExclamationIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-icon";
import { Text, TextContent } from "@patternfly/react-core/dist/js/components/Text";
import { ErrorBoundary } from "@kie-tools/form";
import { useOnlineI18n } from "../../i18n";
import { Unitables, UnitablesApi } from "@kie-tools/unitables/dist/Unitables";
import { DmnTableResults } from "@kie-tools/unitables-dmn/dist/DmnTableResults";
import { DmnUnitablesValidator } from "@kie-tools/unitables-dmn/dist/DmnUnitablesValidator";

interface Props {
  isReady?: boolean;
  setPanelOpen: React.Dispatch<React.SetStateAction<PanelId>>;
  dmnRunnerResults: Array<DecisionResult[] | undefined>;
  setDmnRunnerResults: React.Dispatch<React.SetStateAction<Array<DecisionResult[] | undefined>>>;
  workspaceFile: WorkspaceFile;
}

export function DmnRunnerTable(props: Props) {
  const dmnRunnerState = useDmnRunnerState();
  const dmnRunnerDispatch = useDmnRunnerDispatch();
  const [rowCount, setRowCount] = useState<number>(dmnRunnerState.inputRows?.length ?? 1);
  const [dmnRunnerTableError, setDmnRunnerTableError] = useState<boolean>(false);
  const dmnRunnerTableErrorBoundaryRef = useRef<ErrorBoundary>(null);
  const unitablesRef = useRef<UnitablesApi>(null);
  const { i18n } = useOnlineI18n();

  const jsonSchemaBridge = useMemo(
    () => new DmnUnitablesValidator(i18n.dmnRunner.table).getBridge(dmnRunnerState.jsonSchema ?? {}),
    [i18n, dmnRunnerState.jsonSchema]
  );

  useEffect(() => {
    dmnRunnerTableErrorBoundaryRef.current?.reset();
  }, [dmnRunnerState.jsonSchema]);

  const inputsContainerRef = useRef<HTMLDivElement>(null);
  const outputsContainerRef = useRef<HTMLDivElement>(null);
  const { drawerPanelDefaultSize, drawerPanelMinSize, drawerPanelMaxSize, forceDrawerPanelRefresh } =
    useAnchoredUnitablesDrawerPanel({
      inputsContainerRef,
      outputsContainerRef,
    });

  useEffect(() => {
    forceDrawerPanelRefresh();
  }, [forceDrawerPanelRefresh, dmnRunnerState.inputRows, props.dmnRunnerResults]);

  const onRowNumberUpdate = useCallback((rowQtt: number, operation?: TableOperation, rowIndex?: number) => {
    setRowCount(rowQtt);
    if (unitablesRef.current && operation !== undefined && rowIndex !== undefined) {
      unitablesRef.current.operationHandler(operation, rowIndex);
    }
  }, []);

  const updateDmnRunnerResults = useCallback(
    async (inputRows: Array<InputRow>, canceled: Holder<boolean>) => {
      if (!props.isReady) {
        dmnRunnerDispatch.setDidUpdateOutputRows(true);
        return;
      }

      try {
        if (canceled.get()) {
          return;
        }
        const payloads = await Promise.all(inputRows.map((data) => dmnRunnerDispatch.preparePayload(data)));
        const results = await Promise.all(
          payloads.map((payload) => {
            if (payload === undefined) {
              return;
            }
            return dmnRunnerState.service.result(payload);
          })
        );
        if (canceled.get()) {
          return;
        }

        const runnerResults: Array<DecisionResult[] | undefined> = [];
        for (const result of results) {
          if (Object.hasOwnProperty.call(result, "details") && Object.hasOwnProperty.call(result, "stack")) {
            dmnRunnerDispatch.setError(true);
            break;
          }
          if (result) {
            runnerResults.push(result.decisionResults);
          }
        }
        props.setDmnRunnerResults(runnerResults);
        dmnRunnerDispatch.setDidUpdateOutputRows(true);
      } catch (err) {
        dmnRunnerDispatch.setDidUpdateOutputRows(true);
        return undefined;
      }
    },
    [props.isReady, dmnRunnerDispatch, dmnRunnerState.service]
  );

  // Debounce to avoid multiple updates caused by uniforms library
  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        const timeout = setTimeout(() => {
          updateDmnRunnerResults(dmnRunnerState.inputRows, canceled);
        }, 100);
        return () => {
          clearTimeout(timeout);
        };
      },
      [dmnRunnerState.inputRows, updateDmnRunnerResults]
    )
  );

  const openRow = useCallback(
    (rowIndex: number) => {
      dmnRunnerDispatch.setMode(DmnRunnerMode.FORM);
      dmnRunnerDispatch.setCurrentInputRowIndex(rowIndex);
      props.setPanelOpen(PanelId.NONE);
    },
    [dmnRunnerDispatch, props.setPanelOpen]
  );

  useElementsThatStopKeyboardEventsPropagation(
    window,
    useMemo(() => [".kie-tools--dmn-runner-table--drawer"], [])
  );

  return (
    <div style={{ height: "100%" }}>
      <DmnRunnerLoading>
        {dmnRunnerState.jsonSchema &&
          (dmnRunnerTableError ? (
            dmnRunnerTableError
          ) : (
            <ErrorBoundary
              ref={dmnRunnerTableErrorBoundaryRef}
              setHasError={setDmnRunnerTableError}
              error={<DmnRunnerTableError />}
            >
              <Drawer isInline={true} isExpanded={true} className={"kie-tools--dmn-runner-table--drawer"}>
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
                          <DmnTableResults
                            i18n={i18n.dmnRunner.table}
                            jsonSchemaBridge={jsonSchemaBridge}
                            rowCount={rowCount}
                            results={props.dmnRunnerResults}
                            onRowNumberUpdate={onRowNumberUpdate}
                          />
                        </div>
                      </DrawerPanelContent>
                    </>
                  }
                >
                  <Unitables
                    ref={unitablesRef}
                    name={"DMN Runner Table"}
                    i18n={i18n.dmnRunner.table}
                    jsonSchema={dmnRunnerState.jsonSchema}
                    rowCount={rowCount}
                    openRow={openRow}
                    inputRows={dmnRunnerState.inputRows}
                    setInputRows={dmnRunnerDispatch.setInputRows}
                    error={dmnRunnerState.error}
                    setError={dmnRunnerDispatch.setError}
                    jsonSchemaBridge={jsonSchemaBridge}
                    propertiesEntryPath={"definitions.InputSet"}
                    onRowNumberUpdate={onRowNumberUpdate}
                    inputsContainerRef={inputsContainerRef}
                  />
                </DrawerContent>
              </Drawer>
            </ErrorBoundary>
          ))}
      </DmnRunnerLoading>
    </div>
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

    const ADJUSTMENT_TO_HIDE_OUTPUTS_LINE_NUMBERS_IN_PX = 59;
    const newTotalWidth = (outputsTable as HTMLElement).offsetWidth - ADJUSTMENT_TO_HIDE_OUTPUTS_LINE_NUMBERS_IN_PX;
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
