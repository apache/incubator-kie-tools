/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from "react";
import {
  ForwardRefRenderFunction,
  useImperativeHandle,
  useEffect,
  forwardRef,
  useState,
  useMemo,
  useCallback,
} from "react";
import { useSettingsDispatch } from "../../settings/SettingsContext";
import {
  useUpdateVirtualServiceRegistryOnVsrFileEvents,
  useUpdateVirtualServiceRegistryOnVsrWorkspaceEvents,
  useUpdateVirtualServiceRegistryOnWorkspaceFileEvents,
} from "../../virtualServiceRegistry/hooks/useUpdateVirtualServiceRegistry";
import { useVirtualServiceRegistry } from "../../virtualServiceRegistry/VirtualServiceRegistryContext";
import { SwfLanguageServiceChannelApiImpl } from "../api/SwfLanguageServiceChannelApiImpl";
import { SwfServiceCatalogChannelApiImpl } from "../api/SwfServiceCatalogChannelApiImpl";
import { WebToolsSwfLanguageService } from "../api/WebToolsSwfLanguageService";
import { useSwfFeatureToggle } from "../hooks/useSwfFeatureToggle";
import { EditorChannelComponentProps, EditorChannelComponentRef } from "../WebToolsEmbeddedEditor";
import {
  SwfCombinedEditorChannelApiImpl,
  SwfFeatureToggleChannelApiImpl,
} from "@kie-tools/serverless-workflow-combined-editor/dist/impl";
import { MessageBusClientApi } from "@kie-tools-core/envelope-bus/dist/api";
import { ServerlessWorkflowCombinedEditorChannelApi } from "@kie-tools/serverless-workflow-combined-editor/dist/api";
import { Notification } from "@kie-tools-core/notifications/dist/api";
import { Position } from "monaco-editor";
import { useCancelableEffect } from "@kie-tools-core/react-hooks/dist/useCancelableEffect";

const RefForwardingSwfChannelComponent: ForwardRefRenderFunction<
  EditorChannelComponentRef,
  EditorChannelComponentProps
> = (props, fowardedRef) => {
  const [isReady, setReady] = useState(false);
  const settingsDispatch = useSettingsDispatch();
  const virtualServiceRegistry = useVirtualServiceRegistry();
  const swfFeatureToggle = useSwfFeatureToggle(props.editor);

  useUpdateVirtualServiceRegistryOnWorkspaceFileEvents({ workspaceFile: props.workspaceFile });
  useUpdateVirtualServiceRegistryOnVsrWorkspaceEvents({ catalogStore: settingsDispatch.serviceRegistry.catalogStore });
  useUpdateVirtualServiceRegistryOnVsrFileEvents({
    workspaceId: props.workspaceFile.workspaceId,
    catalogStore: settingsDispatch.serviceRegistry.catalogStore,
  });

  useEffect(() => {
    if (
      !settingsDispatch.serviceRegistry.catalogStore.virtualServiceRegistry ||
      settingsDispatch.serviceRegistry.catalogStore.currentFile !== props.workspaceFile
    ) {
      settingsDispatch.serviceRegistry.catalogStore.setVirtualServiceRegistry(
        virtualServiceRegistry,
        props.workspaceFile
      );
    }
  }, [settingsDispatch.serviceRegistry.catalogStore, virtualServiceRegistry, props]);

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        setReady(false);
        settingsDispatch.serviceRegistry.catalogStore.refresh().then(() => {
          if (canceled.get()) {
            return;
          }
          setReady(true);
        });
      },
      [settingsDispatch.serviceRegistry.catalogStore]
    )
  );

  const languageService = useMemo(() => {
    const webToolsSwfLanguageService = new WebToolsSwfLanguageService(settingsDispatch.serviceRegistry.catalogStore);
    return webToolsSwfLanguageService.getLs(props.workspaceFile.relativePath);
  }, [props.workspaceFile, settingsDispatch.serviceRegistry.catalogStore]);

  const apiImpl = useMemo(() => {
    const lsChannelApiImpl = new SwfLanguageServiceChannelApiImpl(languageService);
    const serviceCatalogChannelApiImpl = new SwfServiceCatalogChannelApiImpl(
      settingsDispatch.serviceRegistry.catalogStore
    );
    const featureToggleChannelApiImpl = new SwfFeatureToggleChannelApiImpl(swfFeatureToggle);
    return new SwfCombinedEditorChannelApiImpl(
      props.channelApiImpl,
      featureToggleChannelApiImpl,
      serviceCatalogChannelApiImpl,
      lsChannelApiImpl
    );
  }, [languageService, settingsDispatch.serviceRegistry.catalogStore, swfFeatureToggle, props.channelApiImpl]);

  const messageBusClient = useMemo(
    () =>
      props.editor?.getEnvelopeServer()
        .envelopeApi as unknown as MessageBusClientApi<ServerlessWorkflowCombinedEditorChannelApi>,
    [props.editor]
  );

  const onNotificationClick = useCallback(
    (notification: Notification) => {
      if (!notification.position) {
        return;
      }
      messageBusClient.notifications.kogitoSwfCombinedEditor_moveCursorToPosition.send(
        new Position(notification.position.startLineNumber, notification.position.startColumn)
      );
    },
    [messageBusClient]
  );

  useImperativeHandle(
    fowardedRef,
    () => {
      return {
        kogitoEditorChannelApi: apiImpl,
        languageService: languageService,
        isReady,
        notificationHandler: {
          isSupported: true,
          onClick: onNotificationClick,
        },
      };
    },
    [apiImpl, languageService, isReady, onNotificationClick]
  );

  return <></>;
};

export const SwfChannelComponent = forwardRef(RefForwardingSwfChannelComponent);
