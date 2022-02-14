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
import { initJsonWidgets } from "./augmentation/widgets";
import { initCommands } from "./augmentation/commands";

interface Props {
  content: string;
  fileName: string;
  onContentChange: (content: string) => void;
}

const RefForwardingSwfMonacoEditor: React.ForwardRefRenderFunction<SwfMonacoEditorApi | undefined, Props> = (
  { content, fileName, onContentChange },
  forwardedRef
) => {
  const container = useRef<HTMLDivElement>(null);

  const controller: SwfMonacoEditorApi = useMemo<SwfMonacoEditorApi>(() => {
    if (fileName.endsWith(".sw.json")) {
      return new DefaultSwfMonacoEditorController(content, onContentChange, "json");
    }
    if (fileName.endsWith(".sw.yaml") || fileName.endsWith(".sw.yml")) {
      return new DefaultSwfMonacoEditorController(content, onContentChange, "yaml");
    }

    throw new Error(`Unsupported extension '${fileName}'`);
  }, [content, fileName]);

  useEffect(() => {
    if (!container.current) {
      return;
    }

    const instance = controller.show(container.current);
    const commands = initCommands(instance);

    initJsonCompletion(commands);
    initJsonWidgets(commands);

    // TODO: Add support to YAML
    // initYamlCompletion(commands);
    // initYamlWidgets(commands);

    return () => controller.dispose();
  }, [content, fileName]);

  useImperativeHandle(forwardedRef, () => controller, [controller]);

  return <div style={{ height: "100%" }} ref={container} />;
};

export const SwfMonacoEditor = React.forwardRef(RefForwardingSwfMonacoEditor);
