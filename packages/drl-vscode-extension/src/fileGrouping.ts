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
import { LanguageClient } from "vscode-languageclient/node";

/**
 * Shows which group of DRL files the open document compiles with, and lets the
 * user pin it to a different one.
 *
 * The group map comes from the server (`drools/fileGroups`) rather than being
 * re-derived here. The server already reads kmodule descriptors, the
 * `drl-lsp-kbases.json` config and any manifests it adopts; parsing those a
 * second time in the client would only create a second thing to keep correct.
 */

type Logger = {
  info: (msg: string) => void;
  error: (msg: string) => void;
};

/**
 * One group as the server reports it. `kind` is set only when the server can be
 * more specific than "group" — "KIE base" for a group read from a kmodule.xml —
 * so a project that never declared a kmodule is never shown kmodule vocabulary.
 */
type FileGroup = {
  /** Normalized paths, for membership tests. */
  files: string[];
  kind?: string;
  /** The file that declared the group, for answering "why is this file here?". */
  declaredIn?: string;
};

/** Group name to what the server last reported for it. */
let groups = new Map<string, FileGroup>();
/** Document fsPath to the group the user pinned it to. Persisted per workspace. */
let overrides = new Map<string, string>();
let statusItem: vscode.StatusBarItem | undefined;
let log: Logger = { info: () => undefined, error: () => undefined };
/** Watchers over the files that declared the current groups; rebuilt on refresh. */
let declaringFileWatchers: vscode.FileSystemWatcher[] = [];

const OVERRIDES_STATE_KEY = "drools.fileGroupOverrides";
const CONFIG_FILE_GLOB = "**/{drl-lsp-kbases.json,kmodule.xml}";
const GROUPING_SETTING = "drools.lsp.grouping";

/**
 * The `drools.lsp.grouping` setting, or undefined when unset or empty. Sent to
 * the server as an object rather than a JSON string, so it arrives as structured
 * JSON instead of a quoted, escaped string.
 */
export function groupingSetting(): object | undefined {
  const value = vscode.workspace.getConfiguration().get<object>(GROUPING_SETTING);
  return !value || Object.keys(value).length === 0 ? undefined : value;
}

/** Files the grouping layer needs to know about. */
const WORKSPACE_FILE_GLOB = "**/{*.drl,kmodule.xml,drl-lsp-kbases.json}";

/**
 * Enumerates the workspace files the server should consider, as URIs.
 *
 * The client does this rather than the server walking the filesystem, because
 * `findFiles` already applies the user's `files.exclude`, `search.exclude` and
 * ignore files. A server-side walk can only approximate that with a hardcoded
 * list of directory names to skip, which goes stale and silently drops files.
 */
export async function enumerateWorkspaceFiles(): Promise<string[]> {
  if (!vscode.workspace.workspaceFolders?.length) {
    return [];
  }
  const found = await vscode.workspace.findFiles(WORKSPACE_FILE_GLOB);
  return found.map((uri) => uri.toString());
}

/**
 * Windows and macOS resolve paths case-insensitively, and the same file can
 * reach us with different casing (a drive letter, most often). Elsewhere case is
 * significant, and folding it would conflate `rules/Foo.drl` with
 * `rules/foo.drl`.
 */
const CASE_INSENSITIVE_PATHS = process.platform === "win32" || process.platform === "darwin";

function normalize(p: string): string {
  const forwardSlashed = p.replace(/\\/g, "/");
  return CASE_INSENSITIVE_PATHS ? forwardSlashed.toLowerCase() : forwardSlashed;
}

function activeDrlUri(): vscode.Uri | undefined {
  const uri = vscode.window.activeTextEditor?.document.uri;
  return uri && uri.fsPath.toLowerCase().endsWith(".drl") ? uri : undefined;
}

/** Every group containing `uri`, in the order the server reported them. */
function groupsContaining(uri: vscode.Uri): string[] {
  const target = normalize(uri.fsPath);
  const matches: string[] = [];
  for (const [name, group] of groups) {
    if (group.files.includes(target)) {
      matches.push(name);
    }
  }
  return matches;
}

/** The noun for a group, defaulting to wording that needs no Drools background. */
function labelFor(name: string): string {
  return groups.get(name)?.kind ?? "DRL group";
}

/** A tooltip line naming the declaring file, or "" when the server gave none. */
function provenanceOf(name: string): string {
  const declaredIn = groups.get(name)?.declaredIn;
  if (!declaredIn) {
    return "";
  }
  const fsPath = vscode.Uri.parse(declaredIn).fsPath;
  return `\nDeclared in ${vscode.workspace.asRelativePath(fsPath)}`;
}

/**
 * The pin for `uri`, but only while the group it names still exists.
 *
 * A config edit can remove a group a file was pinned to; the server falls back
 * to another scope in that case, so showing the vanished group as active would
 * misreport what is actually in scope. The entry is kept rather than purged, so
 * the pin takes effect again if the group comes back.
 */
function activePin(uri: vscode.Uri): string | undefined {
  const pinned = overrides.get(uri.fsPath);
  return pinned !== undefined && groups.has(pinned) ? pinned : undefined;
}

