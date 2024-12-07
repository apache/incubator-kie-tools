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

import { StateControl } from "@kie-tools-core/editor/dist/channel";
import { WorkspaceEdit } from "@kie-tools-core/workspace/dist/api";

/**
 * A Base64PngEdit is a object containing all Editor information necessary to update the current state.
 */
export interface Base64PngEdit extends WorkspaceEdit {
  filter: string;
  contrast: string;
  brightness: string;
  saturate: string;
  sepia: string;
  grayscale: string;
  invert: string;
}

/**
 * A Base64PngEditorStateControl extends the default StateControl which is a class that implements a command stack, and all
 * methods necessary to do undo/redo operations or even if the editor is dirty.
 *
 * The use of this implementation is optional, you can use your own if required.
 */
export class Base64PngEditorStateControl extends StateControl {
  /**
   * The command stack on the Kogito State Control accept strings only, this method retrieve the Base64PngEdit object.
   */
  getCurrentBase64PngEdit(): Base64PngEdit | undefined {
    const command = super.getCurrentCommand();
    if (command) {
      return JSON.parse(command.id) as Base64PngEdit;
    }
    return;
  }
}
