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

import { act } from "react-dom/test-utils";
import * as electron from "electron";
import { FileSaveActions } from "../../common/ElectronFile";
import { DesktopUserData } from "../../backend/DesktopUserData";
import { Menu } from "../../backend/Menu";
import { FileOperations } from "../../backend/FileOperations";
import { showSaveDialogMock } from "../../../__mocks__/electron";
import { desktopI18nDefaults, desktopI18nDictionaries } from "../../backend/i18n";
import { I18n } from "@kie-tooling-core/i18n/dist/core";

beforeEach(() => {
  document.execCommand = () => true;
});

jest.mock("../../backend/DesktopUserData", () => {
  return {
    DesktopUserData: jest.fn().mockImplementation(),
  };
});

jest.mock("../../backend/Menu", () => {
  return {
    Menu: jest.fn().mockImplementation(),
  };
});

describe("saveFile ipc event", () => {
  test("check dialog for save file as operation", () => {
    const window = new electron.BrowserWindow();
    const userData = new DesktopUserData();
    const desktopI18n = new I18n(desktopI18nDefaults, desktopI18nDictionaries);
    const menu = new Menu(window, userData, desktopI18n);
    const fileOperations = new FileOperations(window, menu, userData, desktopI18n);

    act(() =>
      electron.ipcRenderer.send("saveFile", {
        action: FileSaveActions.SAVE_AS,
        file: {
          fileType: "dmn",
          fileContent: "content",
        },
      })
    );

    expect(showSaveDialogMock).toHaveBeenCalledTimes(1);
    expect(showSaveDialogMock.mock.calls[0][0]).toEqual(window);
    expect(showSaveDialogMock.mock.calls[0][1].defaultPath).toEqual("model.dmn");
    expect(showSaveDialogMock.mock.calls[0][1].title).toEqual("Save file");
    expect(showSaveDialogMock.mock.calls[0][1].filters!).toHaveLength(1);
    expect(showSaveDialogMock.mock.calls[0][1].filters![0].name).toEqual("DMN");
    expect(showSaveDialogMock.mock.calls[0][1].filters![0].extensions).toHaveLength(1);
    expect(showSaveDialogMock.mock.calls[0][1].filters![0].extensions[0]).toEqual("dmn");
  });
});
