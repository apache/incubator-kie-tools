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

import { WorkspacesContextType } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { LocalFile } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/LocalFile";
import { isAbsolute } from "path";

function resolveRelativePath(args: { keepRootDirs: boolean; file: File & { path?: string } }) {
  if (!args.file.path) {
    return args.file.name;
  }

  if (args.keepRootDirs) {
    // Keeps the root directories name, just remove the preceding slash.
    return !isAbsolute(args.file.path) ? args.file.path : args.file.path.substring(args.file.path.indexOf("/") + 1);
  }

  // Remove first portion of the path, which is the uploaded directory name.
  return isAbsolute(args.file.path)
    ? args.file.path.substring(args.file.path.indexOf("/", 1) + 1)
    : args.file.path.substring(args.file.path.indexOf("/") + 1);
}

export async function CreateWorkspaceFromUploadedFolder(args: { files: File[]; workspaces: WorkspacesContextType }) {
  if (args.files.length === 0) {
    return;
  }

  const uploadedRootDirs = args.files.reduce((acc: Set<string>, file: File & { path?: string }) => {
    if (!file.path) {
      return acc.add(file.name);
    }

    return acc.add(
      isAbsolute(file.path)
        ? file.path.substring(1, file.path.indexOf("/", 1))
        : file.path.substring(0, file.path.indexOf("/"))
    );
  }, new Set<string>());

  const localFiles: LocalFile[] = await Promise.all(
    Array.from(args.files ?? []).map(async (file: File & { path?: string }) => {
      const path = resolveRelativePath({
        file,
        keepRootDirs: uploadedRootDirs.size > 1,
      });

      return {
        path,
        fileContents: await (async () =>
          new Promise<Uint8Array>((res) => {
            const reader = new FileReader();
            reader.onload = (event: ProgressEvent<FileReader>) =>
              res(new Uint8Array(event.target?.result as ArrayBuffer));
            reader.readAsArrayBuffer(file);
          }))(),
      };
    })
  );

  const preferredName =
    uploadedRootDirs.size !== 1
      ? undefined
      : [...uploadedRootDirs][0] === localFiles[0].path
        ? undefined
        : [...uploadedRootDirs][0];

  const { workspace, suggestedFirstFile } = await args.workspaces.createWorkspaceFromLocal({
    localFiles,
    preferredName,
  });

  if (!suggestedFirstFile) {
    return;
  }

  return {
    workspaceId: workspace.workspaceId,
    fileRelativePath: suggestedFirstFile.relativePath,
  };
}
