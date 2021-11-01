import * as React from "react";
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { TableOperation } from "boxed-expression-component/src/api";
import { DmnValidator } from "./DmnValidator";
import { AutoRow } from "../core";
import { createPortal } from "react-dom";
import { context as UniformsContext } from "uniforms";
import { ErrorBoundary } from "../common/ErrorBoundary";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { ExclamationIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-icon";
import { Text, TextContent } from "@patternfly/react-core/dist/js/components/Text";
import { DmnGrid } from "./DmnGrid";
import { DmnRunnerRule, DmnRunnerTabular, DmnRunnerTabularProps } from "../boxed";
import { NotificationSeverity } from "@kogito-tooling/notifications/dist/api";
import { dmnAutoTableDictionaries, DmnAutoTableI18nContext, dmnAutoTableI18nDefaults } from "../i18n";
import { I18nDictionariesProvider } from "@kie-tooling-core/i18n/dist/react-components";
import nextId from "react-id-generator";
import { BoxedExpressionProvider } from "boxed-expression-component/src/components";
import { DmnTableJsonSchemaBridge } from "./DmnTableJsonSchemaBridge";
import { ColumnInstance } from "react-table";

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
  results: Array<DecisionResult[] | undefined>;
  formError: boolean;
  setFormError: React.Dispatch<any>;
}

const FORMS_ID = "unitable-forms";

let grid: DmnGrid | undefined;

export function DmnAutoTable(props: Props) {
  const errorBoundaryRef = useRef<ErrorBoundary>(null);

  const [rowQuantity, setRowQuantity] = useState<number>(1);
  const [formsDivRendered, setFormsDivRendered] = useState<boolean>(false);

  const bridge = useMemo(() => {
    return new DmnValidator().getBridge(props.schema ?? {});
  }, [props.schema]);

  // grid is a singleton
  grid = useMemo(() => {
    return bridge ? (grid ? grid : new DmnGrid(bridge)) : undefined;
  }, [bridge]);
  const shouldRender = useMemo(() => (grid?.getInput().length ?? 0) > 0, [grid]);

  // grid should be updated everytime the bridge is updated
  useEffect(() => {
    grid?.updateBridge(bridge);
  }, [bridge]);

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
    (data, rowIndex: number, bridge: DmnTableJsonSchemaBridge) =>
      ({ children }: any) =>
        (
          <AutoRow
            schema={bridge}
            autosave={true}
            autosaveDelay={500}
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
    [onSubmit, onValidate]
  );

  const [selectedExpression, setExpression] = useState<Partial<DmnRunnerTabularProps>>();
  useEffect(() => {
    const filteredResults = props.results?.filter((result) => result !== undefined);
    if (grid && filteredResults) {
      const [outputSet, outputEntries] = grid.generateBoxedOutputs(props.schema ?? {}, filteredResults);
      // generate output
      const output: any[] = Array.from(outputSet.values());

      // generate rules
      const rules: any[] = [];
      const inputEntriesLength = grid
        .getInput()
        .reduce((acc, i) => (i.insideProperties ? acc + i.insideProperties.length : acc + 1), 0);
      const inputEntries = new Array(inputEntriesLength);
      for (let i = 0; i < rowQuantity; i++) {
        const rule: DmnRunnerRule = {
          inputEntries,
          outputEntries: (outputEntries?.[i] as string[]) ?? [],
        };
        if (formsDivRendered) {
          rule.rowDelegate = getAutoRow(props.tableData[i], i, bridge);
        }
        rules.push(rule);
      }
      // clone without references an array that maybe contains an object
      output.forEach((o, i) => {
        const filteredOutputEntries = rules[i]?.outputEntries.filter(
          (outputEntry: any[]) => typeof outputEntry === "object"
        );
        if (filteredOutputEntries?.length > 0) {
          o.insideProperties = filteredOutputEntries?.reduce((acc: any[], outputEntry: any[]) => {
            if (Array.isArray(outputEntry)) {
              acc.push([...outputEntry]);
              return acc;
            }
            if (typeof outputEntry === "object") {
              acc.push(Object.assign({}, outputEntry));
              return acc;
            }
            return [...acc, outputEntry];
          }, []);
        }
      });
      grid?.updateWidth(output, rules);
      setExpression({
        input: grid.getInput(),
        output,
        rules,
        uid: selectedExpression?.uid ?? nextId(),
      });
    }
  }, [
    bridge,
    formsDivRendered,
    getAutoRow,
    grid,
    props.results,
    props.schema,
    props.tableData,
    rowQuantity,
    selectedExpression?.uid,
  ]);

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

  return (
    <>
      {shouldRender && bridge && selectedExpression && (
        <ErrorBoundary ref={errorBoundaryRef} setHasError={props.setFormError} error={formErrorMessage}>
          <I18nDictionariesProvider
            defaults={dmnAutoTableI18nDefaults}
            dictionaries={dmnAutoTableDictionaries}
            initialLocale={navigator.language}
            ctx={DmnAutoTableI18nContext}
          >
            <BoxedExpressionProvider expressionDefinition={selectedExpression} isRunnerTable={true}>
              <DmnRunnerTabular
                name={"DMN Runner"}
                onRowNumberUpdated={onRowNumberUpdated}
                onColumnsUpdate={onColumnsUpdate}
                {...selectedExpression}
              />
            </BoxedExpressionProvider>
          </I18nDictionariesProvider>
        </ErrorBoundary>
      )}
      <div ref={() => setFormsDivRendered(true)} id={FORMS_ID} />
    </>
  );
}
