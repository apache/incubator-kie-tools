/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import "@patternfly/react-core/dist/styles/base.css";
import "@patternfly/patternfly/patternfly-addons.scss";
import * as React from "react";
import * as ReactDOM from "react-dom";
import { DMNFormApp } from "./DMNFormApp";
import "../static/resources/style.css";
import { I18n } from "../../i18n/dist/core";
import { DmnFormI18n, dmnFormI18nDefaults, dmnFormI18nDictionaries } from "./i18n";
import { Alert, AlertVariant } from "@patternfly/react-core/dist/js/components/Alert";
import { DATA_JSON_PATH, FormData } from "./FormData";

const dmnFormI18n = new I18n<DmnFormI18n>(dmnFormI18nDefaults, dmnFormI18nDictionaries, "en");
const i18n = dmnFormI18n.getCurrent();

fetch(DATA_JSON_PATH)
  .then((response: any) => {
    if (!response.ok) {
      throw Error(i18n.error.notFound);
    }
    return response.json();
  })
  .then((formData: FormData) => {
    ReactDOM.render(<DMNFormApp formData={formData} />, document.getElementById("app")!);
  })
  .catch((error: any) => showError(error.toString()));

function showError(errorMessage: string): void {
  ReactDOM.render(
    <div className={"kogito--alert-container"}>
      <Alert variant={AlertVariant.danger} title={i18n.error.title}>
        <br />
        {errorMessage}
      </Alert>
    </div>,
    document.getElementById("app")!
  );
}
