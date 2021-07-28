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

import * as React from "react";
import * as ReactDOM from "react-dom";
import * as electron from "electron";
import { App } from "./App";
import { I18nDictionariesProvider } from "@kie-tooling-core/i18n/dist/react-components";
import { HubI18nContext, hubI18nDefaults, hubI18nDictionaries } from "../common/i18n";

document.addEventListener("DOMContentLoaded", () => {
  ReactDOM.render(
    <I18nDictionariesProvider
      defaults={hubI18nDefaults}
      dictionaries={hubI18nDictionaries}
      initialLocale={navigator.language}
      ctx={HubI18nContext}
    >
      <App />
    </I18nDictionariesProvider>,
    document.getElementById("app")!,
    () => {
      electron.ipcRenderer.send("mainWindowLoaded");
    }
  );
});
