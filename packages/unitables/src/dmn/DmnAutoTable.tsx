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
import { TableOperation } from "@kie-tools/boxed-expression-component/dist/api";
import { ErrorBoundary } from "../common/ErrorBoundary";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { ExclamationIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-icon";
import { Text, TextContent } from "@patternfly/react-core/dist/js/components/Text";
import { CustomTable } from "../boxed";
import { NotificationSeverity } from "@kie-tools-core/notifications/dist/api";
import { dmnUnitablesDictionaries, dmnUnitablesI18n, DmnUnitablesI18nContext, dmnUnitablesI18nDefaults } from "../i18n";
import { I18nDictionariesProvider } from "@kie-tools-core/i18n/dist/react-components";
import nextId from "react-id-generator";
import { BoxedExpressionProvider } from "@kie-tools/boxed-expression-component/dist/components";
import { ColumnInstance } from "react-table";
import { Drawer, DrawerContent, DrawerPanelContent } from "@patternfly/react-core/dist/js/components/Drawer";
import { CubeIcon } from "@patternfly/react-icons/dist/js/icons/cube-icon";
import "./style.css";
import { DmnSchema, InputRow } from "@kie-tools/form-dmn";
import { UnitablesRowApi } from "../core/UnitablesRow";
import { BoxedExpressionOutputRule, UnitablesClause } from "../core/UnitablesBoxedTypes";
import { DmnValidator } from "./DmnValidator";
import { useGenerateBoxedOutputs } from "../core/BoxedOutputs";
import { useAnchoredUnitablesDrawerPanel } from "../core/DmnRunnerDrawerHooks";
import { Unitables } from "../core/Unitables";

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
  inputRows: Array<InputRow>;
  setInputRows: React.Dispatch<React.SetStateAction<Array<object>>>;
  results?: Array<DecisionResult[] | undefined>;
  error: boolean;
  setError: React.Dispatch<React.SetStateAction<boolean>>;
  openRow: (rowIndex: number) => void;
}

export function DmnAutoTable(props: Props) {
  const outputErrorBoundaryRef = useRef<ErrorBoundary>(null);
  const dmnAutoTableErrorBoundaryRef = useRef<ErrorBoundary>(null);
  const [rowCount, setRowCount] = useState<number>(props.inputRows?.length ?? 1);
  const [outputError, setOutputError] = useState<boolean>(false);
  const [dmnAutoTableError, setDmnAutoTableError] = useState<boolean>(false);
  const rowsRef = useMemo(() => new Map<number, React.RefObject<UnitablesRowApi> | null>(), []);
  const i18n = useMemo(() => {
    dmnUnitablesI18n.setLocale(dmnUnitablesI18nDefaults.locale ?? navigator.language);
    return dmnUnitablesI18n.getCurrent();
  }, []);

  const outputColumnsCache = useRef<ColumnInstance[]>([]);

  const validator = useMemo(() => {
    return new DmnValidator(i18n);
  }, [i18n]);

  const jsonSchemaBridge = useMemo(() => {
    return validator.getBridge(props.jsonSchema ?? {});
  }, [props.jsonSchema, validator]);

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

  const { outputs, outputRules, updateOutputCellsWidth } = useGenerateBoxedOutputs(
    jsonSchemaBridge,
    props.results,
    rowCount,
    outputColumnsCache
  );

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

  const onOutputColumnsUpdate = useCallback(
    (columns: ColumnInstance[]) => {
      outputColumnsCache.current = columns;
      updateOutputCellsWidth(outputs);
    },
    [outputs, updateOutputCellsWidth]
  );

  const outputUid = useMemo(() => nextId(), []);

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
    useAnchoredUnitablesDrawerPanel({
      inputsContainerRef,
      outputsContainerRef,
    });

  // useEffect(() => {
  //   forceDrawerPanelRefresh();
  // }, [forceDrawerPanelRefresh, inputRules, outputRules]);

  return (
    <>
      {jsonSchemaBridge && outputRules && (
        <I18nDictionariesProvider
          defaults={dmnUnitablesI18nDefaults}
          dictionaries={dmnUnitablesDictionaries}
          initialLocale={navigator.language}
          ctx={DmnUnitablesI18nContext}
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
                                dataTypes={[]}
                              >
                                <CustomTable
                                  name={"DMN Runner Output"}
                                  onRowNumberUpdated={onRowNumberUpdated}
                                  onColumnsUpdate={onOutputColumnsUpdate}
                                  output={outputs as UnitablesClause[]}
                                  rules={outputRules as BoxedExpressionOutputRule[]}
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
                  <Unitables
                    i18n={i18n}
                    openRow={props.openRow}
                    error={props.error}
                    inputRows={props.inputRows}
                    inputsContainerRef={inputsContainerRef}
                    jsonSchema={props.jsonSchema}
                    setError={props.setError}
                    setInputRows={props.setInputRows}
                    validator={validator}
                    name={"DMN Runner Table"}
                  />
                </DrawerContent>
              </Drawer>
            </ErrorBoundary>
          )}
        </I18nDictionariesProvider>
      )}
    </>
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
