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

import { UNSAVED_FILE_NAME } from "../common/File";
import { BrowserWindow, dialog, ipcMain } from "electron";
import { Files } from "../storage/core/Files";
import { FS } from "../storage/core/FS";
import {extractFileExtension, removeFileExtension} from "../common/utils";
import { DesktopUserData } from "./DesktopUserData";
import IpcMainEvent = Electron.IpcMainEvent;
import { Menu } from "./Menu";
import * as path from "path";

export enum Actions {
  SAVE,
  SAVE_AS
}

export class FileOperations {
  private readonly window: BrowserWindow;
  private readonly menu: Menu;
  private readonly userData: DesktopUserData;

  constructor(window: BrowserWindow, menu: Menu, userData: DesktopUserData) {
    this.window = window;
    this.menu = menu;
    this.userData = userData;

    ipcMain.on("saveFile", (event: IpcMainEvent, data: any) => {
      if (
        data.action === Actions.SAVE_AS ||
        (data.action === Actions.SAVE && data.file.filePath === UNSAVED_FILE_NAME)
      ) {
        dialog
          .showSaveDialog(this.window, {
            title: "Save file",
            filters: [{ name: data.file.fileType.toUpperCase(), extensions: [data.file.fileType] }]
          })
          .then(result => {
            if (!result.canceled) {
              this.saveFile(result.filePath!, data.file.fileContent);
            }
          });
      } else {
        this.saveFile(data.file.filePath, data.file.fileContent);
      }
    });

    ipcMain.on("savePreview", (event: IpcMainEvent, data: any) => {
      console.info(removeFileExtension(data.filePath) + "." + data.fileType);
      dialog
        .showSaveDialog(this.window, {
          defaultPath: removeFileExtension(data.filePath) + "." + data.fileType,
          title: "Save preview",
          filters: [{ name: data.fileType.toUpperCase(), extensions: [data.fileType] }]
        })
        .then(result => {
          if (!result.canceled) {
            this.savePreview(result.filePath!, data.fileContent);
          }
        });
    });

    ipcMain.on("saveThumbnail", (event: IpcMainEvent, data: any) => {
      if (data.filePath !== UNSAVED_FILE_NAME) {
        this.userData.saveFileThumbnail(data.filePath, data.fileType, data.fileContent);
      }
    });

    ipcMain.on("requestLastOpenedFiles", () => {
      this.userData.getLastOpenedFiles().then(lastOpenedFiles => {
        this.window.webContents.send("returnLastOpenedFiles", {
          lastOpenedFiles: lastOpenedFiles
        });
      });
    });

    ipcMain.on("openFileByPath", (event: IpcMainEvent, data: { filePath: string }) => {
      this.open(data.filePath);
    });

    ipcMain.on("createNewFile", (event: IpcMainEvent, data: { type: string }) => {
      this.new(data.type);
    });

    ipcMain.on("openSample", (event: IpcMainEvent, data: { type: string }) => {
      this.openSample(path.join(__dirname, "samples/sample." + data.type));
    });

    ipcMain.on("mainWindowLoaded", (event: IpcMainEvent) => {
      if (process.argv.length > 1 && process.argv[1] !== "dist") {
        this.open(process.argv[1]);
      }
    });
  }

  public new(type: string) {
    this.window.webContents.send("openFile", {
      file: {
        filePath: UNSAVED_FILE_NAME,
        fileType: type,
        fileContent: ""
      }
    });
    this.menu.setFileMenusEnabled(true);
  }

  public open(filePath: string) {
    Files.read(FS.newFile(filePath))
      .then(content => {
        this.window.webContents.send("openFile", {
          file: {
            filePath: filePath,
            fileType: extractFileExtension(filePath),
            fileContent: content
          }
        });
        this.menu.setFileMenusEnabled(true);
        this.userData.registerFile(filePath);
        console.info("File " + filePath + " opened.");
      })
      .catch(error => {
        console.info("Failed to open file" + filePath + ":" + error);
        this.window.webContents.send("returnLastOpenedFiles", {
          lastOpenedFiles: this.userData.getLastOpenedFiles()
        });
      });
  }

  public openSample(filePath: string) {
    Files.read(FS.newFile(filePath))
      .then(content => {
        this.window.webContents.send("openFile", {
          file: {
            filePath: UNSAVED_FILE_NAME,
            fileType: extractFileExtension(filePath),
            fileContent: content
          }
        });
        this.menu.setFileMenusEnabled(true);
        console.info("Sample " + filePath + " opened.");
      })
      .catch(error => {
        console.info("Failed to open sample" + filePath + ":" + error);
      });
  }

  public save() {
    this.window.webContents.send("requestOpenedFile", {
      action: Actions.SAVE
    });
  }

  public saveAs() {
    this.window.webContents.send("requestOpenedFile", {
      action: Actions.SAVE_AS
    });
  }

  private saveFile(filePath: string, fileContent: string) {
    Files.write(FS.newFile(filePath), fileContent)
      .then(() => {
        this.userData.registerFile(filePath);
        console.info("File " + filePath + " saved.");

        this.window.webContents.send("saveFileSuccess", {
          filePath: filePath
        });
      })
      .catch(error => {
        console.info("Failed to save file" + filePath + ":" + error);
      });
  }

  private savePreview(filePath: string, fileContent: string) {
    Files.write(FS.newFile(filePath), fileContent)
      .then(() => {
        console.info("Preview " + filePath + " saved.");

        this.window.webContents.send("savePreviewSuccess", {
          filePath: filePath
        });
      })
      .catch(error => {
        console.info("Failed to save preview" + filePath + ":" + error);
      });
  }
}
