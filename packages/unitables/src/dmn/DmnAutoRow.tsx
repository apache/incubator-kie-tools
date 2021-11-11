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
import { PropsWithChildren, useCallback } from "react";
import { AutoRow } from "../core";
import { createPortal } from "react-dom";
import { context as UniformsContext } from "uniforms";
import { DmnTableJsonSchemaBridge } from "./DmnTableJsonSchemaBridge";

export const FORMS_ID = "unitables-forms";

interface Props {
  bridge: DmnTableJsonSchemaBridge;
  rowIndex: number;
  model: object;
  setModel: (model: object, index: number) => void;
}

export function DmnAutoRow(props: PropsWithChildren<Props>) {
  const onSubmit = useCallback(
    (model: object) => {
      props.setModel(model, props.rowIndex);
    },
    [props.rowIndex]
  );

  const onValidate = useCallback(
    (model: any, error: any) => {
      props.setModel(model, props.rowIndex);
    },
    [props.rowIndex]
  );

  return (
    <>
      <AutoRow
        schema={props.bridge}
        autosave={true}
        autosaveDelay={200}
        model={props.model}
        onSubmit={(model: object) => onSubmit(model)}
        onValidate={(model: object, error: any) => onValidate(model, error)}
        placeholder={true}
      >
        <UniformsContext.Consumer>
          {(ctx: any) => (
            <>
              {createPortal(
                <form id={`dmn-auto-form-${props.rowIndex}`} onSubmit={(data) => ctx?.onSubmit(data)} />,
                document.getElementById(FORMS_ID)!
              )}
              {props.children}
            </>
          )}
        </UniformsContext.Consumer>
      </AutoRow>
    </>
  );
}
