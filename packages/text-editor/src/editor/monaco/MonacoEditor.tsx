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

import { ChannelType, EditorTheme, useKogitoEditorEnvelopeContext } from "@kie-tools-core/editor/dist/api";
import { useSharedValue } from "@kie-tools-core/envelope-bus/dist/hooks";
import { editor } from "monaco-editor";
import { extname } from "path";
import * as React from "react";
import { useEffect, useImperativeHandle, useMemo, useRef } from "react";
import { TextEditorChannelApi } from "../../api";
import { MonacoEditorApi, MonacoEditorController } from "./MonacoEditorController";

interface Props {
  content: string;
  fileName: string;
  onContentChange: (content: string) => void;
  channelType: ChannelType;
  setValidationErrors: (errors: editor.IMarker[]) => void;
  isReadOnly: boolean;
}

const extensionLanguageMap = new Map<string, string>([
  [".yml", "yaml"],
  [".bpmn", "xml"],
  [".bpmn2", "xml"],
  [".dmn", "xml"],
  [".scesim", "xml"],
  [".pmml", "xml"],
  [".md", "markdown"],
]);

const RefForwardingMonacoEditor: React.ForwardRefRenderFunction<MonacoEditorApi | undefined, Props> = (
  { content, fileName, onContentChange, channelType, isReadOnly, setValidationErrors },
  forwardedRef
) => {
  const container = useRef<HTMLDivElement>(null);
  const editorEnvelopeCtx = useKogitoEditorEnvelopeContext<TextEditorChannelApi>();
  const [theme] = useSharedValue(editorEnvelopeCtx.channelApi?.shared.kogitoEditor_theme);

  const controller: MonacoEditorApi = useMemo<MonacoEditorApi>(() => {
    return new MonacoEditorController(
      content,
      onContentChange,
      extensionLanguageMap.get(extname(fileName.toLowerCase())) ?? extname(fileName).slice(1),
      editorEnvelopeCtx.operatingSystem,
      isReadOnly,
      setValidationErrors
    );
  }, [content, editorEnvelopeCtx.operatingSystem, fileName, isReadOnly, onContentChange, setValidationErrors]);

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

    controller.show(container.current, theme ?? EditorTheme.LIGHT);

    return () => {
      controller.dispose();
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

export const MonacoEditor = React.forwardRef(RefForwardingMonacoEditor);
