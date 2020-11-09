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
import { Alert, Button, Checkbox, Modal, ModalVariant, Radio, TextInput, Tooltip } from "@patternfly/react-core";
import { useCallback, useContext, useMemo, useEffect, useState, useRef } from "react";
import { useFileUrl } from "../common/Hooks";
import { GlobalContext } from "../common/GlobalContext";
import { EmbeddedEditorRef } from "@kogito-tooling/editor/dist/embedded";
import { useOnlineI18n } from "../common/i18n";

type SupportedStandaloneEditorFileExtensions = "bpmn" | "bpmn2" | "dmn";
type StandaloneEditorLibraryName = "BpmnEditor" | "DmnEditor";

interface StandaloneConfigs {
  libraryName: StandaloneEditorLibraryName;
  scriptUrl: string;
}

const editorStandaloneClassMapping = new Map<SupportedStandaloneEditorFileExtensions, StandaloneConfigs>([
  [
    "bpmn",
    { libraryName: "BpmnEditor", scriptUrl: "https://kiegroup.github.io/kogito-online/standalone/bpmn/index.js" }
  ],
  [
    "bpmn2",
    { libraryName: "BpmnEditor", scriptUrl: "https://kiegroup.github.io/kogito-online/standalone/bpmn/index.js" }
  ],
  ["dmn", { libraryName: "DmnEditor", scriptUrl: "https://kiegroup.github.io/kogito-online/standalone/dmn/index.js" }]
]);

interface Props {
  fileExtension: string;
  editor?: EmbeddedEditorRef;
  isOpen: boolean;
  onClose: () => void;
}

enum ContentSource {
  CURRENT_CONTENT,
  GIST
}

export function EmbedEditorModal(props: Props) {
  const fileUrl = useFileUrl();
  const context = useContext(GlobalContext);
  const [readOnly, setReadonly] = useState(true);
  const [contentSource, setContentSource] = useState(ContentSource.CURRENT_CONTENT);
  const [copied, setCopied] = useState(false);
  const [embedCode, setEmbedCode] = useState("");
  const copyContentTextArea = useRef<HTMLInputElement>(null);
  const { i18n } = useOnlineI18n();

  const isGist = useMemo(() => context.githubService.isGist(fileUrl), [fileUrl, context]);

  const isSupportedStandaloneEditorFileExtensions = useCallback(
    (toBeDetermined: string): toBeDetermined is SupportedStandaloneEditorFileExtensions => {
      return toBeDetermined === "bpmn" || toBeDetermined === "bpmn2" || toBeDetermined === "dmn";
    },
    []
  );

  const getStandaloneEditorScriptFromGist = useCallback(
    (editor: StandaloneEditorLibraryName, gistUrl: string) => {
      return `
    <script>
      fetch("${gistUrl}")
        .then(response => response.text())
        .then(content => ${editor}.open({container: document.body, readOnly: ${readOnly}, initialContent: content, origin: "*" }))
    </script>`;
    },
    [readOnly]
  );

  const getStandaloneEditorScriptFromCurrentContent = useCallback(
    (editor: StandaloneEditorLibraryName, content: string) => {
      return `
    <script>
      ${editor}.open({container: document.body, readOnly: ${readOnly}, initialContent: '${content}', origin: "*" })
    </script>`;
    },
    [readOnly]
  );

  const getStandaloneEditorIframeSrcdoc = useCallback((script: string, scriptUrl: string) => {
    return `<!DOCTYPE html>
    <html lang="en">
    <head>
      <script src="${scriptUrl}"></script>
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

  const getStandaloneEditorIframeOuterHtml = useCallback(async () => {
    if (isSupportedStandaloneEditorFileExtensions(props.fileExtension)) {
      const iframe = document.createElement("iframe");
      iframe.width = "100%";
      iframe.height = "100%";
      const { libraryName, scriptUrl } = editorStandaloneClassMapping.get(props.fileExtension)!;

      if (contentSource === ContentSource.CURRENT_CONTENT) {
        const editorContent = ((await props.editor?.getContent()) ?? "").replace(/(\r\n|\n|\r)/gm, "");
        const script = getStandaloneEditorScriptFromCurrentContent(libraryName, editorContent);
        iframe.srcdoc = getStandaloneEditorIframeSrcdoc(script, scriptUrl);
        return iframe.outerHTML;
      }

      if (contentSource === ContentSource.GIST) {
        const script = getStandaloneEditorScriptFromGist(libraryName, fileUrl);
        iframe.srcdoc = getStandaloneEditorIframeSrcdoc(script, scriptUrl);
        return iframe.outerHTML;
      }
    }
    return "";
  }, [
    props.isOpen,
    props.editor,
    props.fileExtension,
    contentSource,
    getStandaloneEditorIframeSrcdoc,
    getStandaloneEditorScriptFromCurrentContent,
    getStandaloneEditorScriptFromGist,
    isSupportedStandaloneEditorFileExtensions
  ]);

  useEffect(() => {
    getStandaloneEditorIframeOuterHtml().then(outerHtml => setEmbedCode(outerHtml));
  }, [getStandaloneEditorIframeOuterHtml]);

  const onCopy = useCallback(() => {
    copyContentTextArea.current!.value = embedCode;
    copyContentTextArea.current!.select();
    if (document.execCommand("copy")) {
      setCopied(true);
    }
  }, [embedCode, copyContentTextArea]);

  return (
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
          id={"read-only"}
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
            isChecked={contentSource === ContentSource.CURRENT_CONTENT}
            name={"Current content"}
            label={i18n.embedEditorModal.source.current.label}
            description={i18n.embedEditorModal.source.current.description}
            onChange={() => setContentSource(ContentSource.CURRENT_CONTENT)}
          />
          <Tooltip
            aria-label={"Only available when editing a file from a GitHub gist"}
            content={<p>{i18n.embedEditorModal.source.gist.tooltip}</p>}
            trigger={!isGist ? "mouseenter click" : ""}
          >
            <Radio
              aria-label="GitHub gist source option"
              id={"github-gist"}
              isDisabled={!isGist}
              name={"GitHub gist"}
              label={i18n.embedEditorModal.source.gist.label}
              isChecked={contentSource === ContentSource.GIST}
              description={i18n.embedEditorModal.source.gist.description}
              onChange={() => setContentSource(ContentSource.GIST)}
            />
          </Tooltip>
        </div>
      </div>
      <br />
      <div className={"kogito--editor__embed-editor-modal-embed-code"}>
        <p className={"kogito--editor__embed-editor-modal-embed-code-label"}>{i18n.embedEditorModal.embedCode}</p>
        <TextInput
          ref={copyContentTextArea}
          aria-label={"Embed code"}
          value={embedCode}
          type={"text"}
          isReadOnly={true}
        />
      </div>
      <br />
      {copied ? (
        <Alert
          className={"kogito--editor__embed-editor-modal-copied-alert"}
          variant="success"
          title={i18n.embedEditorModal.copiedToClipboard}
          isInline={true}
        />
      ) : (
        <div className={"kogito--editor__embed-editor-modal-copied-alert"} />
      )}
    </Modal>
  );
}
