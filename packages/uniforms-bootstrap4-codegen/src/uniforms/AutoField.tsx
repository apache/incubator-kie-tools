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

import { createAutoField } from "uniforms/es5/createAutoField";
import {
  BoolField,
  CheckBoxGroupField,
  DateField,
  NestField,
  NumField,
  RadioField,
  SelectField,
  TextField,
  UnsupportedField,
} from "./index";

export type AutoFieldProps = Parameters<typeof AutoField>[0];

const AutoField = createAutoField((props) => {
  if (props.allowedValues) {
    if (props.checkboxes) {
      return props.fieldType !== Array ? RadioField : CheckBoxGroupField;
    }
    return SelectField;
  }

  switch (props.fieldType) {
    /*
    TODO: implement array support
    case Array:
      return  ListField;*/
    case Boolean:
      return BoolField;
    case Date:
      return DateField;
    case Number:
      return NumField;
    case Object:
      return NestField;
    case String:
      return TextField;
  }

  console.log(`Unsupported field type: ${props.fieldType.name}`);
  return UnsupportedField;
});

export default AutoField;
