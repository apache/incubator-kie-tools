/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as electron from "electron";

const mockIpcRendererEvents = new Map<string, (event?: electron.IpcRendererEvent, ...args: any[]) => void>();
const mockIpcMainEvents = new Map<string, (event?: electron.IpcMainEvent, ...args: any[]) => void>();

export const ipcRenderer = {
  on(channel: string, listener: (event?: electron.IpcRendererEvent, ...args: any[]) => void) {
    mockIpcRendererEvents.set(channel, listener);
  },
  send(channel: string, ...args: any[]) {
    mockIpcRendererEvents.get(channel)?.(undefined, ...args);
    mockIpcMainEvents.get(channel)?.(undefined, ...args);
  },
  removeAllListeners(channel: string) {
    mockIpcRendererEvents.delete(channel);
  }
};

export const ipcMain = {
  on(channel: string, listener: (event: electron.IpcMainEvent, ...args: any[]) => void) {
    mockIpcMainEvents.set(channel, listener);
  },
  removeAllListeners(channel: string) {
    mockIpcMainEvents.delete(channel);
  }
};

export class BrowserWindow {
  constructor(options?: electron.BrowserWindowConstructorOptions) { /**/ }
};

export const showSaveDialogMock = jest.fn((browserWindow: BrowserWindow, options: electron.SaveDialogOptions) => {
  return Promise.resolve();
});

export const dialog = {
  showSaveDialog: showSaveDialogMock
};