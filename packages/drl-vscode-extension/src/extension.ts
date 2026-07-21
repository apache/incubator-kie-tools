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

import * as fs from "fs";
import * as path from "path";
import * as vscode from "vscode";

import { LanguageClient, LanguageClientOptions, ServerOptions, StreamInfo } from "vscode-languageclient/node";
import * as net from "net";

let languageClient: LanguageClient | undefined;

const DEBUG_MODE = process.env.LSDEBUG === "true";

let channel: vscode.OutputChannel | undefined;
const log = {
  info: (msg: string) => channel?.appendLine("INFO: " + msg),
  warn: (msg: string) => channel?.appendLine("WARNING: " + msg),
  error: (msg: string) => channel?.appendLine("ERROR: " + msg),
  debug: (msg: string) => {
    if (DEBUG_MODE) {
      channel?.appendLine("DEBUG: " + msg);
    }
  },
};

export function activate(context: vscode.ExtensionContext) {
  channel = vscode.window.createOutputChannel("DRL Language Server");
  context.subscriptions.push(channel);

  log.info('Activating extension "DRL Language Server"....');

  context.subscriptions.push(
    vscode.commands.registerCommand(
      "drools.peekReferences",
      (
        uri: string,
        position: { line: number; character: number },
        locations: Array<{
          uri: string;
          range: {
            start: { line: number; character: number };
            end: { line: number; character: number };
          };
        }>
      ) => {
        const target = vscode.Uri.parse(uri);
        const at = new vscode.Position(position.line, position.character);
        const locs = (locations ?? []).map(
          (l) =>
            new vscode.Location(
              vscode.Uri.parse(l.uri),
              new vscode.Range(l.range.start.line, l.range.start.character, l.range.end.line, l.range.end.character)
            )
        );
        return vscode.commands.executeCommand("editor.action.showReferences", target, at, locs);
      }
    )
  );

  let serverOptions: ServerOptions | undefined = undefined;

  if (DEBUG_MODE) {
    log.debug("Starting in debug mode");
    const connectionInfo = {
      port: 9925,
      host: "127.0.0.1",
    };
    log.debug("connectionInfo " + JSON.stringify(connectionInfo));
    serverOptions = () => {
      const socket = net.connect(connectionInfo);
      const result: StreamInfo = {
        writer: socket,
        reader: socket,
      };
      return Promise.resolve(result);
    };
  } else {
    const javaHome = getJavaHome();

    let executable: string = `java`;

    if (javaHome) {
      executable = path.join(javaHome, "bin", "java");
      log.debug("java executable path : " + executable);
    } else {
      log.warn("java home is not found. Invoking java without path.");
    }

    const serverJar = path.join(context.extensionPath, "dist", "server", "drools-lsp-server-jar-with-dependencies.jar");
    if (fs.existsSync(serverJar)) {
      log.debug("server jar path : " + serverJar);
    } else {
      log.error(`${serverJar} not found`);
      return;
    }

    const config = vscode.workspace.getConfiguration();
    const args: string[] = [];

    const logLevel: string | undefined = config.get("drools.lsp.logLevel");
    if (logLevel) {
      args.push(`-Ddrools.lsp.logLevel=${logLevel}`);
    }

    const lintProps = [
      "drools.lsp.lint.missingEnd",
      "drools.lsp.lint.missingSeparator",
      "drools.lsp.lint.missingSemicolon",
      "drools.lsp.lint.unbalancedParens",
      "drools.lsp.lint.unknownTypes",
      "drools.lsp.lint.mvelPropertyAccess",
    ];
    for (const prop of lintProps) {
      const value: string | undefined = config.get(prop);
      if (value !== undefined) {
        args.push(`-D${prop}=${value}`);
      }
    }

    const inlayHintsEnabled: boolean | undefined = config.get("drools.lsp.inlayHints.enabled");
    if (inlayHintsEnabled !== undefined) {
      args.push(`-Ddrools.lsp.inlayHints.enabled=${inlayHintsEnabled}`);
    }

    const pomPathSetting = config.get<string | string[]>("drools.lsp.maven.pomPath");
    const pomPaths = (Array.isArray(pomPathSetting) ? pomPathSetting : pomPathSetting ? [pomPathSetting] : [])
      .map((p) => p.trim())
      .filter((p) => p.length > 0);
    if (pomPaths.length > 0) {
      const workspaceRoot = vscode.workspace.workspaceFolders?.[0]?.uri.fsPath;
      const resolved = pomPaths.map((p) => (workspaceRoot && !path.isAbsolute(p) ? path.join(workspaceRoot, p) : p));
      args.push(`-Ddrools.lsp.maven.pomPath=${resolved.join(path.delimiter)}`);
    }

    args.push("-jar", serverJar);

    serverOptions = {
      command: executable,
      args: [...args],
      options: {},
    };
  }

  if (serverOptions) {
    log.info("Starting language client");
    const clientOptions: LanguageClientOptions = {
      documentSelector: [{ scheme: "file", language: "drools" }],
      synchronize: {
        fileEvents: vscode.workspace.createFileSystemWatcher("**/target/classes/**/*.class"),
      },
      outputChannel: channel,
    };
    languageClient = new LanguageClient("Drools", "DRL Language Server", serverOptions, clientOptions);
    languageClient.start();

    log.info("DRL Language Server activated.");
  }
}

export function deactivate(): Thenable<void> | undefined {
  log.info("DRL Language Server deactivated.");
  if (!languageClient) {
    return undefined;
  }
  return languageClient.stop();
}

function getJavaHome(): string | undefined {
  let javaHome: string | undefined;

  javaHome = vscode.workspace.getConfiguration().get("java.home");
  if (javaHome) {
    log.debug("java.home from workspace configuration : " + javaHome);
    return javaHome;
  }

  javaHome = process.env.GHA_JAVA_HOME;
  if (javaHome) {
    log.debug("GHA_JAVA_HOME from process env : " + javaHome);
    return javaHome;
  }

  javaHome = process.env.JAVA_HOME;
  if (javaHome) {
    log.debug("JAVA_HOME from process env : " + javaHome);
    return javaHome;
  }

  log.warn("JAVA_HOME not found");
  return javaHome;
}
