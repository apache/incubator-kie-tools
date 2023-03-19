/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from "react";
import { useMemo } from "react";
import { DmnValidator } from "./DmnValidator";
import { dmnFormI18n } from "./i18n";
import { FormComponent, FormComponentProps, FormProps } from "@kie-tools/form";
import { DmnAutoFieldProvider } from "./uniforms/DmnAutoFieldProvider";

export type InputRow = Record<string, string>;

type DmnDecisionNodes = "InputSet" | string;

export interface DmnSchema {
  definitions?: Record<
    DmnDecisionNodes,
    {
      type: string;
      properties: Record<string, DmnDeepProperty>;
    }
  >;
}

export interface DmnFormData {
  definitions: DmnFormDefinitions;
}

type DmnFormDefinitions = Record<
  DmnDecisionNodes,
  {
    required?: string[];
    properties: object;
    type: string;
    placeholder?: string;
    title?: string;
    format?: string;
    items: object[] & { properties: object };
    "x-dmn-type"?: string;
  }
>;

interface DmnDeepProperty {
  $ref?: string;
  type?: string;
  placeholder?: string;
  title?: string;
  format?: string;
  "x-dmn-type"?: string;
  properties?: DmnDeepProperty;
}

export function DmnForm(props: FormProps<InputRow, DmnSchema>) {
  const i18n = useMemo(() => {
    dmnFormI18n.setLocale(props.locale ?? navigator.language);
    return dmnFormI18n.getCurrent();
  }, [props.locale]);
  const dmnValidator = useMemo(() => new DmnValidator(i18n), [i18n]);

  return (
    <FormComponent
      {...props}
      i18n={i18n}
      validator={dmnValidator}
      removeRequired={true}
      entryPath={"definitions.InputSet"}
      propertiesEntryPath={"definitions.InputSet.properties"}
    >
      <DmnAutoFieldProvider />
    </FormComponent>
  );
}
