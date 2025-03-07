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
import { DefaultDashbuilderMonacoEditorController, DashbuilderMonacoEditorApi } from "./DashbuilderMonacoEditorApi";
import { ChannelType, useKogitoEditorEnvelopeContext } from "@kie-tools-core/editor/dist/api";
import { DashbuilderEditorChannelApi } from "../editor";
import { EditorTheme } from "@kie-tools-core/editor/dist/api/EditorTheme";
import { initCodeLenses } from "./augmentation/codeLenses";
import { initAugmentationCommands } from "./augmentation/commands";
import { initCompletion } from "./augmentation/completion";
import { DashbuilderEditorEnvelopeApi } from "../api";

interface Props {
  content: string;
  fileName: string;
  onContentChange: (content: string) => void;
  channelType: ChannelType;
}

const RefForwardingDashbuilderMonacoEditor: React.ForwardRefRenderFunction<
  DashbuilderMonacoEditorApi | undefined,
  Props
> = ({ content, fileName, onContentChange, channelType }, forwardedRef) => {
  const container = useRef<HTMLDivElement>(null);
  const editorEnvelopeCtx = useKogitoEditorEnvelopeContext<DashbuilderEditorEnvelopeApi, DashbuilderEditorChannelApi>();
  const theme = EditorTheme.LIGHT;

  const controller: DashbuilderMonacoEditorApi = useMemo<DashbuilderMonacoEditorApi>(
    () => new DefaultDashbuilderMonacoEditorController(content, onContentChange, editorEnvelopeCtx.operatingSystem),
    [content, editorEnvelopeCtx.operatingSystem, onContentChange]
  );

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

export const DashbuilderMonacoEditor = React.forwardRef(RefForwardingDashbuilderMonacoEditor);
