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

import { EmbeddedEditorFile, StateControl } from "@kie-tools-core/editor/dist/channel";
import { EmbeddedEditorChannelApiImpl, EmbeddedEditorRef } from "@kie-tools-core/editor/dist/embedded";
import { MessageBusClientApi } from "@kie-tools-core/envelope-bus/dist/api";
import { ServerlessWorkflowDiagramEditorChannelApi } from "@kie-tools/serverless-workflow-diagram-editor-envelope/dist/api";
import { ServerlessWorkflowTextEditorEnvelopeApi } from "@kie-tools/serverless-workflow-text-editor/dist/api";
import { useMemo } from "react";
import { ServerlessWorkflowDiagramEditorChannelApiImpl } from "../../impl/ServerlessWorkflowDiagramEditorChannelApiImpl";

export function useSwfDiagramEditorChannelApi(args: {
  locale: string;
  channelApi?: MessageBusClientApi<ServerlessWorkflowDiagramEditorChannelApi>;
  embeddedEditorFile?: EmbeddedEditorFile;
  onEditorReady: () => void;
  getSwfTextEditorEnvelopeApi?: () => MessageBusClientApi<ServerlessWorkflowTextEditorEnvelopeApi>;
}) {
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

  const swfTextEditorEnvelopeApi = useMemo(
    () => args.getSwfTextEditorEnvelopeApi?.(),
    [args.getSwfTextEditorEnvelopeApi]
  );

  const channelApi = useMemo(
    () =>
      args.channelApi &&
      channelApiImpl &&
      swfTextEditorEnvelopeApi &&
      new ServerlessWorkflowDiagramEditorChannelApiImpl(channelApiImpl, swfTextEditorEnvelopeApi),
    [args.channelApi, channelApiImpl, swfTextEditorEnvelopeApi]
  );

  return {
    stateControl,
    channelApi,
  };
}
