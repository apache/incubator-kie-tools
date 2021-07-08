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

import { Context } from "uniforms";
import * as React from "react";
import AutoField from "../AutoField";
import { BootstrapCodeGenContext, CodeGenContextProvider } from "../BootstrapCodeGenContext";

export interface Props {
  codegenCtx: BootstrapCodeGenContext;
  uniformsContext: Context<any>;
  field: any;
  itempProps: any;
  disabled?: boolean;
}

export const NestedFieldInput: React.FC<Props> = ({ codegenCtx, uniformsContext, field, itempProps, disabled }) => {
  return (
    <CodeGenContextProvider schema={uniformsContext.schema} codegenCtx={codegenCtx} uniformsCtx={uniformsContext}>
      <AutoField key={field} name={field} disabled={disabled} {...itempProps} />
    </CodeGenContextProvider>
  );
};

export default NestedFieldInput;
