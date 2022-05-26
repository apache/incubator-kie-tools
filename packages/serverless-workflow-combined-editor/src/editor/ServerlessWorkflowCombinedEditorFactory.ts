/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import {
  Editor,
  EditorFactory,
  EditorInitArgs,
  KogitoEditorEnvelopeContextType,
} from "@kie-tools-core/editor/dist/api";
import { ServerlessWorkflowCombinedEditorChannelApi } from "../api";
import { ServerlessWorkflowCombinedEditorView } from "./ServerlessWorkflowCombinedEditorView";

export class ServerlessWorkflowCombinedEditorFactory
  implements EditorFactory<Editor, ServerlessWorkflowCombinedEditorChannelApi>
{
  public async createEditor(
    ctx: KogitoEditorEnvelopeContextType<ServerlessWorkflowCombinedEditorChannelApi>,
    initArgs: EditorInitArgs
  ) {
    return new ServerlessWorkflowCombinedEditorView(ctx, initArgs);
  }
}