function updateStatusItem(): void {
  if (!statusItem) {
    return;
  }
  const uri = activeDrlUri();
  if (!uri) {
    statusItem.hide();
    return;
  }

  const containing = groupsContaining(uri);
  const pinned = activePin(uri);
  const active = pinned ?? containing[0];

  if (!active) {
    statusItem.text = "$(question) DRL group: none";
    statusItem.tooltip =
      groups.size === 0
        ? "No DRL file grouping is configured. Scope falls back to this directory. Click to pin a group."
        : "This file is in no configured group. Scope falls back to this directory. Click to pin one.";
    statusItem.backgroundColor = undefined;
  } else if (!pinned && containing.length > 1) {
    // Overlapping "packages" patterns make this normal, not a misconfiguration.
    statusItem.text = `$(warning) ${labelFor(active)}: ${active}`;
    statusItem.tooltip =
      `This file is in ${containing.length} groups (${containing.join(", ")}).` +
      ` Using "${active}".${provenanceOf(active)}\nClick to pin one.`;
    statusItem.backgroundColor = new vscode.ThemeColor("statusBarItem.warningBackground");
  } else {
    statusItem.text = `$(database) ${labelFor(active)}: ${active}${pinned ? " $(pinned)" : ""}`;
    statusItem.tooltip =
      `${labelFor(active)} "${active}"${pinned ? " (pinned)" : ""}.` + `${provenanceOf(active)}\nClick to change.`;
    statusItem.backgroundColor = undefined;
  }
  statusItem.show();
}

/** Re-reads the group map from the server. Safe to call before the client is up. */
export async function refreshFileGroups(client: LanguageClient | undefined): Promise<void> {
  if (!client) {
    return;
  }
  try {
    type Reported = { files?: string[]; kind?: string; declaredIn?: string };
    const reported = await client.sendRequest<Record<string, Reported>>("drools/fileGroups");
    groups = new Map(
      Object.entries(reported ?? {}).map(([name, group]) => [
        name,
        {
          files: (group.files ?? []).map((u) => normalize(vscode.Uri.parse(u).fsPath)),
          kind: group.kind ?? undefined,
          declaredIn: group.declaredIn ?? undefined,
        },
      ])
    );
    log.info(`DRL file groups: ${groups.size} group(s)`);
  } catch (e) {
    log.error("Failed to read DRL file groups from the language server: " + String(e));
    groups = new Map();
  }
  watchDeclaringFiles(client);
  updateStatusItem();
}

/**
 * Watches the files that actually declared the current groups.
 *
 * Manifests adopted through `sources[].include` can be named anything, so no
 * fixed glob covers them. The server already reports which file declared each
 * group, which is exactly the set worth watching — and it stays correct as the
 * configuration changes, without the client having to parse any of it.
 */
function watchDeclaringFiles(client: LanguageClient | undefined): void {
  declaringFileWatchers.forEach((w) => w.dispose());
  declaringFileWatchers = [];

  const declaring = new Set<string>();
  for (const group of groups.values()) {
    if (group.declaredIn) {
      declaring.add(vscode.Uri.parse(group.declaredIn).fsPath);
    }
  }
  for (const fsPath of declaring) {
    const watcher = vscode.workspace.createFileSystemWatcher(
      new vscode.RelativePattern(vscode.Uri.file(path.dirname(fsPath)), path.basename(fsPath))
    );
    const reload = () => {
      client?.sendNotification("drools/reloadFileGroups");
    };
    watcher.onDidCreate(reload);
    watcher.onDidChange(reload);
    watcher.onDidDelete(reload);
    declaringFileWatchers.push(watcher);
  }
}

async function persistOverrides(context: vscode.ExtensionContext): Promise<void> {
  await context.workspaceState.update(OVERRIDES_STATE_KEY, Object.fromEntries(overrides.entries()));
}

function sendOverride(client: LanguageClient | undefined, uri: vscode.Uri, group: string | undefined): void {
  client?.sendNotification("drools/setFileGroup", { uri: uri.toString(), group: group ?? "" });
}

/** Re-applies pins the user made in an earlier session. */
function sendPersistedOverrides(client: LanguageClient | undefined): void {
  for (const [fsPath, group] of overrides) {
    sendOverride(client, vscode.Uri.file(fsPath), group);
  }
}

/**
 * Connects to a started client: re-applies pins, and subscribes to the server's
 * "the groups changed" push.
 *
 * Scanning a workspace takes long enough that asking once at startup can easily
 * ask too early, so the server tells us when the answer is ready rather than
 * leaving the status bar stale until something else triggers a refresh.
 */
export async function attachToClient(client: LanguageClient): Promise<void> {
  client.onNotification("drools/fileGroupsChanged", () => {
    void refreshFileGroups(client);
  });
  sendPersistedOverrides(client);
  await refreshFileGroups(client);
}

