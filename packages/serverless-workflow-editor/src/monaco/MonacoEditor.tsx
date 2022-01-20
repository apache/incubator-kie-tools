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
import { buildEditor, MonacoEditorApi } from "./augmentation";
import * as path from "path";

interface Props {
  content: string;
  fileName: string;
  onContentChange: (content: string) => void;
}

export interface MonacoEditorRef {
  undo(): Promise<void>;
  redo(): Promise<void>;
}

const RefForwardingMonacoEditor: React.ForwardRefRenderFunction<MonacoEditorRef | undefined, Props> = (
  { content, fileName, onContentChange },
  forwardedRef
) => {
  const editorContainer = useRef<HTMLDivElement>(null);
  const monacoInstance: MonacoEditorApi = useMemo<MonacoEditorApi>(() => {
    return buildEditor(content, fileName, onContentChange);
  }, [content, fileName]);

  useEffect(() => {
    if (editorContainer.current) {
      monacoInstance.show(editorContainer.current);
    }

    return () => {
      if (monacoInstance) {
        monacoInstance.dispose();
      }
    };
  }, [content, fileName]);

  useImperativeHandle(
    forwardedRef,
    () => {
      return {
        redo: () => {
          monacoInstance.redo();
          return Promise.resolve();
        },
        undo: () => {
          monacoInstance.undo();
          return Promise.resolve();
        },
      };
    },
    [monacoInstance]
  );

  return <div style={{ height: "100%" }} ref={editorContainer} />;
};

export const MonacoEditor = React.forwardRef(RefForwardingMonacoEditor);
