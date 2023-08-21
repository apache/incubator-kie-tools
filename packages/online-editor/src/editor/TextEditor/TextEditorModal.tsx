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

import { Modal } from "@patternfly/react-core/dist/js/components/Modal";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import * as React from "react";
import { useEffect, useRef, useState } from "react";
import { useOnlineI18n } from "../../i18n";
import { EmbeddedEditorRef } from "@kie-tools-core/editor/dist/embedded";
import { WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";

const importMonacoEditor = () => import(/* webpackChunkName: "monaco-editor" */ "@kie-tools-core/monaco-editor");

export function TextEditorModal(props: {
  editor: EmbeddedEditorRef | undefined;
  isOpen: boolean;
  workspaceFile: WorkspaceFile;
  refreshEditor: () => void;
}) {
  const { i18n } = useOnlineI18n();
  const textEditorContainerRef = useRef<HTMLDivElement>(null);
  const [textEditorContent, setTextEditorContext] = useState<string | undefined>(undefined);

  useEffect(() => {
    if (!props.isOpen) {
      return;
    }

    let monacoInstance: any;

    importMonacoEditor().then((monaco) => {
      monacoInstance = monaco.editor.create(textEditorContainerRef.current!, {
        value: textEditorContent!,
        language: "xml", //FIXME: Not all editors will be XML when converted to text
        scrollBeyondLastLine: false,
      });
    });

    return () => {
      if (!monacoInstance || !props.workspaceFile) {
        return;
      }

      const contentAfterFix = monacoInstance.getValue();
      monacoInstance.dispose();

      props.editor
        ?.setContent(props.workspaceFile.name, contentAfterFix)
        .then(() => {
          props.editor?.getStateControl().updateCommandStack({
            id: "fix-from-text-editor",
            undo: () => {
              if (props.workspaceFile) {
                props.editor?.setContent(props.workspaceFile.name, textEditorContent!);
              }
            },
            redo: () => {
              if (props.workspaceFile) {
                props.editor?.setContent(props.workspaceFile.name, contentAfterFix).then(props.refreshEditor);
              }
            },
          });
        })
        .catch(() => {
          setTextEditorContext(contentAfterFix);
        });
    };
  }, [props.refreshEditor, props.isOpen, props.editor, props.workspaceFile, textEditorContent]);

  useEffect(() => {
    props.workspaceFile.getFileContentsAsString().then((content) => {
      setTextEditorContext(content);
    });
  }, [props.workspaceFile]);

  return (
    <Modal
      showClose={false}
      width={"100%"}
      height={"100%"}
      title={i18n.editorPage.textEditorModal.title(props.workspaceFile.nameWithoutExtension)}
      isOpen={props.isOpen}
      actions={[
        <Button key="confirm" variant="primary" onClick={props.refreshEditor}>
          {i18n.terms.done}
        </Button>,
      ]}
    >
      <div style={{ width: "100%", minHeight: "calc(100vh - 210px)" }} ref={textEditorContainerRef} />
    </Modal>
  );
}
