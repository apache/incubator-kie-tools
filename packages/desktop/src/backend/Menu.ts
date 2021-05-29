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

import { Menu as ElectronMenu, BrowserWindow, dialog, app, ipcMain } from "electron";
import * as path from "path";
import { FileOperations } from "./FileOperations";
import { DesktopUserData } from "./DesktopUserData";
import MenuItemConstructorOptions = Electron.MenuItemConstructorOptions;
import MenuItem = Electron.MenuItem;
import IpcMainEvent = Electron.IpcMainEvent;
import { I18n } from "@kie-tooling-core/i18n/dist/core";
import { DesktopI18n } from "./i18n";

export class Menu {
  private readonly window: BrowserWindow;
  private readonly userData: DesktopUserData;
  private readonly fileOperations: FileOperations;
  private readonly i18n: DesktopI18n;
  private menu: ElectronMenu;
  private newMenu: MenuItemConstructorOptions;
  private openMenu: MenuItemConstructorOptions;
  private saveMenu: MenuItemConstructorOptions;
  private saveAsMenu: MenuItemConstructorOptions;
  private savePreviewAsMenu: MenuItemConstructorOptions;
  private closeWindowMenu: MenuItemConstructorOptions;
  private quitMenu: MenuItemConstructorOptions;
  private fileMenu: MenuItemConstructorOptions;
  private macOsFileMenu: MenuItemConstructorOptions;
  private macOsAppMenu: MenuItemConstructorOptions;
  private editMenu: MenuItemConstructorOptions;
  private devMenu: MenuItemConstructorOptions;

  constructor(window: BrowserWindow, userData: DesktopUserData, desktopI18n: I18n<DesktopI18n>) {
    this.window = window;
    this.userData = userData;
    this.fileOperations = new FileOperations(window, this, userData, desktopI18n);
    this.i18n = desktopI18n.getCurrent();
    this.initializeMenuProperties();

    ipcMain.on("setFileMenusEnabled", (event: IpcMainEvent, data: { enabled: boolean }) => {
      this.setFileMenusEnabled(data.enabled);
    });
  }

  private initializeMenuProperties() {
    this.newMenu = {
      label: this.i18n.terms.new,
      submenu: [
        {
          label: this.i18n.names.bpmn,
          click: () => {
            this.fileOperations.newFile("bpmn");
          },
        },
        {
          label: this.i18n.names.dmn,
          click: () => {
            this.fileOperations.newFile("dmn");
          },
        },
      ],
    };

    this.openMenu = {
      label: this.i18n.terms.open,
      submenu: [
        {
          label: this.i18n.terms.file,
          accelerator: "CmdOrCtrl+O",
          click: () => {
            dialog
              .showOpenDialog(this.window, {
                title: this.i18n.menu.open.submenu.file.title,
                filters: [
                  {
                    name: this.i18n.menu.open.submenu.file.supported,
                    extensions: ["bpmn", "bpmn2", "dmn"],
                  },
                ],
              })
              .then((result) => {
                if (result && !result.canceled) {
                  this.fileOperations.openFile(result.filePaths[0]);
                }
              });
          },
        },
        {
          label: this.i18n.menu.open.submenu.sample,
          submenu: [
            {
              label: this.i18n.names.bpmn,
              click: () => {
                this.fileOperations.openSample(path.join(__dirname, "samples/sample.bpmn"));
              },
            },
            {
              label: this.i18n.names.dmn,
              click: () => {
                this.fileOperations.openSample(path.join(__dirname, "samples/sample.dmn"));
              },
            },
          ],
        },
      ],
    };

    this.saveMenu = {
      label: this.i18n.terms.save,
      accelerator: "CmdOrCtrl+S",
      click: () => {
        this.fileOperations.saveFile();
      },
      enabled: false,
    };

    this.saveAsMenu = {
      label: this.i18n.menu.saveAs,
      accelerator: "CmdOrCtrl+Shift+S",
      click: () => {
        this.fileOperations.saveFileAs();
      },
      enabled: false,
    };

    this.savePreviewAsMenu = {
      label: this.i18n.menu.savePreviewAs,
      click: () => {
        this.window.webContents.send("savePreview");
      },
      enabled: false,
    };

    this.closeWindowMenu = {
      label: this.i18n.menu.closeWindow,
      accelerator: "Command+W",
      click: () => {
        this.window.close();
      },
    };

    this.quitMenu = {
      label: this.i18n.terms.quit,
      accelerator: "CmdOrCtrl+Q",
      click: () => {
        app.quit();
      },
    };

    this.fileMenu = {
      label: this.i18n.terms.file,
      submenu: [
        this.newMenu,
        this.openMenu,
        this.saveMenu,
        this.saveAsMenu,
        this.savePreviewAsMenu,
        {
          type: "separator",
        },
        this.quitMenu,
      ],
    };

    this.macOsFileMenu = {
      label: this.i18n.terms.file,
      submenu: [
        this.newMenu,
        this.openMenu,
        this.saveMenu,
        this.saveAsMenu,
        this.savePreviewAsMenu,
        {
          type: "separator",
        },
        this.closeWindowMenu,
      ],
    };

    this.macOsAppMenu = {
      label: this.i18n.names.businessModeler.name,
      submenu: [
        {
          label: this.i18n.menu.macOsAppMenu.submenu.about,
          role: "about",
        },
        {
          type: "separator",
        },
        {
          label: this.i18n.menu.macOsAppMenu.submenu.services,
          role: "services",
          submenu: [],
        },
        {
          type: "separator",
        },
        {
          label: this.i18n.menu.macOsAppMenu.submenu.hide,
          accelerator: "Command+H",
          role: "hide",
        },
        {
          label: this.i18n.menu.macOsAppMenu.submenu.hideOthers,
          accelerator: "Command+Alt+H",
          role: "hideOthers",
        },
        {
          label: this.i18n.menu.macOsAppMenu.submenu.showAll,
          role: "unhide",
        },
        {
          type: "separator",
        },
        {
          label: this.i18n.terms.quit,
          accelerator: "Command+Q",
          click: () => app.quit(),
        },
      ],
    };

    this.editMenu = {
      label: this.i18n.terms.edit,
      submenu: [
        {
          label: this.i18n.menu.edit.submenu.label,
          click: () => {
            this.window.webContents.send("copyContentToClipboard");
          },
        },
        { label: this.i18n.terms.undo, accelerator: "CmdOrCtrl+Z", selector: "undo:" },
        { label: this.i18n.terms.redo, accelerator: "Shift+CmdOrCtrl+Z", selector: "redo:" },
        { label: this.i18n.terms.cut, accelerator: "CmdOrCtrl+X", selector: "cut:" },
        { label: this.i18n.terms.copy, accelerator: "CmdOrCtrl+C", selector: "copy:" },
        { label: this.i18n.terms.paste, accelerator: "CmdOrCtrl+V", selector: "paste:" },
        { label: this.i18n.menu.edit.submenu.selectAll, accelerator: "CmdOrCtrl+A", selector: "selectAll:" },
      ] as MenuItemConstructorOptions[],
    };

    this.devMenu = {
      label: this.i18n.menu.devMenu.label,
      submenu: [
        {
          label: this.i18n.menu.devMenu.submenu.showDevTools,
          click: () => {
            this.window.webContents.openDevTools();
          },
        },
        {
          label: this.i18n.menu.devMenu.submenu.clearUserData,
          click: () => {
            this.userData.clear();
          },
        },
      ],
    };
  }

