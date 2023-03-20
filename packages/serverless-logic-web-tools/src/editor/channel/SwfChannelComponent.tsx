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
  const { editor, workspaceFile, channelApiImpl } = { ...props };
  const [isReady, setReady] = useState(false);
  const virtualServiceRegistry = useVirtualServiceRegistry();
  const swfFeatureToggle = useSwfFeatureToggle(editor);
  const {
    serviceRegistry: { catalogStore },
  } = useSettingsDispatch();

  useUpdateVirtualServiceRegistryOnWorkspaceFileEvents({ workspaceFile: workspaceFile });
  useUpdateVirtualServiceRegistryOnVsrWorkspaceEvents({ catalogStore: catalogStore });
  useUpdateVirtualServiceRegistryOnVsrFileEvents({
    workspaceId: workspaceFile.workspaceId,
    catalogStore: catalogStore,
  });

  useEffect(() => {
    if (catalogStore.virtualServiceRegistry || catalogStore.currentFile === workspaceFile) {
      return;
    }

    catalogStore.setVirtualServiceRegistry(virtualServiceRegistry, workspaceFile);
  }, [catalogStore, virtualServiceRegistry, workspaceFile]);

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        setReady(false);
        catalogStore.refresh().then(() => {
          if (canceled.get()) {
            return;
          }
          setReady(true);
        });
      },
      [catalogStore]
    )
  );

  const languageService = useMemo(() => {
    const webToolsSwfLanguageService = new WebToolsSwfLanguageService(catalogStore);
    return webToolsSwfLanguageService.getLs(workspaceFile.relativePath);
  }, [workspaceFile, catalogStore]);

  const apiImpl = useMemo(() => {
    const lsChannelApiImpl = new SwfLanguageServiceChannelApiImpl(languageService);
    const serviceCatalogChannelApiImpl = new SwfServiceCatalogChannelApiImpl(catalogStore);
    const featureToggleChannelApiImpl = new SwfFeatureToggleChannelApiImpl(swfFeatureToggle);
    return new SwfCombinedEditorChannelApiImpl(
      channelApiImpl,
      featureToggleChannelApiImpl,
      serviceCatalogChannelApiImpl,
      lsChannelApiImpl
    );
  }, [languageService, catalogStore, swfFeatureToggle, channelApiImpl]);

  const onNotificationClick = useCallback(
    (notification: Notification) => {
      if (!editor || !notification.position) {
        return;
      }

      const messageBusClient = editor.getEnvelopeServer()
        .envelopeApi as unknown as MessageBusClientApi<ServerlessWorkflowCombinedEditorChannelApi>;

      messageBusClient.notifications.kogitoSwfCombinedEditor_moveCursorToPosition.send(
        new Position(notification.position.startLineNumber, notification.position.startColumn)
      );
    },
    [editor]
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
