/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from "react";
import { Button, Checkbox, Divider, Modal, ModalVariant, Radio } from "@patternfly/react-core";
import { EmbeddableClass, FileExtension } from "../common/utils";
import { useCallback, useContext, useMemo, useRef, useState } from "react";
import { useFileUrl } from "../common/Hooks";
import { GlobalContext } from "../common/GlobalContext";

const BPMN_SOURCE = "https://kiegroup.github.io/kogito-online/standalone/bpmn/index.js";
const DMN_SOURCE = "https://kiegroup.github.io/kogito-online/standalone/dmn/index.js";

const editorEmbeddableClassMapping = new Map<FileExtension, EmbeddableClass>([
  ["bpmn", "BpmnEditor"],
  ["bpmn2", "BpmnEditor"],
  ["dmn", "DmnEditor"]
]);

interface Props {
  fileExtension?: FileExtension;
  editor: any;
  isOpen: boolean;
  onClose: () => void;
}

enum Source {
  CURRENT,
  GIST
}

export function ExportStandaloneEditorModal(props: Props) {
  const fileUrl = useFileUrl();
  const context = useContext(GlobalContext);
  const [readOnly, setReadonly] = useState(true);
  const [copyFromSource, setCopyFromSource] = useState(Source.CURRENT);
  const copyContentTextArea = useRef<HTMLTextAreaElement>(null);
  const [copied, setCopied] = useState(false);

  const isGist = useMemo(() => context.githubService.isGist(fileUrl), [fileUrl, context]);

  const getStandaloneEditorFromGist = useCallback(
    (editor: EmbeddableClass, gistUrl: string) => {
      return `
    <script>
      fetch("${gistUrl}")
        .then(response => response.text())
        .then(content => ${editor}.open({container: document.body, readOnly: ${readOnly}, initialContent: content, origin: "*" }))
    </script>`;
    },
    [readOnly]
  );

  const getStandaloneEditorFromCurrentContent = useCallback(
    (editor: EmbeddableClass, content: string) => {
      return `
    <script>
      ${editor}.open({container: document.body, readOnly: ${readOnly}, initialContent: '${content}', origin: "*" })
    </script>`;
    },
    [readOnly]
  );

  const getStandaloneEditorSrcdoc = useCallback((script: string, type: FileExtension) => {
    return `<!DOCTYPE html>
    <html lang="en">
    <head>
      <script src="${type === "dmn" ? DMN_SOURCE : BPMN_SOURCE}"></script>
      <title></title>
      <style>
        html,
        body,
        iframe {
          margin: 0;
          border: 0;
          padding: 0;
          height: 100%;
          width: 100%;
        }
      </style>
    </head>
    <body>
      ${script}
    </body>
    </html>`;
  }, []);

  const onCopy = useCallback(async () => {
    if (props.fileExtension) {
      const iframe = document.createElement("iframe");
      iframe.width = "100%";
      iframe.height = "100%";
      const embeddableClass = editorEmbeddableClassMapping.get(props.fileExtension)!;

      if (copyFromSource === Source.CURRENT) {
        const content = ((await props.editor?.getContent()) ?? "").replace(/(\r\n|\n|\r)/gm, "");
        const script = getStandaloneEditorFromCurrentContent(embeddableClass, content);
        iframe.srcdoc = getStandaloneEditorSrcdoc(script, props.fileExtension);
      }

      if (copyFromSource === Source.GIST) {
        const script = getStandaloneEditorFromGist(embeddableClass, fileUrl);
        iframe.srcdoc = getStandaloneEditorSrcdoc(script, props.fileExtension);
      }

      copyContentTextArea.current!.value = iframe.outerHTML;
      copyContentTextArea.current!.select();

      if (document.execCommand("copy")) {
        setCopied(true);
      }
    }
  }, [
    props.editor,
    props.fileExtension,
    copyContentTextArea,
    copyFromSource,
    getStandaloneEditorSrcdoc,
    getStandaloneEditorFromCurrentContent,
    getStandaloneEditorFromGist
  ]);

  return (
    <>
      <Modal
        data-testid={"export-iframe-modal"}
        variant={ModalVariant.small}
        aria-label={"Export to Iframe"}
        isOpen={props.isOpen}
        onClose={props.onClose}
        title={"Export to Iframe"}
        description={
          "Export your content using the Standalone Editor. Copy to your clip board an iframe element where you can embed on your applications."
        }
        actions={[
          <Button key="confirm" variant="primary" onClick={onCopy}>
            Copy
          </Button>,
          <Button key="cancel" variant="link" onClick={props.onClose}>
            Close
          </Button>
        ]}
      >
        <div>
          <div>
            <p>Choose if the generate Standalone Editor is read only:</p>
            <Checkbox
              id={"is-readOnly"}
              label="Read only"
              aria-label="Read only checkbox"
              description={"New edits made on top of the standalone editor aren't saved."}
              isChecked={readOnly}
              onChange={setReadonly}
            />
          </div>
          <br />

          <div>
            <p>Choose your source:</p>
            <Radio
              aria-label="Export from current content option"
              id={"export-content"}
              defaultChecked={true}
              isChecked={copyFromSource === Source.CURRENT}
              name={"Export from current content"}
              label={"Export from current content"}
              description={"This option will export a static version of the current content"}
              onChange={() => setCopyFromSource(Source.CURRENT)}
            />
            <Radio
              aria-label="Export from gist option - Only available if a gist is being used"
              id={"export-gist"}
              isDisabled={!isGist}
              name={"Export from gist"}
              label={"Export from gist"}
              isChecked={copyFromSource === Source.GIST}
              description={
                "This option will export a dynamic version based on your gist. If the gist content changes the editor content will also be changed."
              }
              onChange={() => setCopyFromSource(Source.GIST)}
            />
          </div>
        </div>
        <br />
        {copied ? <p>Copied to the clip board</p> : <br />}
        <textarea ref={copyContentTextArea} style={{ height: 0, width: 0, position: "absolute", zIndex: -1 }} />
      </Modal>
    </>
  );
}