async function pickGroup(context: vscode.ExtensionContext, getClient: () => LanguageClient | undefined): Promise<void> {
  const uri = activeDrlUri();
  if (!uri) {
    void vscode.window.showInformationMessage("Open a .drl file to choose its file group.");
    return;
  }
  if (groups.size === 0) {
    void vscode.window.showWarningMessage(
      "No DRL file groups are configured. Add a drl-lsp-kbases.json, or a META-INF/kmodule.xml, to group files for completion and validation."
    );
    return;
  }

  const containing = groupsContaining(uri);
  const pinned = activePin(uri);
  const CLEAR = "$(clear-all) Clear pin (use configured group)";

  const active = pinned ?? containing[0];
  const toItem = (name: string): vscode.QuickPickItem => {
    const declaredIn = groups.get(name)?.declaredIn;
    return {
      label: name,
      description: name === active ? "$(check) current" : "",
      // Names the kind and the declaring file, so a workspace mixing kmodule
      // KIE bases with editor-declared groups stays legible in one list.
      detail: declaredIn
        ? `${labelFor(name)} · ${vscode.workspace.asRelativePath(vscode.Uri.parse(declaredIn).fsPath)}`
        : labelFor(name),
    };
  };

  // Groups that already claim this file come first — with many groups in a
  // workspace, the handful containing the open file are the plausible choices
  // and an alphabetical list buries them.
  const byName = (a: string, b: string) => a.localeCompare(b);
  const claiming = [...groups.keys()].filter((n) => containing.includes(n)).sort(byName);
  const rest = [...groups.keys()].filter((n) => !containing.includes(n)).sort(byName);

  const items: vscode.QuickPickItem[] = [];
  if (pinned) {
    items.push({ label: CLEAR });
  }
  if (claiming.length > 0) {
    items.push({ label: "Contains this file", kind: vscode.QuickPickItemKind.Separator });
    items.push(...claiming.map(toItem));
  }
  if (rest.length > 0) {
    items.push({ label: "Other groups", kind: vscode.QuickPickItemKind.Separator });
    items.push(...rest.map(toItem));
  }

  const choice = await vscode.window.showQuickPick(items, {
    title: "Select the DRL file group for this file",
    placeHolder: "The group whose files are in scope for completion, navigation and validation",
    matchOnDescription: true,
  });
  if (!choice) {
    return;
  }

  if (choice.label === CLEAR) {
    overrides.delete(uri.fsPath);
    sendOverride(getClient(), uri, undefined);
  } else {
    overrides.set(uri.fsPath, choice.label);
    sendOverride(getClient(), uri, choice.label);
  }
  await persistOverrides(context);
  updateStatusItem();
}

/**
 * Registers the status bar item, the commands and the config-file watcher.
 * Call once from `activate`.
 */
export function registerFileGrouping(
  context: vscode.ExtensionContext,
  getClient: () => LanguageClient | undefined,
  logger: Logger
): void {
  log = logger;

  overrides = new Map(Object.entries(context.workspaceState.get<Record<string, string>>(OVERRIDES_STATE_KEY, {})));

  statusItem = vscode.window.createStatusBarItem(vscode.StatusBarAlignment.Left, 99);
  statusItem.command = "drools.selectFileGroup";
  context.subscriptions.push(statusItem);

  context.subscriptions.push(
    vscode.commands.registerCommand("drools.selectFileGroup", () => pickGroup(context, getClient))
  );
  context.subscriptions.push(
    vscode.commands.registerCommand("drools.reloadFileGroups", async () => {
      const client = getClient();
      client?.sendNotification("drools/reloadFileGroups");
      await refreshFileGroups(client);
    })
  );

  context.subscriptions.push(vscode.window.onDidChangeActiveTextEditor(() => updateStatusItem()));

  // Editing the setting re-groups the workspace without a restart.
  context.subscriptions.push(
    vscode.workspace.onDidChangeConfiguration(async (e) => {
      if (!e.affectsConfiguration(GROUPING_SETTING)) {
        return;
      }
      getClient()?.sendNotification("drools/setGroupingConfig", { config: groupingSetting() ?? null });
    })
  );

  // Editing a config file re-groups the workspace without a restart.
  const configWatcher = vscode.workspace.createFileSystemWatcher(CONFIG_FILE_GLOB);
  const reload = async () => {
    const client = getClient();
    client?.sendNotification("drools/reloadFileGroups");
    await refreshFileGroups(client);
  };
  configWatcher.onDidCreate(reload);
  configWatcher.onDidChange(reload);
  configWatcher.onDidDelete(reload);
  context.subscriptions.push(configWatcher);

  // Adding or removing a file changes what the server should consider, and only
  // the client knows which files the user counts as part of the project.
  const fileWatcher = vscode.workspace.createFileSystemWatcher(WORKSPACE_FILE_GLOB, false, true, false);
  const resend = async () => {
    getClient()?.sendNotification("drools/setWorkspaceFiles", { uris: await enumerateWorkspaceFiles() });
  };
  fileWatcher.onDidCreate(resend);
  fileWatcher.onDidDelete(resend);
  context.subscriptions.push(fileWatcher);

  context.subscriptions.push({
    dispose: () => {
      declaringFileWatchers.forEach((w) => w.dispose());
      declaringFileWatchers = [];
    },
  });

  updateStatusItem();
}
