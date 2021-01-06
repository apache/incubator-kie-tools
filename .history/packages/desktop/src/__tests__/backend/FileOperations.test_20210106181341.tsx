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
import { I18n } from "@kogito-tooling/i18n/dist/core";

beforeEach(() => {
  document.execCommand = () => true;
});

jest.mock("../../backend/DesktopUserData", () => {
  return {
    DesktopUserData: jest.fn().mockImplementation()
  };
});

jest.mock("../../backend/Menu", () => {
  return {
    Menu: jest.fn().mockImplementation()
  };
});