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
import { DecisionResult } from "@kie-tools/form-dmn";
import { PanelId } from "../EditorPageDockDrawer";
import { useElementsThatStopKeyboardEventsPropagation } from "@kie-tools-core/keyboard-shortcuts/dist/channel";
import { DmnRunnerLoading } from "./DmnRunnerLoading";
import { useCancelableEffect } from "@kie-tools-core/react-hooks/dist/useCancelableEffect";
import { Drawer, DrawerContent, DrawerPanelContent } from "@patternfly/react-core/dist/js/components/Drawer";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { ExclamationIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-icon";
import { Text, TextContent } from "@patternfly/react-core/dist/js/components/Text";
import { ErrorBoundary } from "@kie-tools/form";
import { useOnlineI18n } from "../../i18n";
import { Unitables } from "@kie-tools/unitables/dist/Unitables";
import { DmnRunnerOutputsTable } from "@kie-tools/unitables-dmn/dist/DmnRunnerOutputsTable";
import { DmnUnitablesValidator } from "@kie-tools/unitables-dmn/dist/DmnUnitablesValidator";
import { useExtendedServices } from "../../kieSandboxExtendedServices/KieSandboxExtendedServicesContext";

interface Props {
  setPanelOpen: React.Dispatch<React.SetStateAction<PanelId>>;
}

export function DmnRunnerTable({ setPanelOpen }: Props) {
  const extendedServices = useExtendedServices();
  const dmnRunnerState = useDmnRunnerState();
  const dmnRunnerDispatch = useDmnRunnerDispatch();
  const [dmnRunnerTableError, setDmnRunnerTableError] = useState<boolean>(false);
  const dmnRunnerTableErrorBoundaryRef = useRef<ErrorBoundary>(null);

  const { i18n } = useOnlineI18n();

  const rowCount = useMemo(() => {
    return dmnRunnerState.inputRows?.length ?? 1;
  }, [dmnRunnerState.inputRows?.length]);

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
  }, [forceDrawerPanelRefresh, dmnRunnerState.jsonSchema]);

  const openRow = useCallback(
    (rowIndex: number) => {
      dmnRunnerDispatch.setMode(DmnRunnerMode.FORM);
      dmnRunnerDispatch.setCurrentInputRowIndex(rowIndex);
      setPanelOpen(PanelId.NONE);
    },
    [dmnRunnerDispatch, setPanelOpen]
  );

  // FIXME: Tiago -> !
  // useElementsThatStopKeyboardEventsPropagation(
  //   window,
  //   useMemo(() => [".kie-tools--dmn-runner-table--drawer"], [])
  // );

  const [dmnRunnerResults, setDmnRunnerResults] = useState<Array<DecisionResult[] | undefined>>([]);
  useCancelableEffect(
    useCallback(
      ({ canceled }) => {

        Promise.all(dmnRunnerState.inputRows.map((data) => dmnRunnerDispatch.preparePayload(data)))
          .then((payloads) =>
            Promise.all(
              payloads.map((payload) => {
                if (canceled.get() || payload === undefined) {
                  return;
                }
                return extendedServices.client.result(payload);
              })
            )
          )
          .then((results) => {
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

            setDmnRunnerResults(runnerResults);
          });
      },
      [dmnRunnerDispatch, dmnRunnerState.inputRows, extendedServices.client]
    )
  );

  const inputsScrollableElementRef = useRef<{ current: HTMLDivElement | null }>({ current: null });
  const outputsScrollableElementRef = useRef<{ current: HTMLDivElement | null }>({ current: null });

  useEffect(() => {
    inputsScrollableElementRef.current.current =
      document.querySelector(".kie-tools--dmn-runner-table--drawer")?.querySelector(".pf-c-drawer__content") ?? null;
    outputsScrollableElementRef.current.current =
      document.querySelector(".kie-tools--dmn-runner-table--drawer")?.querySelector(".pf-c-drawer__panel-main") ?? null;
  }, []);

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
                            results={dmnRunnerResults}
                          />
                        </div>
                      </DrawerPanelContent>
                    </>
                  }
                >
                  {/* DMN Runner Inputs */}
                  <Unitables
                    scrollableParentRef={inputsScrollableElementRef.current}
                    i18n={i18n.dmnRunner.table}
                    jsonSchema={dmnRunnerState.jsonSchema}
                    openRow={openRow}
                    rows={dmnRunnerState.inputRows}
                    setRows={dmnRunnerDispatch.setInputRows}
                    error={dmnRunnerState.error}
                    setError={dmnRunnerDispatch.setError}
                    jsonSchemaBridge={jsonSchemaBridge}
                    propertiesEntryPath={"definitions.InputSet"}
                    containerRef={inputsContainerRef}
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

    const ADJUSTMENT_TO_HIDE_OUTPUTS_LINE_NUMBERS_IN_PX = 10;
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
