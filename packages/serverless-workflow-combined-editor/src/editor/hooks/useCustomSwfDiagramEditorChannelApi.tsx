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
import { EmbeddedEditorRef, KogitoEditorChannelApiImpl } from "@kie-tools-core/editor/dist/embedded";
import { MessageBusClientApi } from "@kie-tools-core/envelope-bus/dist/api";
import { ServerlessWorkflowTextEditorEnvelopeApi } from "@kie-tools/serverless-workflow-text-editor/dist/api";
import { useMemo } from "react";
import { ServerlessWorkflowCombinedEditorChannelApi } from "../../api";
import { ServerlessWorkflowDiagramEditorChannelApiImpl } from "../../impl/ServerlessWorkflowDiagramEditorChannelApiImpl";
import { ServerlessWorkflowTextEditorChannelApiImpl } from "../../impl/ServerlessWorkflowTextEditorChannelApiImpl";

/* TODO: useCustomSwfDiagramEditorChannelApi: rename it without custom */
export function useCustomSwfDiagramEditorChannelApi(args: {
  locale: string;
  channelApi?: MessageBusClientApi<ServerlessWorkflowCombinedEditorChannelApi>;
  textEditor?: EmbeddedEditorRef;
  embeddedEditorFile?: EmbeddedEditorFile;
  onEditorReady: () => void;
}) {
  const stateControl = useMemo(() => new StateControl(), [args.embeddedEditorFile?.getFileContents]);

  const kogitoEditorChannelApiImpl = useMemo(
    () =>
      args.embeddedEditorFile &&
      new KogitoEditorChannelApiImpl(stateControl, args.embeddedEditorFile, args.locale, {
        kogitoEditor_ready: () => {
          args.onEditorReady();
        },
      }),
    [args, stateControl]
  );

  const swfTextEditorChannelApiImpl = useMemo(
    () =>
      kogitoEditorChannelApiImpl &&
      args.channelApi &&
      new ServerlessWorkflowTextEditorChannelApiImpl(kogitoEditorChannelApiImpl, args.channelApi),
    [kogitoEditorChannelApiImpl, args.channelApi]
  );

  const textEditorEnvelopeApi = useMemo(
    () =>
      args.textEditor?.getEnvelopeServer()
        .envelopeApi as unknown as MessageBusClientApi<ServerlessWorkflowTextEditorEnvelopeApi>,
    [args.textEditor]
  );

  const channelApi = useMemo(
    () =>
      args.channelApi &&
      kogitoEditorChannelApiImpl &&
      swfTextEditorChannelApiImpl &&
      new ServerlessWorkflowDiagramEditorChannelApiImpl(kogitoEditorChannelApiImpl, textEditorEnvelopeApi),
    [args.channelApi, kogitoEditorChannelApiImpl, swfTextEditorChannelApiImpl, textEditorEnvelopeApi]
  );

  return {
    stateControl,
    channelApi,
  };
}
