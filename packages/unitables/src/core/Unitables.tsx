/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
import { ErrorBoundary } from "../common/ErrorBoundary";
import { UnitablesRowApi } from "./UnitablesRow";
import { UnitablesI18n } from "../i18n";
import { ColumnInstance } from "react-table";
import { useUnitablesInputs } from "./UnitablesInputs";
import { BoxedExpressionProvider, TableOperation } from "@kie-tools/boxed-expression-component";
import nextId from "react-id-generator";
import { CustomTable } from "../boxed";
import { UnitablesInputRule } from "./UnitablesBoxedTypes";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { Text, TextContent } from "@patternfly/react-core/dist/js/components/Text";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { Button } from "@patternfly/react-core";
import { ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { ListIcon } from "@patternfly/react-icons/dist/js/icons/list-icon";
import { FORMS_ID } from "./UnitablesJsonSchemaBridge";
import { ExclamationIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-icon";
import { Validator } from "./Validator";

interface Props {
  jsonSchema: any;
  inputRows: Array<object>;
  setInputRows: React.Dispatch<React.SetStateAction<Array<object>>>;
  error: boolean;
  setError: React.Dispatch<React.SetStateAction<boolean>>;
  openRow: (rowIndex: number) => void;
  i18n: UnitablesI18n;
  inputsContainerRef: React.RefObject<HTMLDivElement>;
  validator: Validator;
  name?: string;
}

export function Unitables(props: Props) {
  const inputErrorBoundaryRef = useRef<ErrorBoundary>(null);
  const [rowCount, setRowCount] = useState<number>(props.inputRows?.length ?? 1);
  const [formsDivRendered, setFormsDivRendered] = useState<boolean>(false);
  const rowsRef = useMemo(() => new Map<number, React.RefObject<UnitablesRowApi> | null>(), []);
  const inputColumnsCache = useRef<ColumnInstance[]>([]);

  const jsonSchemaBridge = useMemo(
    () => (props?.validator ?? new Validator(props.i18n)).getBridge(props.jsonSchema ?? {}),
    [props.i18n, props.jsonSchema, props.validator]
  );

  // TODO: bridge
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

  // TODO: bridge
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

  const { inputs, inputRules, updateInputCellsWidth } = useUnitablesInputs(
    jsonSchemaBridge,
    props.inputRows,
    props.setInputRows,
    rowCount,
    formsDivRendered,
    rowsRef,
    inputColumnsCache,
    defaultModel,
    defaultValues
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

  // columns are saved in the grid instance, so some values can be used to improve re-renders (e.g. cell width)
  const onInputColumnsUpdate = useCallback(
    (columns: ColumnInstance[]) => {
      inputColumnsCache.current = columns;
      updateInputCellsWidth(inputs);
    },
    [inputs, updateInputCellsWidth]
  );

  const inputUid = useMemo(() => nextId(), []);

  const shouldRender = useMemo(() => (inputs?.length ?? 0) > 0, [inputs]);

  // Resets the ErrorBoundary everytime the FormSchema is updated
  useEffect(() => {
    inputErrorBoundaryRef.current?.reset();
  }, [jsonSchemaBridge]);

  return (
    <>
      {shouldRender && inputRules && (
        <ErrorBoundary ref={inputErrorBoundaryRef} setHasError={props.setError} error={<InputError />}>
          <BoxedExpressionProvider
            expressionDefinition={{}}
            isRunnerTable={true}
            decisionNodeId={inputUid}
            dataTypes={[]}
          >
            <div style={{ display: "flex" }} ref={props.inputsContainerRef}>
              <div style={{ display: "flex", flexDirection: "column" }}>
                <div style={{ width: "50px", height: "55px", border: "1px solid", visibility: "hidden" }}>{" # "}</div>
                <div style={{ width: "50px", height: "56px", border: "1px solid", visibility: "hidden" }}>{" # "}</div>
                {Array.from(Array(rowCount)).map((e, rowIndex) => (
                  <Tooltip key={rowIndex} content={`Open row ${rowIndex + 1} in the form view`}>
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
                        className={"kie-tools--masthead-hoverable"}
                        variant={ButtonVariant.plain}
                        onClick={() => props.openRow(rowIndex)}
                      >
                        <ListIcon />
                      </Button>
                    </div>
                  </Tooltip>
                ))}
              </div>
              <CustomTable
                name={props?.name ?? ""}
                onRowNumberUpdated={onRowNumberUpdated}
                onColumnsUpdate={onInputColumnsUpdate}
                input={inputs}
                rules={inputRules as UnitablesInputRule[]}
                id={inputUid}
              />
            </div>
          </BoxedExpressionProvider>
        </ErrorBoundary>
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
