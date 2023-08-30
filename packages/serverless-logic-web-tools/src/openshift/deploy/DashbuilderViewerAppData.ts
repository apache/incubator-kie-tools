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

import { WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { encoder } from "@kie-tools-core/workspaces-git-fs/dist/encoderdecoder/EncoderDecoder";
import { DashbuilderViewer } from "./BaseContainerImages";

export interface DashbuilderViewerAppData {
  primary: { uri: string };
  secondary: { uri: string }[];
  showDisclaimer: boolean;
}

export function createDashbuilderViewerAppDataFile(args: {
  workspaceId: string;
  primary: WorkspaceFile;
  secondary: WorkspaceFile[];
}): WorkspaceFile {
  const appData: DashbuilderViewerAppData = {
    primary: {
      uri: args.primary.relativePath,
    },
    secondary: args.secondary.map((f) => ({ uri: f.relativePath })),
    showDisclaimer: true,
  };

  return new WorkspaceFile({
    workspaceId: args.workspaceId,
    relativePath: DashbuilderViewer.APP_DATA_FILE,
    getFileContents: async () => encoder.encode(JSON.stringify(appData)),
  });
}
