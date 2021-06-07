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
import { Bridge } from "uniforms";
import union from "lodash/union";
import { RenderedElement } from "../api";
import { NS_SEPARATOR } from "./utils/Utils";
import { renderFormInputs } from "./rendering/RenderingUtils";

export type AutoFormProps = {
  id: string;
  disabled?: boolean;
  placeholder?: boolean;
  schema: Bridge;
};

const AutoForm: React.FC<AutoFormProps> = (props) => {
  const inputs: RenderedElement[] = renderFormInputs(props.schema);

  let pfImports: string[] = ["Form"];
  let reactImports: string[] = [];

  inputs.forEach((input) => {
    pfImports = union(pfImports, input.pfImports);
    reactImports = union(reactImports, input.reactImports);
  });

  pfImports = union(["Form"], { ...pfImports });

  const formName = `Form${props.id ? `${NS_SEPARATOR}${props.id}` : ""}`;
  const hooks = inputs.map((input) => input.stateCode).join("\n");
  const elements = inputs.map((input) => input.jsxCode).join("\n");

  const formTemplate = `import React, { ${reactImports.join(", ")} }  from "react";
    import { ${pfImports.join(", ")} } from "@patternfly/react-core";
    
    const ${formName}: React.FC<any> = ( props ) => {
      ${hooks}
      
      return (
        <Form>
          ${elements}        
        </Form>
      )    
    }
    
    export default ${formName};`;

  return <>{formTemplate}</>;
};

export default AutoForm;
