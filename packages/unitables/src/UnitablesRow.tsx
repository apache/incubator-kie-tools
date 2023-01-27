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
import { PropsWithChildren, useCallback, useImperativeHandle, useRef, useState } from "react";
import { AutoRow } from "./uniforms/AutoRow";
import { createPortal } from "react-dom";
import { context as UniformsContext } from "uniforms";
import { UnitablesJsonSchemaBridge } from "./uniforms";

interface Props {
  formId: string;
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
  ({ children, formId, rowIndex, jsonSchemaBridge, model, onModelUpdate }, forwardRef) => {
    const [_model, _setModel] = useState<object>(model);
    const autoRowRef = useRef<HTMLFormElement>(null);

    const onSubmit = useCallback(
      (model: object) => {
        _setModel(model);
        onModelUpdate(model, rowIndex);
      },
      [onModelUpdate, rowIndex]
    );

    const onValidate = useCallback(
      (model: object, error: object) => {
        _setModel(model);
        onModelUpdate(model, rowIndex);
      },
      [onModelUpdate, rowIndex]
    );

    useImperativeHandle(forwardRef, () => ({
      submit: () => autoRowRef.current?.submit(),
      reset: (defaultValues?: object) => _setModel({ ...defaultValues }),
    }));

    return (
      <>
        <AutoRow
          ref={autoRowRef}
          schema={jsonSchemaBridge}
          autosave={true}
          autosaveDelay={200}
          model={_model}
          onSubmit={(model: object) => onSubmit(model)}
          onValidate={(model: object, error: object) => onValidate(model, error)}
          placeholder={true}
        >
          <UniformsContext.Consumer>
            {(uniformsContext) => (
              <>
                {createPortal(
                  <form id={`unitables-row-${rowIndex}`} onSubmit={(data) => uniformsContext?.onSubmit(data)} />,
                  document.getElementById(formId)!
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
