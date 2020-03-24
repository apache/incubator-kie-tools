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

import { app, BrowserWindow, ipcMain } from "electron";
import * as path from "path";
import { Menu } from "./Menu";
import { HubUserData } from "./HubUserData";
import * as os from "os";
import * as child from "child_process";
import { Constants } from "../common/Constants";
import IpcMainEvent = Electron.IpcMainEvent;

const desktop_APPLICATION_PATH = applicationPath("lib/Desktop.app");
const vscode_EXTENSION_PATH = applicationPath(
  "lib/vscode_extension_kogito_kie_editors_0.2.9-new-webview-api-release.vsix"
);

app.on("ready", () => {
  createWindow();
});

app.on("activate", () => {
  if (BrowserWindow.getAllWindows().length === 0) {
    createWindow();
  }
});

app.on("window-all-closed", () => {
  if (process.platform !== "darwin") {
    app.quit();
  }
});

interface CommandExecutionResult {
  success: boolean;
  output: string;
}

function createWindow() {
  const mainWindow = new BrowserWindow({
    width: 1000,
    height: 940,
    show: false,
    icon: path.join(__dirname, "build/icon.png"),
    resizable: false,
    webPreferences: {
      nodeIntegrationInWorker: true,
      nodeIntegration: true // https://github.com/electron/electron/issues/9920#issuecomment-575839738
    }
  });

  mainWindow.loadFile(path.join(__dirname, "index.html"));
  mainWindow.once("ready-to-show", () => mainWindow.show());

  const hubUserData = new HubUserData();
  const menu = new Menu(mainWindow, hubUserData);
  menu.setup();

  function checkIfVsCodeIsOpen(): Promise<{ success: boolean; msg: string; isOpen: boolean }> {
    return new Promise((res, rej) => {
      const vsCodeLocation = hubUserData.getVsCodeLocation()!;
      const vscodeLocationForGrep = vsCodeLocation.replace(vsCodeLocation[0], `[${vsCodeLocation[0]}]`);
      child.exec(`ps aux | grep '${vscodeLocationForGrep}'`, (error, stdout, stderr) => {
        const isOpen = stdout.trim().length !== 0;
        res({ success: !error, msg: stdout + stderr, isOpen: isOpen });
      });
    });
  }

  function checkIfVsCodeIsOpenWithProposedApiEnabled(): Promise<
    CommandExecutionResult & { isProposedApiEnabled: boolean }
  > {
    return new Promise((res, rej) => {
      const vsCodeLocation = hubUserData.getVsCodeLocation()!;
      const vscodeLocationForGrep = vsCodeLocation.replace(vsCodeLocation[0], `[${vsCodeLocation[0]}]`);
      child.exec(
        `ps aux | grep '${vscodeLocationForGrep}' | grep '\\--enable-proposed-api ${Constants.VSCODE_EXTENSION_PACKAGE_NAME}' `,
        (error, stdout, stderr) => {
          const isProposedApiEnabled = stdout.trim().length !== 0;
          res({ success: !error, output: stdout + stderr, isProposedApiEnabled: isProposedApiEnabled });
        }
      );
    });
  }

  function openVsCode(): Promise<CommandExecutionResult> {
    return new Promise((res, rej) => {
      child.exec(
        `'${hubUserData.getVsCodeLocation()}/Contents/Resources/app/bin/code' --enable-proposed-api ${
          Constants.VSCODE_EXTENSION_PACKAGE_NAME
        }`,
        (error, stdout, stderr) => {
          res({ success: !error, output: stdout + stderr });
        }
      );
    });
  }

  //
  //
  //
  //
  // VSCODE
  ipcMain.on("vscode__launch", async (e: IpcMainEvent) => {
    switch (os.platform()) {
      case "darwin":
        if (!(await checkIfVsCodeIsOpen()).isOpen) {
          mainWindow.webContents.send("vscode__launch_complete", await openVsCode());
          return;
        }

        if ((await checkIfVsCodeIsOpenWithProposedApiEnabled()).isProposedApiEnabled) {
          mainWindow.webContents.send("vscode__launch_complete", await openVsCode());
          return;
        }

        mainWindow.webContents.send("vscode__already_open", {});
        break;
      case "win32":
      case "linux":
      default:
        console.log("Platform not supported");
        break;
    }
  });

  ipcMain.on("vscode__launch_after_told_to_close", async (e: IpcMainEvent) => {
    if (!(await checkIfVsCodeIsOpen()).isOpen) {
      mainWindow.webContents.send("vscode__launch_complete", await openVsCode());
      return;
    }

    mainWindow.webContents.send("vscode__still_open_after_told_to_close", {});
  });

  ipcMain.on("vscode__uninstall_extension", (e: IpcMainEvent) => {
    switch (os.platform()) {
      case "darwin":
        child.exec(
          `'${hubUserData.getVsCodeLocation()}/Contents/Resources/app/bin/code' --uninstall-extension ${
            Constants.VSCODE_EXTENSION_PACKAGE_NAME
          }`,
          (error, stdout, stderr) => {
            console.log(stdout);
            console.log(error);
            console.log(stderr);
            mainWindow.webContents.send("vscode__uninstall_extension_complete", {
              success: !error,
              msg: stdout + stderr
            });
            hubUserData.deleteVsCodeLocation();
          }
        );
        break;
      case "win32":
      case "linux":
      default:
        console.log("Platform not supported");
        break;
    }
  });

  ipcMain.on("vscode__install_extension", (e: IpcMainEvent, data: { location: string }) => {
    hubUserData.setVsCodeLocation(data.location);
    switch (os.platform()) {
      case "darwin":
        child.exec(
          `'${hubUserData.getVsCodeLocation()}/Contents/Resources/app/bin/code' --install-extension ${vscode_EXTENSION_PATH}`,
          (error, stdout, stderr) => {
            console.log(stdout);
            console.log(error);
            console.log(stderr);
            mainWindow.webContents.send("vscode__install_extension_complete", {
              success: !error,
              msg: stdout + stderr
            });
          }
        );
        break;
      case "win32":
      case "linux":
      default:
        console.log("Platform not supported");
        break;
    }
  });

  ipcMain.on("vscode__list_extensions", (e: IpcMainEvent) => {
    listVsCodeExtensions(hubUserData).then(extensions =>
      mainWindow.webContents.send("vscode__list_extensions_complete", { extensions })
    );
  });

  //
  //
  //
  //
  // DESKTOP
  ipcMain.on("desktop_open", (e: IpcMainEvent) => {
    switch (os.platform()) {
      case "darwin":
        child.exec(`open ${desktop_APPLICATION_PATH}`, (error, stdout, stderr) => {
          console.log(stdout);
          console.log(error);
          console.log(stderr);
        });
        break;
      case "win32":
      case "linux":
      default:
        console.log("Platform not supported");
        break;
    }
  });

  //
  //
  //
  //
  // CHROME
}

function listVsCodeExtensions(userData: HubUserData) {
  if (!userData.getVsCodeLocation()) {
    return Promise.resolve([]);
  }

  return new Promise(res => {
    switch (os.platform()) {
      case "darwin":
        child.exec(
          `'${userData.getVsCodeLocation()}/Contents/Resources/app/bin/code' --list-extensions`,
          (error, stdout, stderr) => {
            console.log(stdout);
            console.log(error);
            console.log(stderr);
            if (error) {
              res([]);
            } else {
              res(stdout.split("\n"));
            }
          }
        );
        break;
      case "win32":
      case "linux":
      default:
        res([]);
        break;
    }
  });
}

function applicationPath(relativePath: string) {
  return path.join(__dirname, `${relativePath}`).replace(/(\s+)/g, "\\$1");
}
