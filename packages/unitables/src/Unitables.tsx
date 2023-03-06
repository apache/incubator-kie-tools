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
import { Button } from "@patternfly/react-core";
import { ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { Text, TextContent } from "@patternfly/react-core/dist/js/components/Text";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { ExclamationIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-icon";
import { ListIcon } from "@patternfly/react-icons/dist/js/icons/list-icon";
import * as React from "react";
import { useCallback, useEffect, useLayoutEffect, useMemo, useRef, useState } from "react";
import nextId from "react-id-generator";
import { ROWTYPE, UnitablesBeeTable } from "./bee";
import { UnitablesI18n } from "./i18n";
import { FORMS_ID, UnitablesJsonSchemaBridge } from "./uniforms";
import { useUnitablesColumns } from "./UnitablesColumns";
import "./Unitables.css";
import { UnitablesRow } from "./UnitablesRow";
import { generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import isEqual from "lodash/isEqual";

const EMPTY_UNITABLES_INPUTS = [{}];

interface Props {
  jsonSchema: object;
  rows: object[];
  setInputRows: React.Dispatch<React.SetStateAction<object[]>>;
  error: boolean;
  setError: React.Dispatch<React.SetStateAction<boolean>>;
  openRow: (rowIndex: number) => void;
  i18n: UnitablesI18n;
  jsonSchemaBridge: UnitablesJsonSchemaBridge;
  propertiesEntryPath: string;
  containerRef: React.RefObject<HTMLDivElement>;
  scrollableParentRef: React.RefObject<HTMLElement>;
  onRowAdded: (args: { beforeIndex: number }) => void;
  onRowDuplicated: (args: { rowIndex: number }) => void;
  onRowReset: (args: { rowIndex: number }) => void;
  onRowDeleted: (args: { rowIndex: number }) => void;
}

export const Unitables = ({
  rows,
  setInputRows,
  setError,
  openRow,
  i18n,
  jsonSchemaBridge,
  propertiesEntryPath,
  containerRef,
  scrollableParentRef,
  onRowAdded,
  onRowDuplicated,
  onRowReset,
  onRowDeleted,
}: Props) => {
  const inputErrorBoundaryRef = useRef<ErrorBoundary>(null);
  const [formsDivRendered, setFormsDivRendered] = useState<boolean>(false);
  const { columns: unitablesColumns } = useUnitablesColumns(jsonSchemaBridge, setInputRows, propertiesEntryPath);
  const inputUid = useMemo(() => nextId(), []);
  const cachedRows = useRef<object[]>([...EMPTY_UNITABLES_INPUTS]);

  // Erase cache;
  useLayoutEffect(() => {
    if (isEqual(rows, EMPTY_UNITABLES_INPUTS)) {
      cachedRows.current = [...EMPTY_UNITABLES_INPUTS];
    }
  }, [rows]);

  const beeTableRows = useMemo<ROWTYPE[]>(() => {
    return rows.map((row) => {
      return { id: generateUuid(), ...row };
    });
  }, [rows]);

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
  }, [formsDivRendered, rows, containerRef, searchRecursively]);
  // Set in-cell input heights (end)

  // Perform a autosaveDelay and update all rows simultaneously;
  const timeout = useRef<number | undefined>(undefined);
  const onValidateRow = useCallback(
    (rowInput: object, rowIndex: number) => {
      // Save all rowInputs before timeout;
      cachedRows.current[rowIndex] = rowInput;

      // Debounce;
      if (timeout.current) {
        window.clearTimeout(timeout.current);
      }

      timeout.current = window.setTimeout(() => {
        // Update all rows if a value was changed;
        setInputRows?.((currentInputRows) => {
          // if cached length isn't equal to current a table event occured. e.g. add, delete;
          // if cached has the same value as current
          if (cachedRows.current.length !== currentInputRows.length || isEqual(cachedRows.current, currentInputRows)) {
            return currentInputRows;
          }
          return [...cachedRows.current];
        });
      }, 400); // autoSaveDelay
    },
    [setInputRows]
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
          onValidateRow={onValidateRow}
        >
          {children}
        </UnitablesRow>
      );
    },
    [jsonSchemaBridge, onValidateRow]
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
              {rows.map((e, rowIndex) => (
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
              rows={beeTableRows}
              columns={unitablesColumns}
              id={inputUid}
              onRowAdded={onRowAdded}
              onRowDuplicated={onRowDuplicated}
              onRowReset={onRowReset}
              onRowDeleted={onRowDeleted}
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
