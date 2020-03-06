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
import { KogitoEditorIframe } from "./KogitoEditorIframe";
import { IsolatedEditorRef } from "./IsolatedEditorRef";
import { useGitHubApi } from "./GitHubContext";

interface Props {
  getFileContents: () => Promise<string | undefined>;
  contentPath: string;
  openFileExtension: string;
  readonly: boolean;
  textMode: boolean;
  keepRenderedEditorInTextMode: boolean;
}

const RefForwardingIsolatedEditor: React.RefForwardingComponent<IsolatedEditorRef, Props> = (props, forwardedRef) => {
  const shouldRenderIframe = (props.keepRenderedEditorInTextMode && props.textMode) || !props.textMode;
  const githubApi = useGitHubApi();

  return (
    <>
      {shouldRenderIframe && (
        <KogitoEditorIframe
          key={githubApi.token}
          ref={forwardedRef}
          contentPath={props.contentPath}
          openFileExtension={props.openFileExtension}
          getFileContents={props.getFileContents}
          readonly={props.readonly}
        />
      )}
    </>
  );
};

export const IsolatedEditor = React.forwardRef(RefForwardingIsolatedEditor);
