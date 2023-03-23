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
import { PropsWithChildren, useCallback, useRef, useEffect } from "react";
import { AutoRow } from "./uniforms/AutoRow";
import { createPortal } from "react-dom";
import { context as UniformsContext } from "uniforms";
import { AUTO_ROW_ID, UnitablesJsonSchemaBridge } from "./uniforms";

interface Props {
  formsId: string;
  rowIndex: number;
  jsonSchemaBridge: UnitablesJsonSchemaBridge;
  rowInput: Record<string, any>;
  onSubmitRow: (rowInput: Record<string, any>, index: number, error: Record<string, any>) => void;
}

export interface UnitablesRowApi {
  submit: (rowInput?: Record<string, any>) => void;
}

export function UnitablesRow({
  children,
  formsId,
  rowIndex,
  jsonSchemaBridge,
  rowInput,
  onSubmitRow,
}: PropsWithChildren<Props>) {
  const autoRowRef = useRef<HTMLFormElement>(null);

  const onSubmit = useCallback(
    (rowInput: Record<string, any>) => {
      console.log("SUBMITTING ROW: " + rowIndex);
      onSubmitRow(rowInput, rowIndex, {});
    },
    [onSubmitRow, rowIndex]
  );

  const onValidate = useCallback((model, error) => {}, []);

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
        autosave={true}
        validate={"onChange"}
        onValidate={onValidate}
      >
        <UniformsContext.Consumer>
          {(uniformsContext) => (
            <>
              {createPortal(
                <form id={`${AUTO_ROW_ID}-${rowIndex}`} onSubmit={(data) => uniformsContext?.onSubmit(data)} />,
                document.getElementById(formsId)!
              )}
              {children}
            </>
          )}
        </UniformsContext.Consumer>
      </AutoRow>
    </>
  );
}
