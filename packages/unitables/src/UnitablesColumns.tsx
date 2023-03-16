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
import { useEffect, useMemo, useRef } from "react";
import { diff } from "deep-object-diff";
import { UnitablesJsonSchemaBridge } from "./uniforms";

export function usePrevious<T>(value: T) {
  const ref = useRef<T>();

  useEffect(() => {
    ref.current = value;
  }, [value]);

  return ref.current;
}

const getObjectByPath = (obj: Record<string, object | undefined> | undefined, path: string) => {
  return path.split(".").reduce((acc, key) => {
    return acc?.[key];
  }, obj);
};

export function useUnitablesColumns(
  jsonSchemaBridge: UnitablesJsonSchemaBridge,
  setRows: React.Dispatch<React.SetStateAction<object[]>>,
  propertiesEntryPath = "definitions"
) {
  // Check differences on schema and delete inputs from cells that were deleted.
  const previousBridge = usePrevious(jsonSchemaBridge);

  const defaultInputValues = useMemo(
    () =>
      Object.keys(jsonSchemaBridge?.schema?.properties ?? {}).reduce((acc, key) => {
        const field = jsonSchemaBridge.getField(key);
        if (field.default) {
          acc[key] = field.default;
        }
        return acc;
      }, {} as Record<string, any>),
    [jsonSchemaBridge]
  );

  // Removes or updates the rows after a bridge change;
  useEffect(() => {
    if (previousBridge === undefined) {
      return;
    }

    setRows((currentRows) => {
      const newRows = [...currentRows];
      const propertiesDifference = diff(
        getObjectByPath(previousBridge?.schema, propertiesEntryPath) ?? {},
        getObjectByPath(jsonSchemaBridge?.schema, propertiesEntryPath) ?? {}
      );

      const updatedData = newRows.map((row) => {
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
          { ...defaultInputValues, ...row }
        );
      });

      return updatedData;
    });
  }, [defaultInputValues, jsonSchemaBridge, previousBridge, propertiesEntryPath, setRows]);

  const unitablesColumns = useMemo(() => {
    return jsonSchemaBridge.getUnitablesColumns();
  }, [jsonSchemaBridge]);

  return useMemo(() => {
    return {
      jsonSchemaBridge,
      columns: unitablesColumns,
    };
  }, [unitablesColumns, jsonSchemaBridge]);
}
