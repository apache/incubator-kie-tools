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

import * as path from "path";
import * as vscode from "vscode";

type ConfigurationValueInterpolationToken =
  | "${workspaceFolder}"
  | "${fileDirname}"
  | "${fileExtname}"
  | "${fileBasename}"
  | "${fileBasenameNoExtension}";

export const configurationTokenKeys: Record<
  ConfigurationValueInterpolationToken,
  ConfigurationValueInterpolationToken
> = {
  "${workspaceFolder}": "${workspaceFolder}",
  "${fileDirname}": "${fileDirname}",
  "${fileExtname}": "${fileExtname}",
  "${fileBasename}": "${fileBasename}",
  "${fileBasenameNoExtension}": "${fileBasenameNoExtension}",
};

export const definitelyPosixPath = (filePath: string) => {
  let result = filePath;
  // Prepend / to Windows drive letters, from C:\foo to /C:\foo, resulting in /C:/foo.
  if (/^(\w:\\)/.test(filePath)) {
    result = `/${filePath}`;
  }
  return result.split(path.sep).join(path.posix.sep);
};

export function doInterpolation(tokens: Record<string, string>, value: string) {
  return Object.entries(tokens).reduce(
    (result, [tokenName, tokenValue]) => result.replaceAll(tokenName, tokenValue),
    value
  );
}

export function getInterpolatedConfigurationValue(args: { currentFileAbsolutePosixPath: string; value: string }) {
  const parsedPath = path.posix.parse(args.currentFileAbsolutePosixPath);
  const workspace = vscode.workspace.workspaceFolders?.length
    ? vscode.workspace.workspaceFolders.find((workspace) => {
        const relative = path.posix.relative(workspace.uri.path, args.currentFileAbsolutePosixPath);
        return relative && !relative.startsWith("..") && !path.isAbsolute(relative);
      })
    : undefined;

  const fileExtensionWithDot = parsedPath.base.substring(parsedPath.base.indexOf("."));

  const tokens: Record<ConfigurationValueInterpolationToken, string> = {
    "${workspaceFolder}": workspace?.uri.path ?? parsedPath.dir,
    "${fileDirname}": parsedPath.dir,
    "${fileExtname}": fileExtensionWithDot,
    "${fileBasename}": parsedPath.base,
    "${fileBasenameNoExtension}": parsedPath.base.substring(0, parsedPath.base.indexOf(".")),
  };

  return doInterpolation(tokens, args.value);
}
