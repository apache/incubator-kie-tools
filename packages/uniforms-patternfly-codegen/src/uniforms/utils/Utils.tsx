/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import React from "react";
import { FormGroupProps } from "@patternfly/react-core";
//import ReactDOMServer from 'react-dom/server';
import { InputReference, RenderedElement } from "../../api";

export const NS_SEPARATOR = "__";
export const FIELD_SET_PREFFIX = `set`;

export const getInputReference = (binding: string): InputReference => {
  const stateName = binding.split(".").join(NS_SEPARATOR);
  const stateSetter = `${FIELD_SET_PREFFIX}${NS_SEPARATOR}${stateName}`;
  return {
    binding: binding,
    stateName,
    stateSetter,
  };
};

export const getStateCode = (ref: InputReference, dataType: string): string => {
  return `const [ ${ref.stateName}, ${ref.stateSetter} ] = useState<${dataType}>();`;
};

type WrapperProps = {
  id: string;
  label: string;
  disabled: boolean;
  required: boolean;
} & Omit<FormGroupProps, "onChange" | "fieldId">;

export const buildDefaultInputElement = (
  pfImports: string[],
  input: string,
  ref: InputReference,
  dataType: string,
  { id, label, disabled, required }: WrapperProps
): RenderedElement => {
  const stateCode = getStateCode(ref, dataType);

  const jsxCode = `<FormGroup
      fieldId="${id}"
      label="${label}"
      isRequired={${required}}
      isDisabled={${disabled}}
    >
      ${input}
    </FormGroup>`;

  pfImports.push("FormGroup");

  return {
    ref,
    pfImports,
    reactImports: ["useState"],
    jsxCode,
    stateCode,
  };
};

export const renderField = (element: RenderedElement) => {
  return <>{JSON.stringify(element)}</>;
};
/*

export const renderNestedInputFragmentWithContext = (
  parentContext: any,
  field: any,
  itempProps: any,
  disabled?: boolean
): RenderedField => {
  const content = ReactDOMServer.renderToString(
    React.createElement(NestedFormInputs, {
      parentContext,
      field,
      itempProps,
      disabled
    })
  );
  return JSON.parse(parse(content));
};
*/
