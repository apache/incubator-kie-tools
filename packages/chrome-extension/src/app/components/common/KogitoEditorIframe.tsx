/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { ResourceContentRequest, ResourceListRequest } from "@kie-tools-core/workspace/dist/api";
import { EmbeddedEditor, useEditorRef } from "@kie-tools-core/editor/dist/embedded";
import { ChannelType } from "@kie-tools-core/editor/dist/api";
import * as React from "react";
import { useCallback, useContext, useEffect, useImperativeHandle, useMemo } from "react";
import { runScriptOnPage } from "../../utils";
import { useGitHubApi } from "./GitHubContext";
import { useGlobals } from "./GlobalContext";
import { IsolatedEditorContext } from "./IsolatedEditorContext";
import { IsolatedEditorRef } from "./IsolatedEditorRef";
import { useChromeExtensionI18n } from "../../i18n";

interface Props {
  openFileExtension: string;
  contentPath: string;
  getFileContents: () => Promise<string | undefined>;
  readonly: boolean;
  onSetContentError: () => void;
}

const RefForwardingKogitoEditorIframe: React.ForwardRefRenderFunction<IsolatedEditorRef | undefined, Props> = (
  props,
  forwardedRef
) => {
  const githubApi = useGitHubApi();
  const { editor, editorRef } = useEditorRef();
  const { envelopeLocator, resourceContentServiceFactory } = useGlobals();
  const { repoInfo, textMode, fullscreen, onEditorReady } = useContext(IsolatedEditorContext);
  const { locale } = useChromeExtensionI18n();
  const wasOnTextMode = usePrevious(textMode);

  const resourceContentService = useMemo(() => {
    return resourceContentServiceFactory.createNew(githubApi.octokit(), repoInfo);
  }, [repoInfo]);

  const onResourceContentRequest = useCallback(
    (request: ResourceContentRequest) => resourceContentService.get(request.path, request.opts),
    [resourceContentService]
  );

  const onResourceContentList = useCallback(
    (request: ResourceListRequest) => resourceContentService.list(request.pattern, request.opts),
    [resourceContentService]
  );

  // Wrap file content into object for EmbeddedEditor
  const file = useMemo(() => {
    return {
      fileName: props.contentPath,
      fileExtension: props.openFileExtension,
      getFileContents: props.getFileContents,
      isReadOnly: props.readonly,
    };
  }, [props.contentPath, props.openFileExtension, props.getFileContents, props.readonly]);

  // When changing from textMode to !textMode, we should update the diagram content
  useEffect(() => {
    if (!textMode && wasOnTextMode) {
      props.getFileContents().then((content) => editor?.setContent(props.contentPath, content ?? ""));
    }
  }, [textMode, wasOnTextMode, editor]);

  // When !textMode, we should listen for changes on the diagram to update GitHub's default text editor.
  useEffect(() => {
    if (props.readonly || textMode || !editor) {
      return;
    }

    const stateControlSubscription = editor.getStateControl().subscribe(() => {
      editor.getContent().then((content) => {
        const pre = (document.getElementById("kogito-content") ?? document.createElement("pre")) as HTMLPreElement;
        pre.textContent = content;
        pre.style.display = "none";
        pre.id = "kogito-content";
        document.body.appendChild(pre);
        runScriptOnPage(chrome.runtime.getURL(`scripts/update_content.js`));
      });
    });
    return () => editor.getStateControl().unsubscribe(stateControlSubscription);
  }, [textMode, editor]);

  // Forward reference methods to set content programmatically vs property
  useImperativeHandle(
    forwardedRef,
    () => {
      if (!editor) {
        return undefined;
      }

      return {
        setContent: (content: string) => editor.setContent(props.contentPath, content),
      };
    },
    [editor]
  );

  return (
    <>
      <div className={`kogito-iframe ${fullscreen ? "fullscreen" : "not-fullscreen"}`}>
        <EmbeddedEditor
          ref={editorRef}
          file={file}
          channelType={ChannelType.GITHUB}
          kogitoEditor_ready={onEditorReady}
          kogitoWorkspace_resourceContentRequest={onResourceContentRequest}
          kogitoWorkspace_resourceListRequest={onResourceContentList}
          kogitoEditor_setContentError={props.onSetContentError}
          editorEnvelopeLocator={envelopeLocator}
          locale={locale}
        />
      </div>
    </>
  );
};

export const KogitoEditorIframe = React.forwardRef(RefForwardingKogitoEditorIframe);

function usePrevious<T>(value: T): T | undefined {
  const ref = React.useRef<T>();

  useEffect(() => {
    ref.current = value;
  }, [value]);

  return ref.current;
}
