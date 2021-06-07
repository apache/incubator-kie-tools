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

import { ComponentType, createElement } from "react";
import { useField } from "uniforms";
/*
import DateField from './DateField';
import ListField from './ListField';
import NestField from './NestField';
import NumField from './NumField';
import RadioField from './RadioField';
import SelectField from './SelectField';*/
import TextField from "./TextField";
import BoolField from "./BoolField";
import NumField from "./NumField";
//import NestField from './NestField';
//import { useField } from "./useField";

export type AutoFieldProps = {
  component?: ComponentType<any>;
  name: string;
} & Record<string, unknown>;

export default function AutoField(originalProps: AutoFieldProps) {
  const props = useField(originalProps.name, originalProps)[0];
  const { allowedValues, fieldType } = props;
  let { component } = props;

  if (allowedValues) {
    /*if (checkboxes && fieldType !== Array) {
      component = RadioField;
    } else {
      component = SelectField;
    }*/
  } else {
    switch (fieldType) {
      /*case Array:
        component = ListField;
        break;*/
      case Boolean:
        component = BoolField;
        break;
      /*
      case Date:
        component = DateField;
        break;*/
      case Number:
        component = NumField;
        break;
      /*    case Object:
        component = NestField;
        break;*/
      case String:
        component = TextField;
        break;
    }
    if (!component) {
      throw new Error(`Unsupported field type: ${fieldType}`);
    }
  }

  return createElement(component!, originalProps);
}
