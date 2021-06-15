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
import { Bridge } from "uniforms";
import union from "lodash/union";
import escape from "lodash/escape";
import * as prettier from "prettier";
import { FormElement } from "../api";
import { NS_SEPARATOR } from "./utils/Utils";
import { renderFormInputs } from "./rendering/RenderingUtils";
import { getStaticCodeBlock } from "./staticCode/staticCodeBlocks";

export type AutoFormProps = {
  id: string;
  disabled?: boolean;
  placeholder?: boolean;
  schema: Bridge;
};

const AutoForm: React.FC<AutoFormProps> = (props) => {
  const inputs: FormElement<any>[] = renderFormInputs(props.schema);

  let pfImports: string[] = [];
  let reactImports: string[] = [];
  let staticCodeArray: string[] = [];

  inputs.forEach((input) => {
    pfImports = union(pfImports, input.pfImports);
    reactImports = union(reactImports, input.reactImports);
    staticCodeArray = union(staticCodeArray, input.requiredCode);
  });

  const formName = `Form${props.id ? `${NS_SEPARATOR}${props.id}` : ""}`;
  const hooks = inputs.map((input) => input.stateCode).join("\n");
  const elements = inputs.map((input) => input.jsxCode).join("\n");
  const staticCodeStr: string = staticCodeArray.map((id) => getStaticCodeBlock(id)).join("\n");

  const formTemplate = `import React, { ${reactImports.join(", ")} }  from "react";
    import { ${pfImports.join(", ")} } from "@patternfly/react-core";
    
    const ${formName}: React.FC<any> = ( props:any ) => {
      ${hooks}
      
      ${staticCodeStr}
      
      return (
        <div className={'pf-c-form'}>
          ${elements}        
        </div>
      )    
    }
    
    export default ${formName};`;

  const formattedFormTemplate = prettier.format(formTemplate, {
    parser: "typescript",
    singleQuote: true,
    jsxSingleQuote: true,
    jsxBracketSameLine: true,
    tabWidth: 2,
  });
  return <>{escape(formattedFormTemplate)}</>;
};

export default AutoForm;
