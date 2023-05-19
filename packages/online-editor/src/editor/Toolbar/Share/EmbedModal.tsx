/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import { Radio } from "@patternfly/react-core/dist/js/components/Radio";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { ClipboardCopy, ClipboardCopyVariant } from "@patternfly/react-core/dist/js/components/ClipboardCopy";
import { useOnlineI18n } from "../../../i18n";
import { WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { WorkspaceDescriptor } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceDescriptor";
import { GistOrigin, WorkspaceKind } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceOrigin";
import { Stack, StackItem } from "@patternfly/react-core/dist/js/layouts/Stack";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Alert } from "@patternfly/react-core/dist/js/components/Alert";
import { WorkspaceGitStatusType } from "@kie-tools-core/workspaces-git-fs/dist/hooks/WorkspaceHooks";
import { PromiseState } from "@kie-tools-core/react-hooks/dist/PromiseState";

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

export function EmbedModal(props: {
  workspace: WorkspaceDescriptor;
  workspaceFile: WorkspaceFile;
  workspaceGitStatusPromise: PromiseState<WorkspaceGitStatusType>;
  isOpen: boolean;
  onClose: () => void;
}) {
  const [embedCode, setEmbedCode] = useState("");
  const [contentSource, setContentSource] = useState(
    props.workspace.origin.kind === WorkspaceKind.GITHUB_GIST ? ContentSource.GIST : ContentSource.CURRENT_CONTENT
  );
  const [editorContent, setEditorContent] = useState("");
  const { i18n } = useOnlineI18n();
  const hasLocalChanges = props.workspaceGitStatusPromise.data?.hasLocalChanges;

  useEffect(() => {
    if (props.isOpen) {
      props.workspaceFile.getFileContentsAsString().then(setEditorContent);
    }
  }, [props.workspaceFile, props.isOpen]);

  const isGist = useMemo(() => props.workspace.origin.kind === WorkspaceKind.GITHUB_GIST, [props.workspace]);

  const getCurrentContentScript = useCallback((content: string, libraryName: string) => {
    const fileContent = content.replace(/(\r\n|\n|\r)/gm, "");
    return `
<script>
  ${libraryName}.open({container: document.body, readOnly: true, initialContent: '${fileContent}', origin: "*" })
</script>
`;
  }, []);

  const getGithubGistScript = useCallback(
    (libraryName: string) => {
      const gistId = (props.workspace.origin as GistOrigin).url.toString().split("/").pop()!.replace(".git", "");
      return `
<script type="module">
  import {Octokit} from "https://cdn.skypack.dev/@octokit/rest";
  async function main() {
    const gist = await new Octokit().gists.get({ gist_id: "${gistId}" });
    ${libraryName}.open({
      container: document.body,
      readOnly: true,
      initialContent: await fetch(gist.data.files["${props.workspaceFile.relativePath}"].raw_url).then(r => r.text()),
      origin: "*",
      resources: new Map([...Object.keys(gist.data.files)].map(n => ([n, { contentType: "text", content: fetch(gist.data.files[n].raw_url).then(r => r.text()) }])))
    });
  }
  main();
</script>
`;
    },
    [props.workspace, props.workspaceFile]
  );

  const getStandaloneEditorIframeSrcdoc = useCallback((contentScript: string, standaloneEditorLibraryUrl: string) => {
    return `<!DOCTYPE html>
    <html lang="en">
    <head>
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
      <script src="${standaloneEditorLibraryUrl}"></script>
    </head>
    <body>
      ${contentScript}
    </body>
    </html>`;
  }, []);

  useEffect(() => {
    if (
      props.workspaceFile.extension !== "bpmn" &&
      props.workspaceFile.extension !== "bpmn2" &&
      props.workspaceFile.extension !== "dmn"
    ) {
      return;
    }

    const iframe = document.createElement("iframe");
    iframe.width = "100%";
    iframe.height = "100%";
    iframe.style.border = "0";
    const { libraryName, scriptUrl } = editorStandaloneClassMapping.get(props.workspaceFile.extension)!;

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
    props.workspaceFile,
  ]);

  return (
    <Modal
      variant={ModalVariant.medium}
      aria-label={"Embed the editor and content in your page"}
      isOpen={props.isOpen}
      onClose={props.onClose}
      title={i18n.embedModal.title}
      description={i18n.embedModal.description}
    >
      {hasLocalChanges && contentSource === ContentSource.GIST && (
        <>
          <Alert isInline={true} variant={"warning"} title={i18n.embedModal.source.gist.alert}></Alert>
          <br />
        </>
      )}
      <div style={{ padding: "0 16px 0 16px" }}>
        <Radio
          aria-label="Current content source option"
          id={"current-content"}
          isChecked={contentSource === ContentSource.CURRENT_CONTENT}
          name={"Current content"}
          label={i18n.embedModal.source.current.label}
          description={i18n.embedModal.source.current.description}
          onChange={() => setContentSource(ContentSource.CURRENT_CONTENT)}
        />
        <br />
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
        <Stack hasGutter={true}>
          <StackItem>
            <TextContent>
              <Text component={TextVariants.h4}>{i18n.embedModal.embedCode}</Text>
            </TextContent>
          </StackItem>
          <StackItem>
            <ClipboardCopy
              isCode={true}
              clickTip={"Copied"}
              hoverTip={"Copy"}
              variant={ClipboardCopyVariant.expansion}
              aria-label={"Embed code"}
            >
              {embedCode
                .split("\n")
                .map((line) => line.trim())
                .join("")}
            </ClipboardCopy>
          </StackItem>
        </Stack>
      </div>
    </Modal>
  );
}
