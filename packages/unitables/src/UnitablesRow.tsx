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
import { PropsWithChildren, useCallback, useImperativeHandle, useRef } from "react";
import { AutoRow } from "./uniforms/AutoRow";
import { createPortal } from "react-dom";
import { context as UniformsContext } from "uniforms";
import { AUTO_ROW_ID, UnitablesJsonSchemaBridge } from "./uniforms";

interface Props {
  formsId: string;
  rowIndex: number;
  jsonSchemaBridge: UnitablesJsonSchemaBridge;
  model: object;
  onModelUpdate: (model: object, index: number) => void;
}

export interface UnitablesRowApi {
  submit: () => void;
  reset: (defaultValues?: object) => void;
}

export const UnitablesRow = React.forwardRef<UnitablesRowApi, PropsWithChildren<Props>>(
  ({ children, formsId, rowIndex, jsonSchemaBridge, model, onModelUpdate }, forwardRef) => {
    const autoRowRef = useRef<HTMLFormElement>(null);

    const onSubmit = useCallback(
      (model: object) => {
        console.log("SUBMITTING ROW: " + rowIndex);
        onModelUpdate(model, rowIndex);
      },
      [onModelUpdate, rowIndex]
    );

    const onValidate = useCallback(
      (model: object, error: object) => {
        onModelUpdate(model, rowIndex);
      },
      [onModelUpdate, rowIndex]
    );

    useImperativeHandle(forwardRef, () => ({
      submit: () => autoRowRef.current?.submit(),
      reset: onSubmit,
    }));

    return (
      <>
        <AutoRow
          ref={autoRowRef}
          schema={jsonSchemaBridge}
          autosave={true}
          autosaveDelay={200}
          model={model}
          onSubmit={onSubmit}
          onValidate={onValidate}
          placeholder={true}
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
);
