/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from "react";
import { Context, BaseForm, context, randomIds } from "uniforms";
import SimpleSchema from "simpl-schema";
import { SimpleSchema2Bridge } from "uniforms-bridge-simple-schema-2";

const randomId = randomIds();

export function createSimpleSchema(schema = {}) {
  return new SimpleSchema2Bridge(new SimpleSchema(schema));
}

export function usingUniformsContext(children: React.ReactElement, schema = {}, ctx?: Partial<Context<any>>) {
  const partialCtx = {
    changed: false,
    changedMap: {},
    error: null,
    model: {},
    name: [],
    onChange: () => {},
    onSubmit: () => {},
    randomId,
    submitting: false,
    validating: false,
    submitted: false,
    schema: createSimpleSchema(schema),
    formRef: new BaseForm<any>({
      autosave: false,
      autosaveDelay: 0,
      error: false,
      label: true,
      model: {},
      noValidate: false,
      onSubmit: (event) => {},
      schema: createSimpleSchema(schema),
    }),
    ...ctx,
    state: {
      disabled: false,
      label: false,
      placeholder: false,
      showInlineError: false,
      readOnly: false,
    },
  };
  return <context.Provider value={partialCtx}>{children}</context.Provider>;
}
