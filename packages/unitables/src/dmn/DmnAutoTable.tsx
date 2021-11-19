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
import { PropsWithChildren, useCallback, useEffect, useMemo, useRef, useState } from "react";
import { TableOperation } from "@kogito-tooling/boxed-expression-component/dist/api";
import { DmnValidator } from "./DmnValidator";
import { ErrorBoundary } from "../common/ErrorBoundary";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { ExclamationIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-icon";
import { Text, TextContent } from "@patternfly/react-core/dist/js/components/Text";
import { DmnGrid } from "./DmnGrid";
import { DmnRunnerRule, DmnRunnerTabular } from "../boxed";
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
import { DmnAutoRow, DmnAutoRowApi } from "./DmnAutoRow";
import { diff } from "deep-object-diff";
import { DmnTableJsonSchemaBridge } from "./DmnTableJsonSchemaBridge";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
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

interface Props {
  jsonSchema: DmnSchema;
  inputRows: Array<object>;
  setInputRows: React.Dispatch<React.SetStateAction<Array<object>>>;
  results?: Array<DecisionResult[] | undefined>;
  error: boolean;
  setError: React.Dispatch<React.SetStateAction<boolean>>;
  openRow: (rowIndex: number) => void;
}

function usePrevious<T>(value: T) {
  const ref = useRef<T>();

  useEffect(() => {
    ref.current = value;
  }, [value]);

  return ref.current;
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

  const bridge = useMemo(() => {
    return new DmnValidator().getBridge(props.jsonSchema ?? {});
  }, [props.jsonSchema]);

  const grid = useRef<DmnGrid>();
  grid.current = bridge ? (grid.current ? grid.current : new DmnGrid(bridge)) : undefined;

  // grid should be updated everytime the bridge is updated
  const { input } = useMemo(() => {
    grid.current?.updateBridge(bridge);
    return { input: grid.current?.getInput() };
  }, [bridge]);

  const shouldRender = useMemo(() => (input?.length ?? 0) > 0, [input]);

  const handleOperation = useCallback(
    (tableOperation: TableOperation, rowIndex: number) => {
      switch (tableOperation) {
        case TableOperation.RowInsertAbove:
          props.setInputRows?.((previousData: any) => {
            const updatedData = [...previousData.slice(0, rowIndex), {}, ...previousData.slice(rowIndex)];
            return updatedData;
          });
          break;
        case TableOperation.RowInsertBelow:
          props.setInputRows?.((previousData: any) => {
            const updatedData = [...previousData.slice(0, rowIndex + 1), {}, ...previousData.slice(rowIndex + 1)];
            return updatedData;
          });
          break;
        case TableOperation.RowDelete:
          props.setInputRows?.((previousData: any) => {
            const updatedData = [...previousData.slice(0, rowIndex), ...previousData.slice(rowIndex + 1)];
            return updatedData;
          });
          break;
        case TableOperation.RowClear:
          props.setInputRows?.((previousData: any) => {
            const updatedData = [...previousData];
            updatedData[rowIndex] = {};
            return updatedData;
          });
          rowsRef.get(rowIndex)?.current?.reset();
          break;
        case TableOperation.RowDuplicate:
          props.setInputRows?.((previousData: any) => {
            const updatedData = [
              ...previousData.slice(0, rowIndex + 1),
              previousData[rowIndex],
              ...previousData.slice(rowIndex + 1),
            ];
            return updatedData;
          });
      }
    },
    [props.setInputRows]
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

  const onModelUpdate = useCallback((model: any, index) => {
    props.setInputRows?.((previousData) => {
      const newData = [...previousData];
      newData[index] = model;
      return newData;
    });
  }, []);

  // Compare bridge with previousBridge and erase data from deleted input nodes
  const previousBridge: DmnTableJsonSchemaBridge | undefined = usePrevious(bridge);
  useEffect(() => {
    props.setInputRows((previousModelData: any) => {
      if (previousBridge === undefined) {
        return previousModelData;
      }
      const newModelData = [...previousModelData];
      const propertiesDifference = diff(
        ((previousBridge as DmnTableJsonSchemaBridge | undefined)?.schema ?? {}).definitions?.InputSet?.properties ??
          {},
        bridge.schema?.definitions?.InputSet?.properties ?? {}
      );

      return newModelData.map((modelData) => {
        return Object.entries(propertiesDifference).reduce(
          (row, [property, value]) => {
            if (Object.keys(row).length === 0) {
              return row;
            }
            if (!value || value.type || value.$ref) {
              delete (row as any)[property];
            }
            if (value?.["x-dmn-type"]) {
              (row as any)[property] = undefined;
            }
            return row;
          },
          { ...modelData }
        );
      });
    });
  }, [bridge, previousBridge]);

  const inputUid = useMemo(() => nextId(), []);
  const inputRules: Partial<DmnRunnerRule>[] = useMemo(() => {
    if (input && formsDivRendered) {
      const inputEntriesLength = input.reduce(
        (acc, i) => (i.insideProperties ? acc + i.insideProperties.length : acc + 1),
        0
      );
      const inputEntries = Array.from(Array(inputEntriesLength));
      return Array.from(Array(rowCount)).map((e, rowIndex) => {
        return {
          inputEntries,
          rowDelegate: ({ children }: PropsWithChildren<any>) => {
            const dmnAutoRowRef = React.createRef<DmnAutoRowApi>();
            rowsRef.set(rowIndex, dmnAutoRowRef);
            return (
              <DmnAutoRow
                ref={dmnAutoRowRef}
                formId={FORMS_ID}
                rowIndex={rowIndex}
                model={props.inputRows[rowIndex]}
                jsonSchemaBridge={bridge}
                onModelUpdate={(model) => onModelUpdate(model, rowIndex)}
              >
                {children}
              </DmnAutoRow>
            );
          },
        } as Partial<DmnRunnerRule>;
      });
    }
    return [] as Partial<DmnRunnerRule>[];
  }, [input, formsDivRendered, rowCount]);

  const outputUid = useMemo(() => nextId(), []);
  const { output, rules: outputRules } = useMemo(() => {
    const filteredResults = props.results?.filter((result) => result !== undefined);
    if (grid.current && filteredResults) {
      const [outputSet, outputEntries] = grid.current.generateBoxedOutputs(filteredResults);
      const output: any[] = Array.from(outputSet.values());

      const rules: Partial<DmnRunnerRule>[] = Array.from(Array(rowCount)).map((e, i) => ({
        outputEntries: (outputEntries?.[i] as string[]) ?? [],
      }));
      grid.current?.updateWidth(output);
      return {
        output,
        rules,
      };
    }
    return { output: [], rules: [] };
  }, [rowCount, props.results]);

  // columns are saved in the grid instance, so some values can be used to improve re-renders (e.g. cell width)
  const onColumnsUpdate = useCallback(
    (columns: ColumnInstance[]) => {
      grid.current?.setPreviousColumns(columns);
      grid.current?.updateWidth(output);
    },
    [output]
  );

  // Resets the ErrorBoundary everytime the FormSchema is updated
  useEffect(() => {
    inputErrorBoundaryRef.current?.reset();
  }, [bridge]);

  useEffect(() => {
    outputErrorBoundaryRef.current?.reset();
  }, [output]);

  useEffect(() => {
    dmnAutoTableErrorBoundaryRef.current?.reset();
  }, [bridge, output]);

  const outputEntries = useMemo(
    () => outputRules.reduce((acc, rules) => acc + (rules.outputEntries?.length ?? 0), 0),
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
      // props.setInputRows?.([...rowsModel]);
      // rowsRef.get(rowIndex)?.current?.submit();
      props.openRow(rowIndex);
    },
    [props.setInputRows, props.openRow]
  );

  return (
    <>
      {shouldRender && bridge && inputRules && outputRules && (
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
                        minSize={outputEntries > 0 ? drawerPanelMinSize : "30%"}
                        maxSize={drawerPanelMaxSize}
                        defaultSize={drawerPanelDefaultSize}
                      >
                        <div ref={outputsContainerRef}>
                          {outputError ? (
                            outputError
                          ) : outputEntries > 0 ? (
                            <ErrorBoundary
                              ref={outputErrorBoundaryRef}
                              setHasError={setOutputError}
                              error={<OutputError />}
                            >
                              <BoxedExpressionProvider expressionDefinition={{ uid: outputUid }} isRunnerTable={true}>
                                <DmnRunnerTabular
                                  name={"DMN Runner Output"}
                                  onRowNumberUpdated={onRowNumberUpdated}
                                  onColumnsUpdate={onColumnsUpdate}
                                  output={output}
                                  rules={outputRules as DmnRunnerRule[]}
                                  uid={outputUid}
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
                    <BoxedExpressionProvider expressionDefinition={{ uid: inputUid }} isRunnerTable={true}>
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
                        <DmnRunnerTabular
                          name={"DMN Runner Input"}
                          onRowNumberUpdated={onRowNumberUpdated}
                          onColumnsUpdate={onColumnsUpdate}
                          input={input}
                          rules={inputRules as DmnRunnerRule[]}
                          uid={inputUid}
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
