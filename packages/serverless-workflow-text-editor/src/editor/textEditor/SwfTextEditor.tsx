/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from "react";
import { useEffect, useImperativeHandle, useMemo, useRef, useCallback } from "react";
import { SwfTextEditorController, SwfTextEditorApi } from "./SwfTextEditorController";
import { initCompletion } from "./augmentation/completion";
import { initJsonCodeLenses } from "./augmentation/codeLenses";
import { initAugmentationCommands } from "./augmentation/commands";
import { ChannelType, EditorTheme, useKogitoEditorEnvelopeContext } from "@kie-tools-core/editor/dist/api";
import { useSharedValue, useSubscription } from "@kie-tools-core/envelope-bus/dist/hooks";
import { getFileLanguage } from "@kie-tools/serverless-workflow-language-service/dist/api";
import { ServerlessWorkflowTextEditorChannelApi } from "../../api";
import { editor } from "monaco-editor";

interface Props {
  content: string;
  fileName: string;
  onContentChange: (content: string) => void;
  channelType: ChannelType;
  setValidationErrors: (errors: editor.IMarker[]) => void;
  isReadOnly: boolean;
}

const RefForwardingSwfTextEditor: React.ForwardRefRenderFunction<SwfTextEditorApi | undefined, Props> = (
  { content, fileName, onContentChange, channelType, isReadOnly, setValidationErrors },
  forwardedRef
) => {
  const container = useRef<HTMLDivElement>(null);
  const editorEnvelopeCtx = useKogitoEditorEnvelopeContext<ServerlessWorkflowTextEditorChannelApi>();
  const [theme] = useSharedValue(editorEnvelopeCtx.channelApi?.shared.kogitoEditor_theme);
  const [services] = useSharedValue(editorEnvelopeCtx.channelApi?.shared.kogitoSwfServiceCatalog_services);
  const [serviceRegistriesSettings] = useSharedValue(
    editorEnvelopeCtx.channelApi?.shared.kogitoSwfServiceCatalog_serviceRegistriesSettings
  );

  const fileLanguage = useMemo(() => getFileLanguage(fileName), [fileName]);

  const onSelectionChanged = useCallback((nodeName: string) => {
    editorEnvelopeCtx.channelApi.notifications.kogitoSwfTextEditor__onSelectionChanged.send({ nodeName });
  }, []);

  const controller: SwfTextEditorApi = useMemo<SwfTextEditorApi>(() => {
    if (fileLanguage !== null) {
      return new SwfTextEditorController(
        content,
        onContentChange,
        fileLanguage,
        editorEnvelopeCtx.operatingSystem,
        isReadOnly,
        setValidationErrors,
        onSelectionChanged
      );
    }
    throw new Error(`Unsupported extension '${fileName}'`);
  }, [content, editorEnvelopeCtx.operatingSystem, fileName, onContentChange, isReadOnly, setValidationErrors]);

  useEffect(() => {
    controller.forceRedraw();
  }, [services, serviceRegistriesSettings, controller]);

  useEffect(() => {
    if (!container.current) {
      return;
    }

    if (editorEnvelopeCtx.channelApi && theme === undefined) {
      return;
    }

    const instance = controller.show(container.current, theme ?? EditorTheme.LIGHT);
    const commands = initAugmentationCommands(instance, editorEnvelopeCtx.channelApi);

    const completion = initCompletion(commands, editorEnvelopeCtx.channelApi);
    const codeLenses = initJsonCodeLenses(commands, editorEnvelopeCtx.channelApi);

    return () => {
      controller.dispose();
      codeLenses.dispose();
      completion.dispose();
    };
  }, [
    content,
    fileName,
    channelType,
    controller,
    theme,
    editorEnvelopeCtx.channelApi,
    editorEnvelopeCtx.operatingSystem,
  ]);

  useImperativeHandle(forwardedRef, () => controller, [controller]);

  return <div style={{ height: "100%" }} ref={container} />;
};

export const SwfTextEditor = React.forwardRef(RefForwardingSwfTextEditor);
