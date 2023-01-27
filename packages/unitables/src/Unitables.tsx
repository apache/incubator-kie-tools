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

interface Props {
  jsonSchema: object;
  rows: object[];
  setRows: React.Dispatch<React.SetStateAction<object[]>>;
  error: boolean;
  setError: React.Dispatch<React.SetStateAction<boolean>>;
  openRow: (rowIndex: number) => void;
  i18n: UnitablesI18n;
  jsonSchemaBridge: UnitablesJsonSchemaBridge;
  propertiesEntryPath: string;
  containerRef: React.RefObject<HTMLDivElement>;
  scrollableParentRef: React.RefObject<HTMLElement>;
}

export const Unitables = ({
  jsonSchema,
  rows,
  setRows,
  error,
  setError,
  openRow,
  i18n,
  jsonSchemaBridge,
  propertiesEntryPath,
  containerRef,
  scrollableParentRef,
}: Props) => {
  const inputErrorBoundaryRef = useRef<ErrorBoundary>(null);

  const [formsDivRendered, setFormsDivRendered] = useState<boolean>(false);

  const { columns: unitablesColumns } = useUnitablesColumns(jsonSchemaBridge, rows, setRows, propertiesEntryPath);

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
  }, [formsDivRendered, rows, containerRef, searchRecursively]);
  // Set in-cell input heights (end)

  const onModelUpdate = useCallback(
    (model: object, index: number) => {
      console.info("updating model " + JSON.stringify(model));
      setRows?.((prev) => {
        const n = [...prev];
        n[index] = model;
        return n;
      });
    },
    [setRows]
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
        <UnitablesRowWrapper
          rowIndex={rowIndex}
          model={row}
          jsonSchemaBridge={jsonSchemaBridge}
          onModelUpdate={onModelUpdate}
        >
          {children}
        </UnitablesRowWrapper>
      );
    },
    [jsonSchemaBridge, onModelUpdate]
  );

  const beeTableRows = useMemo<ROWTYPE[]>(() => {
    return rows.map((row) => {
      return { id: generateUuid(), ...row };
    });
  }, [rows]);

  return (
    <>
      {unitablesColumns.length > 0 && rows.length > 0 && formsDivRendered && (
        <ErrorBoundary ref={inputErrorBoundaryRef} setHasError={setError} error={<InputError />}>
          <div style={{ display: "flex" }} ref={containerRef}>
            <div style={{ display: "flex", flexDirection: "column", marginTop: "5px", paddingLeft: "5px" }}>
              <OutsideRowMenu height={64} isFirstChild={true}>{`#`}</OutsideRowMenu>
              <OutsideRowMenu height={65} borderBottomSizeBasis={2}>{`#`}</OutsideRowMenu>
              {rows.map((e, rowIndex) => (
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
              rows={beeTableRows}
              columns={unitablesColumns}
              id={inputUid}
              setRows={setRows}
            />
          </div>
        </ErrorBoundary>
      )}

      <div ref={() => setFormsDivRendered(true)} id={FORMS_ID} />
    </>
  );
};

function UnitablesRowWrapper({
  children,
  rowIndex,
  model,
  jsonSchemaBridge,
  onModelUpdate,
}: React.PropsWithChildren<{
  rowIndex: number;
  model: object;
  jsonSchemaBridge: UnitablesJsonSchemaBridge;
  onModelUpdate: (model: object, index: number) => void;
}>) {
  console.info(model);
  return (
    <UnitablesRow
      key={rowIndex}
      formId={FORMS_ID}
      rowIndex={rowIndex}
      model={model}
      jsonSchemaBridge={jsonSchemaBridge}
      onModelUpdate={onModelUpdate}
    >
      {children}
    </UnitablesRow>
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
        height: `${height + (isFirstChild ? 3 : 0) + (isLastChild ? 2 : 0)}px`,
        display: "flex",
        fontSize: "14px",
        alignItems: "center",
        justifyContent: "center",
        borderBottom: `${isLastChild ? 3 : borderBottomSizeBasis}px solid lightgray`,
        borderTop: `${isFirstChild ? 3 : 0}px solid lightgray`,
        borderLeft: "3px solid lightgray",
      }}
    >
      {children}
    </div>
  );
}
