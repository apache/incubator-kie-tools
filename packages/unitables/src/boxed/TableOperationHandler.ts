/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF object KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from "react";
import { useCallback, useEffect, useRef } from "react";
import { BeeTableOperation } from "@kie-tools/boxed-expression-component/dist/api";
import { UnitablesRowApi } from "../UnitablesRow";

export function useTableOperationHandler(
  inputRows: Array<object>,
  setInputRows: React.Dispatch<React.SetStateAction<Array<object>>>,
  defaultInputValues: Record<string, any>,
  rowsRef: Map<number, React.RefObject<UnitablesRowApi> | null>
) {
  const inputRowsCache = useRef<Array<object>>(inputRows.map((inputRow) => ({ ...defaultInputValues, ...inputRow })));
  useEffect(() => {
    inputRowsCache.current = inputRows.map((inputRow) => ({ ...defaultInputValues, ...inputRow }));
  }, [inputRowsCache, inputRows, defaultInputValues]);

  const operationHandler = useCallback(
    (tableOperation: BeeTableOperation, rowIndex: number) => {
      switch (tableOperation) {
        case BeeTableOperation.RowInsertAbove:
          setInputRows?.((previousInputs: Array<object>) => {
            const updatedInputs = [
              ...previousInputs.slice(0, rowIndex),
              { ...defaultInputValues },
              ...previousInputs.slice(rowIndex),
            ];
            inputRowsCache.current = updatedInputs;
            return updatedInputs;
          });
          break;
        case BeeTableOperation.RowInsertBelow:
          setInputRows?.((previousInputs: Array<object>) => {
            const updatedInputs = [
              ...previousInputs.slice(0, rowIndex + 1),
              { ...defaultInputValues },
              ...previousInputs.slice(rowIndex + 1),
            ];
            inputRowsCache.current = updatedInputs;
            return updatedInputs;
          });
          break;
        case BeeTableOperation.RowDelete:
          setInputRows?.((previousInputs: Array<object>) => {
            const updatedInputs = [...previousInputs.slice(0, rowIndex), ...previousInputs.slice(rowIndex + 1)];
            inputRowsCache.current = updatedInputs;
            return updatedInputs;
          });
          break;
        case BeeTableOperation.RowClear:
          setInputRows?.((previousInputs: Array<object>) => {
            const updatedInputs = [...previousInputs];
            updatedInputs[rowIndex] = { ...defaultInputValues };
            inputRowsCache.current = updatedInputs;
            return updatedInputs;
          });
          rowsRef.get(rowIndex)?.current?.reset(defaultInputValues);
          break;
        case BeeTableOperation.RowDuplicate:
          setInputRows?.((previousInputs: Array<object>) => {
            const updatedInputs = [
              ...previousInputs.slice(0, rowIndex + 1),
              previousInputs[rowIndex],
              ...previousInputs.slice(rowIndex + 1),
            ];
            inputRowsCache.current = updatedInputs;
            return updatedInputs;
          });
      }
    },
    [setInputRows, rowsRef, defaultInputValues, inputRowsCache]
  );

  return { inputRowsCache, operationHandler };
}
