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
import * as os from "os";
import * as child from "child_process";
import { Constants } from "../common/Constants";
import { CommandExecutionResult } from "../common/CommandExecutionResult";
import { OperatingSystem } from "@kie-tooling-core/operating-system";
import IpcMainEvent = Electron.IpcMainEvent;

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

setInterval(() => {
  ipcMain.emit("vscode__list_extensions", {});
}, 2000);

function createWindow() {
  const mainWindow = new BrowserWindow({
    width: 1000,
    height: 780,
    show: false,
    icon: path.join(__dirname, "build/icon.png"),
    resizable: false,
    webPreferences: {
      enableRemoteModule: true,
      nodeIntegrationInWorker: true,
      nodeIntegration: true, // https://github.com/electron/electron/issues/9920#issuecomment-575839738
      contextIsolation: false,
    },
  });

  mainWindow.loadFile(path.join(__dirname, "index.html"));
  mainWindow.once("ready-to-show", () => mainWindow.show());

  const menu = new Menu(mainWindow);
  menu.setup();

  function checkIfVsCodeIsOpen(): Promise<CommandExecutionResult & { isOpen: boolean }> {
    const vsCodeLocation = getVsCodeLocation()!;
    const vscodeLocationForGrep = vsCodeLocation.replace(vsCodeLocation[0], `[${vsCodeLocation[0]}]`);
    return executeCommand({
      macOS: `ps aux | grep '${vscodeLocationForGrep}' | xargs echo`,
      linux: `ps aux | grep '${vscodeLocationForGrep}' | xargs echo`,
      windows: `WMIC path win32_process get Caption,Processid,Commandline | FINDSTR /V "FINDSTR" | FINDSTR "Code.exe"`,
    }).then((result) => {
      return { ...result, isOpen: result.output.trim().length !== 0 };
    });
  }

  function launchVsCode(): Promise<CommandExecutionResult> {
    return executeCommand({
      macOS: `'${getVsCodeLocation()}/Contents/Resources/app/bin/code'`,
      linux: `${getVsCodeLocation()}/bin/code`,
      windows: `"${getVsCodeLocation()}\\bin\\code`,
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
    }
  });

  ipcMain.on("vscode__uninstall_extension", (e: IpcMainEvent) => {
    executeCommand({
      macOS: `'${getVsCodeLocation()}/Contents/Resources/app/bin/code' --uninstall-extension ${
        Constants.VSCODE_EXTENSION_PACKAGE_NAME
      }`,
      linux: `'${getVsCodeLocation()}/bin/code' --uninstall-extension ${Constants.VSCODE_EXTENSION_PACKAGE_NAME}`,
      windows: `"${getVsCodeLocation()}\\bin\\code" --uninstall-extension ${Constants.VSCODE_EXTENSION_PACKAGE_NAME}`,
    }).then((result) => {
      mainWindow.webContents.send("vscode__uninstall_extension_complete", { ...result });
    });
  });

  ipcMain.on("vscode__list_extensions", (e: IpcMainEvent) => {
    if (!getVsCodeLocation()) {
      mainWindow.webContents.send("vscode__list_extensions_complete", { extensions: [] });
      return;
    }

    executeCommand({
      macOS: `'${getVsCodeLocation()}/Contents/Resources/app/bin/code' --list-extensions`,
      linux: `'${getVsCodeLocation()}/bin/code' --list-extensions`,
      windows: `"${getVsCodeLocation()}\\bin\\code" --list-extensions`,
    })
      .then((result) => {
        if (!result.success) {
          return [];
        } else {
          return result.output.split("\n");
        }
      })
      .then((extensions) => mainWindow.webContents.send("vscode__list_extensions_complete", { extensions }));
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
      windows: `"${getApplicationPath("lib/Business Modeler Preview-win32-x64/Business Modeler Preview.exe")}"`,
    }).then((result) => {
      mainWindow.webContents.send("desktop__launch_complete", result);
    });
  });
}

function getVsCodeLocation() {
  switch (process.platform) {
    case "darwin":
      return "/Applications/Visual Studio Code.app/";
      break;
    case "win32":
      return `C:\\Users\\${os.userInfo().username}\\AppData\\Local\\Programs\\Microsoft VS Code`;
      break;
    default:
      return "/usr/share/code";
      break;
  }
}

function getApplicationPath(relativePath: string) {
  return path.join(__dirname, `${relativePath}`);
}

function executeCommand(args: { macOS: string; linux: string; windows: string }): Promise<CommandExecutionResult> {
  let command: string;
  let platform: OperatingSystem;

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
  return new Promise((res) => {
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
