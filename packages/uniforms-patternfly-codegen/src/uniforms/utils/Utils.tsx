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
import { DataType, FormElement, FormInput, InputReference, InputsContainer } from "../../api";
import { OBJECT } from "./dataTypes";

export const NS_SEPARATOR = "__";
export const FIELD_SET_PREFFIX = `set`;

export const getInputReference = (binding: string, dataType: DataType): InputReference => {
  const stateName = binding.split(".").join(NS_SEPARATOR);
  const stateSetter = `${FIELD_SET_PREFFIX}${NS_SEPARATOR}${stateName}`;
  return {
    binding: binding,
    stateName,
    stateSetter,
    dataType,
  };
};

export const getStateCodeFromRef = (ref: InputReference): string => {
  return getStateCode(ref.stateName, ref.stateSetter, ref.dataType.name, ref.dataType.defaultValue);
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
  ref,
  wrapper,
  requiredCode,
  disabled,
}: DefaultInputProps): FormInput => {
  const stateCode = getStateCodeFromRef(ref);

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
    if (input.ref.dataType === OBJECT) {
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
  const dataType: DataType = ref.dataType;
  if (dataType === OBJECT) {
    return "";
  }
  const defaultValueStr: string = dataType.defaultValue ? ` ?? ${dataType.defaultValue}` : "";
  return `\n${ref.stateSetter}(data?.${fieldNameToOptionalChain(ref.binding)}${defaultValueStr});`;
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
        result += buildWriteModelValueExpression(container.ref);
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
  const result: string[] = [];
  inputs
    .filter((input) => !input.isReadonly)
    .forEach((input) => {
      if ((input as any)?.childRefs) {
        const container = input as InputsContainer;
        container.childRefs
          .filter((ref) => ref.dataType !== OBJECT)
          .forEach((ref) => {
            result.push(ref.stateName);
          });
      } else {
        if (input.ref.dataType !== OBJECT) {
          result.push(input.ref.stateName);
        }
      }
    });
  return result.join(",");
};

const buildWriteModelValueExpression = (ref: InputReference): string => {
  if (ref.dataType === OBJECT) {
    return `\nformData.${ref.binding} = {}`;
  }
  return `\nformData.${ref.binding} = ${ref.stateName}`;
};
