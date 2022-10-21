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
import { useEffect, useImperativeHandle, useMemo, useRef } from "react";
import { YardTextEditorController, YardTextEditorApi } from "./YardTextEditorController";
import { ChannelType, EditorTheme, useKogitoEditorEnvelopeContext } from "@kie-tools-core/editor/dist/api";
import { useSharedValue } from "@kie-tools-core/envelope-bus/dist/hooks";
import { YardEditorChannelApi } from "../api";
import { editor } from "monaco-editor";
import { YardFile } from "../types";

interface Props {
  file: YardFile;
  onContentChange: (content: string) => void;
  channelType: ChannelType;
  setValidationErrors: (errors: editor.IMarker[]) => void;
  isReadOnly: boolean;
}

const RefForwardingYardTextEditor: React.ForwardRefRenderFunction<YardTextEditorApi | undefined, Props> = (
  { file, onContentChange, channelType, isReadOnly, setValidationErrors },
  forwardedRef
) => {
  const container = useRef<HTMLDivElement>(null);
  const editorEnvelopeCtx = useKogitoEditorEnvelopeContext<YardEditorChannelApi>();
  const [theme] = useSharedValue(editorEnvelopeCtx.channelApi?.shared.kogitoEditor_theme);

  const controller: YardTextEditorApi = useMemo<YardTextEditorApi>(() => {
    if (file.path.endsWith(".yard.json")) {
      return new YardTextEditorController(
        file.content,
        onContentChange,
        "json",
        editorEnvelopeCtx.operatingSystem,
        isReadOnly,
        setValidationErrors
      );
    }
    if (file.path.endsWith(".yard.yaml") || file.path.endsWith(".yard.yml")) {
      return new YardTextEditorController(
        file.content,
        onContentChange,
        "yaml",
        editorEnvelopeCtx.operatingSystem,
        isReadOnly,
        setValidationErrors
      );
    }
    throw new Error(`Unsupported extension '${file.path}'`);
  }, [editorEnvelopeCtx.operatingSystem, file, onContentChange, isReadOnly, setValidationErrors]);

  useEffect(() => {
    controller.forceRedraw();
  }, [controller]);

  useEffect(() => {
    if (!container.current) {
      return;
    }

    if (editorEnvelopeCtx.channelApi && theme === undefined) {
      return;
    }

    // TODO: Add support to JSON code completion and code lenses
    const instance = controller.show(container.current, theme ?? EditorTheme.LIGHT);
    // const commands = initAugmentationCommands(instance, editorEnvelopeCtx.channelApi);
    // initJsonCompletion(commands, editorEnvelopeCtx.channelApi);
    // initJsonCodeLenses(commands, editorEnvelopeCtx.channelApi);

    return () => {
      controller.dispose();
    };

    // TODO: Add support to YAML
    // initYamlCompletion(commands);
    // initYamlWidgets(commands);
  }, [file, channelType, controller, theme, editorEnvelopeCtx.channelApi, editorEnvelopeCtx.operatingSystem]);

  useImperativeHandle(forwardedRef, () => controller, [controller]);

  return <div style={{ height: "100%" }} ref={container} />;
};

export const YardTextEditor = React.forwardRef(RefForwardingYardTextEditor);
