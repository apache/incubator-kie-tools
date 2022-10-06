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
import { EmbeddedEditorChannelApiImpl } from "@kie-tools-core/editor/dist/embedded";
import { MessageBusClientApi } from "@kie-tools-core/envelope-bus/dist/api";
import { useSharedValue } from "@kie-tools-core/envelope-bus/dist/hooks";
import { ServerlessWorkflowDiagramEditorEnvelopeApi } from "@kie-tools/serverless-workflow-diagram-editor-envelope/dist/api";
import { SwfServiceCatalogChannelApi } from "@kie-tools/serverless-workflow-service-catalog/dist/api";
import { ServerlessWorkflowTextEditorChannelApi } from "@kie-tools/serverless-workflow-text-editor/dist/api";
import { useMemo } from "react";
import { SwfServiceCatalogChannelApiImpl } from "../../impl";
import { ServerlessWorkflowCombinedEditorChannelApi } from "../../api";
import { ServerlessWorkflowTextEditorChannelApiImpl } from "../../impl/ServerlessWorkflowTextEditorChannelApiImpl";

export function useSwfTextEditorChannelApi(args: {
  locale: string;
  channelApi?: MessageBusClientApi<ServerlessWorkflowTextEditorChannelApi>;
  embeddedEditorFile?: EmbeddedEditorFile;
  onEditorReady: () => void;
  swfDiagramEditorEnvelopeApi?: MessageBusClientApi<ServerlessWorkflowDiagramEditorEnvelopeApi>;
}) {
  const [services] = useSharedValue(args.channelApi?.shared.kogitoSwfServiceCatalog_services);
  const [serviceRegistriesSettings] = useSharedValue(
    args.channelApi?.shared.kogitoSwfServiceCatalog_serviceRegistriesSettings
  );

  // Keep getFileContents in the dependency list to update the stateControl instance
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

  const swfServiceCatalogChannelApiImpl = useMemo(
    () =>
      args.channelApi &&
      services &&
      serviceRegistriesSettings &&
      new SwfServiceCatalogChannelApiImpl(args.channelApi, services, serviceRegistriesSettings),
    [args.channelApi, serviceRegistriesSettings, services]
  );

  const channelApi = useMemo(
    () =>
      channelApiImpl &&
      args.channelApi &&
      new ServerlessWorkflowTextEditorChannelApiImpl(
        channelApiImpl,
        args.channelApi,
        swfServiceCatalogChannelApiImpl,
        args.swfDiagramEditorEnvelopeApi
      ),
    [channelApiImpl, args.channelApi, args.swfDiagramEditorEnvelopeApi, swfServiceCatalogChannelApiImpl]
  );

  return {
    stateControl,
    channelApi,
  };
}
