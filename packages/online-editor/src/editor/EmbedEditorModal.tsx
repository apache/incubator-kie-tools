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
import {
  Alert,
  Button,
  Checkbox,
  List,
  ListItem,
  Modal,
  ModalVariant,
  Radio,
  TextInput,
  Tooltip
} from "@patternfly/react-core";
import { EmbeddableClass, FileExtension } from "../common/utils";
import { useCallback, useContext, useMemo, useEffect, useState, useRef } from "react";
import { useFileUrl } from "../common/Hooks";
import { GlobalContext } from "../common/GlobalContext";
import { EmbeddedEditorRef } from "@kogito-tooling/editor/dist/embedded";
import { useOnlineI18n } from "../common/i18n";

const BPMN_SOURCE = "https://kiegroup.github.io/kogito-online/standalone/bpmn/index.js";
const DMN_SOURCE = "https://kiegroup.github.io/kogito-online/standalone/dmn/index.js";

const editorEmbeddableClassMapping = new Map<FileExtension, EmbeddableClass>([
  ["bpmn", "BpmnEditor"],
  ["bpmn2", "BpmnEditor"],
  ["dmn", "DmnEditor"]
]);

interface Props {
  fileExtension?: FileExtension;
  editor: EmbeddedEditorRef | undefined;
  isOpen: boolean;
  onClose: () => void;
}

enum Source {
  CURRENT,
  GIST
}

export function EmbedEditorModal(props: Props) {
  const fileUrl = useFileUrl();
  const context = useContext(GlobalContext);
  const [readOnly, setReadonly] = useState(true);
  const [copyFromSource, setCopyFromSource] = useState(Source.CURRENT);
  const [copied, setCopied] = useState(false);
  const [embedCode, setEmbedCode] = useState("");
  const copyContentTextArea = useRef<HTMLTextAreaElement>(null);
  const { i18n } = useOnlineI18n();

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

  useEffect(() => {
    if (props.fileExtension) {
      const iframe = document.createElement("iframe");
      iframe.width = "100%";
      iframe.height = "100%";
      const embeddableClass = editorEmbeddableClassMapping.get(props.fileExtension)!;

      if (copyFromSource === Source.CURRENT) {
        props.editor?.getContent().then(editorContent => {
          const clearContent = editorContent.replace(/(\r\n|\n|\r)/gm, "");
          const script = getStandaloneEditorFromCurrentContent(embeddableClass, clearContent);
          iframe.srcdoc = getStandaloneEditorSrcdoc(script, props.fileExtension!);
          setEmbedCode(iframe.outerHTML);
        });
      }

      if (copyFromSource === Source.GIST) {
        const script = getStandaloneEditorFromGist(embeddableClass, fileUrl);
        iframe.srcdoc = getStandaloneEditorSrcdoc(script, props.fileExtension);
        setEmbedCode(iframe.outerHTML);
      }
    }
  }, [
    props.editor,
    props.fileExtension,
    copyFromSource,
    getStandaloneEditorSrcdoc,
    getStandaloneEditorFromCurrentContent,
    getStandaloneEditorFromGist
  ]);

  const onCopy = useCallback(() => {
    copyContentTextArea.current!.value = embedCode;
    copyContentTextArea.current!.select();
    if (document.execCommand("copy")) {
      setCopied(true);
    }
  }, [embedCode, copyContentTextArea]);

  return (
    <>
      <Modal
        variant={ModalVariant.small}
        aria-label={"Embed the editor and content in your page"}
        isOpen={props.isOpen}
        onClose={props.onClose}
        title={i18n.embedEditorModal.title}
        description={i18n.embedEditorModal.description}
        actions={[
          <Button key="confirm" variant="primary" onClick={onCopy}>
            {i18n.embedEditorModal.copy}
          </Button>,
          <Button key="cancel" variant="link" onClick={props.onClose}>
            {i18n.terms.close}
          </Button>
        ]}
      >
        <div>
          <Checkbox
            id={"is-readOnly"}
            label={i18n.embedEditorModal.readOnly.label}
            aria-label="Read only checkbox"
            description={i18n.embedEditorModal.readOnly.description}
            isChecked={readOnly}
            onChange={setReadonly}
          />
          <br />
          <div>
            <Radio
              aria-label="Current content source option"
              id={"current-content"}
              isChecked={copyFromSource === Source.CURRENT}
              name={"Current content"}
              label={i18n.embedEditorModal.source.current.label}
              description={i18n.embedEditorModal.source.current.description}
              onChange={() => setCopyFromSource(Source.CURRENT)}
            />
            <Tooltip
              aria-label={"Only available when editing a file from a GitHub gist"}
              content={<p>{i18n.embedEditorModal.source.gist.tooltip}</p>}
              trigger={!isGist ? "mouseenter click" : ""}
            >
              <Radio
                aria-label="GitHub gist source option"
                id={"export-gist"}
                isDisabled={!isGist}
                name={"GitHub gist"}
                label={i18n.embedEditorModal.source.gist.label}
                isChecked={copyFromSource === Source.GIST}
                description={i18n.embedEditorModal.source.gist.description}
                onChange={() => setCopyFromSource(Source.GIST)}
              />
            </Tooltip>
          </div>
        </div>
        <br />
        <div style={{ display: "flex", alignItems: "center" }}>
          <p style={{ width: "150px" }}>{i18n.embedEditorModal.embedCode}</p>
          <TextInput aria-label={"Embed code"} value={embedCode} type={"text"} isReadOnly={true} />
        </div>
        <br />
        {copied ? (
          <Alert
            style={{ height: "50px" }}
            variant="success"
            title={i18n.embedEditorModal.copiedToClipboard}
            isInline={true}
          />
        ) : (
          <div style={{ height: "50px" }} />
        )}
        <textarea ref={copyContentTextArea} style={{ height: 0, width: 0, position: "absolute", zIndex: -1 }} />
      </Modal>
    </>
  );
}
