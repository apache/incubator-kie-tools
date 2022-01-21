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
