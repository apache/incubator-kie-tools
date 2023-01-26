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
import { PropsWithChildren, useCallback, useEffect, useMemo, useRef } from "react";
import * as ReactTable from "react-table";
import { diff } from "deep-object-diff";
import { FORMS_ID, InputFields, isInputWithInsideProperties, UnitablesJsonSchemaBridge } from "./uniforms";
import { UnitablesRow, UnitablesRowApi } from "./UnitablesRow";
import { UnitablesInputRows } from "./UnitablesTypes";
import { UNITABLES_COLUMN_MIN_WIDTH } from "./bee";

export function usePrevious<T>(value: T) {
  const ref = useRef<T>();

  useEffect(() => {
    ref.current = value;
  }, [value]);

  return ref.current;
}

const getObjectByPath = (obj: Record<string, Record<string, object>>, path: string) =>
  path.split(".").reduce((acc: Record<string, Record<string, object>>, key: string) => acc[key], obj);

export function useUnitablesInputs(
  jsonSchemaBridge: UnitablesJsonSchemaBridge,
  inputRows: Array<object>,
  setInputRows: React.Dispatch<React.SetStateAction<Array<object>>>,
  rowCount: number,
  formsDivRendered: boolean,
  inputColumnsCache: React.MutableRefObject<ReactTable.Column[]>,
  propertiesEntryPath = "definitions"
) {
  const rowsRef = useMemo(() => new Map<number, React.RefObject<UnitablesRowApi> | null>(), []);
  const previousBridge = usePrevious(jsonSchemaBridge);

  const defaultInputValues = useMemo(
    () =>
      Object.keys(jsonSchemaBridge?.schema?.properties ?? {}).reduce((acc, property) => {
        const field = jsonSchemaBridge.getField(property);
        if (field.default) {
          acc[`${property}`] = field.default;
        }
        return acc;
      }, {} as Record<string, any>),
    [jsonSchemaBridge]
  );

  // Check differences on schema and delete inputs from cells that were deleted.
  useEffect(() => {
    if (previousBridge === undefined) {
      return;
    }
    setInputRows((inputRows) => {
      const newInputRows = [...inputRows];
      const propertiesDifference = diff(
        getObjectByPath(previousBridge?.schema ?? {}, propertiesEntryPath) ?? {},
        getObjectByPath(jsonSchemaBridge?.schema ?? {}, propertiesEntryPath) ?? {}
      );

      const updatedData = newInputRows.map((inputRow) => {
        return Object.entries(propertiesDifference).reduce(
          (row, [property, value]) => {
            if (Object.keys(row).length === 0) {
              return row;
            }
            if (!value || value.type || value.$ref) {
              delete row[property];
            }
            if (value?.format) {
              row[property] = undefined;
            }
            return row;
          },
          { ...defaultInputValues, ...inputRow }
        );
      });

      return updatedData;
    });
  }, [defaultInputValues, jsonSchemaBridge, previousBridge, setInputRows, propertiesEntryPath]);

  const updateInputCellsWidth = useCallback(
    (inputs: InputFields[]) => {
      inputColumnsCache.current?.map((inputColumn) => {
        if (inputColumn.groupType === "input") {
          const inputToUpdate = inputs.find((e) => e.name === inputColumn.label);
          if (inputToUpdate && isInputWithInsideProperties(inputToUpdate)) {
            if (inputColumn?.columns) {
              inputToUpdate.insideProperties.forEach((insideProperty) => {
                const columnFound = inputColumn.columns?.find(
                  (nestedColumn) => nestedColumn.label === insideProperty.name
                );
                if (columnFound && columnFound.width) {
                  insideProperty.width = columnFound.width;
                }
              });
            } else if (inputColumn.width) {
              inputToUpdate.insideProperties.forEach((insideProperty) => {
                const width = inputColumn.width ? inputColumn.width / inputToUpdate.insideProperties.length : 0;
                if (width < UNITABLES_COLUMN_MIN_WIDTH) {
                  insideProperty.width = UNITABLES_COLUMN_MIN_WIDTH;
                } else {
                  insideProperty.width = width;
                }
              });
            }
          }
          if (inputToUpdate && inputColumn.width && inputColumn.width > inputToUpdate.width) {
            inputToUpdate.width = inputColumn.width;
          }
        }
      });
    },
    [inputColumnsCache]
  );

  // Generate inputs header. Uses the input cache to update the input header cell width.
  const inputs = useMemo(() => {
    const newInputs = jsonSchemaBridge.getBoxedHeaderInputs();
    updateInputCellsWidth(newInputs);
    return newInputs;
  }, [jsonSchemaBridge, updateInputCellsWidth]);

  const onModelUpdate = useCallback(
    (model: object, index) => {
      setInputRows?.((prev) => {
        const n = [...prev];
        n[index] = model;
        return n;
      });
    },
    [setInputRows]
  );

  // Inputs form
  const inputRules: UnitablesInputRows[] = useMemo(() => {
    if (jsonSchemaBridge === undefined || !formsDivRendered) {
      return [] as UnitablesInputRows[];
    }
    const inputEntriesLength = inputs?.reduce(
      (length, input) => (isInputWithInsideProperties(input) ? length + input.insideProperties.length : length + 1),
      0
    );
    const inputEntries = Array.from(Array(inputEntriesLength));

    return Array.from(Array(rowCount)).map((e, rowIndex) => {
      return {
        inputEntries,
        rowDelegate: ({ children }: PropsWithChildren<{}>) => {
          const unitablesRowRef = React.createRef<UnitablesRowApi>();
          rowsRef.set(rowIndex, unitablesRowRef);
          return (
            <UnitablesRow
              key={rowIndex}
              ref={unitablesRowRef}
              formId={FORMS_ID}
              rowIndex={rowIndex}
              model={inputRows[rowIndex]}
              jsonSchemaBridge={jsonSchemaBridge}
              onModelUpdate={(model) => onModelUpdate(model, rowIndex)}
            >
              {children}
            </UnitablesRow>
          );
        },
      } as UnitablesInputRows;
    });
  }, [jsonSchemaBridge, formsDivRendered, inputs, rowCount, rowsRef, inputRows, onModelUpdate]);

  return useMemo(() => {
    return {
      jsonSchemaBridge,
      inputs,
      inputRows: inputRules,
      updateInputCellsWidth,
    };
  }, [inputRules, inputs, jsonSchemaBridge, updateInputCellsWidth]);
}
