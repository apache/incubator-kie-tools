/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { Context, BaseForm, randomIds } from "uniforms/cjs";

import createSchema from "./_createSchema";

const randomId = randomIds();

export default function createContext(schema?: {}, context?: Partial<Context<any>>): Context<any> {
  return {
    changed: false,
    changedMap: {},
    error: null,
    model: {},
    name: [],
    onChange() {},
    onSubmit() {},
    randomId,
    submitting: false,
    validating: false,
    formRef: new BaseForm<any>({
      autosave: false,
      autosaveDelay: 0,
      error: false,
      label: true,
      model: {},
      noValidate: false,
      onSubmit: (event) => {},
      schema: createSchema(schema),
    }),
    ...context,
    schema: createSchema(schema),
    submitted: false,
    state: {
      readOnly: false,
      disabled: false,
      label: false,
      placeholder: false,
      showInlineError: false,
      ...context?.state,
    },
  };
}
