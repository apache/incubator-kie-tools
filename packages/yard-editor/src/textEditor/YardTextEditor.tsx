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
import { useEffect, useImperativeHandle, useMemo, useRef } from "react";
import { YardTextEditorController, YardTextEditorApi } from "./YardTextEditorController";
import { ChannelType, EditorTheme, useKogitoEditorEnvelopeContext } from "@kie-tools-core/editor/dist/api";
import { useSharedValue } from "@kie-tools-core/envelope-bus/dist/hooks";
import { YardEditorChannelApi, YardEditorEnvelopeApi } from "../api";
import { editor } from "monaco-editor";
import { YardFile } from "../types";
import { initCodeLenses } from "./augmentation/codeLenses";
import { initAugmentationCommands } from "./augmentation/commands";
import { initCompletion } from "./augmentation/completion";

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
  const editorEnvelopeCtx = useKogitoEditorEnvelopeContext<YardEditorEnvelopeApi, YardEditorChannelApi>();
  const [theme] = useSharedValue(editorEnvelopeCtx.channelApi?.shared.kogitoEditor_theme);

  const controller: YardTextEditorApi = useMemo<YardTextEditorApi>(() => {
    if (
      file.normalizedPosixPathRelativeToTheWorkspaceRoot.endsWith(".yard.yaml") ||
      file.normalizedPosixPathRelativeToTheWorkspaceRoot.endsWith(".yard.yml")
    ) {
      return new YardTextEditorController(
        file.content,
        onContentChange,
        "yaml",
        editorEnvelopeCtx.operatingSystem,
        isReadOnly,
        setValidationErrors
      );
    }
    throw new Error(`Unsupported extension '${file.normalizedPosixPathRelativeToTheWorkspaceRoot}'`);
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

    const instance = controller.show(container.current, theme ?? EditorTheme.LIGHT);
    const commands = initAugmentationCommands(instance, editorEnvelopeCtx.channelApi);
    const completion = initCompletion(commands, editorEnvelopeCtx.channelApi);
    const codeLenses = initCodeLenses(commands, editorEnvelopeCtx.channelApi);

    return () => {
      controller.dispose();
      codeLenses.dispose();
      completion.dispose();
    };
  }, [file, channelType, controller, theme, editorEnvelopeCtx.channelApi, editorEnvelopeCtx.operatingSystem]);

  useImperativeHandle(forwardedRef, () => controller, [controller]);

  return <div style={{ height: "100%" }} ref={container} />;
};

export const YardTextEditor = React.forwardRef(RefForwardingYardTextEditor);
