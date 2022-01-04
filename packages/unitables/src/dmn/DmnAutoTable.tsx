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
import { TableOperation } from "@kogito-tooling/boxed-expression-component/dist/api";
import { ErrorBoundary } from "../common/ErrorBoundary";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { ExclamationIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-icon";
import { Text, TextContent } from "@patternfly/react-core/dist/js/components/Text";
import { DmnRunnerClause, DmnRunnerRule, DmnRunnerTable } from "../boxed";
import { NotificationSeverity } from "@kie-tooling-core/notifications/dist/api";
import { dmnAutoTableDictionaries, DmnAutoTableI18nContext, dmnAutoTableI18nDefaults } from "../i18n";
import { I18nDictionariesProvider } from "@kie-tooling-core/i18n/dist/react-components";
import nextId from "react-id-generator";
import { BoxedExpressionProvider } from "@kogito-tooling/boxed-expression-component/dist/components";
import { ColumnInstance } from "react-table";
import { Drawer, DrawerContent, DrawerPanelContent } from "@patternfly/react-core/dist/js/components/Drawer";
import { CubeIcon } from "@patternfly/react-icons/dist/js/icons/cube-icon";
import { Button } from "@patternfly/react-core";
import { ListIcon } from "@patternfly/react-icons/dist/js/icons/list-icon";
import "./style.css";
import { DmnAutoRowApi } from "./DmnAutoRow";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { useGrid } from "../core/Grid";
import { DmnSchema } from "@kogito-tooling/form/dist/dmn";

export enum EvaluationStatus {
  SUCCEEDED = "SUCCEEDED",
  SKIPPED = "SKIPPED",
  FAILED = "FAILED",
}

export interface DecisionResultMessage {
  severity: NotificationSeverity;
  message: string;
  messageType: string;
  sourceId: string;
  level: string;
}

export type Result = boolean | number | null | object | object[] | string;

export interface DecisionResult {
  decisionId: string;
  decisionName: string;
  result: Result;
  messages: DecisionResultMessage[];
  evaluationStatus: EvaluationStatus;
}

export interface DmnResult {
  details?: string;
  stack?: string;
  decisionResults?: DecisionResult[];
  messages: DecisionResultMessage[];
}

export type DmnSchemaProperties = { "x-dmn-type": string; type: string; $ref: string };

interface Props {
  jsonSchema: DmnSchema;
  inputRows: Array<object>;
  setInputRows: React.Dispatch<React.SetStateAction<Array<object>>>;
  results?: Array<DecisionResult[] | undefined>;
  error: boolean;
  setError: React.Dispatch<React.SetStateAction<boolean>>;
  openRow: (rowIndex: number) => void;
}

export const FORMS_ID = "unitables-forms";

export function DmnAutoTable(props: Props) {
  const inputErrorBoundaryRef = useRef<ErrorBoundary>(null);
  const outputErrorBoundaryRef = useRef<ErrorBoundary>(null);
  const dmnAutoTableErrorBoundaryRef = useRef<ErrorBoundary>(null);
  const [rowCount, setRowCount] = useState<number>(props.inputRows?.length ?? 1);
  const [outputError, setOutputError] = useState<boolean>(false);
  const [dmnAutoTableError, setDmnAutoTableError] = useState<boolean>(false);
  const [formsDivRendered, setFormsDivRendered] = useState<boolean>(false);
  const rowsRef = useMemo(() => new Map<number, React.RefObject<DmnAutoRowApi> | null>(), []);
  const inputColumnsCache = useRef<ColumnInstance[]>([]);
  const outputColumnsCache = useRef<ColumnInstance[]>([]);

  const getDefaultValueByType = useCallback((type, defaultValues: { [x: string]: any }, property: string) => {
    if (type === "object") {
      defaultValues[`${property}`] = {};
    }
    if (type === "array") {
      defaultValues[`${property}`] = [];
    }
    if (type === "boolean") {
      defaultValues[`${property}`] = false;
    }
    return defaultValues;
  }, []);

  const defaultValues = useMemo(
    () =>
      Object.keys(props.jsonSchema?.definitions?.InputSet?.properties ?? {}).reduce((defaultValues, property) => {
        if (Object.hasOwnProperty.call(props.jsonSchema?.definitions?.InputSet?.properties?.[property], "$ref")) {
          const refPath = props.jsonSchema?.definitions?.InputSet?.properties?.[property]?.$ref!.split("/").pop() ?? "";
          return getDefaultValueByType(props.jsonSchema?.definitions?.[refPath]?.type, defaultValues, property);
        }
        return getDefaultValueByType(
          props.jsonSchema?.definitions?.InputSet?.properties?.[property]?.type,
          defaultValues,
          property
        );
      }, {} as { [x: string]: any }),
    [getDefaultValueByType, props.jsonSchema?.definitions]
  );

  const defaultModel = useRef<Array<object>>(props.inputRows.map((inputRow) => ({ ...defaultValues, ...inputRow })));
  useEffect(() => {
    defaultModel.current = props.inputRows.map((inputRow) => ({ ...defaultValues, ...inputRow }));
  }, [defaultValues, props.inputRows]);

  const { jsonSchemaBridge, inputs, inputRules, outputs, outputRules, updateWidth } = useGrid(
    props.jsonSchema,
    props.results,
    props.inputRows,
    props.setInputRows,
    rowCount,
    formsDivRendered,
    rowsRef,
    inputColumnsCache,
    outputColumnsCache,
    defaultModel,
    defaultValues
  );

  const shouldRender = useMemo(() => {
    return (inputs?.length ?? 0) > 0;
  }, [inputs]);

  const handleOperation = useCallback(
    (tableOperation: TableOperation, rowIndex: number) => {
      switch (tableOperation) {
        case TableOperation.RowInsertAbove:
          props.setInputRows?.((previousData: any) => {
            const updatedData = [
              ...previousData.slice(0, rowIndex),
              { ...defaultValues },
              ...previousData.slice(rowIndex),
            ];
            defaultModel.current = updatedData;
            return updatedData;
          });
          break;
        case TableOperation.RowInsertBelow:
          props.setInputRows?.((previousData: any) => {
            const updatedData = [
              ...previousData.slice(0, rowIndex + 1),
              { ...defaultValues },
              ...previousData.slice(rowIndex + 1),
            ];
            defaultModel.current = updatedData;
            return updatedData;
          });
          break;
        case TableOperation.RowDelete:
          props.setInputRows?.((previousData: any) => {
            const updatedData = [...previousData.slice(0, rowIndex), ...previousData.slice(rowIndex + 1)];
            defaultModel.current = updatedData;
            return updatedData;
          });
          break;
        case TableOperation.RowClear:
          props.setInputRows?.((previousData: any) => {
            const updatedData = [...previousData];
            updatedData[rowIndex] = { ...defaultValues };
            defaultModel.current = updatedData;
            return updatedData;
          });
          rowsRef.get(rowIndex)?.current?.reset(defaultValues);
          break;
        case TableOperation.RowDuplicate:
          props.setInputRows?.((previousData: any) => {
            const updatedData = [
              ...previousData.slice(0, rowIndex + 1),
              previousData[rowIndex],
              ...previousData.slice(rowIndex + 1),
            ];
            defaultModel.current = updatedData;
            return updatedData;
          });
      }
    },
    [props.setInputRows, defaultValues]
  );

  const onRowNumberUpdated = useCallback(
    (rowQtt: number, operation?: TableOperation, rowIndex?: number) => {
      setRowCount(rowQtt);
      if (operation !== undefined && rowIndex !== undefined) {
        handleOperation(operation, rowIndex);
      }
    },
    [handleOperation]
  );

  // columns are saved in the grid instance, so some values can be used to improve re-renders (e.g. cell width)
  const onInputColumnsUpdate = useCallback(
    (columns: ColumnInstance[]) => {
      inputColumnsCache.current = columns;
      updateWidth(outputs);
    },
    [outputs, updateWidth]
  );

  const onOutputColumnsUpdate = useCallback(
    (columns: ColumnInstance[]) => {
      outputColumnsCache.current = columns;
      updateWidth(outputs);
    },
    [outputs, updateWidth]
  );

  const inputUid = useMemo(() => nextId(), []);
  const outputUid = useMemo(() => nextId(), []);

  // Resets the ErrorBoundary everytime the FormSchema is updated
  useEffect(() => {
    inputErrorBoundaryRef.current?.reset();
  }, [jsonSchemaBridge]);

  useEffect(() => {
    outputErrorBoundaryRef.current?.reset();
  }, [outputs]);

  useEffect(() => {
    dmnAutoTableErrorBoundaryRef.current?.reset();
  }, [jsonSchemaBridge, outputs]);

  const outputEntriesLength = useMemo(
    () => outputRules.reduce((length, rules) => length + (rules.outputEntries?.length ?? 0), 0),
    [outputRules]
  );

  const inputsContainerRef = useRef<HTMLDivElement>(null);
  const outputsContainerRef = useRef<HTMLDivElement>(null);

  const { drawerPanelDefaultSize, drawerPanelMinSize, drawerPanelMaxSize, forceDrawerPanelRefresh } =
    useAnchoredDmnRunnerTableDrawerPanel({
      inputsContainerRef,
      outputsContainerRef,
    });

  useEffect(() => {
    forceDrawerPanelRefresh();
  }, [forceDrawerPanelRefresh, inputRules, outputRules]);

  const updateDataWithModel = useCallback(
    (rowIndex: number) => {
      props.openRow(rowIndex);
    },
    [props.openRow]
  );

  return (
    <>
      {shouldRender && jsonSchemaBridge && inputRules && outputRules && (
        <I18nDictionariesProvider
          defaults={dmnAutoTableI18nDefaults}
          dictionaries={dmnAutoTableDictionaries}
          initialLocale={navigator.language}
          ctx={DmnAutoTableI18nContext}
        >
          {dmnAutoTableError ? (
            dmnAutoTableError
          ) : (
            <ErrorBoundary
              ref={dmnAutoTableErrorBoundaryRef}
              setHasError={setDmnAutoTableError}
              error={<DmnAutoTableError />}
            >
              <Drawer isInline={true} isExpanded={true} className={"unitables--dmn-runner-drawer"}>
                <DrawerContent
                  panelContent={
                    <>
                      <DrawerPanelContent
                        isResizable={true}
                        minSize={outputEntriesLength > 0 ? drawerPanelMinSize : "30%"}
                        maxSize={drawerPanelMaxSize}
                        defaultSize={drawerPanelDefaultSize}
                      >
                        <div ref={outputsContainerRef}>
                          {outputError ? (
                            outputError
                          ) : outputEntriesLength > 0 ? (
                            <ErrorBoundary
                              ref={outputErrorBoundaryRef}
                              setHasError={setOutputError}
                              error={<OutputError />}
                            >
                              <BoxedExpressionProvider
                                expressionDefinition={{}}
                                isRunnerTable={true}
                                decisionNodeId={outputUid}
                              >
                                <DmnRunnerTable
                                  name={"DMN Runner Output"}
                                  onRowNumberUpdated={onRowNumberUpdated}
                                  onColumnsUpdate={onOutputColumnsUpdate}
                                  output={outputs as DmnRunnerClause[]}
                                  rules={outputRules as DmnRunnerRule[]}
                                  id={outputUid}
                                />
                              </BoxedExpressionProvider>
                            </ErrorBoundary>
                          ) : (
                            <EmptyState>
                              <EmptyStateIcon icon={CubeIcon} />
                              <TextContent>
                                <Text component={"h2"}>Without Responses Yet</Text>
                              </TextContent>
                              <EmptyStateBody>
                                <TextContent>Add decision nodes and fill the input nodes!</TextContent>
                              </EmptyStateBody>
                            </EmptyState>
                          )}
                        </div>
                      </DrawerPanelContent>
                    </>
                  }
                >
                  <ErrorBoundary ref={inputErrorBoundaryRef} setHasError={props.setError} error={<InputError />}>
                    <BoxedExpressionProvider expressionDefinition={{}} isRunnerTable={true} decisionNodeId={inputUid}>
                      <div style={{ display: "flex" }} ref={inputsContainerRef}>
                        <div style={{ display: "flex", flexDirection: "column" }}>
                          <div style={{ width: "50px", height: "55px", border: "1px solid", visibility: "hidden" }}>
                            {" # "}
                          </div>
                          <div style={{ width: "50px", height: "56px", border: "1px solid", visibility: "hidden" }}>
                            {" # "}
                          </div>
                          {Array.from(Array(rowCount)).map((e, i) => (
                            <Tooltip key={i} content={`Open row ${i + 1} in the form view`}>
                              <div
                                style={{
                                  width: "50px",
                                  height: "62px",
                                  display: "flex",
                                  alignItems: "center",
                                  paddingTop: "8px",
                                }}
                              >
                                <Button
                                  className={"kogito-tooling--masthead-hoverable"}
                                  variant={ButtonVariant.plain}
                                  onClick={() => updateDataWithModel(i)}
                                >
                                  <ListIcon />
                                </Button>
                              </div>
                            </Tooltip>
                          ))}
                        </div>
                        <DmnRunnerTable
                          name={"DMN Runner Input"}
                          onRowNumberUpdated={onRowNumberUpdated}
                          onColumnsUpdate={onInputColumnsUpdate}
                          input={inputs}
                          rules={inputRules as DmnRunnerRule[]}
                          id={inputUid}
                        />
                      </div>
                    </BoxedExpressionProvider>
                  </ErrorBoundary>
                </DrawerContent>
              </Drawer>
            </ErrorBoundary>
          )}
        </I18nDictionariesProvider>
      )}
      <div ref={() => setFormsDivRendered(true)} id={FORMS_ID} />
    </>
  );
}

function InputError() {
  return (
    <div>
      <EmptyState>
        <EmptyStateIcon icon={ExclamationIcon} />
        <TextContent>
          <Text component={"h2"}>Error</Text>
        </TextContent>
        <EmptyStateBody>
          <p>An error has happened while trying to show your inputs</p>
        </EmptyStateBody>
      </EmptyState>
    </div>
  );
}

function OutputError() {
  return (
    <div>
      <EmptyState>
        <EmptyStateIcon icon={ExclamationIcon} />
        <TextContent>
          <Text component={"h2"}>Error</Text>
        </TextContent>
        <EmptyStateBody>
          <p>An error has happened while trying to show your outputs</p>
        </EmptyStateBody>
      </EmptyState>
    </div>
  );
}

function DmnAutoTableError() {
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

function useAnchoredDmnRunnerTableDrawerPanel(args: {
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
    const newWidth = children.reduce((acc, child: HTMLElement) => acc + child.offsetWidth, 0);
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
        ".unitables--dmn-runner-drawer .pf-c-drawer__panel .pf-c-drawer__splitter.pf-m-vertical"
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
    const content = document.querySelector(".unitables--dmn-runner-drawer .pf-c-drawer__content") as
      | HTMLElement
      | undefined;

    const panel = document.querySelector(".unitables--dmn-runner-drawer .pf-c-drawer__panel-main") as
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
