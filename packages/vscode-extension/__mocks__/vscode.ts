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

const workspace = {
  getConfiguration: jest.fn(),
  workspaceFolders: [],
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
};

module.exports = vscode;
