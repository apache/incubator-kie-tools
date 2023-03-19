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
  onModelUpdate: (model: object) => void;
}

export interface UnitablesRowApi {
  submit: () => void;
  reset: (defaultValues?: object) => void;
}

export const UnitablesRow = React.forwardRef<UnitablesRowApi, PropsWithChildren<Props>>((props, forwardRef) => {
  const [model, setModel] = useState<object>(props.model);
  const autoRowRef = useRef<HTMLFormElement>(null);

  const onSubmit = useCallback((model: object) => {
    setModel(model);
    props.onModelUpdate(model);
  }, []);

  const onValidate = useCallback((model: object, error: object) => {
    setModel(model);
    props.onModelUpdate(model);
  }, []);

  useImperativeHandle(forwardRef, () => ({
    submit: () => autoRowRef.current?.submit(),
    reset: (defaultValues?: object) => setModel({ ...defaultValues }),
  }));

  return (
    <>
      <AutoRow
        ref={autoRowRef}
        schema={props.jsonSchemaBridge}
        autosave={true}
        autosaveDelay={200}
        model={model}
        onSubmit={(model: object) => onSubmit(model)}
        onValidate={(model: object, error: object) => onValidate(model, error)}
        placeholder={true}
      >
        <UniformsContext.Consumer>
          {(ctx: any) => (
            <>
              {createPortal(
                <form id={`unitables-row-${props.rowIndex}`} onSubmit={(data) => ctx?.onSubmit(data)} />,
                document.getElementById(props.formId)!
              )}
              {props.children}
            </>
          )}
        </UniformsContext.Consumer>
      </AutoRow>
    </>
  );
});
