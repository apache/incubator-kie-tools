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
import {
  ChannelType,
  DEFAULT_WORKSPACE_ROOT_ABSOLUTE_POSIX_PATH,
  EditorFactory,
  EditorInitArgs,
  KogitoEditorEnvelopeContextType,
} from "@kie-tools-core/editor/dist/api";
import { NewDmnEditorChannelApi } from "./NewDmnEditorChannelApi";
import { DmnEditorInterface, DmnEditorRootWrapper } from "./DmnEditorFactory";
import { NewDmnEditorEnvelopeApi } from "./NewDmnEditorEnvelopeApi";
import { NewDmnEditorTypes } from "./NewDmnEditorTypes";

export class NewDmnEditorFactory
  implements EditorFactory<NewDmnEditorInterface, NewDmnEditorEnvelopeApi, NewDmnEditorChannelApi>
{
  public createEditor(
    envelopeContext: KogitoEditorEnvelopeContextType<NewDmnEditorEnvelopeApi, NewDmnEditorChannelApi>,
    initArgs: EditorInitArgs
  ): Promise<NewDmnEditorInterface> {
    return Promise.resolve(new NewDmnEditorInterface(envelopeContext, initArgs));
  }
}

export class NewDmnEditorInterface extends DmnEditorInterface {
  /**
   * Open boxed expression editor for given node
   * @param nodeId id of the node to open
   */
  public openBoxedExpressionEditor(nodeId: string): void {
    this.self.openBoxedExpressionEditor(nodeId);
  }

  public showDmnEvaluationResults(evaluationResultsByNodeId: NewDmnEditorTypes.EvaluationResultsByNodeId): void {
    this.self.showDmnEvaluationResults(evaluationResultsByNodeId);
  }

  public af_componentRoot() {
    return (
      <DmnEditorRootWrapper
        exposing={(dmnEditorRoot) => (this.self = dmnEditorRoot)}
        envelopeContext={this.envelopeContext}
        workspaceRootAbsolutePosixPath={
          this.initArgs.workspaceRootAbsolutePosixPath ?? DEFAULT_WORKSPACE_ROOT_ABSOLUTE_POSIX_PATH
        }
        isEvaluationHighlightsSupported={
          this.initArgs.channel === ChannelType.ONLINE || this.initArgs.channel === ChannelType.ONLINE_MULTI_FILE
        }
        isReadOnly={this.initArgs.isReadOnly}
        onOpenedBoxedExpressionEditorNodeChange={(newOpenedNodeId) => {
          (
            this.envelopeContext as KogitoEditorEnvelopeContextType<NewDmnEditorEnvelopeApi, NewDmnEditorChannelApi>
          )?.shared.newDmnEditor_openedBoxedExpressionEditorNodeId.set(newOpenedNodeId);
        }}
      />
    );
  }
}
