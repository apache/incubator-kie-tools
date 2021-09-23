import { Modal } from "@patternfly/react-core/dist/js/components/Modal";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import * as React from "react";
import { useEffect, useRef, useState } from "react";
import { useOnlineI18n } from "../../common/i18n";
import { File } from "@kie-tooling-core/editor/dist/channel";
import { EmbeddedEditorRef } from "@kie-tooling-core/editor/dist/embedded";

const importMonacoEditor = () => import(/* webpackChunkName: "monaco-editor" */ "@kie-tooling-core/monaco-editor");

export function TextEditorModal(props: {
  editor: EmbeddedEditorRef | undefined;
  isOpen: boolean;
  currentFile: File;
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
      if (!monacoInstance) {
        return;
      }

      const contentAfterFix = monacoInstance.getValue();
      monacoInstance.dispose();

      props.editor
        ?.setContent(props.currentFile.fileName, contentAfterFix)
        .then(() => {
          props.editor?.getStateControl().updateCommandStack({
            id: "fix-from-text-editor",
            undo: () => {
              props.editor?.setContent(props.currentFile.fileName, textEditorContent!);
            },
            redo: () => {
              props.editor?.setContent(props.currentFile.fileName, contentAfterFix).then(props.refreshEditor);
            },
          });
        })
        .catch(() => {
          setTextEditorContext(contentAfterFix);
        });
    };
  }, [props.refreshEditor, props.isOpen, props.editor, props.currentFile, textEditorContent]);

  useEffect(() => {
    props.currentFile.getFileContents().then((content) => {
      setTextEditorContext(content);
    });
  }, [props.currentFile]);

  return (
    <Modal
      showClose={false}
      width={"100%"}
      height={"100%"}
      title={i18n.editorPage.textEditorModal.title(props.currentFile.fileName.split("/").pop()!)}
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
