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
import { PropsWithChildren, useCallback, useRef, useEffect, useImperativeHandle } from "react";
import { AutoRow } from "./uniforms/AutoRow";
import { createPortal } from "react-dom";
import { context as UniformsContext } from "uniforms";
import { AUTO_ROW_ID, UnitablesJsonSchemaBridge } from "./uniforms";
import { DmnAutoFieldProvider } from "@kie-tools/dmn-runner/dist/uniforms/DmnAutoFieldProvider";
import { unitablesDmnRunnerAutoFieldValue } from "./uniforms/UnitablesDmnRunnerAutoFieldValue";

interface Props {
  formsId: string;
  rowIndex: number;
  jsonSchemaBridge: UnitablesJsonSchemaBridge;
  rowInput: Record<string, any>;
  onSubmitRow: (rowInput: Record<string, any>, index: number, error: Record<string, any>) => void;
}

export interface UnitablesRowApi {
  submit: () => void;
}

export const UnitablesRow = React.forwardRef<UnitablesRowApi, PropsWithChildren<Props>>(
  ({ children, formsId, rowIndex, jsonSchemaBridge, rowInput, onSubmitRow }, forwardRef) => {
    const autoRowRef = useRef<HTMLFormElement>(null);

    const onSubmit = useCallback(
      (rowInput: Record<string, any>) => {
        console.debug("DMN RUNNER TABLE: submit row: ", rowIndex);
        onSubmitRow(rowInput, rowIndex, {});
      },
      [onSubmitRow, rowIndex]
    );

    // Without it the errors will be returned in "onChange" validation;
    const onValidate = useCallback((inputs, error) => {
      // returns the validation errors;
      return null;
    }, []);

    useImperativeHandle(
      forwardRef,
      () => {
        return {
          submit: () => autoRowRef.current?.submit(),
        };
      },
      []
    );

    // Submits the table in the first render triggering the onValidate function
    useEffect(() => {
      autoRowRef.current?.submit();
    }, [autoRowRef]);

    return (
      <>
        <AutoRow
          ref={autoRowRef}
          schema={jsonSchemaBridge}
          model={rowInput}
          onSubmit={onSubmit}
          placeholder={true}
          validate={"onSubmit"}
          onValidate={onValidate}
        >
          <UniformsContext.Consumer>
            {(uniformsContext) => (
              <>
                {createPortal(
                  <form id={`${AUTO_ROW_ID}-${rowIndex}`} onSubmit={(data) => uniformsContext?.onSubmit(data)} />,
                  document.getElementById(formsId)!
                )}
                <DmnAutoFieldProvider value={unitablesDmnRunnerAutoFieldValue}>{children}</DmnAutoFieldProvider>
              </>
            )}
          </UniformsContext.Consumer>
        </AutoRow>
      </>
    );
  }
);
