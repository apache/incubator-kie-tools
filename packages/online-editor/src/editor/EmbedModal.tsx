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
import { Alert, Button, Modal, ModalVariant, TextArea } from "@patternfly/react-core";
import { useCallback, useEffect, useState } from "react";
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
  isOpen: boolean;
  onClose: () => void;
  fileExtension: string;
  title: string;
  description: string;
  getContentScript: (libraryName: string) => Promise<string>;
}

export function EmbedModal(props: Props) {
  const [copied, setCopied] = useState(false);
  const [embedCode, setEmbedCode] = useState("");
  const { i18n } = useOnlineI18n();

  const isSupportedStandaloneEditorFileExtensions = useCallback(
    (toBeDetermined: string): toBeDetermined is SupportedStandaloneEditorFileExtensions => {
      return toBeDetermined === "bpmn" || toBeDetermined === "bpmn2" || toBeDetermined === "dmn";
    },
    []
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
    if (!isSupportedStandaloneEditorFileExtensions(props.fileExtension)) {
      return "";
    }

    const iframe = document.createElement("iframe");
    iframe.width = "100%";
    iframe.height = "100%";
    const { libraryName, scriptUrl } = editorStandaloneClassMapping.get(props.fileExtension)!;

    const script = await props.getContentScript(libraryName);

    iframe.srcdoc = getStandaloneEditorIframeSrcdoc(script, scriptUrl);
    return iframe.outerHTML;
  }, [
    props.isOpen,
    props.getContentScript,
    props.fileExtension,
    getStandaloneEditorIframeSrcdoc,
    isSupportedStandaloneEditorFileExtensions
  ]);

  useEffect(() => {
    getStandaloneEditorIframeOuterHtml().then(outerHtml => setEmbedCode(outerHtml));
  }, [getStandaloneEditorIframeOuterHtml]);

  const onCopy = useCallback(() => {
    const textArea = document.getElementById("embed-code-text-area") as HTMLTextAreaElement;
    textArea.select();
    if (document.execCommand("copy")) {
      setCopied(true);
    }
  }, []);

  return (
    <Modal
      variant={ModalVariant.small}
      aria-label={"Embed the editor and content in your page"}
      isOpen={props.isOpen}
      onClose={props.onClose}
      title={props.title}
      description={props.description}
      actions={[
        <Button key="confirm" variant="primary" onClick={onCopy}>
          {i18n.embedEditorModal.copy}
        </Button>,
        <Button key="cancel" variant="link" onClick={props.onClose}>
          {i18n.terms.close}
        </Button>
      ]}
    >
      <p>{i18n.embedEditorModal.embedCode}</p>
      <TextArea
        id={"embed-code-text-area"}
        aria-label={"Embed code"}
        value={embedCode}
        type={"text"}
      />
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
