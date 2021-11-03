import * as React from "react";
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { TableOperation } from "@kogito-tooling/boxed-expression-component/dist/api";
import { DmnValidator } from "./DmnValidator";
import { AutoRow } from "../core";
import { createPortal } from "react-dom";
import { context as UniformsContext } from "uniforms";
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
  schema: any;
  tableData?: any;
  setTableData?: React.Dispatch<React.SetStateAction<any>>;
  results?: Array<DecisionResult[] | undefined>;
  formError: boolean;
  setFormError: React.Dispatch<any>;
  openRowOnForm: (rowIndex: number) => void;
}

const FORMS_ID = "unitable-forms";

let grid: DmnGrid | undefined;

export function DmnAutoTable(props: Props) {
  const errorBoundaryRef = useRef<ErrorBoundary>(null);
  const [rowQuantity, setRowQuantity] = useState<number>(props.tableData?.length ?? 1);
  const [formsDivRendered, setFormsDivRendered] = useState<boolean>(false);

  const bridge = useMemo(() => {
    return new DmnValidator().getBridge(props.schema ?? {});
  }, [props.schema]);

  // grid is a singleton
  grid = useMemo(() => {
    return bridge ? (grid ? grid : new DmnGrid(bridge)) : undefined;
  }, [bridge]);

  // grid should be updated everytime the bridge is updated
  const { input } = useMemo(() => {
    grid?.updateBridge(bridge);
    return { input: grid?.getInput() };
  }, [bridge]);

  const shouldRender = useMemo(() => (input?.length ?? 0) > 0, [input]);

  // columns are saved in the grid instance, so some values can be used to improve re-renders (e.g. cell width)
  const onColumnsUpdate = useCallback((columns: ColumnInstance[]) => {
    grid?.setPreviousColumns(columns);
  }, []);

  const handleOperation = useCallback(
    (tableOperation: TableOperation, rowIndex: number) => {
      switch (tableOperation) {
        case TableOperation.RowInsertAbove:
          props.setTableData?.((previousTableData: any) => {
            return [...previousTableData.slice(0, rowIndex), {}, ...previousTableData.slice(rowIndex)];
          });
          break;
        case TableOperation.RowInsertBelow:
          props.setTableData?.((previousTableData: any) => {
            return [...previousTableData.slice(0, rowIndex + 1), {}, ...previousTableData.slice(rowIndex + 1)];
          });
          break;
        case TableOperation.RowDelete:
          props.setTableData?.((previousTableData: any) => {
            return [...previousTableData.slice(0, rowIndex), ...previousTableData.slice(rowIndex + 1)];
          });
          break;
        case TableOperation.RowClear:
          props.setTableData?.((previousTableData: any) => {
            const newTableData = [...previousTableData];
            newTableData[rowIndex] = {};
            return newTableData;
          });
          break;
        case TableOperation.RowDuplicate:
          props.setTableData?.((previousTableData: any) => {
            return [
              ...previousTableData.slice(0, rowIndex + 1),
              previousTableData[rowIndex],
              ...previousTableData.slice(rowIndex + 1),
            ];
          });
      }
    },
    [props.setTableData]
  );

  const onRowNumberUpdated = useCallback(
    (rowQtt: number, operation?: TableOperation, rowIndex?: number) => {
      setRowQuantity(rowQtt);
      if (operation !== undefined && rowIndex !== undefined) {
        handleOperation(operation, rowIndex);
      }
    },
    [handleOperation]
  );

  const onSubmit = useCallback(
    (model: any, index) => {
      props.setTableData?.((previousTableData: any) => {
        const newTableData = [...previousTableData];
        newTableData[index] = model;
        return newTableData;
      });
    },
    [props.setTableData]
  );

  const onValidate = useCallback(
    (model: any, error: any, index) => {
      props.setTableData?.((previousTableData: any) => {
        const newTableData = [...previousTableData];
        newTableData[index] = model;
        return newTableData;
      });
    },
    [props.setTableData]
  );

  // every input row is managed by an AutoRow. Each row is a form, and inside of it, cell are auto generated
  // using the uniforms library
  const getAutoRow = useCallback(
    (data, rowIndex: number) =>
      ({ children }: any) =>
        (
          <AutoRow
            schema={bridge}
            autosave={true}
            autosaveDelay={1000}
            model={data}
            onSubmit={(model: any) => onSubmit(model, rowIndex)}
            onValidate={(model: any, error: any) => onValidate(model, error, rowIndex)}
            placeholder={true}
          >
            <UniformsContext.Consumer>
              {(ctx: any) => (
                <>
                  {createPortal(
                    <form id={`dmn-auto-form-${rowIndex}`} onSubmit={(data) => ctx?.onSubmit(data)} />,
                    document.getElementById(FORMS_ID)!
                  )}
                  {children}
                </>
              )}
            </UniformsContext.Consumer>
          </AutoRow>
        ),
    [bridge, onSubmit, onValidate]
  );

  const inputUid = useMemo(() => nextId(), []);
  const inputRules: Partial<DmnRunnerRule>[] = useMemo(() => {
    if (input && formsDivRendered) {
      const inputEntriesLength = input.reduce(
        (acc, i) => (i.insideProperties ? acc + i.insideProperties.length : acc + 1),
        0
      );
      //       const inputEntries = new Array(inputEntriesLength + 1);
      const inputEntries = new Array(inputEntriesLength);
      return Array.from(Array(rowQuantity)).map((e, i) => {
        return {
          inputEntries,
          rowDelegate: getAutoRow(props.tableData[i], i),
        } as Partial<DmnRunnerRule>;
      });
    }
    return [] as Partial<DmnRunnerRule>[];
  }, [input, formsDivRendered, getAutoRow, props.tableData, rowQuantity]);

  const outputUid = useMemo(() => nextId(), []);
  const { output, rules: outputRules } = useMemo(() => {
    const filteredResults = props.results?.filter((result) => result !== undefined);
    if (grid && filteredResults) {
      const [outputSet, outputEntries] = grid.generateBoxedOutputs(filteredResults);
      const output: any[] = Array.from(outputSet.values());

      const rules: Partial<DmnRunnerRule>[] = Array.from(Array(rowQuantity)).map((e, i) => ({
        outputEntries: (outputEntries?.[i] as string[]) ?? [],
      }));
      // remove references

      // outputEntries.map((outputEntry, rowIndex) => {
      //   // [value, value, null]
      //   // [value, value, obj]
      //   outputEntry.map((response, responseIndex) => {
      //     if (Array.isArray(response)) {
      //       output[responseIndex] = Object.assign({}, response)
      //     }
      //     if (response !== null && typeof response === "object") {
      //       output[responseIndex] = Object.assign({}, response)
      //     }
      //   })
      // })
      //
      // // iterar sobre todos os outputEntries, procurando um objeto.
      // // se encotnrar, adicionar como inside properties, no mesmo index dos outputs
      // // caso o valor seja null, adicionar null para todas as entradas
      // output.forEach((o, i) => {
      //   o.insideProperties = outputEntries[i];
      //
      //   const filteredOutputEntries = rules[i]?.outputEntries?.filter((outputEntry) => typeof outputEntry === "object");
      //   if (filteredOutputEntries?.length ?? 0 > 0) {
      //     o.insideProperties = filteredOutputEntries?.reduce((acc: any[], outputEntry) => {
      //       if (Array.isArray(outputEntry)) {
      //         acc.push([...outputEntry]);
      //         return acc;
      //       }
      //       if (typeof outputEntry === "object") {
      //         acc.push(Object.assign({}, outputEntry));
      //         return acc;
      //       }
      //       return [...acc, outputEntry];
      //     }, []);
      //   }
      // });
      grid?.updateWidth(output, rules);
      return {
        output,
        rules,
      };
    }
    return { output: [], rules: [] };
  }, [rowQuantity, props.results]);

  const formErrorMessage = useMemo(
    () => (
      <div>
        <EmptyState>
          <EmptyStateIcon icon={ExclamationIcon} />
          <TextContent>
            <Text component={"h2"}>Error</Text>
          </TextContent>
          <EmptyStateBody>
            <p>An error has happened</p>
          </EmptyStateBody>
        </EmptyState>
      </div>
    ),
    []
  );

  // Resets the ErrorBoundary everytime the FormSchema is updated
  useEffect(() => {
    errorBoundaryRef.current?.reset();
  }, [bridge]);

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

  return (
    <>
      {shouldRender && bridge && inputRules && outputRules && (
        <I18nDictionariesProvider
          defaults={dmnAutoTableI18nDefaults}
          dictionaries={dmnAutoTableDictionaries}
          initialLocale={navigator.language}
          ctx={DmnAutoTableI18nContext}
        >
          <ErrorBoundary ref={errorBoundaryRef} setHasError={props.setFormError} error={formErrorMessage}>
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
                        {outputEntries > 0 ? (
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
                <BoxedExpressionProvider expressionDefinition={{ uid: inputUid }} isRunnerTable={true}>
                  <div style={{ display: "flex" }} ref={inputsContainerRef}>
                    <div style={{ display: "flex", flexDirection: "column" }}>
                      <div style={{ width: "50px", height: "55px", border: "1px solid", visibility: "hidden" }}>
                        {" "}
                        #{" "}
                      </div>
                      <div style={{ width: "50px", height: "56px", border: "1px solid", visibility: "hidden" }}>
                        {" "}
                        #{" "}
                      </div>
                      {Array.from(Array(rowQuantity)).map((e, i) => (
                        <div key={i} style={{ width: "50px", height: "62px", display: "flex", alignItems: "center" }}>
                          <Button variant={"plain"} onClick={() => props.openRowOnForm(i)}>
                            <ListIcon />
                          </Button>
                        </div>
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
              </DrawerContent>
            </Drawer>
          </ErrorBoundary>
        </I18nDictionariesProvider>
      )}
      <div ref={() => setFormsDivRendered(true)} id={FORMS_ID} />
    </>
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
