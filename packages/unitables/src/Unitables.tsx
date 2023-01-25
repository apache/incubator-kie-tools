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

import { BeeTableOperation } from "@kie-tools/boxed-expression-component/dist/api";
import { ErrorBoundary } from "@kie-tools/form/dist/ErrorBoundary";
import { Button } from "@patternfly/react-core";
import { ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { Text, TextContent } from "@patternfly/react-core/dist/js/components/Text";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { ExclamationIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-icon";
import { ListIcon } from "@patternfly/react-icons/dist/js/icons/list-icon";
import * as React from "react";
import { useEffect, useImperativeHandle, useMemo, useRef, useState } from "react";
import nextId from "react-id-generator";
import * as ReactTable from "react-table";
import { BeeTableWrapper } from "./bee";
import { UnitablesI18n } from "./i18n";
import { FORMS_ID, UnitablesJsonSchemaBridge } from "./uniforms";
import { useUnitablesInputs } from "./UnitablesInputs";

export interface UnitablesApi {
  operationHandler: (tableOperation: BeeTableOperation, rowIndex: number) => void;
}

interface Props {
  jsonSchema: object;
  inputRows: Array<object>;
  setInputRows: React.Dispatch<React.SetStateAction<Array<object>>>;
  error: boolean;
  setError: React.Dispatch<React.SetStateAction<boolean>>;
  openRow: (rowIndex: number) => void;
  i18n: UnitablesI18n;
  rowCount: number;
  jsonSchemaBridge: UnitablesJsonSchemaBridge;
  propertiesEntryPath: string;
  onRowNumberUpdate: (rowQtt: number, operation?: BeeTableOperation, rowIndex?: number) => void;
  inputsContainerRef: React.RefObject<HTMLDivElement>;
}

export const Unitables = React.forwardRef<UnitablesApi, Props>((props, forwardRef) => {
  const inputErrorBoundaryRef = useRef<ErrorBoundary>(null);
  const [formsDivRendered, setFormsDivRendered] = useState<boolean>(false);
  const inputColumnsCache = useRef<ReactTable.Column[]>([]);

  const { inputs, inputRows, updateInputCellsWidth, operationHandler } = useUnitablesInputs(
    props.jsonSchemaBridge,
    props.inputRows,
    props.setInputRows,
    props.rowCount,
    formsDivRendered,
    inputColumnsCache,
    props.propertiesEntryPath
  );

  const inputUid = useMemo(() => nextId(), []);
  const shouldRender = useMemo(() => (inputs?.length ?? 0) > 0, [inputs]);

  // Resets the ErrorBoundary everytime the FormSchema is updated
  useEffect(() => {
    inputErrorBoundaryRef.current?.reset();
  }, [props.jsonSchemaBridge]);

  useImperativeHandle(forwardRef, () => ({ operationHandler }), [operationHandler]);

  const config = useMemo(
    () => ({
      type: "inputs" as const,
      inputs: inputs,
      rows: inputRows,
    }),
    [inputRows, inputs]
  );

  return (
    <>
      {inputs && shouldRender && inputRows && (
        <ErrorBoundary ref={inputErrorBoundaryRef} setHasError={props.setError} error={<InputError />}>
          <div style={{ display: "flex" }} ref={props.inputsContainerRef}>
            <div style={{ display: "flex", flexDirection: "column" }}>
              <div style={{ width: "50px", height: "55px", border: "1px solid", visibility: "hidden" }}>{" # "}</div>
              <div style={{ width: "50px", height: "56px", border: "1px solid", visibility: "hidden" }}>{" # "}</div>
              {Array.from(Array(props.rowCount)).map((e, rowIndex) => (
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
            <BeeTableWrapper i18n={props.i18n} config={config} id={inputUid} />
          </div>
        </ErrorBoundary>
      )}

      <div ref={() => setFormsDivRendered(true)} id={FORMS_ID} />
    </>
  );
});

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
