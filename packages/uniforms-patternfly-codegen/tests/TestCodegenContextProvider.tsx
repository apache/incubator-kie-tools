/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import * as React from "react";
import { CodeGenContext, codeGenContext } from "../src/uniforms/CodeGenContext";
import { context, Context } from "uniforms/cjs";
import createContext from "./_createContext";

export interface ProviderProps {
  ctx: CodeGenContext;
  schema: any;
  children: JSX.Element;
}

export const TestCodeGenContextProvider: React.FC<ProviderProps> = (props: ProviderProps) => {
  const ctx: Context<any> = createContext(props.schema);
  return (
    <codeGenContext.Provider value={props.ctx}>
      <context.Provider value={ctx}>{props.children}</context.Provider>
    </codeGenContext.Provider>
  );
};
