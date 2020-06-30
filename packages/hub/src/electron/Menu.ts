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

import { app, BrowserWindow, Menu as ElectronMenu } from "electron";
import { HubUserData } from "./HubUserData";
import MenuItemConstructorOptions = Electron.MenuItemConstructorOptions;
import MenuItem = Electron.MenuItem;

export class Menu {
  private readonly window: BrowserWindow;
  private readonly userData: HubUserData;

  private readonly macOSAppMenu: any;

  private readonly devMenu = {
    label: "Development Menu",
    submenu: [
      {
        label: "Show Developer Tools",
        click: () => {
          this.window.webContents.openDevTools();
        }
      },
      {
        label: "Clear User Data",
        click: () => {
          this.userData.clearAll();
        }
      }
    ]
  };

  constructor(window: BrowserWindow, userData: HubUserData) {
    this.window = window;
    this.userData = userData;

    this.macOSAppMenu = [
      {
        label: "Business Modeler Hub Preview",
        submenu: [
          {
            label: "About Business Modeler Hub Preview",
            role: "about"
          },
          {
            type: "separator"
          },
          {
            label: "Services",
            role: "services",
            submenu: []
          },
          {
            type: "separator"
          },
          {
            label: "Hide Business Modeler Hub Preview",
            accelerator: "Command+H",
            role: "hide"
          },
          {
            label: "Hide Others",
            accelerator: "Command+Alt+H",
            role: "hideothers"
          },
          {
            label: "Show All",
            role: "unhide"
          },
          {
            type: "separator"
          },
          {
            label: "Quit",
            accelerator: "Command+Q",
            click: () => app.quit()
          }
        ]
      },
      {
        label: "Edit",
        submenu: [
          { label: "Undo", accelerator: "CmdOrCtrl+Z", selector: "undo:" },
          { label: "Redo", accelerator: "Shift+CmdOrCtrl+Z", selector: "redo:" },
          { label: "Cut", accelerator: "CmdOrCtrl+X", selector: "cut:" },
          { label: "Copy", accelerator: "CmdOrCtrl+C", selector: "copy:" },
          { label: "Paste", accelerator: "CmdOrCtrl+V", selector: "paste:" },
          { label: "Select All", accelerator: "CmdOrCtrl+A", selector: "selectAll:" }
        ]
      }
    ];
  }

  public setup() {
    const template: Array<MenuItemConstructorOptions | MenuItem> = [];

    if (process.platform === "darwin") {
      template.unshift(...this.macOSAppMenu);
    }

    if (!app.isPackaged) {
      template.push(this.devMenu);
    }

    ElectronMenu.setApplicationMenu(ElectronMenu.buildFromTemplate(template));
  }
}
