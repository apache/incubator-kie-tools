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

import React, { useContext } from "react";
import { connectField, context, HTMLFieldProps } from "uniforms/es5";
import { renderNestedInputFragmentWithContext } from "./rendering/RenderingUtils";
import { renderField } from "./utils/Utils";
import { InputReference, InputsContainer } from "../api";
import { codeGenContext } from "./CodeGenContext";
import { union } from "lodash";

export type NestFieldProps = HTMLFieldProps<object, HTMLDivElement, { itemProps?: object }>;

const Nest: React.FunctionComponent<NestFieldProps> = ({
  id,
  children,
  error,
  errorMessage,
  fields,
  itemProps,
  label,
  name,
  showInlineError,
  disabled,
  ...props
}: NestFieldProps) => {
  const uniformsContext = useContext(context);
  const codegenCtx = useContext(codeGenContext);

  const nestedRefs: InputReference[] = [];
  const nestedStates: string[] = [];
  const nestedJsx: string[] = [];

  let pfImports: string[] = ["Card", "CardBody"];
  let reactImports: string[] = [];
  let requiredCode: string[] = [];

  if (fields) {
    fields.forEach((field) => {
      const renderedInput = renderNestedInputFragmentWithContext(uniformsContext, field, itemProps, disabled);

      if (renderedInput) {
        nestedStates.push(renderedInput.stateCode);
        nestedJsx.push(renderedInput.jsxCode);
        nestedRefs.push(renderedInput.ref);
        pfImports = union(pfImports, renderedInput.pfImports);
        reactImports = union(reactImports, renderedInput.reactImports);
        if (renderedInput.requiredCode) {
          requiredCode = union(requiredCode, renderedInput.requiredCode);
        }
      } else {
        console.log(`Cannnot render form field for: '${field}'`);
      }
    });
  }

  const bodyLabel = label ? `<label><b>${label}</b></label>` : "";

  const stateCode = nestedStates.join("\n");
  const jsxCode = `<Card>
          <CardBody className="pf-c-form">
          ${bodyLabel}
          ${nestedJsx.join("\n")}
          </CardBody></Card>`;

  const rendered: InputsContainer = {
    pfImports,
    reactImports,
    requiredCode: requiredCode,
    stateCode,
    jsxCode,
    ref: nestedRefs,
  };

  codegenCtx?.rendered.push(rendered);

  return renderField(rendered);
};

export default connectField(Nest);
