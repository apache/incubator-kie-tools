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
import { useCallback, useEffect, useMemo, useState } from "react";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import { Radio } from "@patternfly/react-core/dist/js/components/Radio";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { ClipboardCopy } from "@patternfly/react-core/dist/js/components/ClipboardCopy";
import { useOnlineI18n } from "../common/i18n";
import { EmbeddedEditorRef } from "@kie-tooling-core/editor/dist/embedded";
import { useSettings } from "../settings/SettingsContext";
import { useQueryParams } from "../queryParams/QueryParamsContext";
import { useGlobals } from "../common/GlobalContext";
import { QueryParams } from "../common/Routes";
import { File } from "@kie-tooling-core/editor/dist/channel";

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
  currentFile: File;
  isOpen: boolean;
  onClose: () => void;
  editor?: EmbeddedEditorRef;
}

export function EmbedModal(props: Props) {
  const queryParams = useQueryParams();
  const settings = useSettings();
  const globals = useGlobals();
  const [embedCode, setEmbedCode] = useState("");
  const [contentSource, setContentSource] = useState(ContentSource.CURRENT_CONTENT);
  const [editorContent, setEditorContent] = useState("");
  const { i18n } = useOnlineI18n();

  useEffect(() => {
    if (props.isOpen) {
      props.editor?.getContent().then((c) => setEditorContent(c));
    }
  }, [props.editor, props.isOpen]);

  const isGist = useMemo(
    () => settings.github.service.isGist(queryParams.get(QueryParams.FILE) ?? ""),
    [queryParams, settings.github.service]
  );

  const getCurrentContentScript = useCallback((content: string, libraryName: string) => {
    const editorContent = content.replace(/(\r\n|\n|\r)/gm, "");
    return `
    <script>
      ${libraryName}.open({container: document.body, readOnly: true, initialContent: '${editorContent}', origin: "*" })
    </script>`;
  }, []);

  const getGithubGistScript = useCallback(
    (libraryName: string) => {
      return `
    <script>
      fetch("${queryParams.get(QueryParams.FILE)}")
        .then(response => response.text())
        .then(content => ${libraryName}.open({container: document.body, readOnly: true, initialContent: content, origin: "*" }))
    </script>`;
    },
    [queryParams]
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

  useEffect(() => {
    if (
      props.currentFile.fileExtension !== "bpmn" &&
      props.currentFile.fileExtension !== "bpmn2" &&
      props.currentFile.fileExtension !== "dmn"
    ) {
      return;
    }

    const iframe = document.createElement("iframe");
    iframe.width = "100%";
    iframe.height = "100%";
    const { libraryName, scriptUrl } = editorStandaloneClassMapping.get(props.currentFile.fileExtension)!;

    const script =
      contentSource === ContentSource.CURRENT_CONTENT
        ? getCurrentContentScript(editorContent, libraryName)
        : getGithubGistScript(libraryName);

    iframe.srcdoc = getStandaloneEditorIframeSrcdoc(script, scriptUrl);
    setEmbedCode(iframe.outerHTML);
  }, [
    contentSource,
    editorContent,
    getCurrentContentScript,
    getGithubGistScript,
    getStandaloneEditorIframeSrcdoc,
    props.currentFile.fileExtension,
  ]);

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
