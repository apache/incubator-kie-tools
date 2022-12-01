/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import {
  ChannelType,
  EditorApi,
  EditorEnvelopeLocator,
  KogitoEditorChannelApi,
  KogitoEditorEnvelopeApi,
} from "../../api";
import { useSyncedKeyboardEvents } from "@kie-tools-core/keyboard-shortcuts/dist/channel";
import { useGuidedTourPositionProvider } from "@kie-tools-core/guided-tour/dist/channel";
import type * as CSS from "csstype";
import * as React from "react";
import { useCallback, useImperativeHandle, useMemo, useRef, useState } from "react";
import { EmbeddedEditorFile, StateControl } from "../../channel";
import { useEffectAfterFirstRender } from "../common";
import { EmbeddedEditorChannelApiImpl } from "./EmbeddedEditorChannelApiImpl";
import { EnvelopeServer } from "@kie-tools-core/envelope-bus/dist/channel";
import { useConnectedEnvelopeServer } from "@kie-tools-core/envelope-bus/dist/hooks";
import { getEditorIframeProps } from "../../channel/editorIframeProps";

type Omit<T, K extends keyof T> = Pick<T, Exclude<keyof T, K>>;

type ChannelApiMethodsAlreadyImplementedByEmbeddedEditor =
  | "kogitoGuidedTour_guidedTourUserInteraction"
  | "kogitoGuidedTour_guidedTourRegisterTutorial"
  | "kogitoEditor_contentRequest";

type EmbeddedEditorChannelApiOverrides = Partial<
  Omit<KogitoEditorChannelApi, ChannelApiMethodsAlreadyImplementedByEmbeddedEditor>
>;

export type Props = EmbeddedEditorChannelApiOverrides & {
  file: EmbeddedEditorFile;
  editorEnvelopeLocator: EditorEnvelopeLocator;
  channelType: ChannelType;
  locale: string;
  customChannelApiImpl?: KogitoEditorChannelApi;
  stateControl?: StateControl;
  isReady?: boolean;
};

/**
 * Forward reference for the `EmbeddedEditor` to support consumers to call upon embedded operations.
 */
export type EmbeddedEditorRef = EditorApi & {
  isReady: boolean;
  iframeRef: React.RefObject<HTMLIFrameElement>;
  getStateControl(): StateControl;
  getEnvelopeServer(): EnvelopeServer<KogitoEditorChannelApi, KogitoEditorEnvelopeApi>;
};

const containerStyles: CSS.Properties = {
  display: "flex",
  flex: 1,
  flexDirection: "column",
  width: "100%",
  height: "100%",
  border: "none",
  margin: 0,
  padding: 0,
  overflow: "hidden",
};

const RefForwardingEmbeddedEditor: React.ForwardRefRenderFunction<EmbeddedEditorRef | undefined, Props> = (
  props: Props,
  forwardedRef
) => {
  const iframeRef = useRef<HTMLIFrameElement>(null);
  const stateControl = useMemo(
    () => props.stateControl ?? new StateControl(),
    [props.file.getFileContents, props.stateControl]
  );
  const [isReady, setReady] = useState(false);
  const envelopeMapping = useMemo(
    () => props.editorEnvelopeLocator.getEnvelopeMapping(props.file.path ?? props.file.fileName),
    [props.editorEnvelopeLocator, props.file]
  );

  //Setup envelope bus communication
  const channelApiImpl = useMemo(() => {
    return (
      props.customChannelApiImpl ??
      new EmbeddedEditorChannelApiImpl(stateControl, props.file, props.locale, {
        ...props,
        kogitoEditor_ready: () => {
          setReady(true);
          props.kogitoEditor_ready?.();
        },
      })
    );
  }, [stateControl, props]);

  const envelopeServer = useMemo(() => {
    return new EnvelopeServer<KogitoEditorChannelApi, KogitoEditorEnvelopeApi>(
      { postMessage: (message) => iframeRef.current?.contentWindow?.postMessage(message, "*") },
      props.editorEnvelopeLocator.targetOrigin,
      (self) =>
        self.envelopeApi.requests.kogitoEditor_initRequest(
          { origin: self.origin, envelopeServerId: self.id },
          {
            fileExtension: props.file.fileExtension,
            resourcesPathPrefix: envelopeMapping?.resourcesPathPrefix ?? "",
            initialLocale: props.locale,
            isReadOnly: props.file.isReadOnly,
            channel: props.channelType,
          }
        )
    );
  }, [
    props.editorEnvelopeLocator.targetOrigin,
    props.file.fileExtension,
    props.file.isReadOnly,
    props.locale,
    props.channelType,
    envelopeMapping?.resourcesPathPrefix,
  ]);

  useConnectedEnvelopeServer(envelopeServer, channelApiImpl);

  useEffectAfterFirstRender(() => {
    envelopeServer.envelopeApi.notifications.kogitoI18n_localeChange.send(props.locale);
  }, [props.locale]);

  useEffectAfterFirstRender(() => {
    props.file.getFileContents().then((content) => {
      envelopeServer.envelopeApi.requests.kogitoEditor_contentChanged(
        { content: content!, path: props.file.fileName },
        { showLoadingOverlay: true }
      );
    });
  }, [props.file.getFileContents]);

  // Register position provider for Guided Tour
  useGuidedTourPositionProvider(envelopeServer.envelopeApi, iframeRef);

  // Forward keyboard events to the EditorEnvelope
  useSyncedKeyboardEvents(envelopeServer.envelopeApi);

  //Forward reference methods
  useImperativeHandle(
    forwardedRef,
    () => {
      if (!iframeRef.current) {
        return undefined;
      }

      return {
        iframeRef,
        isReady: props.isReady ?? isReady,
        getStateControl: () => stateControl,
        getEnvelopeServer: () => envelopeServer,
        getElementPosition: (s) =>
          envelopeServer.envelopeApi.requests.kogitoGuidedTour_guidedTourElementPositionRequest(s),
        undo: () => Promise.resolve(envelopeServer.envelopeApi.notifications.kogitoEditor_editorUndo.send()),
        redo: () => Promise.resolve(envelopeServer.envelopeApi.notifications.kogitoEditor_editorRedo.send()),
        getContent: () => envelopeServer.envelopeApi.requests.kogitoEditor_contentRequest().then((c) => c.content),
        getPreview: () => envelopeServer.envelopeApi.requests.kogitoEditor_previewRequest(),
        setContent: (path, content) =>
          envelopeServer.envelopeApi.requests.kogitoEditor_contentChanged(
            { path, content },
            { showLoadingOverlay: false }
          ),
        validate: () => envelopeServer.envelopeApi.requests.kogitoEditor_validate(),
        setTheme: (theme) => Promise.resolve(envelopeServer.shared.kogitoEditor_theme.set(theme)),
      };
    },
    [props.isReady, isReady, stateControl, envelopeServer]
  );

  return (
    <>
      {!envelopeMapping && (
        <>
          <span>{`No Editor available for '${props.file.fileExtension}' extension`}</span>
        </>
      )}
      {envelopeMapping && (
        <iframe
          ref={iframeRef}
          id={"kogito-iframe"}
          data-testid={"kogito-iframe"}
          title="Kogito editor"
          style={containerStyles}
          data-envelope-channel={props.channelType}
          {...getEditorIframeProps(envelopeMapping)}
        />
      )}
    </>
  );
};

export const EmbeddedEditor = React.forwardRef(RefForwardingEmbeddedEditor);
