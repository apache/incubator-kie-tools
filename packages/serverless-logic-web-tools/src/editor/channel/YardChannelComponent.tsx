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
import { ForwardRefRenderFunction, useImperativeHandle, forwardRef, useMemo, useCallback } from "react";
import { EditorChannelComponentProps, EditorChannelComponentRef } from "../WebToolsEmbeddedEditor";
import { YardEditorChannelApiImpl } from "@kie-tools/yard-editor/dist/impl";
import { YardYamlLanguageService } from "@kie-tools/yard-language-service/dist/channel";
import { YardLanguageServiceChannelApiImpl } from "../api/YardLanguageServiceChannelApiImpl";
import { MessageBusClientApi } from "@kie-tools-core/envelope-bus/dist/api";
import { Notification } from "@kie-tools-core/notifications/dist/api";
import { Position } from "monaco-editor";
import { YardEditorEnvelopeApi } from "@kie-tools/yard-editor/dist/api";

const RefForwardingYardChannelComponent: ForwardRefRenderFunction<
  EditorChannelComponentRef,
  EditorChannelComponentProps
> = (props, fowardedRef) => {
  const { editor, channelApiImpl } = { ...props };
  const languageService = useMemo(() => new YardYamlLanguageService(), []);

  const apiImpl = useMemo(() => {
    const lsChannelApiImpl = new YardLanguageServiceChannelApiImpl(languageService);
    return new YardEditorChannelApiImpl(channelApiImpl, lsChannelApiImpl);
  }, [channelApiImpl, languageService]);

  const onNotificationClick = useCallback(
    (notification: Notification) => {
      if (!editor || !notification.position) {
        return;
      }

      const yardEditorEnvelopeApi = editor.getEnvelopeServer()
        .envelopeApi as unknown as MessageBusClientApi<YardEditorEnvelopeApi>;

      yardEditorEnvelopeApi.notifications.yardTextEditor_moveCursorToPosition.send(
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
        isReady: true,
        notificationHandler: {
          isSupported: true,
          onClick: onNotificationClick,
        },
      };
    },
    [apiImpl, languageService, onNotificationClick]
  );

  return <></>;
};

export const YardChannelComponent = forwardRef(RefForwardingYardChannelComponent);
