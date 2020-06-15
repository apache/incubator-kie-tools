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
import { CommandExecutionResult } from "../common/CommandExecutionResult";
import { OperatingSystem } from "@kogito-tooling/core-api";
import IpcMainEvent = Electron.IpcMainEvent;

const vscode_EXTENSION_PATH = getApplicationPath("lib/vscode_extension.vsix");

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

function createWindow() {
  const mainWindow = new BrowserWindow({
    width: 1000,
    height: 760,
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

  function checkIfVsCodeIsOpen(): Promise<CommandExecutionResult & { isOpen: boolean }> {
    const vsCodeLocation = hubUserData.getVsCodeLocation()!;
    const vscodeLocationForGrep = vsCodeLocation.replace(vsCodeLocation[0], `[${vsCodeLocation[0]}]`);
    return executeCommand({
      macOS: `ps aux | grep '${vscodeLocationForGrep}' | xargs echo`,
      linux: `ps aux | grep '${vscodeLocationForGrep}' | xargs echo`,
      windows: `WMIC path win32_process get Caption,Processid,Commandline | FINDSTR /V "FINDSTR" | FINDSTR "Code.exe"`
    }).then(result => {
      return { ...result, isOpen: result.output.trim().length !== 0 };
    });
  }

  function launchVsCode(): Promise<CommandExecutionResult> {
    return executeCommand({
      macOS: `'${hubUserData.getVsCodeLocation()}/Contents/Resources/app/bin/code'`,
      linux: `${hubUserData.getVsCodeLocation()}/bin/code`,
      windows: `"${hubUserData.getVsCodeLocation()}\\bin\\code`
    });
  }

  //
  //
  //
  //
  // VSCODE
  ipcMain.on("vscode__launch", async (e: IpcMainEvent) => {
    if (!(await checkIfVsCodeIsOpen()).isOpen) {
      mainWindow.webContents.send("vscode__launch_complete", await launchVsCode());
      return;
    }

    mainWindow.webContents.send("vscode__already_open", {});
  });

  ipcMain.on("vscode__launch_after_told_to_close", async (e: IpcMainEvent) => {
    if (!(await checkIfVsCodeIsOpen()).isOpen) {
      mainWindow.webContents.send("vscode__launch_complete", await launchVsCode());
      return;
    }

    mainWindow.webContents.send("vscode__still_open_after_told_to_close", {});
  });

  ipcMain.on("vscode__uninstall_extension", (e: IpcMainEvent) => {
    executeCommand({
      macOS: `'${hubUserData.getVsCodeLocation()}/Contents/Resources/app/bin/code' --uninstall-extension ${
        Constants.VSCODE_EXTENSION_PACKAGE_NAME
      }`,
      linux: `'${hubUserData.getVsCodeLocation()}/bin/code' --uninstall-extension ${
        Constants.VSCODE_EXTENSION_PACKAGE_NAME
      }`,
      windows: `"${hubUserData.getVsCodeLocation()}\\bin\\code" --uninstall-extension ${
        Constants.VSCODE_EXTENSION_PACKAGE_NAME
      }`
    }).then(result => {
      mainWindow.webContents.send("vscode__uninstall_extension_complete", { ...result });
      hubUserData.deleteVsCodeLocation();
    });
  });

  ipcMain.on("vscode__install_extension", (e: IpcMainEvent, data: { location: string }) => {
    hubUserData.setVsCodeLocation(data.location);
    executeCommand({
      macOS: `'${hubUserData.getVsCodeLocation()}/Contents/Resources/app/bin/code' --install-extension "${vscode_EXTENSION_PATH}"`,
      linux: `'${hubUserData.getVsCodeLocation()}/bin/code' --install-extension "${vscode_EXTENSION_PATH}"`,
      windows: `"${hubUserData.getVsCodeLocation()}\\bin\\code" --install-extension "${vscode_EXTENSION_PATH}"`
    }).then(result => {
      mainWindow.webContents.send("vscode__install_extension_complete", { ...result });
    });
  });

  ipcMain.on("vscode__list_extensions", (e: IpcMainEvent) => {
    console.info("listing extensions");
    if (!hubUserData.getVsCodeLocation()) {
      mainWindow.webContents.send("vscode__list_extensions_complete", { extensions: [] });
      return;
    }

    executeCommand({
      macOS: `'${hubUserData.getVsCodeLocation()}/Contents/Resources/app/bin/code' --list-extensions`,
      linux: `'${hubUserData.getVsCodeLocation()}/bin/code' --list-extensions`,
      windows: `"${hubUserData.getVsCodeLocation()}\\bin\\code" --list-extensions`
    })
      .then(result => {
        if (!result.success) {
          return [];
        } else {
          return result.output.split("\n");
        }
      })
      .then(extensions => mainWindow.webContents.send("vscode__list_extensions_complete", { extensions }));
  });

  //
  //
  //
  //
  // DESKTOP
  ipcMain.on("desktop__launch", (e: IpcMainEvent) => {
    executeCommand({
      linux: `chmod -R u+x "${getApplicationPath("")}" && "${getApplicationPath(
        "lib/Business Modeler Preview-linux-x64/Business Modeler Preview"
      )}"`,
      macOS: `open "${getApplicationPath("lib/Business Modeler Preview-darwin-x64/Business Modeler Preview.app")}"`,
      windows: `"${getApplicationPath("lib/Business Modeler Preview-win32-x64/Business Modeler Preview.exe")}"`
    }).then(result => {
      mainWindow.webContents.send("desktop__launch_complete", result);
    });
  });

  //
  //
  //
  //
  // GENERAL
  ipcMain.on("business_modeler_hub__init", (e: IpcMainEvent) => {
    mainWindow.webContents.send("business_modeler_hub__init_complete", { username: os.userInfo().username });
  });
}

function getApplicationPath(relativePath: string) {
  return path.join(__dirname, `${relativePath}`);
}

function executeCommand(args: { macOS: string; linux: string; windows: string }): Promise<CommandExecutionResult> {
  let command;
  let platform;

  switch (os.platform()) {
    case "darwin":
      command = args.macOS;
      platform = OperatingSystem.MACOS;
      break;
    case "win32":
      command = args.windows;
      platform = OperatingSystem.WINDOWS;
      break;
    case "linux":
      command = args.linux;
      platform = OperatingSystem.LINUX;
      break;
    default:
      throw new Error("Unknown platform " + os.platform());
  }

  console.info(`Executing command ' ${command} ' on ${platform}`);
  return new Promise(res => {
    child.exec(command, (error, stdout, stderr) => {
      const result = error
        ? { success: false, output: stderr, os: platform }
        : { success: true, output: stdout, os: platform };

      console.info("Success: " + result.success);
      console.info(result.output);
      console.info("");

      res(result);
    });
  });
}