  public setFileMenusEnabled(enabled: boolean) {
    this.getMenuItem(this.i18n.terms.save, this.menu)!.enabled = enabled;
    this.getMenuItem(this.i18n.menu.saveAs, this.menu)!.enabled = enabled;
    this.getMenuItem(this.i18n.menu.savePreviewAs, this.menu)!.enabled = enabled;
    this.getMenuItem(this.i18n.menu.edit.submenu.label, this.menu)!.enabled = enabled;
  }

  private getMenuItem(label: string, menuToSearch: ElectronMenu): MenuItem | undefined {
    for (const menuItem of menuToSearch.items) {
      if (menuItem.label === label) {
        return menuItem;
      } else if (menuItem.submenu) {
        const childMenuItem = this.getMenuItem(label, menuItem.submenu);
        if (childMenuItem) {
          return childMenuItem;
        }
      }
    }

    return undefined;
  }

  public setup() {
    const template: Array<MenuItemConstructorOptions | MenuItem> = [
      process.platform === "darwin" ? this.macOsFileMenu : this.fileMenu,
      this.editMenu,
    ];

    if (process.platform === "darwin") {
      template.unshift(this.macOsAppMenu);
    }

    if (!app.isPackaged) {
      template.push(this.devMenu);
    }

    this.menu = ElectronMenu.buildFromTemplate(template);
    ElectronMenu.setApplicationMenu(this.menu);

    // These menus cannot be hidden on MacOS, otherwise the shortcuts will not work
    if (process.platform !== "darwin") {
      this.getMenuItem(this.i18n.terms.undo, this.menu)!.visible = false;
      this.getMenuItem(this.i18n.terms.redo, this.menu)!.visible = false;
      this.getMenuItem(this.i18n.terms.cut, this.menu)!.visible = false;
      this.getMenuItem(this.i18n.terms.copy, this.menu)!.visible = false;
      this.getMenuItem(this.i18n.terms.paste, this.menu)!.visible = false;
      this.getMenuItem(this.i18n.menu.edit.submenu.selectAll, this.menu)!.visible = false;
    }
  }
}
