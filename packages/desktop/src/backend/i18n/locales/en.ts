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

import { en as en_common } from "@kie-tools/i18n-common-dictionary";
import { DesktopI18n } from "../DesktopI18n";

export const en: DesktopI18n = {
  ...en_common,
  fileOperations: {
    dialog: {
      savePreview: "Save preview",
      saveFile: "Save file",
    },
  },
  menu: {
    open: {
      submenu: {
        file: {
          title: "Open file",
          supported: "Supported file extensions (*.bpmn, *.bpmn2, *.dmn)",
        },
        sample: "Sample",
      },
    },
    saveAs: `${en_common.terms.save} As...`,
    savePreviewAs: `${en_common.terms.save} Preview As...`,
    closeWindow: `${en_common.terms.close} Window`,
    edit: {
      submenu: {
        label: "Copy source",
        selectAll: "Select All",
      },
    },
    devMenu: {
      label: "Development Menu",
      submenu: {
        showDevTools: "Show Developer Tools",
        clearUserData: "Clear User Data",
      },
    },
    macOsAppMenu: {
      submenu: {
        about: `About ${en_common.names.businessModeler}`,
        services: "Services",
        hide: `Hide ${en_common.names.businessModeler}`,
        hideOthers: "Hide Others",
        showAll: "Show All",
      },
    },
  },
};
