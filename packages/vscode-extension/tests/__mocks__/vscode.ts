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

// vscode.js

import * as fs from "fs";
import * as __path from "path";

const languages = {
  createDiagnosticCollection: jest.fn(),
};

const StatusBarAlignment = {};

const vscodeWindow = {
  createStatusBarItem: jest.fn(() => ({
    show: jest.fn(),
  })),
  showErrorMessage: jest.fn(),
  showWarningMessage: jest.fn(),
  createTextEditorDecorationType: jest.fn(),
};

const wsRoot = __path.resolve(__dirname, "..", "test-workspace");

export const workspace = {
  getConfiguration: jest.fn(),
  workspaceFolders: [{ uri: { fsPath: wsRoot } }],
  onDidSaveTextDocument: jest.fn(),
  fs: {
    readDirectory: (uri: any) => ({
      then: (success: (arg0: (string | number)[][]) => void, error: (arg0: NodeJS.ErrnoException) => void) => {
        fs.readdir(uri.toString(), { withFileTypes: true }, (err, files) => {
          if (err) {
            error(err);
            return;
          }
          success(files.map((file) => [file.name, 1]));
        });
      },
    }),
    readFile: (uri: any) => ({
      then: (success: (arg0: Buffer) => void, error: (arg0: NodeJS.ErrnoException) => void) => {
        fs.readFile(uri.toString(), (err, file) => {
          if (err) {
            error(err);
            return;
          }
          success(file);
        });
      },
    }),
  },

  asRelativePath: jest.fn((uri: string | { fsPath: string }, _includeWorkspace?: boolean) => {
    const abs = typeof uri === "string" ? uri : uri.fsPath;
    console.debug(wsRoot);
    console.debug(uri);
    let rel = abs.startsWith(wsRoot + __path.sep) ? abs.slice(wsRoot.length + 1) : abs;
    return rel.replace(/\\/g, "/");
  }),

  /**
   * This is mock for unit testing. I do not want to implement any search mechanism.
   * We do not want to test correctnes of the VSCode implementation in Apache KIE test suite
   * We just want to be able assert `findFiles` parameters
   */
  findFiles: jest.fn(async (include: RelativePattern, exclude?: any, maxResults?: number, token?: any) => {
    return [];
  }),
};

const OverviewRulerLane = {
  Left: null,
};

const Uri = {
  file: (f: any) => f,
  parse: jest.fn((path: string) => path),
};
const vscodeRange = jest.fn();
const Diagnostic = jest.fn();
const DiagnosticSeverity = { Error: 0, Warning: 1, Information: 2, Hint: 3 };

const debug = {
  onDidTerminateDebugSession: jest.fn(),
  startDebugging: jest.fn(),
};

const commands = {
  executeCommand: jest.fn(),
};

/** Just te get unit test working and be able assert `base` and `pattern` */
class RelativePattern {
  constructor(
    public base: string,
    public pattern: string
  ) {}
}

const vscode = {
  languages,
  StatusBarAlignment,
  window: vscodeWindow,
  workspace,
  OverviewRulerLane,
  Uri,
  Range: vscodeRange,
  Diagnostic,
  DiagnosticSeverity,
  debug,
  commands,
  RelativePattern,
};

module.exports = vscode;
