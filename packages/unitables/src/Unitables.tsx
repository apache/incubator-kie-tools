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

import { ErrorBoundary } from "@kie-tools/form/dist/ErrorBoundary";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { Text, TextContent } from "@patternfly/react-core/dist/js/components/Text";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { ExclamationIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-icon";
import { ListIcon } from "@patternfly/react-icons/dist/js/icons/list-icon";
import * as React from "react";
import { useCallback, useEffect, useLayoutEffect, useMemo, useRef, useState } from "react";
import nextId from "react-id-generator";
import { UnitablesBeeTable } from "./bee";
import { UnitablesI18n } from "./i18n";
import { FORMS_ID, UnitablesJsonSchemaBridge } from "./uniforms";
import "./Unitables.css";
import { UnitablesRow } from "./UnitablesRow";
import setObjectValueByPath from "lodash/set";
import getObjectValueByPath from "lodash/get";
import { InputRow } from "@kie-tools/form-dmn";
import { diff } from "deep-object-diff";
import cloneDeep from "lodash/cloneDeep";
import { useUnitablesContext } from "./UnitablesContext";
import { UnitablesInputsConfigs } from "./UnitablesTypes";

export interface UnitablesProps {
  jsonSchema: object;
  rows: object[];
  setRows: (previousStateFunction: (previous: Array<InputRow>) => Array<InputRow>) => void;
  error: boolean;
  setError: React.Dispatch<React.SetStateAction<boolean>>;
  openRow: (rowIndex: number) => void;
  i18n: UnitablesI18n;
  jsonSchemaBridge: UnitablesJsonSchemaBridge;
  containerRef: React.RefObject<HTMLDivElement>;
  scrollableParentRef: React.RefObject<HTMLElement>;
  onRowAdded: (args: { beforeIndex: number }) => void;
  onRowDuplicated: (args: { rowIndex: number }) => void;
  onRowReset: (args: { rowIndex: number }) => void;
  onRowDeleted: (args: { rowIndex: number }) => void;
  configs: UnitablesInputsConfigs;
  setWidth: (newWidth: number, fieldName: string) => void;
}

function isObject(item: any): item is Record<string, any> {
  return item && typeof item === "object" && !Array.isArray(item);
}

// should set the deep key that is going to be changed;
function recursiveCheckForChangedKey(
  rowIndex: number,
  previousInputRows: InputRow,
  newInputRow: InputRow,
  cachedKeysOfRows: Map<number, Set<string>>,
  changedProperties: Record<string, any>,
  parentKey?: string
) {
  for (const [key, value] of Object.entries(changedProperties)) {
    const fullKey: string = parentKey ? `${parentKey}.${key}` : key;
    if (isObject(value)) {
      recursiveCheckForChangedKey(rowIndex, previousInputRows, newInputRow, cachedKeysOfRows, value, fullKey);
    } else {
      const keySet = cachedKeysOfRows.get(rowIndex);
      if (keySet) {
        if (keySet.has(fullKey)) {
          // key shouldnt be updated;
          setObjectValueByPath(newInputRow, fullKey, getObjectValueByPath(previousInputRows, fullKey));
        } else {
          keySet.add(fullKey);
        }
      } else {
        cachedKeysOfRows.set(rowIndex, new Set([fullKey]));
      }
    }
  }
}

export const Unitables = ({
  rows,
  setRows,
  setError,
  openRow,
  i18n,
  jsonSchemaBridge,
  containerRef,
  scrollableParentRef,
  onRowAdded,
  onRowDuplicated,
  onRowReset,
  onRowDeleted,
  configs,
  setWidth,
}: UnitablesProps) => {
  // STATEs
  const [formsDivRendered, setFormsDivRendered] = useState<boolean>(false);

  // REFs
  const cachedKeysOfRows = useRef<Map<number, Set<string>>>(new Map()); // create cache to save changed keys;
  const inputErrorBoundaryRef = useRef<ErrorBoundary>(null);
  const timeout = useRef<number | undefined>(undefined);

  // CUSTOM HOOKs
  const { internalChange } = useUnitablesContext();

  const unitablesColumns = useMemo(() => jsonSchemaBridge.getUnitablesColumns(), [jsonSchemaBridge]);
  const inputUid = useMemo(() => nextId(), []);

  // Resets the ErrorBoundary everytime the FormSchema is updated
  useEffect(() => {
    inputErrorBoundaryRef.current?.reset();
  }, [jsonSchemaBridge]);

  // Set in-cell input heights (begin)
  const searchRecursively = useCallback((child: any) => {
    if (!child) {
      return;
    }
    if (child.tagName === "svg") {
      return;
    }
    if (child.style) {
      child.style.height = "60px";
    }
    if (!child.childNodes) {
      return;
    }
    child.childNodes.forEach(searchRecursively);
  }, []);

  useLayoutEffect(() => {
    const tbody = containerRef.current?.getElementsByTagName("tbody")[0];
    const inputsCells = Array.from(tbody?.getElementsByTagName("td") ?? []);
    inputsCells.shift();
    inputsCells.forEach((inputCell) => {
      searchRecursively(inputCell.childNodes[0]);
    });
  }, [internalChange, jsonSchemaBridge, formsDivRendered, rows, containerRef, searchRecursively]);
  // Set in-cell input heights (end)

  const onSubmitRow = useCallback(
    (inputRow: InputRow, rowIndex: number, error: Record<string, any>) => {
      // After this method is not called by a period, clear the cache and reset the internalChange;
      if (internalChange.current) {
        if (timeout.current) {
          clearTimeout(timeout.current);
        }

        timeout.current = window.setTimeout(() => {
          cachedKeysOfRows.current.clear();
          internalChange.current = false;
        }, 0);
      }

      // Before a cell is updated, the cell key is saved in a cache, and the new value is set;
      // if the same cell is edited before the cache reset, the used value will be the previous one;
      setRows((previousInputRows) => {
        const newInputRows = cloneDeep(previousInputRows);
        const newInputRow = cloneDeep(inputRow);

        if (internalChange.current) {
          const changedValues: Record<string, any> = diff(inputRow, newInputRows[rowIndex]);
          recursiveCheckForChangedKey(
            rowIndex,
            newInputRows[rowIndex],
            newInputRow,
            cachedKeysOfRows.current,
            changedValues
          );
        }

        newInputRows[rowIndex] = newInputRow;
        return newInputRows;
      });
    },
    [internalChange, setRows]
  );

  const rowWrapper = useCallback(
    ({
      children,
      rowIndex,
      row,
    }: React.PropsWithChildren<{
      rowIndex: number;
      row: object;
    }>) => {
      return (
        <UnitablesRow
          key={rowIndex}
          formsId={FORMS_ID}
          rowIndex={rowIndex}
          rowInput={row}
          jsonSchemaBridge={jsonSchemaBridge}
          onSubmitRow={onSubmitRow}
        >
          {children}
        </UnitablesRow>
      );
    },
    [jsonSchemaBridge, onSubmitRow]
  );

  return (
    <>
      {unitablesColumns.length > 0 && rows.length > 0 && formsDivRendered && (
        <ErrorBoundary ref={inputErrorBoundaryRef} setHasError={setError} error={<InputError />}>
          <div style={{ display: "flex" }} ref={containerRef}>
            <div
              className={"kie-tools--unitables-open-on-form-container"}
              style={{ display: "flex", flexDirection: "column" }}
            >
              <OutsideRowMenu height={63} isFirstChild={true}>{`#`}</OutsideRowMenu>
              <OutsideRowMenu height={64.2} borderBottomSizeBasis={1}>{`#`}</OutsideRowMenu>
              {rows.map((_, rowIndex) => (
                <Tooltip key={rowIndex} content={`Open row ${rowIndex + 1} in the form view`}>
                  <OutsideRowMenu height={60.8} isLastChild={rowIndex === rows.length - 1}>
                    <Button
                      className={"kie-tools--masthead-hoverable"}
                      variant={ButtonVariant.plain}
                      onClick={() => openRow(rowIndex)}
                    >
                      <ListIcon />
                    </Button>
                  </OutsideRowMenu>
                </Tooltip>
              ))}
            </div>
            <UnitablesBeeTable
              rowWrapper={rowWrapper}
              scrollableParentRef={scrollableParentRef}
              i18n={i18n}
              rows={rows}
              columns={unitablesColumns}
              id={inputUid}
              onRowAdded={onRowAdded}
              onRowDuplicated={onRowDuplicated}
              onRowReset={onRowReset}
              onRowDeleted={onRowDeleted}
              configs={configs}
              setWidth={setWidth}
            />
          </div>
        </ErrorBoundary>
      )}

      <div ref={() => setFormsDivRendered(true)} id={FORMS_ID} />
    </>
  );
};

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

function OutsideRowMenu({
  children,
  height,
  isLastChild = false,
  isFirstChild = false,
  borderBottomSizeBasis = 1,
}: React.PropsWithChildren<{
  height: number;
  isLastChild?: boolean;
  isFirstChild?: boolean;
  borderBottomSizeBasis?: number;
}>) {
  return (
    <div
      style={{
        width: "60px",
        height: `${height + (isFirstChild ? 3 : 0) + (isLastChild ? 1.6 : 0)}px`,
        minHeight: `${height + (isFirstChild ? 3 : 0) + (isLastChild ? 1.6 : 0)}px`,
        display: "flex",
        fontSize: "16px",
        color: "gray",
        alignItems: "center",
        justifyContent: "center",
        borderBottom: `${isLastChild ? 3 : borderBottomSizeBasis}px solid lightgray`,
        borderTop: `${isFirstChild ? 2 : 0}px solid lightgray`,
        borderLeft: "3px solid lightgray",
      }}
    >
      {children}
    </div>
  );
}
