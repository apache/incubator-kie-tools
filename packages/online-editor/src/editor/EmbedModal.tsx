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
import { useCallback, useContext, useEffect, useMemo, useState } from "react";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import { Radio } from "@patternfly/react-core/dist/js/components/Radio";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { ClipboardCopy } from "@patternfly/react-core/dist/js/components/ClipboardCopy";
import { useOnlineI18n } from "../common/i18n";
import { useFileUrl } from "../common/Hooks";
import { EmbeddedEditorRef } from "@kie-tooling-core/editor/dist/embedded";
import { GlobalContext } from "../common/GlobalContext";

type SupportedStandaloneEditorFileExtensions = "bpmn" | "bpmn2" | "dmn";
type StandaloneEditorLibraryName = "BpmnEditor" | "DmnEditor";

interface StandaloneConfigs {
  libraryName: StandaloneEditorLibraryName;
  scriptUrl: string;
}

const editorStandaloneClassMapping = new Map<SupportedStandaloneEditorFileExtensions, StandaloneConfigs>([
  [
    "bpmn",
    { libraryName: "BpmnEditor", scriptUrl: "https://kiegroup.github.io/kogito-online/standalone/bpmn/index.js" },
  ],
  [
    "bpmn2",
    { libraryName: "BpmnEditor", scriptUrl: "https://kiegroup.github.io/kogito-online/standalone/bpmn/index.js" },
  ],
  ["dmn", { libraryName: "DmnEditor", scriptUrl: "https://kiegroup.github.io/kogito-online/standalone/dmn/index.js" }],
]);

enum ContentSource {
  CURRENT_CONTENT,
  GIST,
}

interface Props {
  isOpen: boolean;
  onClose: () => void;
  editor?: EmbeddedEditorRef;
  fileExtension: string;
}

export function EmbedModal(props: Props) {
  const context = useContext(GlobalContext);
  const [embedCode, setEmbedCode] = useState("");
  const [contentSource, setContentSource] = useState(ContentSource.CURRENT_CONTENT);
  const { i18n } = useOnlineI18n();
  const fileUrl = useFileUrl();

  const isGist = useMemo(() => context.githubService.isGist(fileUrl), [fileUrl, context]);

  const isSupportedStandaloneEditorFileExtensions = useCallback(
    (toBeDetermined: string): toBeDetermined is SupportedStandaloneEditorFileExtensions => {
      return toBeDetermined === "bpmn" || toBeDetermined === "bpmn2" || toBeDetermined === "dmn";
    },
    []
  );

  const getCurrentContentScript = useCallback(
    async (libraryName: string) => {
      const editorContent = ((await props.editor?.getContent()) ?? "").replace(/(\r\n|\n|\r)/gm, "");
      return `
    <script>
      ${libraryName}.open({container: document.body, readOnly: true, initialContent: '${editorContent}', origin: "*" })
    </script>`;
    },
    [props.editor]
  );

  const getGithubGistScript = useCallback(
    (libraryName: string) => {
      return `
    <script>
      fetch("${fileUrl}")
        .then(response => response.text())
        .then(content => ${libraryName}.open({container: document.body, readOnly: true, initialContent: content, origin: "*" }))
    </script>`;
    },
    [fileUrl]
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

    const script =
      contentSource === ContentSource.CURRENT_CONTENT
        ? await getCurrentContentScript(libraryName)
        : getGithubGistScript(libraryName);

    iframe.srcdoc = getStandaloneEditorIframeSrcdoc(script, scriptUrl);
    return iframe.outerHTML;
  }, [
    props.isOpen,
    props.fileExtension,
    contentSource,
    getGithubGistScript,
    getCurrentContentScript,
    getStandaloneEditorIframeSrcdoc,
    isSupportedStandaloneEditorFileExtensions,
  ]);

  useEffect(() => {
    getStandaloneEditorIframeOuterHtml().then((outerHtml) => setEmbedCode(outerHtml));
  }, [getStandaloneEditorIframeOuterHtml]);

  return (
    <Modal
      variant={ModalVariant.small}
      aria-label={"Embed the editor and content in your page"}
      isOpen={props.isOpen}
      onClose={props.onClose}
      title={i18n.embedModal.title}
      description={i18n.embedModal.description}
      actions={[
        <Button key="cancel" variant="link" onClick={props.onClose}>
          {i18n.terms.close}
        </Button>,
      ]}
    >
      <Radio
        aria-label="Current content source option"
        id={"current-content"}
        isChecked={contentSource === ContentSource.CURRENT_CONTENT}
        name={"Current content"}
        label={i18n.embedModal.source.current.label}
        description={i18n.embedModal.source.current.description}
        onChange={() => setContentSource(ContentSource.CURRENT_CONTENT)}
      />
      <Tooltip
        aria-label={"Only available when editing a file from a GitHub gist"}
        content={<p>{i18n.embedModal.source.gist.tooltip}</p>}
        trigger={!isGist ? "mouseenter click" : ""}
      >
        <Radio
          aria-label="GitHub gist source option"
          id={"github-gist"}
          isDisabled={!isGist}
          name={"GitHub gist"}
          label={i18n.embedModal.source.gist.label}
          isChecked={contentSource === ContentSource.GIST}
          description={i18n.embedModal.source.gist.description}
          onChange={() => setContentSource(ContentSource.GIST)}
        />
      </Tooltip>
      <br />
      <div className={"kogito--editor__embed-modal-embed-code"}>
        <p className={"kogito--editor__embed-modal-embed-code-items"}>{i18n.embedModal.embedCode}</p>
        <ClipboardCopy
          className={"kogito--editor__embed-modal-embed-code-items"}
          aria-label={"Embed code"}
          type={"text"}
        >
          {embedCode}
        </ClipboardCopy>
      </div>
    </Modal>
  );
}
