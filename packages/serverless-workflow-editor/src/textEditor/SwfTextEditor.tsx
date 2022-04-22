/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
import { useEffect, useImperativeHandle, useMemo, useRef } from "react";
import {
  DefaultSwfTextEditorController,
  SwfTextEditorOperation,
  SwfTextEditorController,
} from "./SwfTextEditorController";
import { initJsonCompletion } from "./augmentation/completion";
import { initJsonCodeLenses } from "./augmentation/codeLenses";
import { initAugmentationCommands } from "./augmentation/commands";
import { ChannelType, EditorTheme, useKogitoEditorEnvelopeContext } from "@kie-tools-core/editor/dist/api";
import { useSharedValue } from "@kie-tools-core/envelope-bus/dist/hooks";
import { ServerlessWorkflowEditorChannelApi } from "../api";

interface Props {
  content: string;
  fileUri: string;
  onContentChange: (content: string, operation: SwfTextEditorOperation, versionId?: number) => void;
  channelType: ChannelType;
}

const RefForwardingSwfTextEditor: React.ForwardRefRenderFunction<SwfTextEditorController | undefined, Props> = (
  { content, fileUri, onContentChange, channelType },
  forwardedRef
) => {
  const container = useRef<HTMLDivElement>(null);
  const editorEnvelopeCtx = useKogitoEditorEnvelopeContext<ServerlessWorkflowEditorChannelApi>();
  const [theme] = useSharedValue(editorEnvelopeCtx.channelApi.shared.kogitoEditor_theme);

  const controller = useMemo<SwfTextEditorController>(() => {
    if (fileUri.endsWith(".sw.json")) {
      return new DefaultSwfTextEditorController(onContentChange, "json", editorEnvelopeCtx.operatingSystem, fileUri);
    }
    if (fileUri.endsWith(".sw.yaml") || fileUri.endsWith(".sw.yml")) {
      return new DefaultSwfTextEditorController(onContentChange, "yaml", editorEnvelopeCtx.operatingSystem, fileUri);
    }

    throw new Error(`Unsupported extension '${fileUri}'`);
  }, [editorEnvelopeCtx.operatingSystem, fileUri, onContentChange]);

  useEffect(() => {
    if (!container.current) {
      return;
    }

    if (editorEnvelopeCtx.channelApi && theme === undefined) {
      return;
    }

    const instance = controller.show(container.current, theme ?? EditorTheme.LIGHT);
    const commands = initAugmentationCommands(instance, editorEnvelopeCtx.channelApi);

    initJsonCompletion(commands, editorEnvelopeCtx.channelApi);
    initJsonCodeLenses(commands, editorEnvelopeCtx.channelApi);

    // TODO: Add support to YAML
    // initYamlCompletion(commands, editorEnvelopeCtx.channelApi);
    // initYamlWidgets(commands, editorEnvelopeCtx.channelApi);
  }, [controller, theme, editorEnvelopeCtx.channelApi, editorEnvelopeCtx.operatingSystem]);

  useEffect(() => {
    controller.setContent(content);
  }, [controller, content]);

  useImperativeHandle(forwardedRef, () => controller, [controller]);

  return <div style={{ height: "100%" }} ref={container} />;
};

export const SwfTextEditor = React.forwardRef(RefForwardingSwfTextEditor);
