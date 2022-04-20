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
import { DefaultSwfMonacoEditorController, SwfMonacoEditorApi } from "./SwfMonacoEditorApi";
import { initJsonCompletion } from "./augmentation/completion";
import { initJsonCodeLenses } from "./augmentation/codeLenses";
import { initAugmentationCommands } from "./augmentation/commands";
import { ChannelType, EditorTheme, useKogitoEditorEnvelopeContext } from "@kie-tools-core/editor/dist/api";
import { useSharedValue } from "@kie-tools-core/envelope-bus/dist/hooks";
import { SwfServiceCatalogSingleton } from "../serviceCatalog";
import { ServerlessWorkflowEditorChannelApi } from "../editor";

interface Props {
  content: string;
  fileName: string;
  onContentChange: (content: string) => void;
  channelType: ChannelType;
}

const RefForwardingSwfMonacoEditor: React.ForwardRefRenderFunction<SwfMonacoEditorApi | undefined, Props> = (
  { content, fileName, onContentChange, channelType },
  forwardedRef
) => {
  const container = useRef<HTMLDivElement>(null);
  const editorEnvelopeCtx = useKogitoEditorEnvelopeContext<ServerlessWorkflowEditorChannelApi>();
  const [theme] = useSharedValue(editorEnvelopeCtx.channelApi?.shared.kogitoEditor_theme);
  const [services] = useSharedValue(editorEnvelopeCtx.channelApi?.shared.kogitoSwfServiceCatalog_services);
  const [user] = useSharedValue(editorEnvelopeCtx.channelApi?.shared.kogitoSwfServiceCatalog_user);
  const [serviceRegistryUrl] = useSharedValue(
    editorEnvelopeCtx.channelApi?.shared.kogitoSwfServiceCatalog_serviceRegistryUrl
  );

  const controller: SwfMonacoEditorApi = useMemo<SwfMonacoEditorApi>(() => {
    if (fileName.endsWith(".sw.json")) {
      return new DefaultSwfMonacoEditorController(content, onContentChange, "json", editorEnvelopeCtx.operatingSystem);
    }
    if (fileName.endsWith(".sw.yaml") || fileName.endsWith(".sw.yml")) {
      return new DefaultSwfMonacoEditorController(content, onContentChange, "yaml", editorEnvelopeCtx.operatingSystem);
    }

    throw new Error(`Unsupported extension '${fileName}'`);
  }, [content, editorEnvelopeCtx.operatingSystem, fileName, onContentChange]);

  useEffect(() => {
    SwfServiceCatalogSingleton.init(services, user, serviceRegistryUrl);
    controller.forceRedraw();
  }, [services, user, serviceRegistryUrl, controller]);

  useEffect(() => {
    if (!container.current) {
      return;
    }

    if (editorEnvelopeCtx.channelApi && theme === undefined) {
      return;
    }

    const instance = controller.show(container.current, theme ?? EditorTheme.LIGHT);
    const commands = initAugmentationCommands(instance, editorEnvelopeCtx.channelApi);

    initJsonCompletion(commands);
    initJsonCodeLenses(commands, channelType, editorEnvelopeCtx.operatingSystem);

    // TODO: Add support to YAML
    // initYamlCompletion(commands);
    // initYamlWidgets(commands);
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

export const SwfMonacoEditor = React.forwardRef(RefForwardingSwfMonacoEditor);
