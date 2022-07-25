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
import { useMemo } from "react";
import { ServerlessWorkflowCombinedEditorChannelApi } from "../../api";
import { SwfCombinedEditorChannelApiImpl } from "../../impl/SwfCombinedEditorChannelApiImpl";
import { SwfFeatureToggleChannelApiImpl } from "../../impl/SwfFeatureToggleChannelApiImpl";
import { SwfLanguageServiceChannelApiImpl } from "../../impl/SwfLanguageServiceChannelApiImpl";
import { SwfServiceCatalogChannelApiImpl } from "../../impl/SwfServiceCatalogChannelApiImpl";

export function useCustomSwfChannelApi(args: {
  locale: string;
  channelApi?: MessageBusClientApi<ServerlessWorkflowCombinedEditorChannelApi>;
  embeddedEditorFile?: EmbeddedEditorFile;
  onEditorReady: () => void;
}) {
  const [featureToggle] = useSharedValue(args.channelApi?.shared.kogitoSwfFeatureToggle_get);
  const [services] = useSharedValue(args.channelApi?.shared.kogitoSwfServiceCatalog_services);
  const [serviceRegistriesSettings] = useSharedValue(
    args.channelApi?.shared.kogitoSwfServiceCatalog_serviceRegistriesSettings
  );

  const stateControl = useMemo(() => new StateControl(), [args.embeddedEditorFile?.getFileContents]);

  const kogitoEditorChannelApiImpl = useMemo(
    () =>
      args.embeddedEditorFile &&
      new EmbeddedEditorChannelApiImpl(stateControl, args.embeddedEditorFile, args.locale, {
        kogitoEditor_ready: () => {
          args.onEditorReady();
        },
      }),
    [args, stateControl]
  );

  const swfFeatureToggleChannelApiImpl = useMemo(
    () => featureToggle && new SwfFeatureToggleChannelApiImpl(featureToggle),
    [featureToggle]
  );

  const swfServiceCatalogChannelApiImpl = useMemo(
    () =>
      args.channelApi &&
      services &&
      serviceRegistriesSettings &&
      new SwfServiceCatalogChannelApiImpl(args.channelApi, services, serviceRegistriesSettings),
    [args.channelApi, serviceRegistriesSettings, services]
  );

  const swfLanguageServiceChannelApiImpl = useMemo(
    () => args.channelApi && new SwfLanguageServiceChannelApiImpl(args.channelApi),
    [args.channelApi]
  );

  const channelApi = useMemo(
    () =>
      args.channelApi &&
      kogitoEditorChannelApiImpl &&
      new SwfCombinedEditorChannelApiImpl(
        kogitoEditorChannelApiImpl,
        swfFeatureToggleChannelApiImpl,
        swfServiceCatalogChannelApiImpl,
        swfLanguageServiceChannelApiImpl
      ),
    [
      args.channelApi,
      kogitoEditorChannelApiImpl,
      swfFeatureToggleChannelApiImpl,
      swfLanguageServiceChannelApiImpl,
      swfServiceCatalogChannelApiImpl,
    ]
  );

  return {
    stateControl,
    channelApi,
  };
}
