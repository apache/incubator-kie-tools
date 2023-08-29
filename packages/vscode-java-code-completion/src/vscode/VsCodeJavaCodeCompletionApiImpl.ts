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

import * as vscode from "vscode";
import { JavaCodeCompletionAccessor, JavaCodeCompletionApi, JavaCodeCompletionClass } from "../api";
import { JavaCodeCompletionConstants } from "./JavaCodeCompletionConstants";

export class VsCodeJavaCodeCompletionApiImpl implements JavaCodeCompletionApi {
  getAccessors(fqcn: string, query: string): Promise<JavaCodeCompletionAccessor[]> {
    const command = vscode.commands.executeCommand(
      JavaCodeCompletionConstants.WORKSPACE_COMMAND,
      JavaCodeCompletionConstants.GET_ACCESSORS,
      fqcn,
      query
    );

    const thenable = command.then((res) => res as JavaCodeCompletionAccessor[]);
    return Promise.resolve(thenable);
  }
  getClasses(query: string): Promise<JavaCodeCompletionClass[]> {
    const command = vscode.commands.executeCommand(
      JavaCodeCompletionConstants.WORKSPACE_COMMAND,
      JavaCodeCompletionConstants.GET_CLASSES,
      query
    );

    const thenable = command.then((res) => res as JavaCodeCompletionClass[]);
    return Promise.resolve(thenable);
  }

  isLanguageServerAvailable(): Promise<boolean> {
    const command = vscode.commands.executeCommand(
      JavaCodeCompletionConstants.WORKSPACE_COMMAND,
      JavaCodeCompletionConstants.IS_AVAILABLE
    );

    const thenable = command.then((res) => res as boolean);
    return Promise.resolve(thenable);
  }
}
