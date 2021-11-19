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
import { PropsWithChildren, useCallback, useEffect, useImperativeHandle, useRef, useState } from "react";
import { AutoRow } from "../core";
import { createPortal } from "react-dom";
import { context as UniformsContext } from "uniforms";
import { DmnTableJsonSchemaBridge } from "./DmnTableJsonSchemaBridge";

interface Props {
  formId: string;
  rowIndex: number;
  jsonSchemaBridge: DmnTableJsonSchemaBridge;
  model: object;
  onModelUpdate: (model: object) => void;
}

export interface DmnAutoRowApi {
  submit: () => void;
  reset: () => void;
}

export const DmnAutoRow = React.forwardRef<DmnAutoRowApi, PropsWithChildren<Props>>((props, forwardRef) => {
  const [model, setModel] = useState<object>({});
  const autoRowRef = useRef<HTMLFormElement>(null);

  const onSubmit = useCallback((model: object) => {
    setModel(model);
    props.onModelUpdate(model);
  }, []);

  const onValidate = useCallback((model: any, error: any) => {
    setModel(model);
    props.onModelUpdate(model);
  }, []);

  const getDefaultValueByType = useCallback((type, defaultValues: { [x: string]: any }, property: string) => {
    if (type === "object") {
      defaultValues[`${property}`] = {};
    }
    if (type === "array") {
      defaultValues[`${property}`] = [];
    }
    if (type === "boolean") {
      defaultValues[`${property}`] = false;
    }
    return defaultValues;
  }, []);

  const getDefaultValues = useCallback(
    () =>
      Object.keys(props.jsonSchemaBridge?.schema?.properties ?? {}).reduce((defaultValues, property) => {
        if (Object.hasOwnProperty.call(props.jsonSchemaBridge?.schema?.properties[property], "$ref")) {
          const refPath = props.jsonSchemaBridge?.schema?.properties[property].$ref!.split("/").pop() ?? "";
          return getDefaultValueByType(
            props.jsonSchemaBridge?.schema?.definitions?.[refPath].type,
            defaultValues,
            property
          );
        }
        return getDefaultValueByType(
          props.jsonSchemaBridge?.schema?.properties?.[property]?.type,
          defaultValues,
          property
        );
      }, {} as { [x: string]: any }),
    []
  );

  useEffect(() => {
    const defaultValues = getDefaultValues();
    setModel({ ...defaultValues, ...props.model });
    props.onModelUpdate({ ...defaultValues, ...props.model });
  }, []);

  useImperativeHandle(forwardRef, () => ({
    submit: () => autoRowRef.current?.submit(),
    reset: () => setModel(getDefaultValues()),
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
        onValidate={(model: object, error: any) => onValidate(model, error)}
        placeholder={true}
      >
        <UniformsContext.Consumer>
          {(ctx: any) => (
            <>
              {createPortal(
                <form id={`dmn-auto-form-${props.rowIndex}`} onSubmit={(data) => ctx?.onSubmit(data)} />,
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
