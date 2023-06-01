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

import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { ListIcon } from "@patternfly/react-icons/dist/js/icons/list-icon";
import * as React from "react";
import { useCallback, useLayoutEffect, useMemo, useRef, useState } from "react";
import nextId from "react-id-generator";
import { UnitablesBeeTable } from "./bee";
import { UnitablesI18n } from "./i18n";
import { FORMS_ID, UnitablesJsonSchemaBridge } from "./uniforms";
import "./Unitables.css";
import { UnitablesRow, UnitablesRowApi } from "./UnitablesRow";
import setObjectValueByPath from "lodash/set";
import getObjectValueByPath from "lodash/get";
import { diff } from "deep-object-diff";
import cloneDeep from "lodash/cloneDeep";
import { useUnitablesContext } from "./UnitablesContext";
import { UnitablesInputsConfigs } from "./UnitablesTypes";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { Text, TextContent } from "@patternfly/react-core/dist/js/components/Text";
import { CubeIcon } from "@patternfly/react-icons/dist/js/icons/cube-icon";
import { usePreviousRef } from "@kie-tools-core/react-hooks/dist/usePreviousRef";

export interface UnitablesProps {
  rows: Array<Record<string, any>>;
  setRows: (
    previousStateFunction:
      | ((previous: Array<Record<string, any>>) => Array<Record<string, any>>)
      | Array<Record<string, any>>
  ) => void;
  error: boolean;
  setError: React.Dispatch<React.SetStateAction<boolean>>;
  openRow: (rowIndex: number) => void;
  i18n: UnitablesI18n;
  jsonSchemaBridge: UnitablesJsonSchemaBridge;
  scrollableParentRef: React.RefObject<HTMLElement>;
  onRowAdded: (args: { beforeIndex: number }) => void;
  onRowDuplicated: (args: { rowIndex: number }) => void;
  onRowReset: (args: { rowIndex: number }) => void;
  onRowDeleted: (args: { rowIndex: number }) => void;
  configs: UnitablesInputsConfigs;
  setWidth: (newWidth: number, fieldName: string) => void;
}

export const Unitables = ({
  rows,
  setRows,
  openRow,
  i18n,
  jsonSchemaBridge,
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
  const cachedColumnsRows = useRef<Map<number, Set<string>>>(new Map()); // create cache to save changed keys;
  const timeout = useRef<number | undefined>(undefined);
  const containerRef = useRef<HTMLDivElement>(null);
  const previousRows = usePreviousRef(rows);
  const cachedRows = usePreviousRef(cloneDeep(rows));

  // CUSTOM HOOKs
  const { isBeeTableChange, rowsRefs } = useUnitablesContext();

  const unitablesColumns = useMemo(() => jsonSchemaBridge.getUnitablesColumns(), [jsonSchemaBridge]);
  const inputUid = useMemo(() => nextId(), []);

  // Set in-cell input heights (begin)
  const searchRecursively = useCallback((child: HTMLElement) => {
    if (!child) {
      return;
    }
    if (child.tagName === "svg" || child.tagName === "path") {
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
      searchRecursively(inputCell.childNodes[0] as HTMLElement);
    });
  }, [isBeeTableChange, jsonSchemaBridge, formsDivRendered, rows, containerRef, searchRecursively]);
  // Set in-cell input heights (end)

  const onSubmitRow = useCallback(
    (inputRow: Record<string, any>, rowIndex: number, error: Record<string, any>) => {
      // Performing a form submit doesn't trigger a React batch update, causing performances issues;
      // This "if" handles this case, where it performs a fake batch update;
      if (isBeeTableChange.current) {
        if (timeout.current) {
          clearTimeout(timeout.current);
        }

        // After this method is not called by a period, perform an setRow with the cachedRows (aka. batch update);
        timeout.current = window.setTimeout(() => {
          if (cachedRows.current) {
            setRows(cloneDeep(cachedRows.current));
          }

          // clear the cache and reset the isBeeTableChange
          cachedColumnsRows.current.clear();
          isBeeTableChange.current = false;
        }, 0);

        // Using the previous rows values check for what values were changed;
        const changedValues: Record<string, any> = diff(inputRow, previousRows.current[rowIndex]);
        if (!changedValues) {
          return;
        }

        // Get the columns that were changed;
        const changedColumns = Object.entries(changedValues).flatMap(([columnName, columnValue]) => {
          if (columnValue !== null && typeof columnValue === "object") {
            return Object.keys(columnValue).map(
              (columnInsidePropertyName) => `${columnName}.${columnInsidePropertyName}`
            );
          }
          return columnName;
        });

        // Get the cached columns in the current row;
        let cachedColumns = cachedColumnsRows.current.get(rowIndex);
        if (!cachedColumns) {
          cachedColumns = new Set<string>();
          cachedColumnsRows.current.set(rowIndex, cachedColumns);
        }

        // Go through the changed columns and perform an update in the cachedRows with the new value (inpurRow)
        changedColumns.forEach((changedKey) => {
          if (!cachedColumns!.has(changedKey)) {
            setObjectValueByPath(cachedRows.current[rowIndex], changedKey, getObjectValueByPath(inputRow, changedKey));
            cachedColumns!.add(changedKey);
          }
        });
      } else {
        setRows((previousInputRows) => {
          const newInputRows = cloneDeep(previousInputRows);
          newInputRows[rowIndex] = inputRow;
          return newInputRows;
        });
      }
    },
    [cachedRows, isBeeTableChange, previousRows, setRows]
  );

  const saveRowRef = useCallback(
    (ref: UnitablesRowApi | null, rowIndex: number) => {
      if (ref) {
        rowsRefs.set(rowIndex, ref);
      }
    },
    [rowsRefs]
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
          ref={(ref) => saveRowRef(ref, rowIndex)}
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
    [jsonSchemaBridge, onSubmitRow, saveRowRef]
  );

  return (
    <>
      {unitablesColumns.length > 0 && rows.length > 0 && formsDivRendered ? (
        <div style={{ display: "flex" }} ref={containerRef}>
          <div
            className={"kie-tools--unitables-open-on-form-container"}
            style={{ display: "flex", flexDirection: "column" }}
          >
            <OutsideRowMenu height={63} isFirstChild={true}>{`#`}</OutsideRowMenu>
            <OutsideRowMenu height={65} borderBottomSizeBasis={1}>{`#`}</OutsideRowMenu>
            {rows.map((_, rowIndex) => (
              <Tooltip key={rowIndex} content={`Open row ${rowIndex + 1} in the form view`}>
                <OutsideRowMenu height={61} isLastChild={rowIndex === rows.length - 1}>
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
            bridge={jsonSchemaBridge}
          />
        </div>
      ) : (
        <EmptyUnitables />
      )}
      <div ref={() => setFormsDivRendered(true)} id={FORMS_ID} />
    </>
  );
};

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

function EmptyUnitables() {
  return (
    <div style={{ width: "50vw" }}>
      <EmptyState>
        <EmptyStateIcon icon={CubeIcon} />
        <TextContent>
          <Text component={"h2"}>No inputs node yet...</Text>
        </TextContent>
        <EmptyStateBody>
          <TextContent>Add an input node and see a custom table here.</TextContent>
        </EmptyStateBody>
      </EmptyState>
    </div>
  );
}
