/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
import { File } from "@kie-tooling-core/editor/dist/channel";
import { useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import { extractFileExtension } from "../common/utils";
import { ActiveWorkspace } from "./model/ActiveWorkspace";
import { resolveKind } from "./model/WorkspaceOrigin";
import { SUPPORTED_FILES_EDITABLE, SUPPORTED_FILES_PATTERN } from "./SupportedFiles";
import { useWorkspaces } from "./WorkspaceContext";

export interface Props {
  workspaceId: string;
}

// TODO CAPONETTO: Temporary; Improve this
export function WorkspaceOverviewPage(props: Props) {
  const workspaces = useWorkspaces();
  const [workspace, setWorkspace] = useState<ActiveWorkspace | undefined>();
  const [fetchWorkspaceError, setFetchWorkspaceError] = useState<string>();

  const files = useMemo(() => {
    if (!workspace) {
      return null;
    }

    return workspace.files.map((file: File) => {
      const filePath = file.path!.replace("/" + workspace.descriptor.context + "/", "");
      const extension = extractFileExtension(filePath)!;
      const isSupported = SUPPORTED_FILES_EDITABLE.includes(extension);
      return (
        <div key={file.path}>
          {isSupported ? (
            <Link to={`/workspace/${workspace.descriptor.context}/file/${filePath}`}>{file.path}</Link>
          ) : (
            <h2>{filePath}</h2>
          )}
        </div>
      );
    });
  }, [workspace]);

  useEffect(() => {
    setFetchWorkspaceError(undefined);
    workspaces.workspaceService.get(props.workspaceId).then(async (descriptor) => {
      if (!descriptor) {
        setFetchWorkspaceError("Workspace not found");
        return;
      }

      const files = await workspaces.workspaceService.listFiles(descriptor, SUPPORTED_FILES_PATTERN);

      setWorkspace({
        descriptor: descriptor,
        files: files,
        kind: resolveKind(descriptor.origin),
      });
    });
  }, [props.workspaceId, workspaces.workspaceService]);

  return (
    <>
      {fetchWorkspaceError && <div>{fetchWorkspaceError}</div>}
      {!fetchWorkspaceError && workspace && (
        <div>
          <h1>{workspace.descriptor.name}</h1>
          {files && files.length > 0 && files}
          {!files || (files.length === 0 && <div>Empty!</div>)}
        </div>
      )}
    </>
  );
}
