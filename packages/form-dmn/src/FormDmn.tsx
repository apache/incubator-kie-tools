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

import * as React from "react";
import { useMemo } from "react";
import { FormDmnValidator } from "./FormDmnValidator";
import { formDmnI18n } from "./i18n";
import { FormComponent, FormProps } from "@kie-tools/form/dist/FormComponent";
import { DmnAutoFieldProvider } from "@kie-tools/dmn-runner/dist/uniforms";
import { formDmnRunnerAutoFieldValue } from "./uniforms/FormDmnRunnerAutoFieldValue";
import { JSONSchema4 } from "json-schema";

export type InputRow = Record<string, any>;

export function FormDmn(props: FormProps<InputRow, JSONSchema4>) {
  const i18n = useMemo(() => {
    formDmnI18n.setLocale(props.locale ?? navigator.language);
    return formDmnI18n.getCurrent();
  }, [props.locale]);
  const dmnValidator = useMemo(() => new FormDmnValidator(i18n), [i18n]);

  const mergedInputSetSchema = useMemo(() => {
    const definitions = props.formSchema?.definitions ?? {};
    const inputSetProperties: Record<string, any> = {};
    const requiredFields = new Set<string>();

    Object.entries(definitions).forEach(([key, value]) => {
      if (key.startsWith("InputSetDMN") && value.properties) {
        Object.assign(inputSetProperties, value.properties);
      }
      if (Array.isArray(value.required)) {
        value.required.forEach((field: string) => requiredFields.add(field));
      }
    });

    return {
      ...props.formSchema,
      definitions: {
        ...props.formSchema?.definitions,
        InputSet: {
          ...props.formSchema?.definitions?.InputSet,
          properties: {
            ...props.formSchema?.definitions?.InputSet?.properties,
            ...inputSetProperties,
          },
          required: Array.from(requiredFields),
        },
      },
    };
  }, [props.formSchema]);

  return (
    <FormComponent
      {...props}
      i18n={i18n}
      validator={dmnValidator}
      removeRequired={true}
      formSchema={mergedInputSetSchema}
    >
      <DmnAutoFieldProvider value={formDmnRunnerAutoFieldValue} />
    </FormComponent>
  );
}
