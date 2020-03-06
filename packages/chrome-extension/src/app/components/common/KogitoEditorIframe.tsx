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

import * as React from "react";
import { useContext, useEffect, useImperativeHandle, useMemo, useRef } from "react";
import { IsolatedEditorContext } from "./IsolatedEditorContext";
import { EnvelopeBusOuterMessageHandler } from "@kogito-tooling/microeditor-envelope-protocol";
import { runScriptOnPage } from "../../utils";
import { useGlobals } from "./GlobalContext";
import { IsolatedEditorRef } from "./IsolatedEditorRef";
import { useGitHubApi } from "../common/GitHubContext";
import { EditorContent, ResourceContentRequest } from "@kogito-tooling/core-api";

const GITHUB_CODEMIRROR_EDITOR_SELECTOR = `.file-editor-textarea + .CodeMirror`;
const GITHUB_EDITOR_SYNC_POLLING_INTERVAL = 1500;

interface Props {
  openFileExtension: string;
  contentPath: string;
  getFileContents: () => Promise<string | undefined>;
  readonly: boolean;
}

const RefForwardingKogitoEditorIframe: React.RefForwardingComponent<IsolatedEditorRef, Props> = (
  props,
  forwardedRef
) => {
  const githubApi = useGitHubApi();
  const ref = useRef<HTMLIFrameElement>(null);
  const { router, editorIndexPath, resourceContentServiceFactory, logger } = useGlobals();
  const { repoInfo, textMode, fullscreen, onEditorReady } = useContext(IsolatedEditorContext);

  const resourceContentService = useMemo(() => {
    return resourceContentServiceFactory.createNew(githubApi.octokit(), repoInfo);
  }, [repoInfo]);

  const envelopeBusOuterMessageHandler = useMemo(() => {
    return new EnvelopeBusOuterMessageHandler(
      {
        postMessage: msg => {
          if (ref.current && ref.current.contentWindow) {
            ref.current.contentWindow.postMessage(msg, "*");
          }
        }
      },
      self => ({
        pollInit() {
          self.request_initResponse(window.location.origin);
        },
        receive_languageRequest() {
          self.respond_languageRequest(router.getLanguageData(props.openFileExtension));
        },
        receive_contentResponse(editorContent: EditorContent) {
          if (props.readonly) {
            return;
          }

          //keep line breaks
          const content = editorContent.content.split("\n").join("\\n");

          runScriptOnPage(
            `document.querySelector("${GITHUB_CODEMIRROR_EDITOR_SELECTOR}").CodeMirror.setValue('${content}')`
          );
        },
        receive_contentRequest() {
          props.getFileContents().then(c => {
            self.respond_contentRequest({
              content: c || "",
              path: props.contentPath || ""
            });
          });
        },
        receive_setContentError() {
          //TODO: Display a nice message with explanation why "setContent" failed
          logger.log("Set content error");
        },
        receive_dirtyIndicatorChange(isDirty: boolean) {
          //TODO: Perhaps show window.alert to warn that the changes were not saved?
          logger.log(`Dirty indicator changed to ${isDirty}`);
        },
        receive_ready() {
          logger.log(`Editor is ready`);
          onEditorReady?.();
        },
        receive_resourceContentRequest(resourceContentRequest: ResourceContentRequest) {
          console.debug(`Trying to read content from ${resourceContentRequest.path}`);
          resourceContentService.get(resourceContentRequest.path, resourceContentRequest.opts).then(r => {
            self.respond_resourceContent(r!);
          });
        },
        receive_readResourceContentError(errorMessage: string) {
          console.debug(`Error message retrieving a resource content ${errorMessage}`);
        },
        receive_resourceListRequest(pattern: string) {
          resourceContentService.list(pattern).then(list => self.respond_resourceList(list));
        }
      })
    );
  }, [router, resourceContentService]);

  useEffect(() => {
    if (textMode) {
      envelopeBusOuterMessageHandler.request_contentResponse();
      return;
    }

    if (props.readonly) {
      return;
    }

    let task: number;
    Promise.resolve()
      .then(() => props.getFileContents())
      .then(c => envelopeBusOuterMessageHandler.respond_contentRequest({ content: c || "" }))
      .then(() => {
        task = window.setInterval(
          () => envelopeBusOuterMessageHandler.request_contentResponse(),
          GITHUB_EDITOR_SYNC_POLLING_INTERVAL
        );
      });

    return () => clearInterval(task);
  }, [textMode, envelopeBusOuterMessageHandler]);

  useEffect(() => {
    const listener = (msg: MessageEvent) => envelopeBusOuterMessageHandler.receive(msg.data);
    window.addEventListener("message", listener, false);
    envelopeBusOuterMessageHandler.startInitPolling();

    return () => {
      envelopeBusOuterMessageHandler.stopInitPolling();
      window.removeEventListener("message", listener);
    };
  }, [envelopeBusOuterMessageHandler]);

  useImperativeHandle(
    forwardedRef,
    () => {
      if (!ref.current) {
        return null;
      }

      return {
        setContent: (content: string) => {
          envelopeBusOuterMessageHandler.respond_contentRequest({ content: content });
          return Promise.resolve();
        }
      };
    },
    []
  );

  return (
    <iframe
      ref={ref}
      className={`kogito-iframe ${fullscreen ? "fullscreen" : "not-fullscreen"}`}
      src={router.getRelativePathTo(editorIndexPath)}
    />
  );
};

export const KogitoEditorIframe = React.forwardRef(RefForwardingKogitoEditorIframe);
