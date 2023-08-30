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
import { useSharedValue } from "@kie-tools-core/envelope-bus/dist/hooks";
import { ServerlessWorkflowDiagramEditorEnvelopeApi } from "@kie-tools/serverless-workflow-diagram-editor-envelope/dist/api";
import { ServerlessWorkflowTextEditorChannelApi } from "@kie-tools/serverless-workflow-text-editor/dist/api";
import { useMemo } from "react";
import { SwfServiceCatalogChannelApiImpl } from "../../channel";
import { ServerlessWorkflowTextEditorChannelApiImpl } from "../../channel/ServerlessWorkflowTextEditorChannelApiImpl";
import { KogitoEditorChannelApi } from "@kie-tools-core/editor/dist/api";

export interface UseSwfTextEditorChannelApiArgs {
  apiOverrides: Partial<KogitoEditorChannelApi>;
  locale: string;
  channelApi?: MessageBusClientApi<ServerlessWorkflowTextEditorChannelApi>;
  embeddedEditorFile?: EmbeddedEditorFile;
  swfDiagramEditorEnvelopeApi?: MessageBusClientApi<ServerlessWorkflowDiagramEditorEnvelopeApi>;
}

export function useSwfTextEditorChannelApi(args: UseSwfTextEditorChannelApiArgs) {
  const [services] = useSharedValue(args.channelApi?.shared.kogitoSwfServiceCatalog_services);
  const [serviceRegistriesSettings] = useSharedValue(
    args.channelApi?.shared.kogitoSwfServiceCatalog_serviceRegistriesSettings
  );

  // Keep getFileContents in the dependency list to update the stateControl instance
  // eslint-disable-next-line react-hooks/exhaustive-deps
  const stateControl = useMemo(() => new StateControl(), [args.embeddedEditorFile?.getFileContents]);

  const channelApiImpl = useMemo(
    () =>
      args.embeddedEditorFile &&
      new EmbeddedEditorChannelApiImpl(stateControl, args.embeddedEditorFile, args.locale, {
        ...args.apiOverrides,
      }),
    [args, stateControl]
  );

  const swfServiceCatalogChannelApiImpl = useMemo(
    () =>
      args.channelApi &&
      services &&
      serviceRegistriesSettings &&
      new SwfServiceCatalogChannelApiImpl({ channelApi: args.channelApi, services, serviceRegistriesSettings }),
    [args.channelApi, serviceRegistriesSettings, services]
  );

  const channelApi = useMemo(
    () =>
      channelApiImpl &&
      args.channelApi &&
      new ServerlessWorkflowTextEditorChannelApiImpl({
        defaultApiImpl: channelApiImpl,
        channelApi: args.channelApi,
        swfServiceCatalogApiImpl: swfServiceCatalogChannelApiImpl,
        diagramEditorEnvelopeApi: args.swfDiagramEditorEnvelopeApi,
      }),
    [channelApiImpl, args, swfServiceCatalogChannelApiImpl]
  );

  return {
    stateControl,
    channelApi,
  };
}
