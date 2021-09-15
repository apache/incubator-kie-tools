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

import * as React from "react";
import { FormElement, FormInput, InputReference, InputsContainer } from "../../api";

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

export const getStateCodeFromRef = (ref: InputReference, dataType: string, defaultValue?: string): string => {
  return getStateCode(ref.stateName, ref.stateSetter, dataType, defaultValue);
};

export const getStateCode = (
  stateName: string,
  stateSetter: string,
  dataType: string,
  defaultValue?: string
): string => {
  return `const [ ${stateName}, ${stateSetter} ] = useState<${dataType}>(${defaultValue || ""});`;
};

type DefaultInputProps = {
  pfImports: string[];
  inputJsxCode: string;
  ref: InputReference;
  dataType: string;
  defaultValue?: string;
  requiredCode?: string[];
  wrapper: WrapperProps;
  disabled: boolean;
};

type WrapperProps = {
  id: string;
  label: string;
  required: boolean;
};

export const buildDefaultInputElement = ({
  pfImports,
  inputJsxCode,
  dataType,
  defaultValue,
  ref,
  wrapper,
  requiredCode,
  disabled,
}: DefaultInputProps): FormInput => {
  const stateCode = getStateCodeFromRef(ref, dataType, defaultValue);

  const jsxCode = `<FormGroup
      fieldId={'${wrapper.id}'}
      label={'${wrapper.label}'}
      isRequired={${wrapper.required || false}}
    >
      ${inputJsxCode}
    </FormGroup>`;

  pfImports.push("FormGroup");

  return {
    ref,
    pfImports,
    reactImports: ["useState"],
    requiredCode: requiredCode,
    jsxCode,
    stateCode,
    isReadonly: disabled,
  };
};

export const renderField = (element: FormElement) => {
  return <>{JSON.stringify(element).trim()}</>;
};

export const buildSetFormDataCallback = (inputs: FormElement[]): string => {
  let result = "";
  inputs.forEach((input) => {
    if ((input as any)?.childRefs) {
      const container = input as InputsContainer;
      container.childRefs.forEach((ref) => {
        result += buildSetStateValueExpression(ref);
      });
    } else {
      result += buildSetStateValueExpression(input.ref);
    }
  });

  return result;
};

const buildSetStateValueExpression = (ref: InputReference): string => {
  return `\n${ref.stateSetter}(data?.${fieldNameToOptionalChain(ref.binding)})`;
};

const fieldNameToOptionalChain = (fieldName: string): string => {
  if (!fieldName) {
    return "";
  }

  return fieldName.split(".").join("?.");
};

export const buildGetFormDataCallback = (inputs: FormElement[]): string => {
  let result = "";
  inputs
    .filter((input) => !input.isReadonly)
    .forEach((input) => {
      if ((input as any)?.childRefs) {
        const container = input as InputsContainer;
        result += `\nformData.${input.ref.binding} = {}`;
        container.childRefs.forEach((ref) => {
          result += buildWriteModelValueExpression(ref);
        });
      } else {
        result += buildWriteModelValueExpression(input.ref);
      }
    });

  return result;
};

export const buildGetFormDataCallbackDeps = (inputs: FormElement[]): string => {
  return inputs
    .filter((input) => !input.isReadonly)
    .map((input) => input.ref.stateName)
    .join(", ");
};

const buildWriteModelValueExpression = (ref: InputReference): string => {
  return `\nformData.${ref.binding} = ${ref.stateName}`;
};
