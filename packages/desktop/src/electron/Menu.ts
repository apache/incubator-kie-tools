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

export class Menu {
  private readonly window: BrowserWindow;
  private readonly userData: DesktopUserData;
  private readonly fileOperations: FileOperations;
  private menu: ElectronMenu;

  private readonly newMenu = {
    label: "New",
    submenu: [
      {
        label: "BPMN",
        click: () => {
          this.fileOperations.newFile("bpmn");
        }
      },
      {
        label: "DMN",
        click: () => {
          this.fileOperations.newFile("dmn");
        }
      }
    ]
  };

  private readonly openMenu = {
    label: "Open",
    submenu: [
      {
        label: "File",
        accelerator: "CmdOrCtrl+O",
        click: () => {
          dialog
            .showOpenDialog(this.window, {
              title: "Open file",
              filters: [
                {
                  name: "Supported file extensions (*.bpmn, *.bpmn2, *.dmn)",
                  extensions: ["bpmn", "bpmn2", "dmn"]
                }
              ]
            })
            .then(result => {
              if (!result.canceled) {
                this.fileOperations.openFile(result.filePaths[0]);
              }
            });
        }
      },
      {
        label: "Sample",
        submenu: [
          {
            label: "BPMN",
            click: () => {
              this.fileOperations.openSample(path.join(__dirname, "samples/sample.bpmn"));
            }
          },
          {
            label: "DMN",
            click: () => {
              this.fileOperations.openSample(path.join(__dirname, "samples/sample.dmn"));
            }
          }
        ]
      }
    ]
  };

  private readonly saveMenu = {
    label: "Save",
    accelerator: "CmdOrCtrl+S",
    click: () => {
      this.fileOperations.saveFile();
    },
    enabled: false
  };

  private readonly saveAsMenu = {
    label: "Save As...",
    accelerator: "CmdOrCtrl+Shift+S",
    click: () => {
      this.fileOperations.saveFileAs();
    },
    enabled: false
  };

  private readonly savePreviewAsMenu = {
    label: "Save Preview As...",
    click: () => {
      this.window.webContents.send("savePreview");
    },
    enabled: false
  };

  private readonly closeWindowMenu = {
    label: "Close Window",
    accelerator: "Command+W",
    click: () => {
      this.window.close();
    }
  };

  private readonly quitMenu = {
    label: "Quit",
    accelerator: "CmdOrCtrl+Q",
    click: () => {
      app.quit();
    }
  };

  private readonly fileMenu: MenuItemConstructorOptions = {
    label: "File",
    submenu: [
      this.newMenu,
      this.openMenu,
      this.saveMenu,
      this.saveAsMenu,
      this.savePreviewAsMenu,
      {
        type: "separator"
      },
      this.quitMenu
    ]
  };

  private readonly macOSFileMenu: MenuItemConstructorOptions = {
    label: "File",
    submenu: [
      this.newMenu,
      this.openMenu,
      this.saveMenu,
      this.saveAsMenu,
      this.savePreviewAsMenu,
      {
        type: "separator"
      },
      this.closeWindowMenu
    ]
  };

  private readonly macOSAppMenu;

  private readonly editMenu = {
    label: "Edit",
    submenu: [
      {
        label: "Copy source",
        click: () => {
          this.window.webContents.send("copyContentToClipboard");
        }
      },
      { label: "Undo", accelerator: "CmdOrCtrl+Z", selector: "undo:" },
      { label: "Redo", accelerator: "Shift+CmdOrCtrl+Z", selector: "redo:" },
      { label: "Cut", accelerator: "CmdOrCtrl+X", selector: "cut:" },
      { label: "Copy", accelerator: "CmdOrCtrl+C", selector: "copy:" },
      { label: "Paste", accelerator: "CmdOrCtrl+V", selector: "paste:" },
      { label: "Select All", accelerator: "CmdOrCtrl+A", selector: "selectAll:" }
    ] as MenuItemConstructorOptions[]
  };

  private readonly toolsMenu = {
    label: "Help",
    submenu: [
      {
        label: "Developer Tools",
        accelerator: "Alt+Shift+CmdOrCtrl+I",
        click: () => {
          this.window.webContents.openDevTools();
        }
      },
      {
        label: "Clear recent files",
        click: () => {
          this.userData.clear();
        }
      }
    ]
  };

  constructor(window: BrowserWindow, userData: DesktopUserData) {
    this.window = window;
    this.userData = userData;
    this.fileOperations = new FileOperations(window, this, userData);

    this.macOSAppMenu = {
      label: "Business Modeler Preview",
      submenu: [
        {
          label: "About Business Modeler Preview",
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
          label: "Hide Business Modeler Preview",
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
    };

    ipcMain.on("setFileMenusEnabled", (event: IpcMainEvent, data: { enabled: boolean }) => {
      this.setFileMenusEnabled(data.enabled);
    });
  }

  public setFileMenusEnabled(enabled: boolean) {
    this.getMenuItem("Save", this.menu)!.enabled = enabled;
    this.getMenuItem("Save As...", this.menu)!.enabled = enabled;
    this.getMenuItem("Save Preview As...", this.menu)!.enabled = enabled;
    this.getMenuItem("Copy source", this.menu)!.enabled = enabled;
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
      process.platform === "darwin" ? this.macOSFileMenu : this.fileMenu,
      this.editMenu,
      this.toolsMenu
    ];

    if (process.platform === "darwin") {
      template.unshift(this.macOSAppMenu);
    }

    this.menu = ElectronMenu.buildFromTemplate(template);
    ElectronMenu.setApplicationMenu(this.menu);

    // These menus cannot be hidden on MacOS, otherwise the shortcuts will not work
    if (process.platform !== "darwin") {
      this.getMenuItem("Undo", this.menu)!.visible = false;
      this.getMenuItem("Redo", this.menu)!.visible = false;
      this.getMenuItem("Cut", this.menu)!.visible = false;
      this.getMenuItem("Copy", this.menu)!.visible = false;
      this.getMenuItem("Paste", this.menu)!.visible = false;
      this.getMenuItem("Select All", this.menu)!.visible = false;
    }
  }
}
