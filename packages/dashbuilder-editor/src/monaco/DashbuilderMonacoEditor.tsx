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
import { DefaultDashbuilderMonacoEditorController, DashbuilderMonacoEditorApi } from "./DashbuilderMonacoEditorApi";
import { ChannelType, useKogitoEditorEnvelopeContext } from "@kie-tools-core/editor/dist/api";
import { DashbuilderEditorChannelApi } from "../editor";
import { EditorTheme } from "@kie-tools-core/editor/dist/api/EditorTheme";

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
  const editorEnvelopeCtx = useKogitoEditorEnvelopeContext<DashbuilderEditorChannelApi>();
  const theme = EditorTheme.LIGHT;

  const controller: DashbuilderMonacoEditorApi = useMemo<DashbuilderMonacoEditorApi>(
    () => new DefaultDashbuilderMonacoEditorController(content, onContentChange, editorEnvelopeCtx.operatingSystem),
    [content, editorEnvelopeCtx.operatingSystem, onContentChange]
  );

  useEffect(() => {
    if (!container.current) {
      return;
    }
    controller.show(container.current, theme);
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

export const DashbuilderMonacoEditor = React.forwardRef(RefForwardingDashbuilderMonacoEditor);
