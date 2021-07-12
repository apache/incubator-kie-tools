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
import escape from "lodash/escape";
import { Bridge } from "uniforms";
import { CodeGenElement } from "../api";
import { FORM, renderCodeGenElement } from "./templates/templates";
import { renderFormInputs } from "./rendering/RenderingUtils";

export type AutoFormProps = {
  id: string;
  disabled?: boolean;
  placeholder?: boolean;
  schema: Bridge;
};

const AutoForm: React.FC<AutoFormProps> = (props) => {
  const properties = {
    children: renderFormInputs(props.schema),
  };

  const form: CodeGenElement = renderCodeGenElement(FORM, properties);

  return <>{escape(form.html)}</>;
};

export default AutoForm;
