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

import { CommonI18n } from "@kogito-tooling/i18n-common-dictionary";
import { ReferenceDictionary } from "@kogito-tooling/i18n/dist/core";

interface DesktopDictionary extends ReferenceDictionary {
  fileOperations: {
    dialog: {
      savePreview: string;
      saveFile: string;
    };
  };
  menu: {
    open: {
      submenu: {
        file: {
          title: string;
          supported: string;
        };
        sample: string;
      };
    };
    saveAs: string;
    savePreviewAs: string;
    closeWindow: string;
    edit: {
      submenu: {
        label: string;
        selectAll: string;
      };
    };
    devMenu: {
      label: string;
      submenu: {
        showDevTools: string;
        clearUserData: string;
      };
    };
    macOsAppMenu: {
      submenu: {
        about: string;
        services: string;
        hide: string;
        hideOthers: string;
        showAll: string;
      };
    };
  };
}

export interface DesktopI18n extends DesktopDictionary, CommonI18n {}
