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

import { EmbeddedEditorFile, StateControl } from "@kie-tools-core/editor/dist/channel";
import { EmbeddedEditorChannelApiImpl } from "@kie-tools-core/editor/dist/embedded";
import { MessageBusClientApi } from "@kie-tools-core/envelope-bus/dist/api";
import { ServerlessWorkflowDiagramEditorChannelApi } from "@kie-tools/serverless-workflow-diagram-editor-envelope/dist/api";
import { ServerlessWorkflowTextEditorEnvelopeApi } from "@kie-tools/serverless-workflow-text-editor/dist/api";
import { useMemo } from "react";
import { ServerlessWorkflowDiagramEditorChannelApiImpl } from "../../channel/ServerlessWorkflowDiagramEditorChannelApiImpl";

export function useSwfDiagramEditorChannelApi(args: {
  locale: string;
  channelApi?: MessageBusClientApi<ServerlessWorkflowDiagramEditorChannelApi>;
  embeddedEditorFile?: EmbeddedEditorFile;
  onEditorReady: () => void;
  swfTextEditorEnvelopeApi?: MessageBusClientApi<ServerlessWorkflowTextEditorEnvelopeApi>;
}) {
  // Keep getFileContents in the dependency list to update the stateControl instance
  // eslint-disable-next-line react-hooks/exhaustive-deps
  const stateControl = useMemo(() => new StateControl(), [args.embeddedEditorFile?.getFileContents]);

  const channelApiImpl = useMemo(
    () =>
      args.embeddedEditorFile &&
      new EmbeddedEditorChannelApiImpl(stateControl, args.embeddedEditorFile, args.locale, {
        kogitoEditor_ready: () => {
          args.onEditorReady();
        },
      }),
    [args, stateControl]
  );

  const channelApi = useMemo(
    () =>
      args.channelApi &&
      channelApiImpl &&
      args.swfTextEditorEnvelopeApi &&
      new ServerlessWorkflowDiagramEditorChannelApiImpl({
        defaultApiImpl: channelApiImpl,
        textEditorEnvelopeApi: args.swfTextEditorEnvelopeApi,
      }),
    [args.channelApi, channelApiImpl, args.swfTextEditorEnvelopeApi]
  );

  return {
    stateControl,
    channelApi,
  };
}
