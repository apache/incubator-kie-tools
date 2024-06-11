/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import * as React from "react";
import { EmbeddedEditorFile } from "@kie-tools-core/editor/dist/channel";
import { EmbeddedEditor, useEditorRef } from "@kie-tools-core/editor/dist/embedded";
import { useState, useMemo, useCallback } from "react";
import {
  ChannelType,
  EditorEnvelopeLocator,
  EnvelopeMapping,
  EnvelopeContentType,
} from "@kie-tools-core/editor/dist/api";
import { useCancelableEffect } from "@kie-tools-core/react-hooks/dist/useCancelableEffect";
import { Dashboard } from "../data";
import { extractExtension } from "@kie-tools-core/workspaces-git-fs/dist/relativePath/WorkspaceFileRelativePathParser";
import { basename } from "path";

interface DashboardViewerProps {
  dashboard: Dashboard;
}

export function DashboardViewer(props: DashboardViewerProps) {
  const { editor, editorRef } = useEditorRef();
  const [embeddedEditorFile, setEmbeddedEditorFile] = useState<EmbeddedEditorFile>();

  const editorEnvelopeLocator = useMemo(
    () =>
      new EditorEnvelopeLocator(window.location.origin, [
        new EnvelopeMapping({
          type: "dash",
          filePathGlob: "**/*.dash.+(yml|yaml)",
          resourcesPathPrefix: "",
          envelopeContent: { type: EnvelopeContentType.PATH, path: "dashbuilder-viewer-envelope.html" },
        }),
      ]),
    []
  );

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        fetch(props.dashboard.uri)
          .then((response) => response.text())
          .then((content) => {
            if (canceled.get()) {
              return;
            }

            const extension = extractExtension(props.dashboard.uri);
            const filename = basename(props.dashboard.uri, `.${extension}`);

            setEmbeddedEditorFile({
              normalizedPosixPathRelativeToTheWorkspaceRoot: props.dashboard.uri,
              getFileContents: async () => content,
              isReadOnly: true,
              fileExtension: extension,
              fileName: filename,
            });
          });
      },
      [props.dashboard.uri]
    )
  );

  return (
    <div style={{ height: "100%" }}>
      {embeddedEditorFile && (
        <EmbeddedEditor
          channelType={ChannelType.EMBEDDED}
          editorEnvelopeLocator={editorEnvelopeLocator}
          locale={"en"}
          ref={editorRef}
          file={embeddedEditorFile}
        />
      )}
    </div>
  );
}
